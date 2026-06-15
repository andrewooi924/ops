package com.optcg.app.data.repository;

import androidx.lifecycle.LiveData;

import com.optcg.app.Card;

import java.util.List;

/**
 * Repository for card metadata. UI observes cards from here; it never touches Room,
 * Gson or the {@code cards.json} asset directly. Currently backed by a local Room cache
 * seeded from the bundled asset; a future cloud card source can be added behind this
 * interface without changing callers.
 */
public interface CardRepository {

    LiveData<List<Card>> getAllCards();

    LiveData<Card> getCardById(String cardId);

    LiveData<String> getRarityById(String cardId);

    /** Synchronous read — callers must be off the main thread. */
    List<Card> getCardsSync();

    void updateCard(Card card);

    /** Seeds the local cache from the bundled asset if it is empty. Runs in the background. */
    void ensureSeeded();

    /**
     * Refreshes the local card cache from the cloud catalog if a newer version exists.
     * No-op when no remote source is configured. Runs in the background.
     */
    void refreshFromRemote();
}
