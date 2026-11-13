package com.optcg.app

import com.optcg.app.catalog.CatalogMapper
import com.optcg.app.catalog.PullProfile
import com.optcg.app.sim.PullSimulator
import com.optcg.app.sim.SimCard
import com.optcg.app.sim.Slot
import com.optcg.app.sim.simPool
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class PullSimulatorTest {
    private val catalog = BundledCatalog.catalog
    private val profile: PullProfile = catalog.pullProfiles.profileFor("550101", "OP-01", "booster")!!
    private val op01 = CatalogMapper.printings(catalog).filter { it.setId == "550101" }
    private val pool = simPool(op01)
    private val simulator = PullSimulator(profile, pool, Random(42))

    @Test
    fun boxHasTheRightShape() {
        repeat(200) {
            val box = simulator.openBox()
            assertEquals(profile.packsPerBox, box.packs.size)
            box.packs.forEach { pack -> assertEquals(profile.cardsPerPack, pack.cards.size) }
            val counts = box.pulls.groupingBy { it.slot }.eachCount()
            assertTrue(counts.getOrDefault(Slot.L, 0) in 3..4)
            assertTrue(counts.getOrDefault(Slot.SR, 0) in 3..4)
            assertTrue(counts.getOrDefault(Slot.R, 0) in 7..8)
            val sec = counts.getOrDefault(Slot.SEC, 0)
            val parallels = counts.getOrDefault(Slot.PARALLEL, 0) + counts.getOrDefault(Slot.SPECIAL, 0)
            // One of the three box patterns: 2 parallels, 1 parallel, or 1 SEC.
            assertTrue("sec=$sec parallels=$parallels", (sec == 0 && parallels in 1..2) || (sec == 1 && parallels <= 1))
        }
    }

    @Test
    fun pullsComeFromTheSetAndMatchTheirSlot() {
        val byId = pool.associateBy { it.id }
        repeat(50) {
            simulator.openBox().pulls.forEach { pull ->
                val card = byId.getValue(pull.card.id)
                when (pull.slot) {
                    Slot.FILLER -> assertTrue(card.rarity in setOf("C", "UC") && !card.isParallel)
                    Slot.PARALLEL -> assertTrue(card.isParallel)
                    Slot.L, Slot.R, Slot.SR, Slot.SEC -> assertFalse(card.isParallel)
                    Slot.SPECIAL -> assertTrue(card.rarity in setOf("SP", "TR"))
                }
            }
        }
    }

    @Test
    fun patternFrequenciesFollowTheProfile() {
        val boxes = 6000
        val random = PullSimulator(profile, pool, Random(7))
        var secBoxes = 0
        repeat(boxes) { if (random.openBox().pulls.any { it.slot == Slot.SEC }) secBoxes += 1 }
        val expected = profile.boxPatterns.filter { it.sec > 0 }.sumOf { it.weight }.toDouble() /
            profile.boxPatterns.sumOf { it.weight }
        assertEquals(expected, secBoxes.toDouble() / boxes, 0.03)
    }

    @Test
    fun leadersAndHitsAreSpreadAcrossPacks() {
        repeat(100) {
            simulator.openBox().packs.forEach { pack ->
                assertTrue(pack.cards.count { it.slot == Slot.L } <= 1)
                assertTrue(pack.cards.count { it.slot != Slot.FILLER && it.slot != Slot.L } <= 1)
            }
        }
    }

    @Test
    fun leaderIsLastOtherwiseTheBestCard() {
        repeat(300) {
            simulator.openBox().packs.forEach(::assertRevealOrder)
        }
    }

    @Test
    fun packsNeverHoldTheSameCardTwice() {
        repeat(300) {
            simulator.openBox().packs.forEach { pack ->
                val ids = pack.cards.map { it.card.id }
                assertEquals(ids.toString(), ids.size, ids.toSet().size)
            }
        }
    }

    private fun assertRevealOrder(pack: com.optcg.app.sim.Pack) {
        val expected = if (pack.cards.any { it.slot == Slot.L }) Slot.L else pack.best?.slot
        assertEquals(expected, pack.cards.last().slot)
        // Commons come before every hit.
        val firstHit = pack.cards.indexOfFirst { it.slot != Slot.FILLER }
        if (firstHit >= 0) assertTrue(pack.cards.drop(firstHit).none { it.slot == Slot.FILLER })
    }

    @Test
    fun pityGuaranteesABigHitAndKeepsPackSize() {
        repeat(200) {
            val pack = simulator.openPack()
            val forced = simulator.forceHit(pack)
            assertEquals(pack.cards.size, forced.cards.size)
            assertTrue(forced.cards.any { it.slot.isBigHit })
            assertRevealOrder(forced)
            assertEquals(forced.cards.size, forced.cards.map { it.card.id }.toSet().size)
            if (pack.cards.any { it.slot.isBigHit }) assertEquals(pack, forced)
        }
    }

    @Test
    fun setWithoutCommonsStillFillsPacks() {
        val pool = listOf(SimCard("A", "R", false), SimCard("B", "SR", false), SimCard("C", "L", false))
        val box = PullSimulator(profile, pool, Random(1)).openBox()
        assertTrue(box.packs.all { it.cards.size == profile.cardsPerPack })
    }

    @Test
    fun premiumBoostersHaveNoSimulatorByDefault() {
        assertEquals(null, catalog.pullProfiles.profileFor("550301", "PRB-01", "premium"))
    }
}
