/*
 * Uploads card PNGs to Firebase Storage at each card's `img` path (e.g. cards/op01/op01_041.png),
 * which is exactly the object key the app's StorageImageUrlResolver expects. Access is governed by
 * your Storage rules (make cards/** publicly readable).
 *
 * Usage:
 *   1. npm install
 *   2. scripts/serviceAccountKey.json present (see upload-card-catalog.js)
 *   3. IMAGES_DIR=/path/to/original/pngs npm run upload:images
 *
 * IMAGES_DIR is searched recursively for files named "<cardId>.png" (basename of each img path).
 * Point it at your highest-resolution source images. If omitted, it falls back to the bundled
 * app/src/main/res (lower-res density copies).
 */
const admin = require('firebase-admin');
const fs = require('fs');
const path = require('path');

const serviceAccount = require('./serviceAccountKey.json');
const BUCKET = process.env.STORAGE_BUCKET || 'piecelab-157cb.firebasestorage.app';
admin.initializeApp({ credential: admin.credential.cert(serviceAccount), storageBucket: BUCKET });
const bucket = admin.storage().bucket();

const CARDS_JSON = path.join(__dirname, '..', 'app', 'src', 'main', 'assets', 'cards.json');
const IMAGES_DIR = process.env.IMAGES_DIR || path.join(__dirname, '..', 'app', 'src', 'main', 'res');
const CONCURRENCY = 16;

function indexPng(dir, map) {
  for (const entry of fs.readdirSync(dir, { withFileTypes: true })) {
    const p = path.join(dir, entry.name);
    if (entry.isDirectory()) indexPng(p, map);
    else if (entry.isFile() && entry.name.toLowerCase().endsWith('.png') && !(entry.name in map)) {
      map[entry.name] = p;
    }
  }
  return map;
}

async function main() {
  const cards = JSON.parse(fs.readFileSync(CARDS_JSON, 'utf8'));
  console.log('Indexing local PNGs under', IMAGES_DIR, '...');
  const index = indexPng(IMAGES_DIR, {});

  const tasks = [];
  let missing = 0;
  for (const c of cards) {
    if (!c.img) continue;
    const src = index[path.basename(c.img)];
    if (!src) { missing++; continue; }
    tasks.push({ src, dest: c.img });
  }
  console.log(`To upload: ${tasks.length}; cards with no local PNG: ${missing}`);

  let done = 0;
  async function worker(slice) {
    for (const t of slice) {
      await bucket.upload(t.src, {
        destination: t.dest,
        contentType: 'image/png',
        metadata: { cacheControl: 'public,max-age=31536000' },
      });
      done++;
      if (done % 200 === 0) console.log(`  uploaded ${done}/${tasks.length}`);
    }
  }
  const lanes = Array.from({ length: CONCURRENCY }, () => []);
  tasks.forEach((t, i) => lanes[i % CONCURRENCY].push(t));
  await Promise.all(lanes.map(worker));

  console.log(`Done. Uploaded ${done} images; ${missing} cards had no local PNG.`);
}

main().catch((e) => { console.error(e); process.exit(1); });
