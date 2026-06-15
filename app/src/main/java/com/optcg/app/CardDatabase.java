package com.optcg.app;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import android.content.Context;

import com.optcg.app.data.local.AppCounterEntity;
import com.optcg.app.data.local.CollectionEntryEntity;
import com.optcg.app.data.local.PortfolioSnapshotEntity;
import com.optcg.app.data.local.PriceQuoteDao;
import com.optcg.app.data.local.PriceQuoteEntity;
import com.optcg.app.data.local.UserDataDao;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Card.class, PriceQuoteEntity.class, CollectionEntryEntity.class,
        AppCounterEntity.class, PortfolioSnapshotEntity.class}, version = 4)
public abstract class CardDatabase extends RoomDatabase {
    public static final String CARD_DATABASE_NAME = "card_database";
    public abstract CardDao cardDao();
    public abstract PriceQuoteDao priceQuoteDao();
    public abstract UserDataDao userDataDao();
    private static volatile CardDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    /** v2 -> v3: adds the offline-first price cache table. The card table is unchanged. */
    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase db) {
            db.execSQL("CREATE TABLE IF NOT EXISTS `price_quotes` ("
                    + "`url` TEXT NOT NULL, "
                    + "`avgYen` INTEGER NOT NULL, "
                    + "`avgDisplayText` TEXT, "
                    + "`soaringYen` INTEGER NOT NULL, "
                    + "`soaringText` TEXT, "
                    + "`crashYen` INTEGER NOT NULL, "
                    + "`crashText` TEXT, "
                    + "`fetchedAt` INTEGER NOT NULL, "
                    + "PRIMARY KEY(`url`))");
        }
    };

    /** v3 -> v4: adds the local user-data tables (collection, counters, portfolio snapshots). */
    static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase db) {
            db.execSQL("CREATE TABLE IF NOT EXISTS `collection_entries` ("
                    + "`cardId` TEXT NOT NULL, "
                    + "`collected` INTEGER NOT NULL, "
                    + "`count` INTEGER NOT NULL, "
                    + "`ownerId` TEXT NOT NULL, "
                    + "`updatedAt` INTEGER NOT NULL, "
                    + "`pendingSync` INTEGER NOT NULL, "
                    + "PRIMARY KEY(`cardId`))");
            db.execSQL("CREATE TABLE IF NOT EXISTS `app_counters` ("
                    + "`key` TEXT NOT NULL, "
                    + "`value` INTEGER NOT NULL, "
                    + "`ownerId` TEXT NOT NULL, "
                    + "`updatedAt` INTEGER NOT NULL, "
                    + "`pendingSync` INTEGER NOT NULL, "
                    + "PRIMARY KEY(`key`))");
            db.execSQL("CREATE TABLE IF NOT EXISTS `portfolio_snapshots` ("
                    + "`date` TEXT NOT NULL, "
                    + "`value` REAL NOT NULL, "
                    + "`ownerId` TEXT NOT NULL, "
                    + "`updatedAt` INTEGER NOT NULL, "
                    + "`pendingSync` INTEGER NOT NULL, "
                    + "PRIMARY KEY(`date`))");
        }
    };

    public static CardDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (CardDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            CardDatabase.class, CARD_DATABASE_NAME)
                            .addMigrations(MIGRATION_2_3, MIGRATION_3_4)
                            // Card metadata is seeded from the bundled cards.json asset and the
                            // price table is a disposable cache, so both can be safely rebuilt.
                            // The explicit migration above preserves data on the v2 -> v3 bump;
                            // destructive fallback stays as a safety net for unforeseen changes.
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
