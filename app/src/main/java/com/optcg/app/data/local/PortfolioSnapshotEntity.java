package com.optcg.app.data.local;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * One day's portfolio total value (date -> value), formerly stored in PortfolioData prefs.
 * Carries sync metadata for future cloud sync.
 */
@Entity(tableName = "portfolio_snapshots")
public class PortfolioSnapshotEntity {

    @PrimaryKey
    @NonNull
    public String date;
    public float value;
    @NonNull
    public String ownerId;
    public long updatedAt;
    public boolean pendingSync;

    public PortfolioSnapshotEntity(@NonNull String date, float value,
                                   @NonNull String ownerId, long updatedAt, boolean pendingSync) {
        this.date = date;
        this.value = value;
        this.ownerId = ownerId;
        this.updatedAt = updatedAt;
        this.pendingSync = pendingSync;
    }
}
