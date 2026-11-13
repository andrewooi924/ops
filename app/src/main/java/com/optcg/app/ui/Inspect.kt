package com.optcg.app.ui

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.optcg.app.data.PrintingEntity
import kotlinx.coroutines.launch
import kotlin.math.abs

/** Cards that get the gloss. */
fun isFoil(card: PrintingEntity): Boolean = card.isParallel || card.rarity in setOf("SEC", "SP", "TR")

/**
 * Full-screen inspect view. Pinch to zoom (up to 4x) and drag to move around when zoomed;
 * otherwise dragging tilts the card in 3D. Tilting the phone tilts it too. Parallels,
 * SECs and SPs get a mild gloss that follows the tilt. Tap outside or press back to close.
 */
@Composable
fun InspectOverlay(card: PrintingEntity, onClose: () -> Unit, onOpenPage: (() -> Unit)? = null) {
    BackHandler(onBack = onClose)
    val scope = rememberCoroutineScope()
    val scale = remember { Animatable(1f) }
    val panX = remember { Animatable(0f) }
    val panY = remember { Animatable(0f) }
    val dragTiltX = remember { Animatable(0f) }
    val dragTiltY = remember { Animatable(0f) }
    val sensor = rememberDeviceTilt()
    val density = LocalDensity.current.density
    val foil = isFoil(card)

    // Degrees: finger drag wins over the phone's own tilt while touching.
    val tiltX = (dragTiltX.value + sensor.x).coerceIn(-25f, 25f)
    val tiltY = (dragTiltY.value + sensor.y).coerceIn(-25f, 25f)

    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.92f))
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onClose),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                Modifier
                    .fillMaxWidth(0.86f)
                    .widthIn(max = 520.dp)
                    .pointerInput(card.id) {
                        detectTapGestures(
                            onDoubleTap = {
                                scope.launch {
                                    val target = if (scale.value > 1.2f) 1f else 2.5f
                                    launch { scale.animateTo(target) }
                                    launch { panX.animateTo(0f) }
                                    launch { panY.animateTo(0f) }
                                }
                            },
                            onTap = { /* swallow, so tapping the card doesn't close the view */ },
                        )
                    }
                    .pointerInput(card.id) {
                        awaitEachGesture {
                            awaitFirstDown(requireUnconsumed = false)
                            do {
                                val event = awaitPointerEvent()
                                val zoom = event.calculateZoom()
                                val pan = event.calculatePan()
                                val fingers = event.changes.count { it.pressed }
                                if (fingers > 1 || scale.value > 1.01f) {
                                    val newScale = (scale.value * zoom).coerceIn(1f, 4f)
                                    scope.launch {
                                        scale.snapTo(newScale)
                                        panX.snapTo(panX.value + pan.x)
                                        panY.snapTo(panY.value + pan.y)
                                    }
                                } else if (pan != Offset.Zero) {
                                    scope.launch {
                                        dragTiltY.snapTo((dragTiltY.value + pan.x / density * 0.35f).coerceIn(-25f, 25f))
                                        dragTiltX.snapTo((dragTiltX.value - pan.y / density * 0.35f).coerceIn(-25f, 25f))
                                    }
                                }
                                event.changes.forEach { if (it.positionChanged()) it.consume() }
                            } while (event.changes.any { it.pressed })
                            // Let go: the card settles back to follow the phone.
                            scope.launch { dragTiltX.animateTo(0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy)) }
                            scope.launch { dragTiltY.animateTo(0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy)) }
                            if (scale.value <= 1.01f) {
                                scope.launch { panX.animateTo(0f) }
                                scope.launch { panY.animateTo(0f) }
                            }
                        }
                    }
                    .graphicsLayer {
                        scaleX = scale.value
                        scaleY = scale.value
                        translationX = panX.value
                        translationY = panY.value
                        rotationX = tiltX
                        rotationY = tiltY
                        cameraDistance = 14f * density
                    },
            ) {
                CardImage(card, label = card.displayName, detail = true, modifier = Modifier.fillMaxWidth())
                if (foil) Gloss(tiltX, tiltY, Modifier.matchParentSize().clip(RoundedCornerShape(12.dp)))
                Glare(tiltX, tiltY, Modifier.matchParentSize().clip(RoundedCornerShape(12.dp)))
            }
            // Captions step aside while zoomed in, so they don't sit on top of the card.
            val captions = Modifier.graphicsLayer { alpha = if (scale.value > 1.05f) 0f else 1f }
            Text(
                "${card.code}  ·  ${card.rarity.orEmpty()}${if (card.isParallel) " Parallel" else ""}",
                color = Palette.SubText,
                fontSize = 13.sp,
                modifier = captions.padding(top = 18.dp),
            )
            Text(card.displayName, color = Palette.Text, fontSize = 20.sp, modifier = captions)
            Text(
                "Pinch to zoom · drag or tilt your phone",
                color = Palette.SubText.copy(alpha = 0.7f),
                fontSize = 11.sp,
                modifier = captions.padding(top = 6.dp),
            )
            if (onOpenPage != null) {
                Box(captions.padding(top = 14.dp)) { Pill("Card details", onClick = onOpenPage) }
            }
        }
    }
}

