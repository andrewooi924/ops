"""Cards announced on official event and product pages.

These pages show cards weeks or months before the card list or don PDF
includes them. Card images carry alt text such as
``『P-160 ネフェルタリ・ビビ』のカード画像`` or
``『世界統一記念ドン‼︎カード-ナミ-』のカード画像``.
"""

from __future__ import annotations

import re
import urllib.parse
from dataclasses import dataclass
from typing import List, Optional

from bs4 import BeautifulSoup

from .cardlist import SITES

INDEX_PAGES = ["/events/", "/products/", "/products/?subcategory=others"]
PAGE_LINK = re.compile(r"/(?:events|products)/[A-Za-z0-9_\-/]+\.(?:html|php)$")
ALT_PATTERN = re.compile(r"^『(?P<name>.+)』のカード画像$")
CODE_PREFIX = re.compile(r"^(?P<code>[A-Z]+\d*-\d{3})\s*(?P<rest>.*)$")
FILE_CODE = re.compile(r"(?:^|[/_])(?P<code>(?:OP|ST|EB|PRB|P)\d*-\d{3})(?:_?sample)?\.(?:webp|png|jpg)$")


@dataclass
class Announcement:
    kind: str  # "card" or "don"
    name: str
    code: Optional[str]
    image_url: str
    page_url: str
    page_title: str
    sample: bool


def discover_pages(index_html: str, index_url: str) -> List[str]:
    soup = BeautifulSoup(index_html, "html.parser")
    pages = []
    for link in soup.find_all("a", href=True):
        url = urllib.parse.urljoin(index_url, link["href"]).split("#")[0]
        parts = urllib.parse.urlsplit(url)
        if parts.netloc != urllib.parse.urlsplit(SITES["jp"]).netloc:
            continue
        if PAGE_LINK.search(parts.path) and url not in pages:
            pages.append(url)
    return pages


def parse_page(html: str, page_url: str) -> List[Announcement]:
    soup = BeautifulSoup(html, "html.parser")
    title_node = soup.find("title")
    title = title_node.get_text(strip=True).split("｜")[0] if title_node else page_url
    found: List[Announcement] = []
    seen = set()
    for image in soup.find_all("img"):
        src = image.get("data-src") or image.get("src") or ""
        alt = (image.get("alt") or "").strip()
        match = ALT_PATTERN.match(alt)
        file_match = FILE_CODE.search(src)
        if not match and not file_match:
            continue
        # The zoom link, when present, points at the largest copy.
        anchor = image.find_parent("a", href=True)
        if anchor is not None and re.search(r"\.(?:webp|png|jpg)$", anchor["href"]):
            src = anchor["href"]
        url = urllib.parse.urljoin(page_url, src)
        if url in seen:
            continue
        seen.add(url)

        name = match.group("name") if match else ""
        code = None
        code_match = CODE_PREFIX.match(name)
        if code_match:
            code, name = code_match.group("code"), code_match.group("rest").strip()
        elif file_match:
            # Product pages put the product's name in the alt text; only the
            # file name identifies the card, so the card's own name is unknown.
            code, name = file_match.group("code"), ""
        kind = "don" if "ドン" in name and code is None else "card"
        if kind == "card" and code is None:
            continue  # product shots and other images without a card code
        found.append(
            Announcement(
                kind=kind,
                name=name,
                code=code,
                image_url=url,
                page_url=page_url,
                page_title=title,
                sample="sample" in url.lower() or "dummy" in url.lower(),
            )
        )
    return found
