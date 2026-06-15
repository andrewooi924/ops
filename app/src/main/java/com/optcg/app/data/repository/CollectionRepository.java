package com.optcg.app.data.repository;

/**
 * Collection / pack-opening state: which cards are owned, how many copies, per-set
 * completion totals and packs opened. Backed today by the COLLECTION_PREFS SharedPreferences
 * file (identical keys, no migration); the interface is shaped so the store can move to
 * Room / cloud later. Reads are synchronous (SharedPreferences is in-memory), matching the
 * original direct access.
 */
public interface CollectionRepository {

    boolean isCollected(String cardId);

    int getCount(String cardId);

    int getTotalCount(String setPrefix);

    int getTotal(String setPrefix, String raritySuffix);

    int getPacksOpened(String setPrefix);

    /** Increments and returns the set's new packs-opened count. */
    int incrementPacksOpened(String setPrefix);

    /**
     * The reveal-tap path for a feature / guaranteed card: always bumps the card count and
     * marks it collected; if it was not already collected, also bumps the set total and the
     * per-rarity bucket (when {@code raritySuffix} is non-null).
     */
    void recordRevealedCard(String cardId, String setPrefix, String raritySuffix);

    /**
     * The build-time path for a new common card: bumps the set total, the per-rarity bucket,
     * the card count, and marks it collected. Only call when the card is new.
     */
    void recordNewCommon(String cardId, String setPrefix, String raritySuffix);

    /** Bumps only the card count (reveal tap for cards already counted at build time). */
    void incrementCount(String cardId);
}
