package com.optcg.app.ui

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// From the launcher icon background (#648DD4).
private val Brand = Color(0xFF648DD4)
private val BrandDark = Color(0xFF2F5AA6)

private val Light = lightColorScheme(primary = BrandDark, secondary = Brand, tertiary = Color(0xFFB3261E))
private val Dark = darkColorScheme(primary = Color(0xFFAFC6FF), secondary = Brand, tertiary = Color(0xFFF2B8B5))

@Composable
fun PieceLabTheme(content: @Composable () -> Unit) {
    val dark = isSystemInDarkTheme()
    val context = LocalContext.current
    val colors = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> if (dark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        dark -> Dark
        else -> Light
    }
    MaterialTheme(colorScheme = colors, content = content)
}

/** Colours for rarity labels, readable in light and dark themes. */
fun rarityColor(rarity: String?, parallel: Boolean): Color = when {
    parallel -> Color(0xFFB8860B)
    rarity == "SEC" -> Color(0xFFC62828)
    rarity == "SP" || rarity == "TR" -> Color(0xFF8E24AA)
    rarity == "SR" -> Color(0xFF1E88E5)
    rarity == "L" -> Color(0xFF2E7D32)
    else -> Color(0xFF757575)
}
