package com.optcg.app.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.optcg.app.images.HasImage

/** Set ids the app adds for cards that belong to no series in the catalog. */
const val VIRTUAL_DONS = "don"
const val VIRTUAL_ANNOUNCED = "announced"

@Entity(tableName = "sets")
data class SetEntity(
    @PrimaryKey val id: String,
    val code: String?,
    val kind: String,
    val nameJp: String,
    val nameEn: String?,
    /** Position in the catalog (newest first within each kind). */
    val position: Int,
)

@Entity(tableName = "printings", indices = [Index("setId"), Index("code")])
data class PrintingEntity(
    @PrimaryKey override val id: String,
    val code: String,
    val kind: String,
    val variant: Int?,
    val rarity: String?,
    val status: String,
    /**
     * First series the card is listed under, or [VIRTUAL_DONS] / [VIRTUAL_ANNOUNCED]
     * for cards that belong to no series.
     */
    val setId: String,
    val category: String?,
    val cost: Int?,
    val life: Int?,
    val power: Int?,
    val counter: Int?,
    val block: Int?,
    val colors: String,
    val attributes: String,
    val nameJp: String?,
    val nameEn: String?,
    val featureJp: String?,
    val featureEn: String?,
    val effectJp: String?,
    val effectEn: String?,
    val triggerJp: String?,
    val triggerEn: String?,
    val sourceJp: String?,
    val notesEn: String?,
    override val imageSource: String?,
    val imageQuality: String?,
    override val imageThumb: String?,
    override val imageDetail: String?,
    val position: Int,
) : HasImage {
    val isParallel: Boolean get() = variant != null
    val displayName: String get() = nameEn ?: nameJp ?: code
}

/** Cards the user owns. Kept separate from the catalog so catalog updates never touch it. */
@Entity(tableName = "owned")
data class OwnedEntity(
    @PrimaryKey val printingId: String,
    val count: Int,
    @ColumnInfo(defaultValue = "0") val updatedAt: Long,
)

@Entity(tableName = "wishlist")
data class WishlistEntity(
    @PrimaryKey val printingId: String,
    val addedAt: Long,
)

/** A printing with the user's count and wishlist flag. */
data class CardRow(
    override val id: String,
    val code: String,
    val kind: String,
    val variant: Int?,
    val rarity: String?,
    val status: String,
    val setId: String,
    val nameJp: String?,
    val nameEn: String?,
    override val imageSource: String?,
    override val imageThumb: String?,
    override val imageDetail: String?,
    val imageQuality: String?,
    val owned: Int,
    val wished: Boolean,
) : HasImage {
    val displayName: String get() = nameEn ?: nameJp ?: code
}

data class SetProgress(
    val id: String,
    val code: String?,
    val kind: String,
    val nameJp: String,
    val nameEn: String?,
    val total: Int,
    val owned: Int,
)
