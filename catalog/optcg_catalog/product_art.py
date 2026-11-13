"""Box and pack art for each booster, made from Bandai's product photos.

Bandai's product pages show the sealed pack (``img_item01``) and, for newer
sets, the box at an angle (``img_item02``), both on transparent backgrounds.
Each photo is trimmed to the product and placed on the app's canvas: the
product fills the height and is centred, like the hand-made box art the app
bundles. Sets with no photo on Bandai's site can be given one in
``data/product_art.json``; photos on a plain background have it removed.
"""

from __future__ import annotations

import hashlib
import io
import json
import re
from collections import deque
from pathlib import Path
from typing import Dict, Optional

from PIL import Image

from .fetch import FetchError, Fetcher

SITE = "https://www.onepiece-cardgame.com"
DAY = 24 * 60 * 60

# Canvas width / height, matching the app's bundled art (box 325x500, pack 251x500).
CANVAS = {"box": 325 / 500, "pack": 251 / 500}
ITEM = {"pack": "img_item01", "box": "img_item02"}
# Big releases get their own minisite (e.g. OP-17's /products/boosters/op17/) with these names.
MINISITE = {"pack": "product_pack", "box": "product_booster"}
MAX_HEIGHT = 1000  # never upscaled; Bandai's photos give about 590px
KINDS = ("booster", "extra", "premium")


def slug(code: str) -> str:
    return code.lower().replace("-", "")


def find_photos(fetcher: Fetcher, code: str, max_age: float) -> Dict[str, str]:
    """URLs of the pack and box photos on Bandai's product page for ``code``, where they exist."""
    found: Dict[str, str] = {}
    try:
        page = fetcher.get_text(f"{SITE}/products/{slug(code)}.html", max_age=max_age)
    except FetchError:
        page = ""
    for kind, item in ITEM.items():
        match = re.search(r"[\"'(]([^\"'()]*/%s\.(?:webp|png|jpe?g))" % item, page)
        if match:
            found[kind] = match.group(1) if match.group(1).startswith("http") else SITE + match.group(1)
    # Older product pages keep their photos under a fixed path; minisites under another.
    for kind in ITEM:
        for url in (
            f"{SITE}/renewal/images/products/boosters/{slug(code)}/{ITEM[kind]}.webp",
            f"{SITE}/products/boosters/{slug(code)}/images/others/{MINISITE[kind]}.webp",
        ):
            if kind in found:
                break
            try:
                fetcher.get(url, max_age=30 * DAY)
                found[kind] = url
            except FetchError:
                pass
    return found


def _remove_plain_background(image: Image.Image, tolerance: int = 24) -> Image.Image:
    """Makes the background transparent, for photos without transparency.

    The background is whatever matches the corner colour and touches the edge.
    """
    image = image.convert("RGBA")
    width, height = image.size
    pixels = image.load()
    corner = pixels[0, 0][:3]

    def matches(xy) -> bool:
        r, g, b, _ = pixels[xy]
        return abs(r - corner[0]) <= tolerance and abs(g - corner[1]) <= tolerance and abs(b - corner[2]) <= tolerance

    seen = set()
    queue = deque()
    for x in range(width):
        queue.extend([(x, 0), (x, height - 1)])
    for y in range(height):
        queue.extend([(0, y), (width - 1, y)])
    while queue:
        xy = queue.popleft()
        if xy in seen:
            continue
        seen.add(xy)
        if not matches(xy):
            continue
        r, g, b, _ = pixels[xy]
        pixels[xy] = (r, g, b, 0)
        x, y = xy
        for nxt in ((x + 1, y), (x - 1, y), (x, y + 1), (x, y - 1)):
            if 0 <= nxt[0] < width and 0 <= nxt[1] < height and nxt not in seen:
                queue.append(nxt)
    return image


def make_art(source: bytes, kind: str) -> bytes:
    """Trims the product photo and centres it on the canvas for ``kind`` ("box" or "pack")."""
    image = Image.open(io.BytesIO(source))
    image = image.convert("RGBA")
    alpha = image.getchannel("A")
    if alpha.getextrema()[0] == 255:  # fully opaque: a photo on a plain background
        image = _remove_plain_background(image)
        alpha = image.getchannel("A")
    box = alpha.point(lambda value: 255 if value > 16 else 0).getbbox()
    if box is None:
        raise ValueError("the photo is empty")
    product = image.crop(box)

    ratio = CANVAS[kind]
    if product.width / product.height <= ratio:
        height = product.height
        width = round(height * ratio)
    else:  # wider than the canvas: fill the width instead
        width = product.width
        height = round(width / ratio)
    canvas = Image.new("RGBA", (width, height), (0, 0, 0, 0))
    canvas.paste(product, ((width - product.width) // 2, (height - product.height) // 2), product)
    if canvas.height > MAX_HEIGHT:
        canvas = canvas.resize((round(canvas.width * MAX_HEIGHT / canvas.height), MAX_HEIGHT), Image.LANCZOS)
    buffer = io.BytesIO()
    canvas.save(buffer, "WEBP", quality=90, method=6)
    return buffer.getvalue()


def load_overrides(path: Path) -> Dict[str, Dict[str, str]]:
    if not path.exists():
        return {}
    data = json.loads(path.read_text("utf-8"))
    return {code: urls for code, urls in data.get("sets", {}).items()}


def attach_art(fetcher: Fetcher, sets: list, index, out_dir: Path, overrides: Dict[str, Dict[str, str]],
               max_age: float, report: dict, log=print) -> None:
    """Adds ``box_art`` / ``pack_art`` file names to booster sets, making any new images."""
    created = 0
    out_dir.mkdir(parents=True, exist_ok=True)
    for entry in sets:
        code = entry.get("code")
        if not code or entry.get("kind") not in KINDS:
            continue
        photos = find_photos(fetcher, code, max_age)
        photos.update(overrides.get(code, {}))
        for kind in ("box", "pack"):
            url = photos.get(kind)
            if not url:
                continue
            key = f"art:{kind}:{url}"
            art = index.get(key, out_dir)
            if art is None:
                try:
                    data = make_art(fetcher.get(url, max_age=30 * DAY), kind)
                except (FetchError, OSError, ValueError) as error:
                    report["errors"].append(f"{kind} art {code}: {error}")
                    continue
                name = f"{hashlib.sha256(data).hexdigest()[:20]}.webp"
                (out_dir / name).write_bytes(data)
                art = {"art": name}
                index.put(key, art)
                created += 1
            entry[f"{kind}_art"] = art["art"]
    log(f"[art] {created} new box/pack images")
