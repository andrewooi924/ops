"""Thumbnail and detail WebP variants with content-hashed file names.

Every variant is named after a hash of its own bytes, so a published URL
never changes meaning and the app can cache it forever. ``asset-index.json``
remembers which source image produced which variants, so unchanged
originals are not downloaded or re-encoded on later runs.
"""

from __future__ import annotations

import hashlib
import io
import json
from pathlib import Path
from typing import Dict, Optional

from PIL import Image

from . import imagehash

VARIANTS = {"thumb": (240, 80), "detail": (720, 85)}  # max width, WebP quality


def _encode(image: Image.Image, max_width: int, quality: int) -> bytes:
    copy = image.copy()
    if copy.width > max_width:  # never upscale
        height = round(copy.height * max_width / copy.width)
        copy = copy.resize((max_width, height), Image.LANCZOS)
    buffer = io.BytesIO()
    copy.save(buffer, "WEBP", quality=quality, method=6)
    return buffer.getvalue()


def make_variants(source: bytes, out_dir: Path) -> dict:
    image = Image.open(io.BytesIO(source))
    image = image.convert("RGBA" if image.mode in ("RGBA", "LA", "P") else "RGB")
    result = {"width": image.width, "height": image.height, "dhash": imagehash.dhash(image)}
    out_dir.mkdir(parents=True, exist_ok=True)
    for name, (max_width, quality) in VARIANTS.items():
        data = _encode(image, max_width, quality)
        digest = hashlib.sha256(data).hexdigest()[:20]
        file_name = f"{digest}.webp"
        path = out_dir / file_name
        if not path.exists():
            path.write_bytes(data)
        result[name] = file_name
        result[f"{name}_bytes"] = len(data)
    return result


class AssetIndex:
    """Maps a source key (image URL with its version query, or PDF hash) to variants.

    By default an entry only counts when its files are in the output folder. With
    ``published=True`` the files are trusted to be on the image host already (the
    workflow uploads each run's new files and then the index), so only new images
    are made.
    """

    def __init__(self, path: Path, published: bool = False) -> None:
        self.path = Path(path)
        self.published = published
        self.entries: Dict[str, dict] = {}
        if self.path.exists():
            self.entries = json.loads(self.path.read_text("utf-8"))

    def get(self, key: str, out_dir: Path) -> Optional[dict]:
        entry = self.entries.get(key)
        if entry and (self.published or all((out_dir / entry[name]).exists() for name in VARIANTS)):
            return entry
        return None

    def put(self, key: str, entry: dict) -> None:
        self.entries[key] = entry

    def save(self) -> None:
        self.path.parent.mkdir(parents=True, exist_ok=True)
        self.path.write_text(json.dumps(self.entries, sort_keys=True, indent=0) + "\n", "utf-8")
