package com.optcg.app.data.repository;

import com.optcg.app.data.local.UserDataStore;

/**
 * Room-backed {@link CollectionRepository} (via {@link UserDataStore}'s in-memory mirror).
 * The per-operation semantics match the original SharedPreferences edits exactly; only the
 * durable store changed (prefs -> Room), enabling future cloud sync.
 */
public class CollectionRepositoryImpl implements CollectionRepository {

    private final UserDataStore store;

    public CollectionRepositoryImpl(UserDataStore store) {
        this.store = store;
    }

    private static String totalCountKey(String setPrefix) {
        return setPrefix + "_total_count";
    }

    private static String totalKey(String setPrefix, String raritySuffix) {
        return setPrefix + "_total_" + raritySuffix;
    }

    private static String packsKey(String setPrefix) {
        return setPrefix + "_packs_opened";
    }

    @Override
    public boolean isCollected(String cardId) {
        return store.isCardCollected(cardId);
    }

    @Override
    public int getCount(String cardId) {
        return store.getCardCount(cardId);
    }

    @Override
    public int getTotalCount(String setPrefix) {
        return store.getCounter(totalCountKey(setPrefix));
    }

    @Override
    public int getTotal(String setPrefix, String raritySuffix) {
        return store.getCounter(totalKey(setPrefix, raritySuffix));
    }

    @Override
    public int getPacksOpened(String setPrefix) {
        return store.getCounter(packsKey(setPrefix));
    }

    @Override
    public int incrementPacksOpened(String setPrefix) {
        return store.incrementCounter(packsKey(setPrefix));
    }

    @Override
    public void recordRevealedCard(String cardId, String setPrefix, String raritySuffix) {
        boolean wasCollected = store.isCardCollected(cardId);
        store.setCard(cardId, true, store.getCardCount(cardId) + 1);
        if (!wasCollected) {
            store.incrementCounter(totalCountKey(setPrefix));
            if (raritySuffix != null) {
                store.incrementCounter(totalKey(setPrefix, raritySuffix));
            }
        }
    }

    @Override
    public void recordNewCommon(String cardId, String setPrefix, String raritySuffix) {
        store.setCard(cardId, true, store.getCardCount(cardId) + 1);
        store.incrementCounter(totalCountKey(setPrefix));
        if (raritySuffix != null) {
            store.incrementCounter(totalKey(setPrefix, raritySuffix));
        }
    }

    @Override
    public void incrementCount(String cardId) {
        store.incrementCardCount(cardId);
    }
}
