package com.optcg.app;

import android.content.Context;
import android.util.Log;

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
    private final Context context;

    public CardRepository(CardDao cardDao, Context context) {
        this.cardDao = cardDao;
        this.context = context;
    }

    public void loadCards(String setName) {
        Executors.newSingleThreadExecutor().execute(() -> {
            if (cardDao.getAllCards().isEmpty()) {
                String jsonString = loadJsonFromAssets("cards/" + setName + "/cards.json");
                if (jsonString != null) {
                    List<Card> cards = parseJson(jsonString);
                    if (cards != null && !cards.isEmpty()) {
                        cardDao.insertAll(cards);
                    } else {
                        Log.e("CardRepository", "No cards to insert from JSON for set: " + setName);
                    }
                } else {
                    Log.e("CardRepository", "Failed to load JSON for set: " + setName);
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
}