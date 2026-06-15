package com.optcg.app;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.optcg.app.data.repository.CardRepository;
import com.optcg.app.di.ServiceLocator;

import java.util.List;

/**
 * UI-facing bridge to the card data layer. Delegates card reads to {@link CardRepository}
 * (obtained from the {@link ServiceLocator}) and additionally carries the shared
 * deck "total price" value used by the deck screens.
 */
public class CardViewModel extends AndroidViewModel {

    private final CardRepository cardRepository;
    private final MutableLiveData<Double> totalPrice = new MutableLiveData<>(0.0);

    public CardViewModel(@NonNull Application application) {
        super(application);
        cardRepository = ServiceLocator.get(application).cardRepository();
    }

    public LiveData<List<Card>> getAllCards() {
        return cardRepository.getAllCards();
    }

    public List<Card> getCards() {
        return cardRepository.getCardsSync();
    }

    public LiveData<Card> getCardById(String cardId) {
        return cardRepository.getCardById(cardId);
    }

    public LiveData<String> getRarityById(String cardId) {
        return cardRepository.getRarityById(cardId);
    }

    public void updateCard(Card card) {
        cardRepository.updateCard(card);
    }

    public void setTotalPrice(double price) {
        totalPrice.setValue(price);
    }

    public LiveData<Double> getTotalPrice() {
        return totalPrice;
    }
}
