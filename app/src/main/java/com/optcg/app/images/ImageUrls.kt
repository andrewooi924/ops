package com.optcg.app.images

/** Anything with a card image. */
interface HasImage {
    val id: String
    val imageSource: String?
    val imageThumb: String?
    val imageDetail: String?
}

/**
 * Picks image URLs. Hosted, resized images ([base] + file name) are preferred; until image
 * hosting exists the app falls back to the original image on Bandai's site. Dons that only
 * exist inside the don PDF have no image until hosting is set up.
 */
object ImageUrls {
    fun thumb(base: String, card: HasImage): String? =
        hosted(base, card.imageThumb) ?: original(card.imageSource)

    fun detail(base: String, card: HasImage): String? =
        hosted(base, card.imageDetail) ?: original(card.imageSource)

    private fun hosted(base: String, file: String?): String? =
        if (base.isNotBlank() && !file.isNullOrBlank()) base.trimEnd('/') + "/" + file else null

    private fun original(source: String?): String? {
        if (source.isNullOrBlank()) return null
        val path = source.substringBefore('?').lowercase()
        return if (path.endsWith(".pdf")) null else source
    }
}
