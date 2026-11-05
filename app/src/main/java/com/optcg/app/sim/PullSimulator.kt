package com.optcg.app.sim

import com.optcg.app.catalog.PullProfile
import kotlin.random.Random

/** A card that can be pulled from a set. */
data class SimCard(val id: String, val rarity: String, val isParallel: Boolean)

enum class Slot { FILLER, L, R, SR, SEC, PARALLEL, SPECIAL }

data class Pull(val card: SimCard, val slot: Slot)

data class Pack(val cards: List<Pull>) {
    /** The best card in the pack, shown last when revealing. */
    val best: Pull? get() = cards.maxByOrNull { it.slot.ordinal }
}

data class Box(val packs: List<Pack>) {
    val pulls: List<Pull> get() = packs.flatMap { it.cards }
    val hits: List<Pull> get() = pulls.filter { it.slot != Slot.FILLER }.sortedByDescending { it.slot.ordinal }
}

/**
 * Simulates a sealed box from per-box counts (how many leaders, rares, super rares and
 * parallels a box holds) rather than independent per-pack odds, because that is how
 * Japanese boxes are packed and what box openings measure. Pull rates are community
 * estimates (see catalog/data/pull_profiles.json).
 */
class PullSimulator(
    private val profile: PullProfile,
    pool: List<SimCard>,
    private val random: Random = Random.Default,
) {
    private val specialRarities = profile.specialRares?.rarities.orEmpty().toSet()
    private val base = pool.filter { !it.isParallel && it.rarity !in specialRarities }.groupBy { it.rarity }
    private val parallels = pool.filter { it.isParallel && it.rarity !in specialRarities }
    private val specials = pool.filter { it.rarity in specialRarities }
    private val filler: List<Pair<List<SimCard>, Int>> = run {
        val weights = profile.filler?.weights.orEmpty().ifEmpty { mapOf("C" to 2, "UC" to 1) }
        val weighted = weights.mapNotNull { (rarity, weight) -> base[rarity]?.takeIf { it.isNotEmpty() }?.let { it to weight } }
        // Sets without commons (e.g. premium boosters) fill packs from whatever base cards exist.
        weighted.ifEmpty { listOf(base.values.flatten() to 1) }
    }

    val canSimulate: Boolean get() = base.values.any { it.isNotEmpty() }

    fun openPack(): Pack = openBox().packs.random(random)

    fun openBox(): Box {
        val packCount = profile.packsPerBox
        val leaders = mutableListOf<Pull>()
        val hits = mutableListOf<Pull>()

        for ((rarity, range) in profile.perBox) {
            val cards = base[rarity].orEmpty()
            if (cards.isEmpty() || range.isEmpty()) continue
            val count = random.nextInt(range.first(), range.last() + 1)
            val slot = slotFor(rarity)
            val target = if (slot == Slot.L) leaders else hits
            repeat(count) { target += Pull(cards.random(random), slot) }
        }

        val pattern = pickPattern()
        var parallelCount = if (parallels.isEmpty()) 0 else pattern?.parallel ?: 0
        val secCount = if (base["SEC"].isNullOrEmpty()) 0 else pattern?.sec ?: 0
        val perCase = profile.specialRares?.perCase
        if (specials.isNotEmpty() && perCase != null && random.nextDouble() < perCase / profile.boxesPerCase) {
            hits += Pull(specials.random(random), Slot.SPECIAL)
            if (parallelCount > 0) parallelCount -= 1 // a special rare takes a parallel's place
        }
        repeat(parallelCount) { hits += Pull(parallels.random(random), Slot.PARALLEL) }
        repeat(secCount) { hits += Pull(base.getValue("SEC").random(random), Slot.SEC) }

        // Leaders and other hits each go to different packs, as in a real box.
        val packHits = List(packCount) { mutableListOf<Pull>() }
        spread(leaders, packHits)
        spread(hits, packHits)

        val packs = packHits.map { inPack ->
            val fillerCount = (profile.cardsPerPack - inPack.size).coerceAtLeast(0)
            val cards = List(fillerCount) { Pull(pickFiller(), Slot.FILLER) } + inPack.sortedBy { it.slot.ordinal }
            Pack(cards)
        }
        return Box(packs)
    }

    private fun spread(pulls: List<Pull>, packs: List<MutableList<Pull>>) {
        val order = packs.indices.shuffled(random)
        pulls.shuffled(random).forEachIndexed { index, pull -> packs[order[index % order.size]] += pull }
    }

    private fun pickPattern() = profile.boxPatterns.takeIf { it.isNotEmpty() }?.let { patterns ->
        var roll = random.nextInt(patterns.sumOf { it.weight })
        patterns.first { pattern -> (roll < pattern.weight).also { roll -= pattern.weight } }
    }

    private fun pickFiller(): SimCard {
        var roll = random.nextInt(filler.sumOf { it.second })
        val cards = filler.first { (_, weight) -> (roll < weight).also { roll -= weight } }.first
        return cards.random(random)
    }

    private fun slotFor(rarity: String) = when (rarity) {
        "L" -> Slot.L
        "SR" -> Slot.SR
        "SEC" -> Slot.SEC
        else -> Slot.R
    }
}
