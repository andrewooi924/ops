package com.optcg.app.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape

/** Pack width / height, from the original OP-01 pack art (cropped to the pack). */
const val PACK_RATIO = 251f / 500f
const val BOX_RATIO = 325f / 500f

private fun accent(base: Color) = lerp(base, Color.White, 0.35f)

/** Square set banner for the Collection grid, like the original opNN_collection images. */
@Composable
fun SetBanner(code: String?, title: String, modifier: Modifier = Modifier) {
    val art = SetArt.banner(code)
    Box(modifier.aspectRatio(1f).clip(RoundedCornerShape(2.dp))) {
        if (art != null) {
            Image(painterResource(art), title, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
        } else {
            val base = SetArt.gradient(code)
            BoxWithConstraints(
                Modifier
                    .fillMaxSize()
                    .background(Brush.linearGradient(listOf(accent(base), base, lerp(base, Color.Black, 0.4f)))),
                contentAlignment = Alignment.Center,
            ) {
                // Text scales with the banner, so it fits both the grid and the smaller set header.
                val big = with(LocalDensity.current) { (maxWidth * 0.17f).toSp() }
                LogoBlock(title, big = big)
            }
        }
    }
}

/** Booster box for the Home row, like the original opNN_box images. */
@Composable
fun BoxArt(code: String?, title: String, modifier: Modifier = Modifier) {
    val art = SetArt.box(code)
    if (art != null) {
        Image(painterResource(art), title, contentScale = ContentScale.Fit, modifier = modifier.aspectRatio(BOX_RATIO))
        return
    }
    val base = SetArt.gradient(code)
    Box(modifier.aspectRatio(BOX_RATIO)) {
        Canvas(Modifier.fillMaxSize()) {
            // A box drawn in perspective: front face, side face and lid.
            val w = size.width
            val h = size.height
            val depth = w * 0.18f
            val lid = h * 0.06f
            val front = Path().apply {
                moveTo(0f, lid); lineTo(w - depth, lid + depth * 0.25f); lineTo(w - depth, h); lineTo(0f, h - depth * 0.25f); close()
            }
            val side = Path().apply {
                moveTo(w - depth, lid + depth * 0.25f); lineTo(w, lid); lineTo(w, h - depth * 0.35f); lineTo(w - depth, h); close()
            }
            val top = Path().apply {
                moveTo(0f, lid); lineTo(depth, 0f); lineTo(w, 0f); lineTo(w, lid); lineTo(w - depth, lid + depth * 0.25f); close()
            }
            drawPath(front, Brush.verticalGradient(listOf(accent(base), base, lerp(base, Color.Black, 0.3f))))
            drawPath(side, lerp(base, Color.Black, 0.45f))
            drawPath(top, accent(base))
            drawPath(front, Color.White.copy(alpha = 0.15f), style = Stroke(2f))
        }
        Column(
            Modifier.fillMaxSize().padding(start = 8.dp, end = 30.dp, top = 30.dp, bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(code ?: "", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold, fontFamily = YdGothic)
            Box(Modifier.weight(1f), contentAlignment = Alignment.Center) { LogoBlock(title, big = 22.sp) }
        }
    }
}

/** A sealed pack, drawn when there's no original pack art. Crimped at the top and bottom. */
@Composable
fun DrawnPack(code: String?, title: String, modifier: Modifier = Modifier) {
    val base = SetArt.gradient(code)
    Box(modifier.aspectRatio(PACK_RATIO)) {
        Canvas(Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val crimp = h * 0.02f
            drawRect(
                Brush.linearGradient(
                    listOf(accent(base), base, lerp(base, Color.White, 0.55f), base, lerp(base, Color.Black, 0.35f)),
                    start = Offset(0f, 0f), end = Offset(w, h),
                ),
                topLeft = Offset(0f, crimp), size = androidx.compose.ui.geometry.Size(w, h - 2 * crimp),
            )
            val teeth = 40
            val step = w / teeth
            for (edge in listOf(0f, h - crimp)) {
                val path = Path()
                for (i in 0 until teeth) {
                    val x = i * step
                    path.moveTo(x, edge + crimp)
                    path.lineTo(x + step / 2, edge)
                    path.lineTo(x + step, edge + crimp)
                }
                drawPath(path, lerp(base, Color.White, 0.25f))
            }
            // Foil streak.
            drawRect(
                Brush.linearGradient(
                    listOf(Color.Transparent, Color.White.copy(alpha = 0.18f), Color.Transparent),
                    start = Offset(w * 0.1f, 0f), end = Offset(w * 0.9f, h),
                ),
            )
        }
        Column(
            Modifier.fillMaxSize().padding(horizontal = 14.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(code ?: "", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold, fontFamily = YdGothic)
            Box(Modifier.weight(1f), contentAlignment = Alignment.Center) { LogoBlock(title, big = 34.sp) }
        }
    }
}

@Composable
private fun LogoBlock(title: String, big: androidx.compose.ui.unit.TextUnit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("ONE PIECE", color = Color.White, fontSize = big, fontWeight = FontWeight.Bold, fontFamily = YdGothic)
        Text("CARD GAME", color = Color.White.copy(alpha = 0.85f), fontSize = big * 0.38f, fontFamily = YdGothic)
        Text(
            title.uppercase(),
            color = Color.White,
            fontSize = big * 0.45f,
            textAlign = TextAlign.Center,
            fontFamily = YdGothic,
            lineHeight = big * 0.55f,
            modifier = Modifier.padding(top = 10.dp, start = 6.dp, end = 6.dp),
        )
    }
}

/** The Berry currency mark: a B with two vertical strokes, drawn so no image is needed. */
@Composable
fun BerryIcon(size: Dp = 22.dp, color: Color = Color.White) {
    Box(Modifier.size(size), contentAlignment = Alignment.Center) {
        Text("B", color = color, fontSize = (size.value * 0.9f).sp, fontWeight = FontWeight.Bold, fontFamily = YdGothic)
        Canvas(Modifier.size(size)) {
            val stroke = this.size.width * 0.08f
            for (x in listOf(0.42f, 0.58f)) {
                drawLine(color, Offset(this.size.width * x - this.size.width * 0.06f, 0f), Offset(this.size.width * x - this.size.width * 0.06f, this.size.height), strokeWidth = stroke)
            }
        }
    }
}
