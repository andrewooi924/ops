package com.optcg.app.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.optcg.app.R
import com.optcg.app.data.PrintingEntity
import com.optcg.app.sim.Pull
import com.optcg.app.sim.Slot
import kotlinx.coroutines.launch
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
 * The opened pack's cards in a stack, best card at the bottom. Flick the top card away in any
 * direction, or tap it to send it off like the original app did. Big hits glow, shine and
 * buzz when they reach the top. [onDone] runs after the last card.
 */
@Composable
fun CardStack(
    pulls: List<Pull>,
    cards: Map<String, PrintingEntity>,
    isNew: List<Boolean>,
    modifier: Modifier = Modifier,
    onDone: () -> Unit,
) {
    var top by remember(pulls) { mutableIntStateOf(0) }
    val scope = rememberCoroutineScope()
    val haptics = LocalHapticFeedback.current
    val density = LocalDensity.current.density
    val offsetX = remember(pulls) { Animatable(0f) }
    val offsetY = remember(pulls) { Animatable(0f) }
    val rotation = remember(pulls) { Animatable(0f) }
    val rise = remember(pulls) { Animatable(1f) }
    val glow = rememberInfiniteTransition(label = "glow")
    val pulse by glow.animateFloat(0.55f, 1f, infiniteRepeatable(tween(900), RepeatMode.Reverse), label = "pulse")
    val shimmer by glow.animateFloat(-18f, 18f, infiniteRepeatable(tween(2200, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "shimmer")

    // Cards rise out of the torn pack.
    LaunchedEffect(pulls) { rise.animateTo(0f, spring(dampingRatio = 0.8f, stiffness = Spring.StiffnessLow)) }
    LaunchedEffect(top, pulls) {
        if (top < pulls.size && pulls[top].slot.isBigHit) haptics.performHapticFeedback(HapticFeedbackType.LongPress)
    }

    fun throwTop(dx: Float, dy: Float) {
        scope.launch {
            val length = hypot(dx, dy).coerceAtLeast(1f)
            val distance = 1600f * density
            launch { offsetX.animateTo(dx / length * distance, tween(420)) }
            launch { rotation.animateTo(if (dx < 0) -60f else 60f, tween(420)) }
            offsetY.animateTo(dy / length * distance, tween(420))
            offsetX.snapTo(0f); offsetY.snapTo(0f); rotation.snapTo(0f)
            top += 1
            if (top >= pulls.size) onDone()
        }
    }

    BoxWithConstraints(modifier, contentAlignment = Alignment.Center) {
        val cardWidth = minOf(maxWidth * 0.78f, maxHeight * 0.62f * CARD_RATIO / 0.88f)
        // Draw from the bottom of the stack up, showing a few cards' edges behind the top one.
        for (index in (top until pulls.size).reversed()) {
            val depth = index - top
            if (depth > 3) continue
            val pull = pulls[index]
            val printing = cards[pull.card.id] ?: continue
            val isTop = depth == 0
            Box(
                Modifier
                    .size(cardWidth, cardWidth / CARD_RATIO)
                    .offset(y = (depth * 7).dp)
                    .graphicsLayer {
                        translationY = rise.value * 520f * density + if (isTop) offsetY.value else 0f
                        translationX = if (isTop) offsetX.value else 0f
                        rotationZ = if (isTop) rotation.value + offsetX.value / (24f * density) else (if (depth % 2 == 0) 1.5f else -1.5f) * depth
                        val shrink = 1f - depth * 0.025f
                        scaleX = shrink; scaleY = shrink
                    }
                    .then(
                        if (isTop) Modifier
                            .pointerInput(top) { detectTapGestures(onTap = { throwTop(-1f, 0.6f) }) }
                            .pointerInput(top) {
                                val tracker = VelocityTracker()
                                detectDragGestures(
                                    onDragStart = { tracker.resetTracking() },
                                    onDrag = { change, amount ->
                                        change.consume()
                                        tracker.addPosition(change.uptimeMillis, change.position)
                                        scope.launch {
                                            offsetX.snapTo(offsetX.value + amount.x)
                                            offsetY.snapTo(offsetY.value + amount.y)
                                        }
                                    },
                                    onDragEnd = {
                                        val v = tracker.calculateVelocity()
                                        val far = hypot(offsetX.value, offsetY.value) > 110f * density
                                        val fast = hypot(v.x, v.y) > 900f * density
                                        if (far || fast) {
                                            throwTop(offsetX.value + v.x * 0.1f, offsetY.value + v.y * 0.1f)
                                        } else {
                                            scope.launch { offsetX.animateTo(0f, spring()) }
                                            scope.launch { offsetY.animateTo(0f, spring()) }
                                        }
                                    },
                                )
                            }
                        else Modifier,
                    ),
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
                CardImage(printing, label = "${printing.code}\n${printing.displayName}", detail = true, modifier = Modifier.fillMaxWidth())
                if (isTop && isFoil(printing)) {
                    FoilShine(shimmer * 0.3f, shimmer, Modifier.matchParentSize().clip(RoundedCornerShape(12.dp)), strength = 0.8f)
                }
                if (isNew.getOrElse(index) { false }) {
                    Image(painterResource(R.drawable.ic_new), "New", Modifier.align(Alignment.TopStart).padding(2.dp).size(30.dp))
                }
            }
        }
    }
}