/**
 * A mild gloss for parallels, SECs and SPs: a soft, wide band of light that slides across the
 * card as it tilts, like light catching a foil finish.
 */
@Composable
fun Gloss(tiltX: Float, tiltY: Float, modifier: Modifier = Modifier, strength: Float = 1f) {
    Canvas(modifier) {
        // -1..1 as the card tilts from one side to the other.
        val t = ((tiltY / 25f) + (tiltX / 50f)).coerceIn(-1f, 1f)
        val centre = size.width * (0.5f + t * 0.9f)
        val band = size.width * 0.55f
        drawRect(
            Brush.linearGradient(
                0f to Color.Transparent,
                0.5f to Color.White.copy(alpha = 0.16f * strength),
                1f to Color.Transparent,
                start = Offset(centre - band, 0f),
                end = Offset(centre + band, size.height * 0.6f),
            ),
            blendMode = BlendMode.Screen,
        )
    }
}

/** A soft white highlight that moves opposite to the tilt, as light reflecting off the card. */
@Composable
fun Glare(tiltX: Float, tiltY: Float, modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val cx = size.width * (0.5f - tiltY / 50f)
        val cy = size.height * (0.5f + tiltX / 50f)
        val intensity = (abs(tiltX) + abs(tiltY)) / 50f
        drawRect(
            Brush.radialGradient(
                listOf(Color.White.copy(alpha = 0.10f + 0.18f * intensity), Color.Transparent),
                center = Offset(cx, cy),
                radius = size.maxDimension * 0.6f,
            ),
        )
    }
}

class Tilt { var x by mutableFloatStateOf(0f); var y by mutableFloatStateOf(0f) }

/** The phone's tilt in degrees (small range), smoothed; zero when there's no sensor. */
@Composable
fun rememberDeviceTilt(range: Float = 12f): Tilt {
    val context = LocalContext.current
    val tilt = remember { Tilt() }
    DisposableEffect(Unit) {
        val manager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensor = manager.getDefaultSensor(Sensor.TYPE_GRAVITY) ?: manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        var baseX: Float? = null
        var baseY: Float? = null
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                // Relative to how the phone was held when the view opened.
                val gx = event.values[0]
                val gy = event.values[1]
                val bx = baseX ?: gx.also { baseX = it }
                val by = baseY ?: gy.also { baseY = it }
                val targetY = ((bx - gx) / 9.81f * 40f).coerceIn(-range, range)
                val targetX = ((by - gy) / 9.81f * 40f).coerceIn(-range, range)
                tilt.x += (targetX - tilt.x) * 0.15f
                tilt.y += (targetY - tilt.y) * 0.15f
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
        }
        if (sensor != null) manager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_GAME)
        onDispose { manager.unregisterListener(listener) }
    }
    return tilt
}
