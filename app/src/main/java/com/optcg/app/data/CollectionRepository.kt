package com.optcg.app.data

import android.content.SharedPreferences
import com.optcg.app.work.Jobs

/** The user's owned cards and wishlist. Works entirely offline. */
class CollectionRepository(
    private val db: AppDatabase,
    private val prefs: SharedPreferences,
    private val jobs: Jobs,
) {
    private val dao get() = db.collection()

    var keepCollectionOffline: Boolean
        get() = prefs.getBoolean(KEY_KEEP_OFFLINE, true)
        set(value) {
            prefs.edit().putBoolean(KEY_KEEP_OFFLINE, value).apply()
            if (value) jobs.saveCollectionImages()
        }

    fun ownedCount(id: String) = dao.ownedCount(id)
    fun isWished(id: String) = dao.isWished(id)
    fun distinctOwned() = dao.distinctOwned()
    fun totalOwned() = dao.totalOwned()

    suspend fun setCount(id: String, count: Int) {
        dao.setCount(id, count, System.currentTimeMillis())
        if (count > 0) collectionChanged()
    }

    suspend fun add(ids: List<String>) {
        if (ids.isEmpty()) return
        dao.add(ids, System.currentTimeMillis())
        collectionChanged()
    }

    suspend fun setWished(id: String, wished: Boolean) {
        if (wished) {
            dao.wish(WishlistEntity(id, System.currentTimeMillis()))
            collectionChanged()
        } else {
            dao.unwish(id)
        }
    }

    private fun collectionChanged() {
        if (keepCollectionOffline) jobs.saveCollectionImages()
    }

    private companion object {
        const val KEY_KEEP_OFFLINE = "keep_collection_offline"
    }
}
