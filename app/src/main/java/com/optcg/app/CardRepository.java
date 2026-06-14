package com.optcg.app;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.Executors;

public class CardRepository {
    private final CardDao cardDao;
    private LiveData<List<Card>> allCards;
    private Context context;

    CardRepository(Application application) {
        CardDatabase db = CardDatabase.getDatabase(application);
        cardDao = db.cardDao();
        allCards = cardDao.getAllCards();
        context = application.getApplicationContext();

    }

    public void loadCards() {
        // One-shot seed check on a background thread. Previously this used an
        // observeForever observer that was never removed (a leak); the synchronous
        // getCards() query gives the same "seed only when empty" behaviour without
        // holding a permanent observer.
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Card> existing = cardDao.getCards();
            if (existing == null || existing.isEmpty()) {
                String jsonString = loadJsonFromAssets("cards.json");
                if (jsonString != null) {
                    List<Card> cardList = parseJson(jsonString);
                    if (cardList != null && !cardList.isEmpty()) {
                        cardDao.insertAll(cardList);
                    } else {
                        Log.e("CardRepository", "No cards to insert from JSON for set");
                    }
                } else {
                    Log.e("CardRepository", "Failed to load JSON for set");
                }
            }
        });
    }

    private String loadJsonFromAssets(String filePath) {
        try (InputStream is = context.getAssets().open(filePath)) {
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            return new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private List<Card> parseJson(String jsonString) {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<Card>>() {}.getType();
        return gson.fromJson(jsonString, listType);
    }

    public LiveData<List<Card>> getAllCards() {
        return cardDao.getAllCards();
    }

    public List<Card> getCards() {
        return cardDao.getCards();
    }

    public LiveData<Card> getCardById(String cardId) {
        return cardDao.getCardById(cardId);
    }

    public void updateCard(Card card) {
        new Thread(() -> cardDao.updateCard(card)).start();
    }

    public LiveData<String> getRarityById(String cardId) {
        return cardDao.getRarityById(cardId);
    }
}