package com.optcg.app.data.local;

import android.content.SharedPreferences;

import com.optcg.app.util.AppExecutors;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

/**
 * In-memory mirror of the local user-data Room tables (collection entries, integer counters,
 * portfolio snapshots). It exists so the synchronous repository interfaces the UI relies on
 * keep working without doing Room I/O on the main thread: the mirror is loaded from Room once
 * at startup (on a background thread) and every read is served from memory; writes update the
 * mirror immediately and are persisted to Room asynchronously with sync metadata.
 *
 * <p>On first run after the upgrade it imports the existing SharedPreferences data so no user
 * collection/berries/portfolio history is lost. The original preference files are left intact
 * as a backstop.
 */
public class UserDataStore {

    /** Owner id used while the app is local-only (pre-sign-in). */
    public static final String LOCAL_OWNER = "local";

    private final UserDataDao dao;
    private final AppExecutors executors;
    private final SharedPreferences collectionPrefs;
    private final SharedPreferences userPrefs;
    private final SharedPreferences portfolioPrefs;

    private final Map<String, Boolean> collected = new ConcurrentHashMap<>();
    private final Map<String, Integer> counts = new ConcurrentHashMap<>();
    private final Map<String, Integer> counters = new ConcurrentHashMap<>();
    private final Map<String, Float> snapshots = new ConcurrentHashMap<>();

    private final CountDownLatch loaded = new CountDownLatch(1);

    public UserDataStore(UserDataDao dao, AppExecutors executors,
                         SharedPreferences collectionPrefs, SharedPreferences userPrefs,
                         SharedPreferences portfolioPrefs) {
        this.dao = dao;
        this.executors = executors;
        this.collectionPrefs = collectionPrefs;
        this.userPrefs = userPrefs;
        this.portfolioPrefs = portfolioPrefs;
        executors.diskIO().execute(this::initialLoad);
    }

    private void initialLoad() {
        try {
            boolean empty = dao.entryCount() == 0 && dao.counterCount() == 0 && dao.snapshotCount() == 0;
            if (empty) {
                importFromPrefs();
            }
            for (CollectionEntryEntity e : dao.getAllEntries()) {
                collected.put(e.cardId, e.collected);
                counts.put(e.cardId, e.count);
            }
            for (AppCounterEntity c : dao.getAllCounters()) {
                counters.put(c.key, c.value);
            }
            for (PortfolioSnapshotEntity s : dao.getAllSnapshots()) {
                snapshots.put(s.date, s.value);
            }
        } finally {
            loaded.countDown();
        }
    }

