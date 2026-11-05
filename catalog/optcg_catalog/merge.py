"""Joins English metadata onto Japanese printings and checks the join.

The Japanese site defines each printing (its id, artwork and products).
English text is matched on the base card code only: variant suffixes are
numbered independently on each site (JP ``OP01-001_p2`` is EN
``OP01-001_p1``), so they are never used to join.
"""

from __future__ import annotations

from collections import OrderedDict
from typing import Dict, Iterable, List, Optional

from .cardlist import RawCard

COLORS = {
    "赤": "red", "緑": "green", "青": "blue", "紫": "purple", "黒": "black", "黄": "yellow",
    "Red": "red", "Green": "green", "Blue": "blue", "Purple": "purple", "Black": "black",
    "Yellow": "yellow",
}
ATTRIBUTES = {
    "斬": "slash", "打": "strike", "射": "ranged", "特": "special", "知": "wisdom",
    "Slash": "slash", "Strike": "strike", "Ranged": "ranged", "Special": "special",
    "Wisdom": "wisdom",
    "？": "unknown", "?": "unknown",
}
RARITIES = {"SPカード": "SP", "SP CARD": "SP", "SP": "SP"}
CATEGORIES = {"LEADER", "CHARACTER", "EVENT", "STAGE"}

# Fields that must agree between the two sites for the same base code.
CHECKED_FIELDS = ("category", "cost", "life", "power", "counter", "colors", "attributes")


def _int(value: Optional[str]) -> Optional[int]:
    if value is None:
        return None
    digits = "".join(ch for ch in value if ch.isdigit())
    return int(digits) if digits else None


def _canonical(values: Iterable[str], table: Dict[str, str]) -> List[str]:
    return sorted({table.get(value, value.lower()) for value in values})


CATEGORY_HINTS = (("LEADER", "リーダー"), ("CHARACTER", "キャラ"), ("EVENT", "イベント"), ("STAGE", "ステージ"))


def category(value: Optional[str]) -> Optional[str]:
    """Upper-case English category; tolerates typos seen on the site (e.g. P-160)."""
    if not value:
        return None
    upper = value.strip().upper()
    if upper in CATEGORIES:
        return upper
    for name, hint in CATEGORY_HINTS:
        if hint in value or name in upper:
            return name
    return upper


def stats(card: RawCard) -> dict:
    """Language-neutral facts about a card, comparable across sites."""
    return {
        "category": category(card.category),
        "cost": _int(card.cost),
        "life": _int(card.life),
        "power": _int(card.power),
        "counter": _int(card.counter),
        "colors": _canonical(card.colors, COLORS),
        "attributes": _canonical(card.attributes, ATTRIBUTES),
        "block": _int(card.block),
    }


def text_fields(card: RawCard) -> dict:
    return {
        "name": card.name,
        "feature": card.feature,
        "effect": card.effect,
        "trigger": card.trigger,
        "source": card.source,
    }


def dedupe(cards: Iterable[RawCard]) -> "OrderedDict[str, RawCard]":
    """One entry per printing id, remembering every series it was listed under."""
    merged: "OrderedDict[str, RawCard]" = OrderedDict()
    for card in cards:
        existing = merged.get(card.id)
        if existing is None:
            merged[card.id] = card
            continue
        for series in card.series:
            if series not in existing.series:
                existing.series.append(series)
    return merged


def english_by_code(en_cards: Iterable[RawCard]) -> Dict[str, RawCard]:
    """Pick one English printing per base code, preferring the base art."""
    chosen: Dict[str, RawCard] = {}
    for card in en_cards:
        current = chosen.get(card.code)
        if current is None or (current.variant is not None and card.variant is None):
            chosen[card.code] = card
    return chosen


def compare(jp: dict, en: dict) -> List[str]:
    return [name for name in CHECKED_FIELDS if jp.get(name) != en.get(name)]


def build_printings(jp_cards: Iterable[RawCard], en_cards: Iterable[RawCard]) -> List[dict]:
    jp_printings = dedupe(jp_cards)
    english = english_by_code(dedupe(en_cards).values())

    # Compare each base code once, using the Japanese base printing when present.
    jp_reference: Dict[str, RawCard] = {}
    for card in jp_printings.values():
        current = jp_reference.get(card.code)
        if current is None or (current.variant is not None and card.variant is None):
            jp_reference[card.code] = card
    mismatches = {
        code: compare(stats(card), stats(english[code]))
        for code, card in jp_reference.items()
        if code in english
    }

    printings = []
    for card in jp_printings.values():
        en_card = english.get(card.code)
        checks = []
        if en_card is None:
            checks.append("no_english")
        else:
            checks.extend(f"en_mismatch:{name}" for name in mismatches.get(card.code, []))
        if stats(card)["category"] not in CATEGORIES:
            checks.append("unknown_category")
        printings.append(
            {
                "id": card.id,
                "code": card.code,
                "kind": "card",
                "variant": card.variant,
                "rarity": RARITIES.get(card.rarity, card.rarity),
                "status": "official",
                "stats": stats(card),
                "jp": text_fields(card),
                "en": None if en_card is None else {**text_fields(en_card), "notes": en_card.notes},
                "series": list(card.series),
                "image": {"source": card.image_url, "quality": "official"} if card.image_url else None,
                "checks": checks,
            }
        )
    return printings
