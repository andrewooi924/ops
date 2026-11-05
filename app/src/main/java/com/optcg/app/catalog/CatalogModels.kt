package com.optcg.app.catalog

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.io.InputStream
import java.util.zip.GZIPInputStream

/** The catalog.json written by the catalog builder (catalog/optcg_catalog/build.py). */
@Serializable
data class Catalog(
    val version: String,
    @SerialName("generated_at") val generatedAt: String,
    @SerialName("schema_version") val schemaVersion: Int,
    val sets: List<CatalogSet>,
    val printings: List<CatalogPrinting>,
    @SerialName("pull_profiles") val pullProfiles: PullProfiles,
)

@Serializable
data class CatalogSet(
    val id: String,
    val code: String? = null,
    val kind: String,
    @SerialName("name_jp") val nameJp: String,
    @SerialName("name_en") val nameEn: String? = null,
)

@Serializable
data class CatalogPrinting(
    val id: String,
    val code: String,
    val kind: String,
    val variant: Int? = null,
    val rarity: String? = null,
    val status: String,
    val stats: Stats? = null,
    val jp: CardText? = null,
    val en: CardText? = null,
    val series: List<String> = emptyList(),
    val image: CardImage? = null,
)

@Serializable
data class Stats(
    val category: String? = null,
    val cost: Int? = null,
    val life: Int? = null,
    val power: Int? = null,
    val counter: Int? = null,
    val colors: List<String> = emptyList(),
    val attributes: List<String> = emptyList(),
    val block: Int? = null,
)

@Serializable
data class CardText(
    val name: String? = null,
    val feature: String? = null,
    val effect: String? = null,
    val trigger: String? = null,
    val source: String? = null,
    val notes: String? = null,
)

@Serializable
data class CardImage(
    val source: String? = null,
    val quality: String? = null,
    val thumb: String? = null,
    val detail: String? = null,
    val width: Int? = null,
    val height: Int? = null,
)

@Serializable
data class PullProfiles(
    val note: String? = null,
    val profiles: Map<String, PullProfile> = emptyMap(),
    /** Set kind (booster, extra, ...) to profile name; null means no simulator. */
    val defaults: Map<String, String?> = emptyMap(),
    /** Per-set override by set id or set code, taking precedence over [defaults]. */
    val sets: Map<String, String?> = emptyMap(),
) {
    fun profileFor(setId: String, setCode: String?, kind: String): PullProfile? {
        val name = when {
            setId in sets -> sets[setId]
            setCode != null && setCode in sets -> sets[setCode]
            else -> defaults[kind]
        }
        return name?.let { profiles[it] }
    }
}

@Serializable
data class PullProfile(
    val label: String,
    val official: Boolean = false,
    @SerialName("packs_per_box") val packsPerBox: Int,
    @SerialName("cards_per_pack") val cardsPerPack: Int,
    @SerialName("boxes_per_case") val boxesPerCase: Int = 12,
    /** Rarity to an inclusive [min, max] count per box. */
    @SerialName("per_box") val perBox: Map<String, List<Int>> = emptyMap(),
    @SerialName("box_patterns") val boxPatterns: List<BoxPattern> = emptyList(),
    val filler: Filler? = null,
    @SerialName("special_rares") val specialRares: SpecialRares? = null,
    val sources: List<String> = emptyList(),
)

@Serializable
data class BoxPattern(
    val weight: Int,
    val parallel: Int = 0,
    @SerialName("SEC") val sec: Int = 0,
)

@Serializable
data class Filler(val weights: Map<String, Int> = emptyMap())

@Serializable
data class SpecialRares(
    val rarities: List<String> = emptyList(),
    @SerialName("per_case") val perCase: Double? = null,
)

object CatalogJson {
    val json = Json { ignoreUnknownKeys = true }

    @OptIn(ExperimentalSerializationApi::class)
    fun decode(stream: InputStream): Catalog = stream.use { json.decodeFromStream(Catalog.serializer(), it) }

    fun decodeGzip(stream: InputStream): Catalog = decode(GZIPInputStream(stream.buffered()))
}

/** Highest schema version this app understands; newer catalogs wait for an app update. */
const val SUPPORTED_SCHEMA_VERSION = 1
