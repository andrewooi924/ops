
<h1 align="center">
  <br>
  <a href="https://asia-en.onepiece-cardgame.com/"><img src="https://github.com/user-attachments/assets/0e70f7cd-dca1-4f3b-a54f-2ee1ffe2f9f9" alt="OPTCG APP" width="200"></a>
  <br>
  OPTCG APP
  <br>
</h1>

<h4 align="center">A One Piece Card Game companion for Japanese cards: every printing with English text, a box simulator, and a collection tracker that works offline.</h4>

## Key Features

* **Every Japanese card**, including parallels, promos and DON!! cards, with English names and effects where an English printing exists.
* **New cards automatically.** The card catalog is rebuilt daily from Bandai's official sites, including cards and DON!! cards only shown on event and product pages so far. The app checks for a new catalog once a day.
* **Box and pack simulator** based on how Japanese boxes are packed (leaders, rares and parallels per box), using community estimates because Bandai doesn't publish pull rates.
* **Collection and wishlist** stored on your phone. Images of cards you own or wishlist are kept for offline use, and any set can be saved offline.
* **Small download.** The app ships without card images (about 2 MB) and loads them as needed.

## How it fits together

| Part | What it does |
|---|---|
| [`catalog/`](catalog/) | Python builder that produces `catalog.json` from Bandai's Japanese and English card lists, the DON!! card PDF and announcement pages. See its [README](catalog/README.md). |
| `.github/workflows/catalog.yml` | Runs the builder daily and publishes the catalog to the `catalog` release, where the app downloads it. |
| [`app/`](app/) | Android app (Kotlin, Jetpack Compose, Room, WorkManager, Coil). Ships with a catalog snapshot so it works offline from the first launch. |

## Building the app

Needs JDK 17 and the Android SDK (platform 35).

```bash
./gradlew testDebugUnitTest assembleDebug
```

To refresh the catalog snapshot bundled in the app, build the catalog and copy it in:

```bash
cd catalog && python -m optcg_catalog.build --out out && gzip -9 -n -c out/catalog.json > ../app/src/main/assets/catalog.json.gzip
```

A debug build can be pointed at a locally served catalog to test updates on an emulator:

```bash
./gradlew installDebug -PcatalogBase=http://10.0.2.2:8765
```

## Download

Version 1.x is still available [here](https://drive.google.com/file/d/1sGAWsGgbHtF4V2CE-S4-nkiMBlTi7uqV/view?usp=drive_link). To install, allow installs from unknown sources on your Android device.

## Credits

- Card data and images: the official ONE PIECE CARD GAME sites in [Japanese](https://www.onepiece-cardgame.com/cardlist/) and [English](https://en.onepiece-cardgame.com/cardlist/). Not affiliated with Bandai.
- Pull-rate estimates: community box-opening reports, listed in [`catalog/data/pull_profiles.json`](catalog/data/pull_profiles.json).
- App icon made with [GIMP](https://www.gimp.org/).

## License

MIT

---

> GitHub [@andrewooi924](https://github.com/andrewooi924) &nbsp;&middot;&nbsp;
> LinkedIn [Ooi Yu Zhang](https://www.linkedin.com/in/ooi-yu-zhang-232684287/)

