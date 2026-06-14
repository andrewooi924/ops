package com.optcg.app;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Card.class}, version = 2)
public abstract class CardDatabase extends RoomDatabase {
    public static final String CARD_DATABASE_NAME = "card_database";
    public abstract CardDao cardDao();
    private static volatile CardDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static CardDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (CardDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            CardDatabase.class, CARD_DATABASE_NAME)
                            // Card metadata is seeded from the bundled cards.json asset, so the
                            // table holds no user-generated data (collection/portfolio state lives
                            // in SharedPreferences). On a schema change it is safe to drop and
                            // re-seed rather than crash for lack of a Migration.
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}