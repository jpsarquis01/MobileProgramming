package com.studio.gladetowns.core.ui.render

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.studio.gladetowns.core.domain.model.time.TimeOfDay
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * Animated lighting + atmosphere, drawn as a SEPARATE overlay above the town
 * Canvas. Keeping the infinite firefly transition and the scrim animation here
 * means the heavy [drawTown] Canvas is never re-rendered by the animation —
 * a key performance decision.
 */
@Composable
fun AtmosphereOverlay(palette: TimeOfDayPalette, modifier: Modifier = Modifier) {
    val scrim by animateColorAsState(palette.ambientScrim, tween(1200), label = "scrim")
    val alpha by animateFloatAsState(palette.scrimAlpha, tween(1200), label = "scrimAlpha")
    val vignette by animateColorAsState(palette.vignette, tween(1200), label = "vignette")

    val fireflies = remember { List(14) { Firefly(Random.nextFloat(), Random.nextFloat(), Random.nextFloat()) } }
    val twinkle = rememberInfiniteTransition(label = "atmosphere")
    val t by twinkle.animateFloat(
        0f, 1f, infiniteRepeatable(tween(4000), RepeatMode.Reverse), label = "twinkle",
    )

    Canvas(modifier) {
        drawRect(scrim.copy(alpha = alpha))
        drawRect(
            Brush.radialGradient(
                colors = listOf(Color.Transparent, vignette),
                center = center,
                radius = size.maxDimension * 0.72f,
            ),
        )
        if (palette.showFireflies) {
            for (f in fireflies) {
                val a = 0.3f + 0.7f * abs(sin((t + f.phase) * Math.PI).toFloat())
                val cx = f.x * size.width + sin((t + f.phase) * 6.2832f) * 8f
                val cy = f.y * size.height + cos((t + f.phase) * 6.2832f) * 8f
                drawCircle(Color(0xFFFFE6A0).copy(alpha = a * 0.8f), radius = 3f, center = Offset(cx, cy))
            }
        }
    }
}

private data class Firefly(val x: Float, val y: Float, val phase: Float)

/** Shared four-state time-of-day selector used by Play and Explore. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeOfDayBar(selected: TimeOfDay, onSelect: (TimeOfDay) -> Unit, modifier: Modifier = Modifier) {
    Row(modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        TimeOfDay.entries.forEach { t ->
            FilterChip(selected = t == selected, onClick = { onSelect(t) }, label = { Text(t.label()) })
        }
    }
}

private fun TimeOfDay.label() = when (this) {
    TimeOfDay.MORNING -> "Morning"
    TimeOfDay.AFTERNOON -> "Afternoon"
    TimeOfDay.SUNSET -> "Sunset"
    TimeOfDay.NIGHT -> "Night"
}
