package com.optcg.app.data.local;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

/** Room access for the local user-data tables (collection, counters, portfolio snapshots). */
@Dao
public interface UserDataDao {

    @Query("SELECT * FROM collection_entries")
    List<CollectionEntryEntity> getAllEntries();

    @Query("SELECT COUNT(*) FROM collection_entries")
    int entryCount();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void upsertEntry(CollectionEntryEntity entry);

    @Query("SELECT * FROM app_counters")
    List<AppCounterEntity> getAllCounters();

    @Query("SELECT COUNT(*) FROM app_counters")
    int counterCount();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void upsertCounter(AppCounterEntity counter);

    @Query("SELECT * FROM portfolio_snapshots")
    List<PortfolioSnapshotEntity> getAllSnapshots();

    @Query("SELECT COUNT(*) FROM portfolio_snapshots")
    int snapshotCount();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void upsertSnapshot(PortfolioSnapshotEntity snapshot);
}
