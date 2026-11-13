package com.optcg.app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

private const val CARD_ROW = """
    SELECT p.id, p.code, p.kind, p.variant, p.rarity, p.status, p.setId, p.nameJp, p.nameEn,
           p.imageSource, p.imageThumb, p.imageDetail, p.imageQuality,
           COALESCE(o.count, 0) AS owned, (w.printingId IS NOT NULL) AS wished
    FROM printings p
    LEFT JOIN owned o ON o.printingId = p.id
    LEFT JOIN wishlist w ON w.printingId = p.id
"""

@Dao
interface CatalogDao {
    @Query(
        """
        SELECT s.id, s.code, s.kind, s.nameJp, s.nameEn, s.boxArt, s.packArt, COUNT(p.id) AS total,
               COALESCE(SUM(CASE WHEN o.count > 0 THEN 1 ELSE 0 END), 0) AS owned
        FROM sets s
        LEFT JOIN printings p ON p.setId = s.id
        LEFT JOIN owned o ON o.printingId = p.id
        GROUP BY s.id
        ORDER BY s.position
        """
    )
    fun setProgress(): Flow<List<SetProgress>>

    @Query("SELECT * FROM sets WHERE id = :id")
    suspend fun set(id: String): SetEntity?

    @Query("$CARD_ROW WHERE p.setId = :setId ORDER BY p.position")
    fun cardsInSet(setId: String): Flow<List<CardRow>>

    @Query("SELECT * FROM printings WHERE setId = :setId ORDER BY position")
    suspend fun printingsInSet(setId: String): List<PrintingEntity>

    @Query("SELECT * FROM printings WHERE id IN (:ids)")
    suspend fun printings(ids: List<String>): List<PrintingEntity>

    @Query("SELECT * FROM printings WHERE id = :id")
    fun printing(id: String): Flow<PrintingEntity?>

    /** Other printings of the same card (parallels, reprints), excluding dons. */
    @Query("$CARD_ROW WHERE p.code = :code AND p.kind = 'card' ORDER BY p.position")
    fun printingsOfCode(code: String): Flow<List<CardRow>>

    @Query(
        """
        $CARD_ROW
        WHERE p.nameEn LIKE :pattern OR p.nameJp LIKE :pattern OR p.code LIKE :pattern
              OR p.featureEn LIKE :pattern OR p.featureJp LIKE :pattern
        ORDER BY (p.code LIKE :pattern) DESC, p.position
        LIMIT :limit
        """
    )
    fun search(pattern: String, limit: Int = 300): Flow<List<CardRow>>

    @Query("$CARD_ROW WHERE o.count > 0 ORDER BY p.position")
    fun ownedCards(): Flow<List<CardRow>>

    @Query("$CARD_ROW WHERE w.printingId IS NOT NULL ORDER BY w.addedAt DESC")
    fun wishlistCards(): Flow<List<CardRow>>

    /** Printings whose images should be kept offline: owned or wishlisted. */
    @Query(
        """
        SELECT * FROM printings WHERE id IN (SELECT printingId FROM owned WHERE count > 0)
                                   OR id IN (SELECT printingId FROM wishlist)
        """
    )
    suspend fun collectionPrintings(): List<PrintingEntity>

    @Query("SELECT COUNT(*) FROM printings")
    suspend fun printingCount(): Int

    @Query("DELETE FROM sets")
    suspend fun clearSets()

    @Query("DELETE FROM printings")
    suspend fun clearPrintings()

    @Insert
    suspend fun insertSets(sets: List<SetEntity>)

    @Insert
    suspend fun insertPrintings(printings: List<PrintingEntity>)

    @Transaction
    suspend fun replaceCatalog(sets: List<SetEntity>, printings: List<PrintingEntity>) {
        clearSets()
        clearPrintings()
        insertSets(sets)
        printings.chunked(500).forEach { insertPrintings(it) }
    }
}

@Dao
interface CollectionDao {
    @Query("SELECT COALESCE((SELECT count FROM owned WHERE printingId = :id), 0)")
    fun ownedCount(id: String): Flow<Int>

    @Query("SELECT EXISTS(SELECT 1 FROM wishlist WHERE printingId = :id)")
    fun isWished(id: String): Flow<Boolean>

    @Query("SELECT COUNT(*) FROM owned WHERE count > 0")
    fun distinctOwned(): Flow<Int>

    @Query("SELECT COALESCE(SUM(count), 0) FROM owned")
    fun totalOwned(): Flow<Int>

    @Query("SELECT count FROM owned WHERE printingId = :id")
    suspend fun count(id: String): Int?

    @Upsert
    suspend fun upsert(entry: OwnedEntity)

    @Query("DELETE FROM owned WHERE printingId = :id")
    suspend fun deleteOwned(id: String)

    @Transaction
    suspend fun setCount(id: String, count: Int, now: Long) {
        if (count <= 0) deleteOwned(id) else upsert(OwnedEntity(id, count, now))
    }

    @Transaction
    suspend fun add(ids: List<String>, now: Long) {
        for ((id, n) in ids.groupingBy { it }.eachCount()) {
            upsert(OwnedEntity(id, (count(id) ?: 0) + n, now))
        }
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun wish(entry: WishlistEntity)

    @Query("DELETE FROM wishlist WHERE printingId = :id")
    suspend fun unwish(id: String)
}
