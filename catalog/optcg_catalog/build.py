"""Command-line entry point: ``python -m optcg_catalog.build``.

Produces ``catalog.json`` (what the app downloads), ``report.json`` (what
needs a human look) and ``images/`` (content-hashed WebP files).
"""

from __future__ import annotations

import argparse
import datetime as dt
import hashlib
import json
import sys
from collections import Counter, OrderedDict
from pathlib import Path
from typing import Dict, List, Optional

from . import SCHEMA_VERSION, announcements, cardlist, dons, imagehash, images, merge
from .fetch import FetchError, Fetcher

HERE = Path(__file__).resolve().parent
DATA_DIR = HERE.parent / "data"
DAY = 24 * 60 * 60


def log(message: str) -> None:
    print(message, file=sys.stderr, flush=True)


def fetch_site(fetcher: Fetcher, site: str, wanted_codes: Optional[set], max_age: float):
    index_url = f"{cardlist.SITES[site]}{cardlist.CARDLIST_PATH}"
    index_html = fetcher.get_text(index_url, max_age=max_age)
    all_series = cardlist.parse_series_options(index_html)
    chosen = [
        series
        for series in all_series
        if wanted_codes is None or series.value in wanted_codes or set(series.codes) & wanted_codes
    ]
    cards: List[cardlist.RawCard] = []
    for number, series in enumerate(chosen, 1):
        url = cardlist.series_url(site, series.value)
        parsed = cardlist.parse_cards(fetcher.get_text(url, max_age=max_age), site, url)
        for card in parsed:
            card.series = [series.value]
        cards.extend(parsed)
        log(f"[{site}] {number}/{len(chosen)} {series.set_code or series.kind}: {len(parsed)} printings")
    return index_html, all_series, chosen, cards


def build_sets(jp_series, en_series) -> List[dict]:
    en_by_code = {}
    for series in en_series:
        for code in series.codes:
            en_by_code.setdefault(code, series)
    en_by_kind = {series.kind: series for series in en_series if not series.set_code}
    sets = []
    for series in jp_series:
        english = en_by_code.get(series.set_code) if series.set_code else en_by_kind.get(series.kind)
        sets.append(
            {
                "id": series.value,
                "code": series.set_code,
                "kind": series.kind,
                "name_jp": series.label,
                "name_en": english.label if english else None,
            }
        )
    return sets


def add_announcements(fetcher: Fetcher, printings: List[dict], registry: dons.DonRegistry,
                      today: str, max_age: float, report: dict) -> None:
    pages: List[str] = []
    for path in announcements.INDEX_PAGES:
        url = f"{cardlist.SITES['jp']}{path}"
        try:
            for page in announcements.discover_pages(fetcher.get_text(url, max_age=max_age), url):
                if page not in pages:
                    pages.append(page)
        except FetchError as error:
            report["errors"].append(str(error))
    log(f"[announcements] {len(pages)} pages")

    by_id = {printing["id"]: printing for printing in printings}
    known_codes = {printing["code"] for printing in printings}
    for page in pages:
        try:
            found = announcements.parse_page(fetcher.get_text(page, max_age=max_age), page)
        except FetchError as error:
            report["errors"].append(str(error))
            continue
        for item in found:
            reference = {"page": item.page_url, "title": item.page_title, "image": item.image_url}
            if item.kind == "card":
                if item.code in known_codes:
                    report["announced_existing_code"].append({"code": item.code, **reference})
                    continue
                known_codes.add(item.code)
                printing = {
                    "id": item.code, "code": item.code, "kind": "card", "variant": None,
                    "rarity": None, "status": "announced", "stats": None,
                    "jp": {"name": item.name, "source": item.page_title},
                    "en": None, "series": [],
                    "image": {"source": item.image_url, "quality": "sample" if item.sample else "announcement"},
                    "checks": ["announced_only"], "announced_in": [reference],
                }
                printings.append(printing)
                by_id[printing["id"]] = printing
                continue

            try:
                picture = fetcher.get(item.image_url, max_age=30 * DAY)
            except FetchError as error:
                report["errors"].append(str(error))
                continue
            entry = registry.assign(
                imagehash.dhash(picture), imagehash.colour_signature(picture),
                hashlib.sha256(picture).hexdigest(), item.name, today,
            )
            existing = by_id.get(entry["id"])
            if existing is not None:
                existing.setdefault("announced_in", []).append(reference)
                continue
            checks = ["announced_only"]
            for other in registry.entries:
                if other["id"] != entry["id"] and imagehash.distance(other["dhash"], entry["dhash"]) <= dons.REVIEW_DISTANCE:
                    checks.append(f"possible_duplicate:{other['id']}")
            printing = {
                "id": entry["id"], "code": "DON", "kind": "don", "variant": None, "rarity": "DON",
                "status": "announced", "stats": None,
                "jp": {"name": item.name, "source": item.page_title},
                "en": {"name": "DON!! Card", "source": None}, "series": [],
                "image": {"source": item.image_url, "quality": "sample" if item.sample else "announcement"},
                "checks": checks, "announced_in": [reference],
            }
            printings.append(printing)
            by_id[printing["id"]] = printing


