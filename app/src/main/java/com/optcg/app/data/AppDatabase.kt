package com.optcg.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [SetEntity::class, PrintingEntity::class, OwnedEntity::class, WishlistEntity::class],
    version = 1,
    exportSchema = true,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun catalog(): CatalogDao
    abstract fun collection(): CollectionDao

    companion object {
        // A new file name, so the old app's database (cards.db) is never opened or migrated.
        fun create(context: Context): AppDatabase =
            Room.databaseBuilder(context, AppDatabase::class.java, "piecelab.db").build()
    }
}
