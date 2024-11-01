package com.optcg.app;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class CardViewModel extends AndroidViewModel {
    private CardRepository cardRepository;
    private LiveData<List<Card>> allCards;

    public CardViewModel(@NonNull Application application) {
        super(application);
        cardRepository = new CardRepository(application);
        cardRepository.loadCards();
        allCards = cardRepository.getAllCards();
    }

    public LiveData<List<Card>> getAllCards() { return allCards; }

    public List<Card> getCards() { return cardRepository.getCards(); }

    public LiveData<Card> getCardById(String cardId) {
        return cardRepository.getCardById(cardId);
    }

    public void updateCard(Card card) {
        cardRepository.updateCard(card);
        allCards = cardRepository.getAllCards();
    }
}
