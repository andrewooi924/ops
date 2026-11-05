"""DON!! cards: the official don card list PDF plus a stable id registry.

The PDF has no card codes. Each page shows up to ten don images with the
product they came from printed underneath, and new dons are added at the
end. The PDF's address contains a random token that changes whenever it is
updated, so it is rediscovered from the card list page on every run.

Dons get ids from ``data/don_registry.json``: an image is matched to an
existing entry by perceptual hash, otherwise it receives the next id. The
registry is committed, so ids stay stable across PDF revisions.
"""

from __future__ import annotations

import hashlib
import json
import re
from dataclasses import dataclass
from pathlib import Path
from typing import Dict, List, Optional

from . import imagehash

PDF_LINK = re.compile(r"https://assets\.carddass\.com/[^\"'\s>]+?don-cardlist\.pdf")

# Distance in bits between 256-bit dhashes. In the October 2026 PDF some
# different dons (normal/foil versions of one artwork) are only 0-4 bits apart,
# so exact bytes are tried first and label/position break near ties.
MATCH_DISTANCE = 24
# Announced (often watermarked) images this close to a PDF don are reported
# as possible duplicates for a human to confirm.
REVIEW_DISTANCE = 60
COLOUR_LIMIT = 60
# Full-page backgrounds and decorations are wider than any card on the page.
MAX_CARD_WIDTH_PT = 400


@dataclass
class PdfDon:
    page: int
    index: int
    label: str
    png: bytes
    width: int
    height: int
    sha256: str
    dhash: str
    colour: str


def find_pdf_url(cardlist_html: str) -> Optional[str]:
    match = PDF_LINK.search(cardlist_html)
    return match.group(0) if match else None


def parse_pdf(pdf_bytes: bytes) -> List[PdfDon]:
    import fitz  # PyMuPDF; imported lazily so the rest works without it

    document = fitz.open(stream=pdf_bytes, filetype="pdf")
    dons: List[PdfDon] = []
    for page_number, page in enumerate(document, 1):
        lines = []
        for block in page.get_text("dict")["blocks"]:
            for line in block.get("lines", []):
                text = "".join(span["text"] for span in line["spans"]).strip()
                if text and "ドン!!カードリスト" not in text:
                    lines.append((*line["bbox"], text))
        images = [
            info
            for info in page.get_image_info(xrefs=True)
            if info["xref"] and (info["bbox"][2] - info["bbox"][0]) < MAX_CARD_WIDTH_PT
        ]
        images.sort(key=lambda info: (round(info["bbox"][1]), info["bbox"][0]))
        for index, info in enumerate(images):
            x0, _, x1, y1 = info["bbox"]
            # Text lines just below the image that overlap its width.
            below = sorted(
                (line for line in lines if y1 - 2 <= line[1] < y1 + 40 and line[0] < x1 and line[2] > x0),
                key=lambda line: line[1],
            )
            label = " ".join(line[4] for line in below)
            pixmap = fitz.Pixmap(document, info["xref"])
            if pixmap.n - pixmap.alpha >= 4:
                pixmap = fitz.Pixmap(fitz.csRGB, pixmap)
            png = pixmap.tobytes("png")
            dons.append(
                PdfDon(
                    page=page_number,
                    index=index,
                    label=label,
                    png=png,
                    width=pixmap.width,
                    height=pixmap.height,
                    sha256=hashlib.sha256(png).hexdigest(),
                    dhash=imagehash.dhash(png),
                    colour=imagehash.colour_signature(png),
                )
            )
    return dons


class DonRegistry:
    def __init__(self, path: Path) -> None:
        self.path = Path(path)
        self.entries: List[dict] = []
        if self.path.exists():
            self.entries = json.loads(self.path.read_text("utf-8"))["dons"]

    def _next_id(self) -> str:
        numbers = [int(entry["id"].split("-")[1]) for entry in self.entries]
        return f"DON-{(max(numbers) if numbers else 0) + 1:04d}"

    def best_match(self, dhash: str, colour: str, label: str = "", position=None, exclude=()) -> Optional[dict]:
        """Closest entry by artwork, then by product label, colour and PDF position.

        Some different dons are near-identical images (a normal and a foil
        version of the same art), so artwork distance alone cannot tell them
        apart; the label and position break those ties.
        """
        candidates = []
        for entry in self.entries:
            if entry["id"] in exclude:
                continue
            gap = imagehash.distance(entry["dhash"], dhash)
            colour_gap = imagehash.colour_distance(entry["colour"], colour)
            if gap > MATCH_DISTANCE or colour_gap > COLOUR_LIMIT:
                continue
            same_label = 0 if (entry.get("label") or "") == (label or "") else 1
            where = entry.get("position")
            position_gap = abs(where[0] * 100 + where[1] - (position[0] * 100 + position[1])) if where and position else 10 ** 6
            candidates.append((gap, same_label, colour_gap, position_gap, entry["id"], entry))
        return min(candidates)[-1] if candidates else None

    def assign(self, dhash: str, colour: str, sha256: str, label: str, seen: str,
               exclude=(), position=None) -> dict:
        """Return the entry for this image, creating one when nothing matches."""
        entry = next(
            (e for e in self.entries if e.get("sha256") == sha256 and e["id"] not in exclude), None
        ) or self.best_match(dhash, colour, label, position, exclude)
        if entry is None:
            entry = {"id": self._next_id(), "first_seen": seen}
            self.entries.append(entry)
        entry.update({"dhash": dhash, "colour": colour, "sha256": sha256, "label": label})
        if position is not None:
            entry["position"] = list(position)
        return entry

    def save(self) -> None:
        self.path.parent.mkdir(parents=True, exist_ok=True)
        payload = {"dons": sorted(self.entries, key=lambda entry: entry["id"])}
        self.path.write_text(json.dumps(payload, ensure_ascii=False, indent=1) + "\n", "utf-8")


def build_don_printings(
    pdf_dons: List[PdfDon], registry: DonRegistry, pdf_url: str, today: str
) -> List[dict]:
    printings = []
    used: Dict[str, bool] = {}
    for don in pdf_dons:
        entry = registry.assign(don.dhash, don.colour, don.sha256, don.label, today,
                                exclude=used, position=(don.page, don.index))
        used[entry["id"]] = True
        printings.append(
            {
                "id": entry["id"],
                "code": "DON",
                "kind": "don",
                "variant": None,
                "rarity": "DON",
                "status": "official",
                "stats": None,
                "jp": {"name": "ドン!!カード", "source": don.label or None},
                "en": {"name": "DON!! Card", "source": None},
                "series": [],
                "image": {"source": pdf_url, "quality": "official", "pdf": {"page": don.page, "index": don.index}},
                "checks": [] if don.label else ["don_without_label"],
                "_png": don.png,
                "_dhash": don.dhash,
                "_colour": don.colour,
            }
        )
    return printings
