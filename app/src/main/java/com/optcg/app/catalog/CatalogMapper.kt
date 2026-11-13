package com.optcg.app.catalog

import com.optcg.app.data.PrintingEntity
import com.optcg.app.data.SetEntity
import com.optcg.app.data.VIRTUAL_ANNOUNCED
import com.optcg.app.data.VIRTUAL_DONS

/** Turns a downloaded catalog into database rows. Pure, so it is unit-tested on the JVM. */
object CatalogMapper {
    fun sets(catalog: Catalog): List<SetEntity> {
        val sets = catalog.sets.mapIndexed { index, set ->
            SetEntity(set.id, set.code, set.kind, set.nameJp, set.nameEn, index, set.boxArt, set.packArt)
        }.toMutableList()
        val known = sets.mapTo(HashSet()) { it.id }
        if (catalog.printings.any { it.kind == "don" }) {
            sets += SetEntity(VIRTUAL_DONS, null, "don", "ドン!!カード", "DON!! cards", sets.size)
        }
        if (catalog.printings.any { it.kind != "don" && it.series.none(known::contains) }) {
            sets += SetEntity(VIRTUAL_ANNOUNCED, null, "announced", "発表済みカード", "Announced cards", sets.size)
        }
        return sets
    }

    fun printings(catalog: Catalog): List<PrintingEntity> {
        val known = catalog.sets.mapTo(HashSet()) { it.id }
        return catalog.printings.mapIndexed { index, p ->
            val setId = when {
                p.kind == "don" -> VIRTUAL_DONS
                else -> p.series.firstOrNull(known::contains) ?: VIRTUAL_ANNOUNCED
            }
            PrintingEntity(
                id = p.id,
                code = p.code,
                kind = p.kind,
                variant = p.variant,
                rarity = p.rarity,
                status = p.status,
                setId = setId,
                category = p.stats?.category,
                cost = p.stats?.cost,
                life = p.stats?.life,
                power = p.stats?.power,
                counter = p.stats?.counter,
                block = p.stats?.block,
                colors = p.stats?.colors.orEmpty().joinToString(","),
                attributes = p.stats?.attributes.orEmpty().joinToString(","),
                nameJp = p.jp?.name?.takeIf { it.isNotBlank() },
                nameEn = p.en?.name?.takeIf { it.isNotBlank() },
                featureJp = p.jp?.feature,
                featureEn = p.en?.feature,
                effectJp = p.jp?.effect,
                effectEn = p.en?.effect,
                triggerJp = p.jp?.trigger,
                triggerEn = p.en?.trigger,
                sourceJp = p.jp?.source,
                notesEn = p.en?.notes,
                imageSource = p.image?.source,
                imageQuality = p.image?.quality,
                imageThumb = p.image?.thumb,
                imageDetail = p.image?.detail,
                position = index,
            )
        }
    }
}
