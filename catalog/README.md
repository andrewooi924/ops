# Catalog builder

Builds the card catalog the app downloads: every Japanese printing (artwork,
variants, products), English names and text, all DON!! cards, and cards
announced on official pages before they reach the card list.

```bash
python3 -m venv .venv && .venv/bin/pip install -r requirements.txt
.venv/bin/python -m optcg_catalog.build --out out --cache .cache
.venv/bin/python -m unittest discover -s tests
```

A full run fetches about 125 card list pages, the don PDF and about 40 event
and product pages, one request per second per host (about 3 minutes). With
`--images all` it also downloads each source image once (about 5,000 on the
first run, then only new or changed ones).

Useful options:

- `--series OP-01,550901`: only these sets (set codes or series ids).
- `--images none`: skip images.
- `--image-limit 50`: create at most 50 new image sets this run.
- `--published-index`: trust `asset-index.json` for images already uploaded to the image host.
- `--image-base URL`: public URL of the uploaded images, written to the catalog (default `$IMAGE_BASE_URL`).
- `--offline`: rebuild from the cache without any requests.

## Sources

| Source | Gives | Notes |
|---|---|---|
| `onepiece-cardgame.com/cardlist/` (all series) | Every Japanese printing: id, rarity, stats, Japanese text, product, image | Defines what a printing is. |
| `en.onepiece-cardgame.com/cardlist/` | English name, type, effect, trigger | Joined on the base code only, because variant suffixes are numbered separately on each site. |
| Don card list PDF (linked from the card list) | Every listed DON!! card and its product | The link changes on each update, so it is found again every run. |
| `/events/` and `/products/` pages | Cards and dons announced before the card list includes them | Often watermarked "SAMPLE"; marked `status: announced`. |

## Outputs

- `out/catalog.json`: sets, printings, pull profiles and a content `version`.
- `out/report.json`: what needs a person to look at (see below).
- `out/images/<hash>.webp`: a `thumb` (240 px wide) and a `detail` (up to 720 px) per image.
  Names are content hashes, so they can be cached forever.
- `out/asset-index.json`: which source produced which images, so later runs skip them.
- `data/don_registry.json` (committed): permanent don ids. A don keeps its id when the PDF
  is re-exported or when a watermarked announcement is later replaced by the clean image.

## Checks in `report.json`

| Check | Meaning |
|---|---|
| `no_english` | No English printing with this base code yet (mostly Japan-only promos). Japanese text is used. |
| `en_mismatch:<field>` | The two sites disagree on a language-neutral field, e.g. OP06-004's attribute. |
| `announced_only` | Seen on an event or product page but not yet in the card list or don PDF. |
| `possible_duplicate:<id>` | An announced don looks similar to an existing one; confirm whether it is the same card. |
| `image_pending` | No image has been generated yet. |

`announced_existing_code` lists announcement images for codes already in the card list. Some
are new parallel artwork that the card list hasn't added yet.

## Pull rates

`data/pull_profiles.json` holds the simulator's pull settings. Bandai doesn't publish pull rates;
these are community estimates from Japanese box openings, recorded with their sources. `defaults`
picks a profile per set kind; to override one set, add its set id or code under `sets` with a
profile name, or `null` for no simulator.

## Image hosting (Cloudflare R2)

When the repo has these Actions settings, the daily run makes images and uploads them to R2:

| Name | Kind | Value |
|---|---|---|
| `R2_ACCESS_KEY_ID`, `R2_SECRET_ACCESS_KEY` | secrets | An R2 API token with Object Read & Write on the bucket |
| `R2_ENDPOINT` | variable | `https://<account-id>.r2.cloudflarestorage.com` |
| `R2_BUCKET` | variable | The bucket name |
| `IMAGE_BASE_URL` | variable | The bucket's public URL followed by `/img`, e.g. `https://pub-….r2.dev/img` |

Each run downloads `asset-index.json` from the bucket, makes only images it doesn't list
(`--published-index`, at most 2,500 a run, so the first backlog takes two runs), uploads them to
`img/`, then uploads the updated index. Only after that is the catalog published, with
`image_base` set, so the app never sees an image name that isn't uploaded yet. Without these
settings the run skips images and the app uses Bandai's originals.

## Publishing

On the default branch the workflow uploads `catalog.json.gz` and `version.txt` to the `catalog`
GitHub release. The app checks `version.txt` once a day and downloads the catalog when it changes.
