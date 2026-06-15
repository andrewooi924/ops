# PieceLab admin scripts

One-off Node scripts to populate the cloud backend that the app reads from:

- **`upload-card-catalog.js`** — pushes `app/src/main/assets/cards.json` into Firestore (`cards/{id}` docs + `catalog/meta.version`) so the app can pull card updates over-the-air.
- **`upload-card-images.js`** — uploads card PNGs to Firebase Storage at each card's `img` path (`cards/<set>/<id>.png`), matching what the app requests.

## Setup
1. `cd scripts && npm install`
2. Firebase Console → **Project settings → Service accounts → Generate new private key**, save it as `scripts/serviceAccountKey.json` (git-ignored — never commit it).

## Run
```bash
# Card catalog (bump the version to trigger an in-app refresh on next launch)
CATALOG_VERSION=1 npm run upload:catalog

# Card images — point IMAGES_DIR at your highest-res source PNGs
IMAGES_DIR=/path/to/original/card/pngs npm run upload:images
```

## Storage rules (required for public image reads)
Console → Storage → Rules:
```
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    match /cards/{allPaths=**} { allow read: if true; allow write: if false; }
  }
}
```

## Notes
- The app builds image URLs as `https://firebasestorage.googleapis.com/v0/b/<bucket>/o/<url-encoded img path>?alt=media`; with the public-read rule above, no per-object token is needed.
- `upload-card-images.js` matches local files by basename (`<id>.png`) found recursively under `IMAGES_DIR`. The bundled `res/` copies are density-reduced — prefer your original full-res images.
- Re-running is safe: catalog writes use `merge`, image uploads overwrite by path.
