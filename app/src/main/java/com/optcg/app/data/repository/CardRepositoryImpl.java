package com.optcg.app.data.repository;

import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.optcg.app.Card;
import com.optcg.app.data.local.AssetCardSeeder;
import com.optcg.app.data.local.LocalCardDataSource;
import com.optcg.app.data.remote.RemoteCardDataSource;
import com.optcg.app.util.AppExecutors;

import java.util.List;

/**
 * Default {@link CardRepository}: serves card metadata from the local Room cache, seeds it
 * from the bundled asset when empty, and refreshes it from the cloud catalog
 * ({@link RemoteCardDataSource}) when a newer version is available.
 */
public class CardRepositoryImpl implements CardRepository {

    private static final String CATALOG_VERSION_KEY = "version";

    private final LocalCardDataSource local;
    private final AssetCardSeeder seeder;
    private final AppExecutors executors;
    @Nullable
    private final RemoteCardDataSource remote;
    private final SharedPreferences catalogPrefs;

    public CardRepositoryImpl(LocalCardDataSource local, AssetCardSeeder seeder, AppExecutors executors,
                              @Nullable RemoteCardDataSource remote, SharedPreferences catalogPrefs) {
        this.local = local;
        this.seeder = seeder;
        this.executors = executors;
        this.remote = remote;
        this.catalogPrefs = catalogPrefs;
    }

    @Override
    public LiveData<List<Card>> getAllCards() {
        return local.getAllCards();
    }

    @Override
    public LiveData<Card> getCardById(String cardId) {
        return local.getCardById(cardId);
    }

    @Override
    public LiveData<String> getRarityById(String cardId) {
        return local.getRarityById(cardId);
    }

    @Override
    public List<Card> getCardsSync() {
        return local.getCardsSync();
    }

    @Override
    public void updateCard(Card card) {
        executors.diskIO().execute(() -> local.updateCard(card));
    }

    @Override
    public void ensureSeeded() {
        executors.diskIO().execute(() -> {
            List<Card> existing = local.getCardsSync();
            if (existing == null || existing.isEmpty()) {
                List<Card> seed = seeder.loadSeedCards();
                if (seed != null && !seed.isEmpty()) {
                    local.insertAll(seed);
                }
            }
        });
    }

    @Override
    public void refreshFromRemote() {
        if (remote == null) {
            return;
        }
        executors.networkIO().execute(() -> {
            try {
                long remoteVersion = remote.fetchCatalogVersion();
                int localVersion = catalogPrefs.getInt(CATALOG_VERSION_KEY, 0);
                if (remoteVersion > localVersion) {
                    List<Card> cards = remote.fetchAllCards();
                    if (cards != null && !cards.isEmpty()) {
                        local.insertAll(cards); // Room insert uses REPLACE, so this updates existing rows
                        catalogPrefs.edit().putInt(CATALOG_VERSION_KEY, (int) remoteVersion).apply();
                    }
                }
            } catch (Exception e) {
                Log.e("CardRepository", "Card catalog refresh failed", e);
            }
        });
    }
}
