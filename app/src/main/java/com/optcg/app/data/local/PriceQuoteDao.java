package com.optcg.app.data.local;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface PriceQuoteDao {

    @Query("SELECT * FROM price_quotes WHERE url = :url")
    PriceQuoteEntity getByUrl(String url);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void upsert(PriceQuoteEntity quote);
}
