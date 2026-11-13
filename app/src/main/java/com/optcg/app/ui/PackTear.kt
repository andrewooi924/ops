package com.optcg.app.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import com.optcg.app.container
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * Where the tear runs, as a fraction of the pack's height. The strip you tear off is the whole top
 * band of the pack (set code and logos), so it reads clearly as a piece coming away.
 */
private const val TEAR_LINE = 0.13f

/** How far down the pack a swipe can start and still tear it. */
private const val TEAR_ZONE = 0.32f

/** How much of the width must be cut before letting go finishes the tear. */
private const val TEAR_NEEDED = 0.6f

/**
 * A sealed pack you slice open by dragging across the top, from either side. A tap slices it
 * automatically. A bright cut follows your finger, then the top strip snaps off with a flash
 * and [onOpened] is called.
 */
@Composable
fun TearablePack(code: String?, title: String, modifier: Modifier = Modifier, hosted: String? = null, onTearStarted: () -> Unit = {}, onOpened: () -> Unit) {
    val scope = rememberCoroutineScope()
    val haptics = LocalHapticFeedback.current
    val density = LocalDensity.current.density
    val sounds = LocalContext.current.container.sounds
    var from by remember { mutableFloatStateOf(1f) }   // cut span, as fractions of the width
    var to by remember { mutableFloatStateOf(0f) }
    var head by remember { mutableFloatStateOf(-1f) }  // where the cut is being made, or -1
    var opened by remember { mutableStateOf(false) }
    var tearing by remember { mutableStateOf(false) }
    var auto by remember { mutableStateOf(false) }       // a tap is slicing it
    var ticks by remember { mutableIntStateOf(0) }
    val fly = remember { Animatable(0f) }               // strip flying away, 0..1
    val flash = remember { Animatable(0f) }             // light from the opened pack, 0..1
    val bounce = remember { Animatable(1f) }
    val torn = if (to > from) to - from else 0f
    val hint = rememberInfiniteTransition(label = "hint")
    val hintX by hint.animateFloat(0f, 1f, infiniteRepeatable(tween(1400, easing = LinearEasing), RepeatMode.Restart), label = "hintX")

    fun started() {
        if (tearing) return
        tearing = true
        onTearStarted()
        sounds.slice()
    }

    /** A light tick every tenth of the width, so the cut feels like it's going through something. */
    fun tick() {
        val now = (torn * 10).toInt()
        if (now > ticks) {
            ticks = now
            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        }
    }

    fun open() {
        if (opened) return
        opened = true
        head = -1f
        sounds.pop()
        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
        scope.launch { flash.snapTo(1f); flash.animateTo(0f, tween(650)) }
        scope.launch {
            bounce.animateTo(1.05f, tween(90))
            bounce.animateTo(1f, spring(dampingRatio = 0.45f, stiffness = Spring.StiffnessMedium))
        }
        scope.launch {
            fly.animateTo(1f, tween(560, easing = FastOutSlowInEasing))
            onOpened()
        }
    }

    fun autoTear() {
        if (opened || tearing) return
        auto = true
        started()
        scope.launch {
            // Sweep the cut across the whole width.
            val anim = Animatable(0f)
            from = 0f
            anim.animateTo(1f, tween(340, easing = LinearEasing)) { to = value; head = value; tick() }
            open()
        }
    }

    Box(
        modifier
            // Height decides the size; width follows the pack's proportions.
            .aspectRatio(PACK_RATIO, matchHeightConstraintsFirst = true)
            .graphicsLayer { scaleX = bounce.value; scaleY = bounce.value }
            .pointerInput(Unit) {
                detectTapGestures(onTap = { autoTear() })
            }
            .pointerInput(Unit) {
                // Counted from the first touch (not from where the drag passes the touch slop),
                // so a swipe from one edge to the other cuts the whole width.
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    if (opened || auto || down.position.y > size.height * TEAR_ZONE) return@awaitEachGesture
                    val startX = (down.position.x / size.width).coerceIn(0f, 1f)
                    var moved = false
                    while (true) {
                        val event = awaitPointerEvent()
                        val change = event.changes.firstOrNull { it.id == down.id } ?: break
                        if (!change.pressed) break
                        val x = (change.position.x / size.width).coerceIn(0f, 1f)
                        if (!moved && abs(x - startX) * size.width > viewConfiguration.touchSlop) {
                            moved = true
                            started()
                            if (to <= from) { from = startX; to = startX }
                        }
                        if (moved && !opened) {
                            change.consume()
                            from = min(from, min(x, startX))
                            to = max(to, max(x, startX))
                            head = x
                            tick()
                            if (to - from >= 0.999f) open()
                        }
                    }
                    if (moved && !opened) {
                        if (to - from >= TEAR_NEEDED) {
                            // Close enough: finish the cut for them.
                            scope.launch {
                                val a = Animatable(0f)
                                val f0 = from
                                val t0 = to
                                a.animateTo(1f, tween(140)) {
                                    from = f0 * (1 - value); to = t0 + (1 - t0) * value
                                    head = if (f0 > 1 - t0) from else to
                                }
                                open()
                            }
                        } else {
                            head = -1f
                            tearing = false
                        }
                    }
                }
            },
    ) {
        // Body: everything below the tear line. It drops a little as the strip comes away.
        Box(
            Modifier
                .fillMaxSize()
                .drawWithContent { clipRect(top = size.height * TEAR_LINE) { this@drawWithContent.drawContent() } }
                .graphicsLayer { translationY = fly.value * 28f * density },
        ) { PackFace(code, title, Modifier.fillMaxSize(), hosted) }

        // Strip: the top band. It lifts at the cut, then snaps up and spins away.
        Box(
            Modifier
                .fillMaxSize()
                .drawWithContent { clipRect(bottom = size.height * TEAR_LINE + 1f) { this@drawWithContent.drawContent() } }
                .graphicsLayer {
                    val fromLeft = from > 0.5f || (from <= 0.5f && to < 0.5f)
                    val lift = if (opened) 1f else torn
                    transformOrigin = TransformOrigin(if (fromLeft) 0f else 1f, TEAR_LINE)
                    rotationZ = (if (fromLeft) 1f else -1f) * (lift * 3f + fly.value * 50f)
                    translationY = -lift * 3f * density - fly.value * 340f * density
                    translationX = (if (fromLeft) -1f else 1f) * fly.value * 150f * density
                    alpha = 1f - fly.value * fly.value
                },
        ) { PackFace(code, title, Modifier.fillMaxSize(), hosted) }

        Canvas(Modifier.fillMaxSize()) {
            val y = size.height * TEAR_LINE
            if (!tearing && !opened) {
                // Where to cut: a dashed guide with a glint running along it.
                drawLine(
                    Color.White.copy(alpha = 0.75f),
                    Offset(0f, y),
                    Offset(size.width, y),
                    strokeWidth = 2f * density,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f * density, 6f * density)),
                )
                val gx = size.width * hintX
                drawCircle(
                    Brush.radialGradient(listOf(Color.White.copy(alpha = 0.9f * (1f - hintX)), Color.Transparent), center = Offset(gx, y), radius = 14f * density),
                    radius = 14f * density,
                    center = Offset(gx, y),
                )
            }
            if (torn > 0f && fly.value < 0.6f) {
                // The cut: a crisp white line with a soft glow around it.
                val fade = 1f - fly.value / 0.6f
                val a = Offset(from * size.width, y)
                val b = Offset(to * size.width, y)
                drawLine(Color.White.copy(alpha = 0.18f * fade), a, b, strokeWidth = 16f * density, cap = StrokeCap.Round)
                drawLine(Color.White.copy(alpha = 0.35f * fade), a, b, strokeWidth = 7f * density, cap = StrokeCap.Round)
                drawLine(Color.White.copy(alpha = fade), a, b, strokeWidth = 2.5f * density, cap = StrokeCap.Round)
            }
            if (head >= 0f && !opened) {
                // A spark where the cut is being made.
                val c = Offset(head * size.width, y)
                drawCircle(Brush.radialGradient(listOf(Color.White, Color(0x88FFE9A8), Color.Transparent), center = c, radius = 26f * density), radius = 26f * density, center = c)
            }
            if (flash.value > 0f) {
                // Light bursting out of the opened pack.
                val c = Offset(size.width / 2, y)
                val r = size.width * (0.6f + 0.6f * (1f - flash.value))
                drawRect(Brush.radialGradient(listOf(Color.White.copy(alpha = 0.85f * flash.value), Color.Transparent), center = c, radius = r))
            }
        }
    }
}
