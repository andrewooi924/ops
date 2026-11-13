import io
import tempfile
import unittest
from pathlib import Path

from PIL import Image, ImageDraw

from optcg_catalog import announcements, cardlist, dons, imagehash, images, merge, product_art
from optcg_catalog.fetch import to_uri

FIXTURES = Path(__file__).parent / "fixtures"


def fixture(name):
    return (FIXTURES / name).read_text("utf-8")


def picture(seed, size=(300, 420), colour=(30, 60, 90)):
    """A deterministic test image with distinct shapes per seed."""
    image = Image.new("RGB", size, colour)
    draw = ImageDraw.Draw(image)
    for step in range(6):
        x = (seed * 37 + step * 53) % size[0]
        y = (seed * 91 + step * 29) % size[1]
        draw.rectangle([x, y, x + 60, y + 80], fill=((seed * 50 + step * 40) % 255, 200, step * 40))
    return image


def png(image):
    buffer = io.BytesIO()
    image.save(buffer, "PNG")
    return buffer.getvalue()


class CardListTests(unittest.TestCase):
    def test_series_options(self):
        series = cardlist.parse_series_options(fixture("jp_cardlist.html"))
        by_value = {s.value: s for s in series}
        self.assertEqual(by_value["550101"].set_code, "OP-01")
        self.assertEqual(by_value["550101"].kind, "booster")
        self.assertEqual(by_value["550901"].kind, "promo")
        self.assertEqual(by_value["550801"].kind, "limited")
        self.assertNotIn("<br", by_value["550101"].label)

    def test_english_series_codes_without_hyphen(self):
        series = cardlist.parse_series_options(fixture("en_cardlist.html"))
        self.assertIn("OP-01", {s.set_code for s in series})

    def test_combined_set_codes(self):
        self.assertEqual(cardlist.set_codes("BOOSTER PACK -THE AZURE SEA’S SEVEN- [OP14-EB04]"), ["OP-14", "EB-04"])
        self.assertEqual(cardlist.set_codes("ブースターパック 蒼海の七傑【OP14】"), ["OP-14"])
        self.assertEqual(cardlist.set_codes("プロモーションカード"), [])

    def test_combined_english_series_names_only_its_own_set(self):
        from optcg_catalog.build import build_sets

        jp = [cardlist.Series("550115", "神の島の冒険【OP-15】", "OP-15", "booster"),
              cardlist.Series("550204", "EGGHEAD CRISIS【EB-04】", "EB-04", "extra")]
        en = [cardlist.Series("569115", "ADVENTURE ON KAMI’S ISLAND- [OP15-EB04]", "OP-15", "booster", ["EB-04"])]
        names = {s["code"]: s["name_en"] for s in build_sets(jp, en)}
        self.assertEqual(names, {"OP-15": "ADVENTURE ON KAMI’S ISLAND- [OP15-EB04]", "EB-04": None})

    def test_parse_japanese_cards(self):
        cards = {c.id: c for c in cardlist.parse_cards(fixture("jp_cardlist.html"), "jp")}
        zoro = cards["OP01-001"]
        self.assertEqual((zoro.code, zoro.variant, zoro.rarity, zoro.category), ("OP01-001", None, "L", "LEADER"))
        self.assertEqual((zoro.life, zoro.cost, zoro.power, zoro.counter), ("5", None, "5000", None))
        self.assertEqual(zoro.attributes, ["斬"])
        self.assertEqual(zoro.colors, ["赤"])
        self.assertTrue(zoro.image_url.startswith("https://www.onepiece-cardgame.com/images/cardlist/card/OP01-001.png"))
        self.assertEqual(cards["OP01-001_p2"].variant, 2)
        self.assertIn("トリガー", cards["OP01-009"].trigger)
        self.assertNotIn("カードリスト", cards["OP01-001"].source)

    def test_parse_english_cards(self):
        cards = {c.id: c for c in cardlist.parse_cards(fixture("en_cardlist.html"), "en")}
        self.assertEqual(cards["OP01-001"].name, "Roronoa Zoro")
        self.assertEqual(cards["OP01-026"].notes, "Errata Card")


