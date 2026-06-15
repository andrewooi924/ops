package com.optcg.app.data.repository;

import com.optcg.app.data.local.UserDataStore;

import java.util.Map;

/**
 * Room-backed {@link PortfolioRepository} (via {@link UserDataStore}). The value history is
 * stored as dated snapshots and the last-card-count as a named counter.
 *
 * <p>Note: {@link #getHistory()} now returns only the dated value snapshots. The previous
 * SharedPreferences loader accidentally also plotted the {@code last_card_count} marker as a
 * data point; that stray point is intentionally no longer included.
 */
public class PortfolioRepositoryImpl implements PortfolioRepository {

    private static final String CARD_COUNT_KEY = "last_card_count";

    private final UserDataStore store;

    public PortfolioRepositoryImpl(UserDataStore store) {
        this.store = store;
    }

    @Override
    public boolean hasValueForDate(String date) {
        return store.hasSnapshot(date);
    }

    @Override
    public float getValueForDate(String date, float defaultValue) {
        return store.getSnapshot(date, defaultValue);
    }

    @Override
    public void putValueForDate(String date, float value) {
        store.setSnapshot(date, value);
    }

    @Override
    public Map<String, Float> getHistory() {
        return store.getSnapshots();
    }

    @Override
    public int getLastCardCount() {
        return store.getCounter(CARD_COUNT_KEY, -1);
    }

    @Override
    public void setLastCardCount(int count) {
        store.setCounter(CARD_COUNT_KEY, count);
    }
}
