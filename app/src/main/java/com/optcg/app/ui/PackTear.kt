package com.optcg.app.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

/**
 * Where the tear runs, as a fraction of the pack's height: through the crimped seal at the top,
 * above the set code and logos on Bandai's pack photos.
 */
private const val TEAR_LINE = 0.028f

/** How far down the pack a swipe can start and still tear it. */
private const val TEAR_ZONE = 0.14f

/**
 * How much of the width must be torn before letting go finishes the tear. Drags only start
 * counting after a few pixels of movement, so starting right at the edge isn't required.
 */
private const val TEAR_NEEDED = 0.65f

/**
 * A sealed pack you rip open by dragging across the top, from either side. A tap tears it
 * automatically. The torn strip flies off, then [onOpened] is called.
 */
@Composable
fun TearablePack(code: String?, title: String, modifier: Modifier = Modifier, hosted: String? = null, onTearStarted: () -> Unit = {}, onOpened: () -> Unit) {
    val scope = rememberCoroutineScope()
    val haptics = LocalHapticFeedback.current
    val density = LocalDensity.current.density
    var from by remember { mutableFloatStateOf(1f) }   // torn span, as fractions of the width
    var to by remember { mutableFloatStateOf(0f) }
    var opened by remember { mutableStateOf(false) }
    var tearing by remember { mutableStateOf(false) }
    val fly = remember { Animatable(0f) }               // strip flying away, 0..1
    val torn = if (to > from) to - from else 0f
    // Jagged edge: fixed per pack so it doesn't flicker.
    val jag = remember { List(41) { Random.nextFloat() } }
    val hint = rememberInfiniteTransition(label = "hint")
    val hintX by hint.animateFloat(0f, 1f, infiniteRepeatable(tween(1400, easing = LinearEasing), RepeatMode.Restart), label = "hintX")

    fun open() {
        if (opened) return
        opened = true
        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
        scope.launch {
            fly.animateTo(1f, tween(520, easing = FastOutSlowInEasing))
            onOpened()
        }
    }

    fun autoTear() {
        if (opened || tearing) return
        tearing = true
        onTearStarted()
        scope.launch {
            // Carry on from any partial tear, sweeping to the far edge.
            val anim = Animatable(if (to > from) to else 0f)
            from = 0f
            anim.animateTo(1f, tween(380, easing = LinearEasing)) { to = value }
            open()
        }
    }

    Box(
        modifier
            // Height decides the size; width follows the pack's proportions.
            .aspectRatio(PACK_RATIO, matchHeightConstraintsFirst = true)
            .pointerInput(Unit) {
                detectTapGestures(onTap = { autoTear() })
            }
            .pointerInput(Unit) {
                // Counted from the first touch (not from where the drag passes the touch slop),
                // so a swipe from one edge to the other tears the whole width.
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    if (opened || down.position.y > size.height * TEAR_ZONE) return@awaitEachGesture
                    val startX = (down.position.x / size.width).coerceIn(0f, 1f)
                    var moved = false
                    while (true) {
                        val event = awaitPointerEvent()
                        val change = event.changes.firstOrNull { it.id == down.id } ?: break
                        if (!change.pressed) break
                        val x = (change.position.x / size.width).coerceIn(0f, 1f)
                        if (!moved && abs(x - startX) * size.width > viewConfiguration.touchSlop) {
                            moved = true
                            onTearStarted()
                            if (to <= from) { from = startX; to = startX }
                        }
                        if (moved && !opened) {
                            change.consume()
                            from = min(from, min(x, startX))
                            to = max(to, max(x, startX))
                            if (to - from >= 0.999f) open()
                        }
                    }
                    if (moved && !opened && to - from >= TEAR_NEEDED) {
                        // Close enough: finish the tear for them.
                        scope.launch {
                            val a = Animatable(0f)
                            val f0 = from
                            val t0 = to
                            a.animateTo(1f, tween(160)) { from = f0 * (1 - value); to = t0 + (1 - t0) * value }
                            open()
                        }
                    }
                }
            },
    ) {
        // Body: everything below the tear line. It drops away a little as the pack opens.
        Box(
            Modifier
                .fillMaxSize()
                .drawWithContent { clipRect(top = size.height * TEAR_LINE) { this@drawWithContent.drawContent() } }
                .graphicsLayer {
                    translationY = fly.value * 40f * density
                    alpha = 1f - fly.value * 0.15f
                },
        ) { PackFace(code, title, Modifier.fillMaxSize(), hosted) }

        // Strip: above the tear line. It lifts from the torn side and flies off.
        Box(
            Modifier
                .fillMaxSize()
                .drawWithContent { clipRect(bottom = size.height * TEAR_LINE + 1f) { this@drawWithContent.drawContent() } }
                .graphicsLayer {
                    val lift = if (opened) 1f else torn * 0.15f
                    transformOrigin = TransformOrigin(if (from > 0.5f) 0f else 1f, TEAR_LINE)
                    rotationZ = (if (from > 0.5f) 1f else -1f) * (lift * 6f + fly.value * 35f)
                    translationY = -fly.value * 260f * density
                    translationX = (if (from > 0.5f) -1f else 1f) * fly.value * 120f * density
                    alpha = 1f - fly.value
                },
        ) { PackFace(code, title, Modifier.fillMaxSize(), hosted) }

        // The tear: a jagged white edge over the torn span, and a perforation hint before it starts.
        Canvas(Modifier.fillMaxSize()) {
            val y = size.height * TEAR_LINE
            if (!opened && torn == 0f) {
                drawLine(
                    Color.White.copy(alpha = 0.55f),
                    Offset(0f, y),
                    Offset(size.width, y),
                    strokeWidth = 1.5f * density,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f * density, 5f * density)),
                )
                // A moving glint showing which way to drag.
                val gx = size.width * hintX
                drawCircle(Color.White.copy(alpha = 0.8f * (1f - hintX)), radius = 5f * density, center = Offset(gx, y))
            }
            if (torn > 0f && fly.value < 1f) {
                val path = Path()
                val steps = jag.size - 1
                var first = true
                for (i in 0..steps) {
                    val fx = i / steps.toFloat()
                    if (fx < from || fx > to) continue
                    val px = fx * size.width
                    val py = y + (jag[i] - 0.5f) * 5f * density
                    if (first) { path.moveTo(px, py); first = false } else path.lineTo(px, py)
                }
                drawPath(path, Color.White.copy(alpha = 1f - fly.value), style = Stroke(2.5f * density))
            }
        }
    }
}