def attach_images(fetcher: Fetcher, printings: List[dict], index: images.AssetIndex,
                  out_dir: Path, mode: str, limit: Optional[int], report: dict) -> None:
    created = 0
    for printing in printings:
        image = printing.get("image")
        if not image or not image.get("source"):
            continue
        png = printing.get("_png")
        key = f"pdf:{hashlib.sha256(png).hexdigest()}" if png else image["source"]
        entry = index.get(key, out_dir)
        if entry is None and mode == "all" and (limit is None or created < limit):
            try:
                source = png if png else fetcher.get(image["source"], max_age=365 * DAY)
                entry = images.make_variants(source, out_dir)
                index.put(key, entry)
                created += 1
            except (FetchError, OSError) as error:
                report["errors"].append(f"image {printing['id']}: {error}")
        if entry is not None:
            image.update({name: entry[name] for name in ("thumb", "detail", "width", "height")})
        else:
            printing["checks"].append("image_pending")
    log(f"[images] {created} new image sets")


def public(printing: dict) -> dict:
    return {key: value for key, value in printing.items() if not key.startswith("_")}


def sort_key(printing: dict):
    kind_order = 0 if printing["kind"] == "card" else 1
    return (kind_order, printing["code"], printing["variant"] or 0, printing["id"])


def summarise(printings: List[dict], report: dict) -> None:
    checks: Dict[str, List[str]] = OrderedDict()
    for printing in printings:
        for check in printing["checks"]:
            checks.setdefault(check, []).append(printing["id"])
    report["counts"] = dict(Counter(f"{p['kind']}:{p['status']}" for p in printings))
    report["checks"] = {name: {"count": len(ids), "ids": ids[:50]} for name, ids in checks.items()}


def main(argv: Optional[List[str]] = None) -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--out", type=Path, default=Path("out"))
    parser.add_argument("--cache", type=Path, default=Path(".cache"))
    parser.add_argument("--data", type=Path, default=DATA_DIR, help="registry and pull profile folder")
    parser.add_argument("--series", help="comma-separated set codes or series ids, e.g. OP-01,550901")
    parser.add_argument("--images", choices=["none", "all"], default="all")
    parser.add_argument("--image-limit", type=int, help="create at most this many new image sets")
    parser.add_argument("--no-dons", action="store_true")
    parser.add_argument("--no-announcements", action="store_true")
    parser.add_argument("--delay", type=float, default=1.0, help="seconds between requests to one host")
    parser.add_argument("--max-age", type=float, default=6 * 60 * 60,
                        help="reuse cached pages younger than this many seconds")
    parser.add_argument("--offline", action="store_true", help="build from the cache only")
    args = parser.parse_args(argv)

    fetcher = Fetcher(args.cache, delay=args.delay, offline=args.offline)
    wanted = {part.strip() for part in args.series.split(",")} if args.series else None
    today = dt.date.today().isoformat()
    report: dict = {"errors": [], "announced_existing_code": []}

    jp_index, jp_all, _, jp_cards = fetch_site(fetcher, "jp", wanted, args.max_age)
    en_wanted = None
    if wanted is not None:
        jp_codes = {s.set_code for s in jp_all if s.set_code in wanted or s.value in wanted}
        jp_kinds = {s.kind for s in jp_all if s.value in wanted and not s.set_code}
        en_index_html = fetcher.get_text(f"{cardlist.SITES['en']}{cardlist.CARDLIST_PATH}", max_age=args.max_age)
        en_wanted = {s.value for s in cardlist.parse_series_options(en_index_html)
                     if set(s.codes) & jp_codes or (not s.set_code and s.kind in jp_kinds)}
    _, en_all, _, en_cards = fetch_site(fetcher, "en", en_wanted, args.max_age)

    printings = merge.build_printings(jp_cards, en_cards)
    registry = dons.DonRegistry(args.data / "don_registry.json")

    if not args.no_dons:
        pdf_url = dons.find_pdf_url(jp_index)
        if pdf_url is None:
            report["errors"].append("don card list PDF link not found on the card list page")
        else:
            pdf_dons = dons.parse_pdf(fetcher.get(pdf_url, max_age=args.max_age))
            printings.extend(dons.build_don_printings(pdf_dons, registry, pdf_url, today))
            report["don_pdf"] = {"url": pdf_url, "dons": len(pdf_dons)}
            log(f"[dons] {len(pdf_dons)} from the PDF")

    if not args.no_announcements:
        add_announcements(fetcher, printings, registry, today, args.max_age, report)

    out_dir = args.out
    out_dir.mkdir(parents=True, exist_ok=True)
    asset_index = images.AssetIndex(out_dir / "asset-index.json")
    attach_images(fetcher, printings, asset_index, out_dir / "images", args.images, args.image_limit, report)
    asset_index.save()
    registry.save()

    printings.sort(key=sort_key)
    pull_profiles = json.loads((args.data / "pull_profiles.json").read_text("utf-8"))
    body = {
        "schema_version": SCHEMA_VERSION,
        "sets": build_sets(jp_all, en_all),
        "printings": [public(printing) for printing in printings],
        "pull_profiles": pull_profiles,
    }
    version = hashlib.sha256(json.dumps(body, sort_keys=True, ensure_ascii=False).encode("utf-8")).hexdigest()[:16]
    catalog = {"version": version, "generated_at": dt.datetime.now(dt.timezone.utc).isoformat(timespec="seconds"), **body}
    (out_dir / "catalog.json").write_text(json.dumps(catalog, ensure_ascii=False, separators=(",", ":")), "utf-8")

    summarise(printings, report)
    report["fetch"] = fetcher.stats
    report["version"] = version
    (out_dir / "report.json").write_text(json.dumps(report, ensure_ascii=False, indent=1), "utf-8")
    log(f"[done] version {version}: {report['counts']}; checks: "
        + ", ".join(f"{name}={value['count']}" for name, value in report["checks"].items()))
    return 0


if __name__ == "__main__":
    sys.exit(main())
