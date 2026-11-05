package com.optcg.app.sim

import com.optcg.app.data.PrintingEntity

/** Cards in a set that can come out of its packs: official cards with a known rarity. */
fun simPool(printings: List<PrintingEntity>): List<SimCard> =
    printings
        .filter { it.kind == "card" && it.status == "official" && it.rarity != null }
        .map { SimCard(it.id, it.rarity!!, it.isParallel) }