class MergeTests(unittest.TestCase):
    def setUp(self):
        self.jp = cardlist.parse_cards(fixture("jp_cardlist.html"), "jp")
        self.en = cardlist.parse_cards(fixture("en_cardlist.html"), "en")

    def test_english_joins_on_base_code_not_variant(self):
        printings = {p["id"]: p for p in merge.build_printings(self.jp, self.en)}
        # JP _p2 has no EN _p2; it still gets the EN text for OP01-001.
        self.assertEqual(printings["OP01-001_p2"]["en"]["name"], "Roronoa Zoro")
        self.assertEqual(printings["OP01-001_p2"]["jp"]["name"], "ロロノア・ゾロ")
        self.assertEqual(printings["OP01-001"]["stats"]["colors"], ["red"])
        self.assertEqual(printings["OP01-001"]["checks"], [])

    def test_mismatch_is_flagged(self):
        for card in self.en:
            if card.code == "OP01-009":
                card.power = "9000"
        printings = {p["id"]: p for p in merge.build_printings(self.jp, self.en)}
        self.assertIn("en_mismatch:power", printings["OP01-009"]["checks"])

    def test_missing_english(self):
        english = [c for c in self.en if c.code != "OP01-026"]
        printings = {p["id"]: p for p in merge.build_printings(self.jp, english)}
        self.assertIsNone(printings["OP01-026"]["en"])
        self.assertIn("no_english", printings["OP01-026"]["checks"])

    def test_category_typo(self):
        self.assertEqual(merge.category("キャラクタークター"), "CHARACTER")
        self.assertEqual(merge.category("LEADER"), "LEADER")


class AnnouncementTests(unittest.TestCase):
    def test_event_page(self):
        found = announcements.parse_page(fixture("event_meetup.html"), "https://www.onepiece-cardgame.com/events/meetup-202611.html")
        cards = [a for a in found if a.kind == "card"]
        don_items = [a for a in found if a.kind == "don"]
        self.assertIn(("P-160", "ネフェルタリ・ビビ"), {(a.code, a.name) for a in cards})
        self.assertEqual({a.name for a in don_items}, {"世界統一記念ドン‼︎カード-ナミ-", "世界統一記念ドン‼︎カード-ウソップ-"})
        self.assertTrue(all(a.sample for a in don_items))
        self.assertTrue(all(a.image_url.startswith("https://www.onepiece-cardgame.com/") for a in found))

    def test_product_page_uses_file_code(self):
        found = announcements.parse_page(fixture("product_page.html"), "https://www.onepiece-cardgame.com/products/x.html")
        self.assertEqual([(a.code, a.name) for a in found], [("EB03-052", "")])

    def test_discover_pages_stays_on_site(self):
        pages = announcements.discover_pages(fixture("product_page.html"), "https://www.onepiece-cardgame.com/products/")
        self.assertEqual(pages, [
            "https://www.onepiece-cardgame.com/events/meetup-202611.html",
            "https://www.onepiece-cardgame.com/products/op18.html",
        ])


class DonTests(unittest.TestCase):
    def test_pdf_link(self):
        url = dons.find_pdf_url(fixture("jp_cardlist.html"))
        self.assertTrue(url.endswith("don-cardlist.pdf"))

    def test_parse_pdf_pairs_images_with_labels(self):
        import fitz

        document = fitz.open()
        page = document.new_page(width=595, height=842)
        for column, (seed, label) in enumerate([(1, "Booster A"), (2, "Event B")]):
            x = 60 + column * 170
            page.insert_image(fitz.Rect(x, 150, x + 135, 340), stream=png(picture(seed)))
            page.insert_text((x, 352), label, fontsize=8)
        parsed = dons.parse_pdf(document.tobytes())
        self.assertEqual([d.label for d in parsed], ["Booster A", "Event B"])
        self.assertEqual(len({d.sha256 for d in parsed}), 2)

    def test_registry_keeps_ids_and_matches_watermarked_copy(self):
        with tempfile.TemporaryDirectory() as folder:
            path = Path(folder) / "registry.json"
            registry = dons.DonRegistry(path)
            original = picture(3)
            data = png(original)
            first = registry.assign(imagehash.dhash(data), imagehash.colour_signature(data), "sha-a", "Set X", "2026-11-01")
            other = png(picture(9, colour=(200, 40, 40)))
            second = registry.assign(imagehash.dhash(other), imagehash.colour_signature(other), "sha-b", "Set Y", "2026-11-01")
            registry.save()
            self.assertEqual((first["id"], second["id"]), ("DON-0001", "DON-0002"))

            reloaded = dons.DonRegistry(path)
            marked = original.copy()
            ImageDraw.Draw(marked).text((40, 200), "SAMPLE", fill=(255, 255, 255))
            marked_bytes = png(marked.resize((434, 607)))
            again = reloaded.assign(imagehash.dhash(marked_bytes), imagehash.colour_signature(marked_bytes),
                                    "sha-c", "Set X", "2026-11-05")
            self.assertEqual(again["id"], "DON-0001")
            self.assertEqual(again["first_seen"], "2026-11-01")

    def test_near_identical_dons_are_told_apart_by_label(self):
        registry = dons.DonRegistry(Path(tempfile.mkdtemp()) / "r.json")
        data = png(picture(5))
        hash_, colour = imagehash.dhash(data), imagehash.colour_signature(data)
        normal = registry.assign(hash_, colour, "sha-1", "Normal", "d", position=(1, 0))
        foil = registry.assign(hash_, colour, "sha-2", "Foil", "d", exclude={normal["id"]}, position=(1, 1))
        self.assertNotEqual(normal["id"], foil["id"])
        # Bytes changed for both (a re-exported PDF): labels keep the ids straight.
        again_foil = registry.assign(hash_, colour, "sha-4", "Foil", "d", position=(1, 1))
        self.assertEqual(again_foil["id"], foil["id"])


