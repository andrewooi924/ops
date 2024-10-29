package com.optcg.app;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CardDao {
    @Query("SELECT * FROM cards WHERE id = :id OR name LIKE :name")
    List<Card> searchByNameOrID(String id, String name);

    @Query("SELECT * FROM cards WHERE rarity = :rarity")
    List<Card> filterByRarity(String rarity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Card> cards);

    @Query("SELECT * FROM cards")
    List<Card> getAllCards();

    @Query("SELECT * FROM cards WHERE rarity = :rarity")
    List<Card> getCardsByRarity(String rarity);
}
