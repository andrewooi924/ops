package com.optcg.app.data.local;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * A named integer counter. Holds the per-set completion totals and packs-opened counts
 * (formerly COLLECTION_PREFS), the berries balance (formerly USER_PREFS), and the portfolio
 * {@code last_card_count} marker — all keyed by their original preference key so the import
 * is a direct copy. Carries sync metadata for future cloud sync.
 */
@Entity(tableName = "app_counters")
public class AppCounterEntity {

    @PrimaryKey
    @NonNull
    public String key;
    public int value;
    @NonNull
    public String ownerId;
    public long updatedAt;
    public boolean pendingSync;

    public AppCounterEntity(@NonNull String key, int value,
                            @NonNull String ownerId, long updatedAt, boolean pendingSync) {
        this.key = key;
        this.value = value;
        this.ownerId = ownerId;
        this.updatedAt = updatedAt;
        this.pendingSync = pendingSync;
    }
}
