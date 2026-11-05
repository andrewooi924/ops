package com.optcg.app.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import com.optcg.app.R

/** Colours from the original 1.x app (activity_main.xml, nav_text_selector.xml, colors.xml). */
object Palette {
    val Background = Color(0xFF201C1C)
    val NavBar = Color(0xFF303030)
    val Surface = Color(0xFF2A2626)
    val Selected = Color(0xFFB77EE4)
    val Unselected = Color(0xFFBCBCBC)
    val Text = Color.White
    val SubText = Color(0xFFBCBCBC)
    val Button = Color(0xFF6A4A9C)
    val New = Color(0xFFE53935)
    val Up = Color(0xFF2ECC40)
    val Down = Color(0xFFE53935)

    val CardRed = Color(0xFFB91719)
    val CardGreen = Color(0xFF007A60)
    val CardBlue = Color(0xFF0077AA)
    val CardPurple = Color(0xFF8F3788)
    val CardBlack = Color(0xFF3A3942)
    val CardYellow = Color(0xFFF7E74A)
}

val YdGothic = FontFamily(Font(R.font.ydgothic))

private val Base = Typography()
private fun TextStyle.yd() = copy(fontFamily = YdGothic)

private val AppTypography = Typography(
    displayLarge = Base.displayLarge.yd(), displayMedium = Base.displayMedium.yd(), displaySmall = Base.displaySmall.yd(),
    headlineLarge = Base.headlineLarge.yd(), headlineMedium = Base.headlineMedium.yd(), headlineSmall = Base.headlineSmall.yd(),
    titleLarge = Base.titleLarge.yd(), titleMedium = Base.titleMedium.yd(), titleSmall = Base.titleSmall.yd(),
    bodyLarge = Base.bodyLarge.yd(), bodyMedium = Base.bodyMedium.yd(), bodySmall = Base.bodySmall.yd(),
    labelLarge = Base.labelLarge.yd(), labelMedium = Base.labelMedium.yd(), labelSmall = Base.labelSmall.yd(),
)

private val Colors = darkColorScheme(
    primary = Palette.Selected,
    onPrimary = Color.White,
    secondary = Palette.Button,
    tertiary = Palette.New,
    background = Palette.Background,
    onBackground = Palette.Text,
    surface = Palette.Background,
    onSurface = Palette.Text,
    surfaceVariant = Palette.Surface,
    onSurfaceVariant = Palette.SubText,
    surfaceContainer = Palette.NavBar,
    surfaceContainerHigh = Palette.Surface,
    outline = Color(0xFF5A5454),
)

/** The original app was dark-only, so this is too. */
@Composable
fun PieceLabTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = Colors, typography = AppTypography, content = content)
}

/** Colours for rarity labels on the dark background. */
fun rarityColor(rarity: String?, parallel: Boolean): Color = when {
    parallel -> Color(0xFFE0B341)
    rarity == "SEC" -> Color(0xFFFF6B6B)
    rarity == "SP" || rarity == "TR" -> Color(0xFFD08CFF)
    rarity == "SR" -> Color(0xFF6FB7FF)
    rarity == "L" -> Color(0xFF6FD08C)
    else -> Palette.SubText
}
