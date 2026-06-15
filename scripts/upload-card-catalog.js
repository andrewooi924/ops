/*
 * Uploads the bundled cards.json into Firestore so the app can pull card updates
 * over-the-air. Writes one document per card to the `cards` collection (doc id = card id)
 * and sets `catalog/meta.version`.
 *
 * Usage:
 *   1. npm install            (in this scripts/ folder)
 *   2. Put your Firebase service-account key at scripts/serviceAccountKey.json
 *      (Firebase Console -> Project settings -> Service accounts -> Generate new private key)
 *   3. CATALOG_VERSION=1 npm run upload:catalog
 *      (bump CATALOG_VERSION each time you change cards to trigger an in-app refresh)
 */
const admin = require('firebase-admin');
const fs = require('fs');
const path = require('path');

const serviceAccount = require('./serviceAccountKey.json');
admin.initializeApp({ credential: admin.credential.cert(serviceAccount) });
const db = admin.firestore();

const CARDS_JSON = path.join(__dirname, '..', 'app', 'src', 'main', 'assets', 'cards.json');
const VERSION = Number(process.env.CATALOG_VERSION || 1);
// Fields the Android app reads (see FirestoreCardDataSource); id is the document id.
const FIELDS = ['number', 'rarity', 'role', 'name', 'life', 'cost', 'attribute',
  'power', 'counter', 'color', 'type', 'effect', 'set', 'img'];

async function main() {
  const cards = JSON.parse(fs.readFileSync(CARDS_JSON, 'utf8'));
  console.log(`Uploading ${cards.length} cards to Firestore (catalog version ${VERSION})...`);

  let batch = db.batch();
  let inBatch = 0;
  let total = 0;
  for (const c of cards) {
    if (!c.id) continue;
    const data = {};
    for (const f of FIELDS) if (c[f] !== undefined && c[f] !== null) data[f] = c[f];
    batch.set(db.collection('cards').doc(c.id), data, { merge: true });
    inBatch++;
    total++;
    if (inBatch >= 400) {
      await batch.commit();
      batch = db.batch();
      inBatch = 0;
      process.stdout.write('.');
    }
  }
  if (inBatch > 0) await batch.commit();

  await db.collection('catalog').doc('meta').set({ version: VERSION }, { merge: true });
  console.log(`\nDone. ${total} cards uploaded; catalog/meta.version = ${VERSION}`);
}

main().catch((e) => { console.error(e); process.exit(1); });
