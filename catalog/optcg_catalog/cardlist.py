"""Parses the official card list pages (Japanese and English sites).

Both sites render every printing as ``<dl class="modalCol" id="OP01-001_p2">``
with the same inner structure; only the labels differ by language.
"""

from __future__ import annotations

import re
import urllib.parse
from dataclasses import dataclass, field
from typing import List, Optional

from bs4 import BeautifulSoup, Tag

SITES = {
    "jp": "https://www.onepiece-cardgame.com",
    "en": "https://en.onepiece-cardgame.com",
}
CARDLIST_PATH = "/cardlist/"

PRINTING_ID = re.compile(r"^(?P<code>[A-Z]+\d*-\d{3})(?:_p(?P<variant>\d+))?$")
SET_CODE = re.compile(r"[【\[]\s*([A-Z]+-?\d{2}(?:\s*-\s*[A-Z]+-?\d{2})*)\s*[】\]]")
CODE_PART = re.compile(r"([A-Z]+)-?(\d{2})")

# Labels on the stat that is either cost or life, per site.
LIFE_LABELS = {"ライフ", "Life"}


@dataclass
class Series:
    value: str
    label: str
    set_code: Optional[str]
    kind: str
    # Other set codes sharing this list, e.g. the English site files EB-04
    # cards under "[OP14-EB04]" and "[OP15-EB04]".
    also: List[str] = field(default_factory=list)

    @property
    def codes(self) -> List[str]:
        return ([self.set_code] if self.set_code else []) + self.also


@dataclass
class RawCard:
    id: str
    code: str
    variant: Optional[int]
    rarity: str
    category: str
    name: str
    cost: Optional[str] = None
    life: Optional[str] = None
    attributes: List[str] = field(default_factory=list)
    power: Optional[str] = None
    counter: Optional[str] = None
    colors: List[str] = field(default_factory=list)
    block: Optional[str] = None
    feature: Optional[str] = None
    effect: Optional[str] = None
    trigger: Optional[str] = None
    source: Optional[str] = None
    notes: Optional[str] = None
    image_url: Optional[str] = None
    series: List[str] = field(default_factory=list)


def series_url(site: str, value: str) -> str:
    return f"{SITES[site]}{CARDLIST_PATH}?series={value}"


def _clean_label(text: str) -> str:
    text = re.sub(r"<[^>]+>", " ", text)
    return re.sub(r"\s+", " ", text).strip()


def series_kind(label: str) -> str:
    lowered = label.lower()
    checks = [
        ("promo", ("プロモーションカード", "promotion card")),
        ("limited", ("限定商品", "other product")),
        ("family", ("ファミリーデッキ", "family deck")),
        ("premium", ("プレミアムブースター", "premium booster")),
        ("extra", ("エクストラブースター", "extra booster")),
        ("starter", ("スタートデッキ", "アルティメットデッキ", "starter deck", "ultimate deck")),
        ("booster", ("ブースターパック", "booster pack")),
    ]
    for kind, needles in checks:
        if any(needle in label or needle in lowered for needle in needles):
            return kind
    return "other"


def set_codes(label: str) -> List[str]:
    """``【OP-01】`` -> ``["OP-01"]``; ``[OP15-EB04]`` -> ``["OP-15", "EB-04"]``."""
    match = SET_CODE.search(label)
    if not match:
        return []
    return [f"{prefix}-{number}" for prefix, number in CODE_PART.findall(match.group(1))]


def normalise_set_code(label: str) -> Optional[str]:
    codes = set_codes(label)
    return codes[0] if codes else None


def parse_series_options(html: str) -> List[Series]:
    soup = BeautifulSoup(html, "html.parser")
    select = soup.find("select", attrs={"name": "series"})
    if select is None:
        raise ValueError("series selector not found; the card list layout may have changed")
    series = []
    for option in select.find_all("option"):
        value = (option.get("value") or "").strip()
        if not value:
            continue
        label = _clean_label(option.get_text(" "))
        codes = set_codes(label)
        series.append(Series(value, label, codes[0] if codes else None, series_kind(label), codes[1:]))
    return series


def _text(node: Optional[Tag]) -> Optional[str]:
    """Text of a stat block without its heading, keeping line breaks."""
    if node is None:
        return None
    node = _copy(node)
    for heading in node.find_all("h3"):
        heading.decompose()
    for extra in node.find_all(["button", "a"], class_=re.compile("getInfoBtn|viewChangeBtn|commonBtn")):
        extra.decompose()
    for wrapper in node.find_all("div", class_="getInfoBtnCol"):
        wrapper.decompose()
    for br in node.find_all("br"):
        br.replace_with("\n")
    text = node.get_text("")
    lines = [re.sub(r"[ \t　]+", " ", line).strip() for line in text.split("\n")]
    text = "\n".join(line for line in lines if line)
    return text or None


def _copy(node: Tag) -> Tag:
    return BeautifulSoup(str(node), "html.parser").find(node.name)


def _value(text: Optional[str]) -> Optional[str]:
    if text is None:
        return None
    text = text.strip()
    return None if text in ("", "-", "−") else text


def _split(text: Optional[str]) -> List[str]:
    if not text:
        return []
    return [part.strip() for part in re.split(r"[/／]", text) if part.strip()]


def parse_cards(html: str, site: str, page_url: Optional[str] = None) -> List[RawCard]:
    soup = BeautifulSoup(html, "html.parser")
    base = page_url or f"{SITES[site]}{CARDLIST_PATH}"
    cards = []
    for block in soup.find_all("dl", class_="modalCol"):
        card_id = (block.get("id") or "").strip()
        match = PRINTING_ID.match(card_id)
        if not match:
            continue
        info = [span.get_text(strip=True) for span in block.select(".infoCol span")]
        rarity = info[1] if len(info) > 1 else ""
        category = info[2] if len(info) > 2 else ""
        name_node = block.find(class_="cardName")
        card = RawCard(
            id=card_id,
            code=match.group("code"),
            variant=int(match.group("variant")) if match.group("variant") else None,
            rarity=rarity,
            category=category,
            name=name_node.get_text(strip=True) if name_node else "",
        )

        cost_node = block.find("div", class_="cost")
        if cost_node is not None:
            heading = cost_node.find("h3")
            value = _value(_text(cost_node))
            if heading is not None and heading.get_text(strip=True) in LIFE_LABELS:
                card.life = value
            else:
                card.cost = value

        attribute = block.find("div", class_="attribute")
        if attribute is not None:
            names = [img.get("alt", "").strip() for img in attribute.find_all("img")]
            card.attributes = [part for name in names for part in _split(name)]

        card.power = _value(_text(block.find("div", class_="power")))
        card.counter = _value(_text(block.find("div", class_="counter")))
        card.colors = _split(_value(_text(block.find("div", class_="color"))))
        card.block = _value(_text(block.find("div", class_="block")))
        card.feature = _value(_text(block.find("div", class_="feature")))
        card.effect = _value(_text(block.find("div", class_="text")))
        card.trigger = _value(_text(block.find("div", class_="trigger")))

        for info_node in block.find_all("div", class_="getInfo"):
            classes = info_node.get("class") or []
            if "remarks" in classes:
                card.notes = _text(info_node)
            elif card.source is None:
                card.source = _text(info_node)

        image = block.select_one(".frontCol img")
        if image is not None:
            src = image.get("data-src") or image.get("src")
            if src and "dummy" not in src:
                card.image_url = urllib.parse.urljoin(base, src)
        cards.append(card)
    return cards
