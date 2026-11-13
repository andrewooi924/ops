package com.optcg.app.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.optcg.app.R
import com.optcg.app.data.PrintingEntity
import com.optcg.app.sim.Pull
import com.optcg.app.sim.Slot
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.math.hypot

/** Glow colour for a revealed hit, by slot. */
fun hitColor(slot: Slot): Color = when (slot) {
    Slot.SPECIAL -> Color(0xFFD08CFF)
    Slot.PARALLEL -> Color(0xFFFFD34D)
    Slot.SEC -> Color(0xFFFF5A5A)
    Slot.SR -> Color(0xFF6FB7FF)
    Slot.L -> Color(0xFF6FD08C)
    else -> Color.Transparent
}

/**
 * The opened pack's cards in a neat, straight stack. Swipe the top card away (or tap it) for the
 * next one. Hold and drag to tilt the whole stack and catch the light; it also follows the phone's
 * tilt. The magnifier opens the top card full screen. Big hits glow and buzz when they reach the
 * top. [onDone] runs after the last card.
 */
@Composable
fun CardStack(
    pulls: List<Pull>,
    cards: Map<String, PrintingEntity>,
    isNew: List<Boolean>,
    modifier: Modifier = Modifier,
    onInspect: (PrintingEntity) -> Unit = {},
    onDone: () -> Unit,
) {
    var top by remember(pulls) { mutableIntStateOf(0) }
    val scope = rememberCoroutineScope()
    val haptics = LocalHapticFeedback.current
    val density = LocalDensity.current.density
    val offsetX = remember(pulls) { Animatable(0f) }
    val offsetY = remember(pulls) { Animatable(0f) }
    val rise = remember(pulls) { Animatable(1f) }
    val holdTiltX = remember { Animatable(0f) }
    val holdTiltY = remember { Animatable(0f) }
    var holding by remember { mutableStateOf(false) }
    val sensor = rememberDeviceTilt(range = 8f)
    val glow = rememberInfiniteTransition(label = "glow")
    val pulse by glow.animateFloat(0.55f, 1f, infiniteRepeatable(tween(900), RepeatMode.Reverse), label = "pulse")

    // The whole stack tilts together: finger while holding, otherwise the phone.
    val tiltX = (holdTiltX.value + sensor.x).coerceIn(-22f, 22f)
    val tiltY = (holdTiltY.value + sensor.y).coerceIn(-22f, 22f)

    // Cards rise out of the torn pack.
    LaunchedEffect(pulls) { rise.animateTo(0f, spring(dampingRatio = 0.8f, stiffness = Spring.StiffnessLow)) }
    LaunchedEffect(top, pulls) {
        if (top < pulls.size && pulls[top].slot.isBigHit) haptics.performHapticFeedback(HapticFeedbackType.LongPress)
    }

    fun throwTop(dx: Float, dy: Float) {
        scope.launch {
            val length = hypot(dx, dy).coerceAtLeast(1f)
            val distance = 1400f * density
            launch { offsetX.animateTo(dx / length * distance, tween(320)) }
            offsetY.animateTo(dy / length * distance, tween(320))
            offsetX.snapTo(0f); offsetY.snapTo(0f)
            top += 1
            if (top >= pulls.size) onDone()
        }
    }

    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        BoxWithConstraints(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
            val cardWidth = minOf(maxWidth * 0.78f, maxHeight * 0.9f * CARD_RATIO)
            Box(
                Modifier
                    .size(cardWidth, cardWidth / CARD_RATIO)
                    .graphicsLayer {
                        translationY = rise.value * 520f * density
                        rotationX = tiltX
                        rotationY = tiltY
                        cameraDistance = 14f * density
                        val grow = if (holding) 1.04f else 1f
                        scaleX = grow; scaleY = grow
                    }
                    .pointerInput(pulls, top) {
                        awaitEachGesture {
                            val down = awaitFirstDown()
                            val tracker = VelocityTracker()
                            tracker.addPosition(down.uptimeMillis, down.position)
                            var outcome = Gesture.HELD
                            var last: PointerInputChange = down
                            // Before the long-press timeout, moving means a swipe and lifting means a tap.
                            withTimeoutOrNull(viewConfiguration.longPressTimeoutMillis) {
                                while (true) {
                                    val change = awaitPointerEvent().changes.firstOrNull { it.id == down.id }
                                    if (change == null || !change.pressed) { outcome = Gesture.TAP; break }
                                    last = change
                                    if ((change.position - down.position).getDistance() > viewConfiguration.touchSlop) {
                                        outcome = Gesture.SWIPE
                                        break
                                    }
                                }
                            }
                            when (outcome) {
                                Gesture.TAP -> throwTop(-1f, 0f)
                                Gesture.SWIPE -> {
                                    // Swipe: the top card follows the finger, then flies off or springs back.
                                    var change = last
                                    while (change.pressed) {
                                        val delta = change.positionChange()
                                        change.consume()
                                        tracker.addPosition(change.uptimeMillis, change.position)
                                        scope.launch {
                                            offsetX.snapTo(offsetX.value + delta.x)
                                            offsetY.snapTo(offsetY.value + delta.y)
                                        }
                                        change = awaitPointerEvent().changes.firstOrNull { it.id == down.id } ?: break
                                    }
                                    val v = tracker.calculateVelocity()
                                    val far = hypot(offsetX.value, offsetY.value) > 100f * density
                                    val fast = hypot(v.x, v.y) > 900f * density
                                    if (far || fast) {
                                        throwTop(offsetX.value + v.x * 0.1f, offsetY.value + v.y * 0.1f)
                                    } else {
                                        scope.launch { offsetX.animateTo(0f, spring()) }
                                        scope.launch { offsetY.animateTo(0f, spring()) }
                                    }
                                }
                                Gesture.HELD -> {
                                    // Held: dragging now tilts the whole stack, like holding it up to the light.
                                    holding = true
                                    haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    while (true) {
                                        val change = awaitPointerEvent().changes.firstOrNull { it.id == down.id } ?: break
                                        if (!change.pressed) break
                                        val delta = change.positionChange()
                                        change.consume()
                                        scope.launch {
                                            holdTiltY.snapTo((holdTiltY.value + delta.x / density * 0.3f).coerceIn(-22f, 22f))
                                            holdTiltX.snapTo((holdTiltX.value - delta.y / density * 0.3f).coerceIn(-22f, 22f))
                                        }
                                    }
                                    holding = false
                                    scope.launch { holdTiltX.animateTo(0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy)) }
                                    scope.launch { holdTiltY.animateTo(0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy)) }
                                }
                            }
                        }
                    },
            ) {
                // Draw from the bottom of the stack up; the cards beneath peek out below, perfectly straight.
                for (index in (top until pulls.size).reversed()) {
                    val depth = index - top
                    if (depth > 3) continue
                    val pull = pulls[index]
                    val printing = cards[pull.card.id] ?: continue
                    val isTop = depth == 0
                    Box(
                        Modifier
                            .matchParentSize()
                            .offset(y = (depth * 9).dp)
                            .graphicsLayer {
                                translationX = if (isTop) offsetX.value else 0f
                                translationY = if (isTop) offsetY.value else 0f
                                rotationZ = if (isTop) offsetX.value / (40f * density) else 0f
                                val shrink = 1f - depth * 0.015f
                                scaleX = shrink; scaleY = shrink
                                alpha = if (depth == 3) 0.6f else 1f
                            },
                    ) {
                        if (isTop && pull.slot.isBigHit) {
                            // Pulsing glow behind the card.
                            Canvas(Modifier.matchParentSize().graphicsLayer { scaleX = 1.25f; scaleY = 1.2f }) {
                                drawRect(
                                    Brush.radialGradient(
                                        listOf(hitColor(pull.slot).copy(alpha = 0.75f * pulse), Color.Transparent),
                                        center = Offset(size.width / 2, size.height / 2),
                                        radius = size.maxDimension * 0.62f,
                                    ),
                                )
                            }
                        }
                        CardImage(printing, label = "${printing.code}\n${printing.displayName}", detail = true, modifier = Modifier.matchParentSize())
                        if (isTop) {
                            val shape = Modifier.matchParentSize().clip(RoundedCornerShape(12.dp))
                            if (isFoil(printing)) Gloss(tiltX, tiltY, shape)
                            Glare(tiltX, tiltY, shape)
                        }
                        if (isNew.getOrElse(index) { false }) {
                            Image(painterResource(R.drawable.ic_new), "New", Modifier.align(Alignment.TopStart).padding(2.dp).size(30.dp))
                        }
                    }
                }
            }
        }
        Row(Modifier.padding(top = 10.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(
                "${(top + 1).coerceAtMost(pulls.size)} / ${pulls.size}   ·   swipe for next · hold to tilt",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 12.sp,
            )
            Image(
                painterResource(R.drawable.icon_search),
                "Inspect",
                Modifier
                    .padding(start = 8.dp)
                    .size(36.dp)
                    .clickable { pulls.getOrNull(top)?.let { cards[it.card.id] }?.let(onInspect) }
                    .padding(8.dp),
            )
        }
    }
}

private enum class Gesture { TAP, SWIPE, HELD }
