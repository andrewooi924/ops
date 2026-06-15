package com.optcg.app.data.local;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Room cache row for a {@link com.optcg.app.domain.model.PriceQuote}, keyed by card page
 * URL. This is the offline-first store for prices — the UI shows the last cached value
 * instantly and the repository refreshes it from the remote source in the background.
 */
@Entity(tableName = "price_quotes")
public class PriceQuoteEntity {

    @PrimaryKey
    @NonNull
    public String url;
    public int avgYen;
    public String avgDisplayText;
    public int soaringYen;
    public String soaringText;
    public int crashYen;
    public String crashText;
    public long fetchedAt;

    public PriceQuoteEntity(@NonNull String url, int avgYen, String avgDisplayText,
                            int soaringYen, String soaringText,
                            int crashYen, String crashText, long fetchedAt) {
        this.url = url;
        this.avgYen = avgYen;
        this.avgDisplayText = avgDisplayText;
        this.soaringYen = soaringYen;
        this.soaringText = soaringText;
        this.crashYen = crashYen;
        this.crashText = crashText;
        this.fetchedAt = fetchedAt;
    }
}