    /** One-time copy of the legacy SharedPreferences data into Room. */
    private void importFromPrefs() {
        long now = System.currentTimeMillis();

        // COLLECTION_PREFS: per-card flags/counts + per-set integer counters.
        Map<String, Boolean> impCollected = new HashMap<>();
        Map<String, Integer> impCount = new HashMap<>();
        for (Map.Entry<String, ?> entry : collectionPrefs.getAll().entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (key.endsWith("_isCollected") && value instanceof Boolean) {
                impCollected.put(key.substring(0, key.length() - "_isCollected".length()), (Boolean) value);
            } else if (key.contains("_total_") || key.endsWith("_packs_opened")) {
                if (value instanceof Integer) {
                    dao.upsertCounter(new AppCounterEntity(key, (Integer) value, LOCAL_OWNER, now, true));
                }
            } else if (key.endsWith("_count") && value instanceof Integer) {
                impCount.put(key.substring(0, key.length() - "_count".length()), (Integer) value);
            }
        }
        java.util.Set<String> cardIds = new java.util.HashSet<>();
        cardIds.addAll(impCollected.keySet());
        cardIds.addAll(impCount.keySet());
        for (String cardId : cardIds) {
            boolean isCol = Boolean.TRUE.equals(impCollected.get(cardId));
            int cnt = impCount.containsKey(cardId) ? impCount.get(cardId) : 0;
            dao.upsertEntry(new CollectionEntryEntity(cardId, isCol, cnt, LOCAL_OWNER, now, true));
        }

        // USER_PREFS: berries.
        if (userPrefs.contains("berries")) {
            dao.upsertCounter(new AppCounterEntity("berries", userPrefs.getInt("berries", 0), LOCAL_OWNER, now, true));
        }

        // PortfolioData: date -> value (float) snapshots, plus the last_card_count (int) marker.
        for (Map.Entry<String, ?> entry : portfolioPrefs.getAll().entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof Float) {
                dao.upsertSnapshot(new PortfolioSnapshotEntity(key, (Float) value, LOCAL_OWNER, now, true));
            } else if (value instanceof Integer) {
                dao.upsertCounter(new AppCounterEntity(key, (Integer) value, LOCAL_OWNER, now, true));
            }
        }
    }

    /** Re-reads the Room tables into the in-memory mirror (e.g. after a sync pull). */
    public void reload() {
        awaitLoaded();
        Map<String, Boolean> newCollected = new HashMap<>();
        Map<String, Integer> newCounts = new HashMap<>();
        for (CollectionEntryEntity e : dao.getAllEntries()) {
            newCollected.put(e.cardId, e.collected);
            newCounts.put(e.cardId, e.count);
        }
        Map<String, Integer> newCounters = new HashMap<>();
        for (AppCounterEntity c : dao.getAllCounters()) {
            newCounters.put(c.key, c.value);
        }
        Map<String, Float> newSnapshots = new HashMap<>();
        for (PortfolioSnapshotEntity s : dao.getAllSnapshots()) {
            newSnapshots.put(s.date, s.value);
        }
        collected.keySet().retainAll(newCollected.keySet());
        collected.putAll(newCollected);
        counts.keySet().retainAll(newCounts.keySet());
        counts.putAll(newCounts);
        counters.keySet().retainAll(newCounters.keySet());
        counters.putAll(newCounters);
        snapshots.keySet().retainAll(newSnapshots.keySet());
        snapshots.putAll(newSnapshots);
    }

    private void awaitLoaded() {
        try {
            loaded.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // ---- Collection entries ----

    public boolean isCardCollected(String cardId) {
        awaitLoaded();
        return Boolean.TRUE.equals(collected.get(cardId));
    }

    public int getCardCount(String cardId) {
        awaitLoaded();
        Integer v = counts.get(cardId);
        return v != null ? v : 0;
    }

    public void setCard(String cardId, boolean isCollected, int count) {
        awaitLoaded();
        collected.put(cardId, isCollected);
        counts.put(cardId, count);
        final long now = System.currentTimeMillis();
        executors.diskIO().execute(() ->
                dao.upsertEntry(new CollectionEntryEntity(cardId, isCollected, count, LOCAL_OWNER, now, true)));
    }

    public void incrementCardCount(String cardId) {
        setCard(cardId, isCardCollected(cardId), getCardCount(cardId) + 1);
    }

    // ---- Integer counters ----

    public int getCounter(String key) {
        return getCounter(key, 0);
    }

    public int getCounter(String key, int defaultValue) {
        awaitLoaded();
        Integer v = counters.get(key);
        return v != null ? v : defaultValue;
    }

    public void setCounter(String key, int value) {
        awaitLoaded();
        counters.put(key, value);
        final long now = System.currentTimeMillis();
        executors.diskIO().execute(() ->
                dao.upsertCounter(new AppCounterEntity(key, value, LOCAL_OWNER, now, true)));
    }

    public int incrementCounter(String key) {
        int newValue = getCounter(key) + 1;
        setCounter(key, newValue);
        return newValue;
    }

    // ---- Portfolio snapshots ----

    public boolean hasSnapshot(String date) {
        awaitLoaded();
        return snapshots.containsKey(date);
    }

    public float getSnapshot(String date, float defaultValue) {
        awaitLoaded();
        Float v = snapshots.get(date);
        return v != null ? v : defaultValue;
    }

    public void setSnapshot(String date, float value) {
        awaitLoaded();
        snapshots.put(date, value);
        final long now = System.currentTimeMillis();
        executors.diskIO().execute(() ->
                dao.upsertSnapshot(new PortfolioSnapshotEntity(date, value, LOCAL_OWNER, now, true)));
    }

    public Map<String, Float> getSnapshots() {
        awaitLoaded();
        return new HashMap<>(snapshots);
    }
}
