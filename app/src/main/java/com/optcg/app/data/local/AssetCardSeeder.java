package com.optcg.app.data.local;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.optcg.app.Card;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Loads the bundled {@code cards.json} asset into {@link Card} objects. This is the
 * single owner of the asset-parsing logic that previously lived in both
 * {@code CardRepository} and {@code HomeFragment}. It seeds the Room cache on first run;
 * when a cloud card backend exists this seeder is simply one of the remote sources the
 * repository can pull from.
 */
public class AssetCardSeeder {

    private static final String CARDS_ASSET = "cards.json";

    private final Context context;

    public AssetCardSeeder(Context context) {
        this.context = context.getApplicationContext();
    }

    /** Reads and parses the asset. Returns {@code null} on failure. Call off the main thread. */
    public List<Card> loadSeedCards() {
        String json = loadJsonFromAssets(CARDS_ASSET);
        if (json == null) {
            return null;
        }
        return parseJson(json);
    }

    private String loadJsonFromAssets(String filePath) {
        try (InputStream is = context.getAssets().open(filePath)) {
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            return new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException e) {
            Log.e("AssetCardSeeder", "Failed to read " + filePath, e);
            return null;
        }
    }

    private List<Card> parseJson(String jsonString) {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<Card>>() {}.getType();
        return gson.fromJson(jsonString, listType);
    }
}
