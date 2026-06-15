package com.optcg.app.data.local;

import androidx.lifecycle.LiveData;

import com.optcg.app.Card;
import com.optcg.app.CardDao;

import java.util.List;

/**
 * Local (Room) data source for card metadata. Thin wrapper over {@link CardDao} so the
 * repository layer depends on a data-source abstraction rather than Room directly.
 */
public class LocalCardDataSource {

    private final CardDao cardDao;

    public LocalCardDataSource(CardDao cardDao) {
        this.cardDao = cardDao;
    }

    public LiveData<List<Card>> getAllCards() {
        return cardDao.getAllCards();
    }

    public LiveData<Card> getCardById(String id) {
        return cardDao.getCardById(id);
    }

    public LiveData<String> getRarityById(String id) {
        return cardDao.getRarityById(id);
    }

    /** Synchronous read — must be called off the main thread. */
    public List<Card> getCardsSync() {
        return cardDao.getCards();
    }

    public void insertAll(List<Card> cards) {
        cardDao.insertAll(cards);
    }

    public void updateCard(Card card) {
        cardDao.updateCard(card);
    }
}
