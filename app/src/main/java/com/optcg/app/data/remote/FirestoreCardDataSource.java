package com.optcg.app.data.remote;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.optcg.app.Card;

import java.util.ArrayList;
import java.util.List;

/**
 * Firestore-backed card catalog. Expects:
 * <ul>
 *   <li>a {@code catalog/meta} document with an integer {@code version} field, and</li>
 *   <li>a {@code cards} collection with one document per card (document id = card id),
 *       carrying the card fields (number, rarity, role, name, life, cost, attribute, power,
 *       counter, color, type, effect, set, img).</li>
 * </ul>
 * Reads block on Firestore tasks, so call off the main thread (the repository does).
 */
public class FirestoreCardDataSource implements RemoteCardDataSource {

    private final FirebaseFirestore firestore;

    public FirestoreCardDataSource(FirebaseFirestore firestore) {
        this.firestore = firestore;
    }

    @Override
    public long fetchCatalogVersion() throws Exception {
        DocumentSnapshot meta = Tasks.await(firestore.collection("catalog").document("meta").get());
        Long version = meta.getLong("version");
        return version != null ? version : 0L;
    }

    @Override
    public List<Card> fetchAllCards() throws Exception {
        List<Card> cards = new ArrayList<>();
        for (QueryDocumentSnapshot d : Tasks.await(firestore.collection("cards").get())) {
            cards.add(new Card(
                    d.getId(),
                    str(d, "number"), str(d, "rarity"), str(d, "role"), str(d, "name"),
                    str(d, "life"), str(d, "cost"), str(d, "attribute"), str(d, "power"),
                    str(d, "counter"), str(d, "color"), str(d, "type"), str(d, "effect"),
                    str(d, "set"), str(d, "img")));
        }
        return cards;
    }

    private static String str(DocumentSnapshot d, String field) {
        Object v = d.get(field);
        return v != null ? v.toString() : null;
    }
}
