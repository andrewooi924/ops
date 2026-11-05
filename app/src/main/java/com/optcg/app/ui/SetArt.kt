package com.optcg.app.ui

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import com.optcg.app.R

/**
 * Artwork from the original app, keyed by set code. Sets released after it (and the
 * few images missing from the repository) fall back to artwork drawn in code.
 */
object SetArt {
    private val boxes = mapOf(
        "OP-01" to R.drawable.op01_box, "OP-02" to R.drawable.op02_box, "OP-03" to R.drawable.op03_box,
        "OP-04" to R.drawable.op04_box, "OP-05" to R.drawable.op05_box, "OP-06" to R.drawable.op06_box,
        "OP-07" to R.drawable.op07_box, "OP-08" to R.drawable.op08_box, "OP-09" to R.drawable.op09_box,
    )
    private val banners = mapOf(
        "OP-01" to R.drawable.op01_collection, "OP-02" to R.drawable.op02_collection, "OP-03" to R.drawable.op03_collection,
        "OP-04" to R.drawable.op04_collection, "OP-05" to R.drawable.op05_collection, "OP-06" to R.drawable.op06_collection,
        "OP-07" to R.drawable.op07_collection, "OP-08" to R.drawable.op08_collection, "OP-09" to R.drawable.op09_collection,
    )
    private val packs = mapOf("OP-01" to R.drawable.op01_pack)

    // Simulator background colours from gradient_opNN.xml (radial, fading to black).
    private val gradients = mapOf(
        "OP-01" to 0xFF00004D, "OP-02" to 0xFF4D1400, "OP-03" to 0xFF00334D, "OP-04" to 0xFF4D4B00,
        "OP-05" to 0xFF30004D, "OP-06" to 0xFF4D0000, "OP-07" to 0xFF4D3400, "OP-08" to 0xFF004D46,
        "OP-09" to 0xFF303030, "OP-10" to 0xFF2C0079,
    )
    private val fallbackGradients = listOf(
        0xFF00334D, 0xFF4D1400, 0xFF30004D, 0xFF004D46, 0xFF4D0000, 0xFF2C0079, 0xFF4D3400, 0xFF00004D,
    )

    @DrawableRes fun box(code: String?): Int? = boxes[code]
    @DrawableRes fun banner(code: String?): Int? = banners[code]
    @DrawableRes fun pack(code: String?): Int? = packs[code]

    fun gradient(code: String?): Color {
        gradients[code]?.let { return Color(it) }
        val index = (code?.hashCode() ?: 0).mod(fallbackGradients.size)
        return Color(fallbackGradients[index])
    }
}