class ImageTests(unittest.TestCase):
    def test_variants_are_hashed_and_not_upscaled(self):
        with tempfile.TemporaryDirectory() as folder:
            out = Path(folder)
            result = images.make_variants(png(picture(1, size=(600, 838))), out)
            thumb = Image.open(out / result["thumb"])
            detail = Image.open(out / result["detail"])
            self.assertEqual(thumb.width, 240)
            self.assertEqual(detail.width, 600)  # source is narrower than 720
            self.assertRegex(result["thumb"], r"^[0-9a-f]{20}\.webp$")
            self.assertNotEqual(result["thumb"], result["detail"])


    def test_published_index_does_not_need_local_files(self):
        with tempfile.TemporaryDirectory() as folder:
            path = Path(folder) / "asset-index.json"
            index = images.AssetIndex(path)
            index.put("https://example/a.png", {"thumb": "t.webp", "detail": "d.webp", "width": 1, "height": 1})
            index.save()
            empty = Path(folder) / "images"
            self.assertIsNone(images.AssetIndex(path).get("https://example/a.png", empty))
            self.assertEqual(images.AssetIndex(path, published=True).get("https://example/a.png", empty)["thumb"], "t.webp")


class ProductArtTests(unittest.TestCase):
    @staticmethod
    def product(size, product_box, background=(0, 0, 0, 0)):
        image = Image.new("RGBA", size, background)
        ImageDraw.Draw(image).rectangle(product_box, fill=(200, 30, 30, 255))
        buffer = io.BytesIO()
        image.save(buffer, "PNG")
        return buffer.getvalue()

    def test_box_fills_the_height_and_is_centred(self):
        # A 354x587 box in a 670x670 transparent photo, like Bandai's.
        art = Image.open(io.BytesIO(product_art.make_art(self.product((670, 670), (158, 43, 511, 629)), "box")))
        self.assertEqual(art.height, 587)
        self.assertAlmostEqual(art.width / art.height, 325 / 500, places=2)
        left, top, right, bottom = art.getchannel("A").getbbox()
        self.assertEqual((top, bottom), (0, 587))
        self.assertLessEqual(abs(left - (art.width - right)), 1)

    def test_plain_background_is_removed(self):
        photo = self.product((400, 400), (150, 50, 249, 349), background=(255, 255, 255, 255))
        art = Image.open(io.BytesIO(product_art.make_art(photo, "pack")))
        self.assertEqual(art.height, 300)
        self.assertEqual(art.getpixel((0, 0))[3], 0)

    def test_solid_background_behind_transparency_is_removed(self):
        # Transparent only at the very edge, with an opaque white background inside it.
        photo = Image.new("RGBA", (400, 400), (255, 255, 255, 0))
        ImageDraw.Draw(photo).rectangle((2, 2, 397, 397), fill=(255, 255, 255, 255))
        ImageDraw.Draw(photo).rectangle((150, 50, 249, 349), fill=(200, 30, 30, 255))
        buffer = io.BytesIO()
        photo.save(buffer, "PNG")
        art = Image.open(io.BytesIO(product_art.make_art(buffer.getvalue(), "box")))
        self.assertEqual(art.height, 300)

    def test_photo_urls_from_product_page(self):
        class Pages:
            def get_text(self, url, max_age=None):
                return '<img src="/onepiececg/x/a1/img_item01.webp"><img src="/onepiececg/x/b2/img_item02.webp">'

            def get(self, url, max_age=None):
                raise AssertionError("no fallback needed")

        found = product_art.find_photos(Pages(), "OP-18", 0)
        self.assertEqual(found["pack"], "https://www.onepiece-cardgame.com/onepiececg/x/a1/img_item01.webp")
        self.assertEqual(found["box"], "https://www.onepiece-cardgame.com/onepiececg/x/b2/img_item02.webp")

    def test_art_entries_need_their_file_unless_published(self):
        with tempfile.TemporaryDirectory() as folder:
            index = images.AssetIndex(Path(folder) / "asset-index.json")
            index.put("art:box:u", {"art": "a.webp"})
            self.assertIsNone(index.get("art:box:u", Path(folder)))
            (Path(folder) / "a.webp").write_bytes(b"x")
            self.assertEqual(index.get("art:box:u", Path(folder))["art"], "a.webp")


class FetchTests(unittest.TestCase):
    def test_non_ascii_paths_are_encoded(self):
        uri = to_uri("https://www.onepiece-cardgame.com/a/mv_交流会_11月.webp?v=1")
        self.assertTrue(uri.isascii())
        self.assertTrue(uri.endswith(".webp?v=1"))


if __name__ == "__main__":
    unittest.main()
