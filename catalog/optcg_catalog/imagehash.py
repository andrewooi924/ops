"""Small perceptual hashes for matching the same artwork across sources."""

from __future__ import annotations

import io
from typing import Union

from PIL import Image

ImageInput = Union[bytes, Image.Image]


def _open(image: ImageInput) -> Image.Image:
    if isinstance(image, Image.Image):
        return image
    return Image.open(io.BytesIO(image))


def dhash(image: ImageInput, size: int = 16) -> str:
    """Difference hash: compares neighbouring pixels of a small greyscale copy.

    Survives resizing, recompression and light watermarks, so a watermarked
    announcement image still lands close to the clean official one.
    """
    grey = _open(image).convert("L").resize((size + 1, size), Image.LANCZOS)
    pixels = list(grey.getdata())
    bits = 0
    for row in range(size):
        for col in range(size):
            left = pixels[row * (size + 1) + col]
            right = pixels[row * (size + 1) + col + 1]
            bits = (bits << 1) | (1 if left > right else 0)
    return f"{bits:0{size * size // 4}x}"


def colour_signature(image: ImageInput) -> str:
    """Average colour of a 4x4 grid, separating e.g. gold and black frames."""
    small = _open(image).convert("RGB").resize((4, 4), Image.BOX)
    return "".join(f"{r >> 4:x}{g >> 4:x}{b >> 4:x}" for r, g, b in small.getdata())


def distance(a: str, b: str) -> int:
    return bin(int(a, 16) ^ int(b, 16)).count("1")


def colour_distance(a: str, b: str) -> int:
    return sum(abs(int(x, 16) - int(y, 16)) for x, y in zip(a, b))
