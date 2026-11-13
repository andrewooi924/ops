package com.optcg.app.sim

import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Simulator progress from the original app: berries earned for duplicate pulls, packs opened
 * per set, and the pity counter that guarantees a hit after enough packs without one.
 */
class SimStats(private val prefs: SharedPreferences) {
    private val _berries = MutableStateFlow(prefs.getInt(KEY_BERRIES, 0))
    val berries: StateFlow<Int> = _berries.asStateFlow()

    fun packsOpened(setId: String): Int = prefs.getInt("$KEY_PACKS$setId", 0)

    fun addPacks(setId: String, count: Int) {
        prefs.edit().putInt("$KEY_PACKS$setId", packsOpened(setId) + count).apply()
    }

    fun addBerries(amount: Int) {
        if (amount == 0) return
        val total = _berries.value + amount
        prefs.edit().putInt(KEY_BERRIES, total).apply()
        _berries.value = total
    }

    /** Packs opened in a row without a parallel, SEC or special rare. */
    fun dryStreak(setId: String): Int = prefs.getInt("$KEY_DRY$setId", 0)

    fun setDryStreak(setId: String, packs: Int) {
        prefs.edit().putInt("$KEY_DRY$setId", packs).apply()
    }

    private companion object {
        const val KEY_BERRIES = "sim_berries"
        const val KEY_PACKS = "sim_packs_"
        const val KEY_DRY = "sim_dry_"
    }
}

/**
 * Berries for a duplicate pull, from the original app's table. New cards earn nothing;
 * they go into the collection instead.
 */
fun berriesFor(slot: Slot, rarity: String, isParallel: Boolean): Int = when {
    slot == Slot.SPECIAL -> 50_000
    rarity == "L" -> if (isParallel) 30_000 else 300
    rarity == "SEC" -> if (isParallel) 10_000 else 5_000
    rarity == "SR" -> if (isParallel) 3_000 else 500
    rarity == "R" -> if (isParallel) 1_000 else 300
    else -> if (isParallel) 1_000 else 0
}
