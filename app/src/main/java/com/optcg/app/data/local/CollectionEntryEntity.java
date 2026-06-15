package com.optcg.app.data.local;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * One owned-card record: whether it's collected and how many copies. Carries sync metadata
 * ({@code ownerId}, {@code updatedAt}, {@code pendingSync}) so it can later be synced to a
 * cloud backend per user.
 */
@Entity(tableName = "collection_entries")
public class CollectionEntryEntity {

    @PrimaryKey
    @NonNull
    public String cardId;
    public boolean collected;
    public int count;
    @NonNull
    public String ownerId;
    public long updatedAt;
    public boolean pendingSync;

    public CollectionEntryEntity(@NonNull String cardId, boolean collected, int count,
                                 @NonNull String ownerId, long updatedAt, boolean pendingSync) {
        this.cardId = cardId;
        this.collected = collected;
        this.count = count;
        this.ownerId = ownerId;
        this.updatedAt = updatedAt;
        this.pendingSync = pendingSync;
    }
}
