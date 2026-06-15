package com.optcg.app.data.local;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory map of card id -> relative image path, loaded once from Room at startup. Lets UI
 * that only has a card id (gacha pools, collection grids, etc.) resolve the image path without
 * a per-item database lookup. Screens that already hold a {@code Card} can use its img directly.
 */
public class CardImageIndex {

    private final Map<String, String> idToPath = new ConcurrentHashMap<>();

    public void setAll(Map<String, String> map) {
        idToPath.putAll(map);
    }

    /** Relative image path for a card id, or {@code null} if unknown / not yet loaded. */
    public String pathFor(String cardId) {
        return cardId == null ? null : idToPath.get(cardId);
    }
}
