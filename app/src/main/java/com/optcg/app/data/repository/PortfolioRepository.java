package com.optcg.app.data.repository;

import java.util.Map;

/**
 * Portfolio value history (a date -> total-value series) plus the last card-count marker.
 * Backed today by the PortfolioData SharedPreferences file (identical keys, no migration);
 * shaped so the store can move to Room / cloud later.
 */
public interface PortfolioRepository {

    boolean hasValueForDate(String date);

    float getValueForDate(String date, float defaultValue);

    void putValueForDate(String date, float value);

    /**
     * The full stored series as date -> value. Mirrors the original {@code getAll()} parse:
     * every entry whose value parses as a float is included (preserving prior behaviour).
     */
    Map<String, Float> getHistory();

    int getLastCardCount();

    void setLastCardCount(int count);
}
