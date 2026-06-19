package com.studio.gladetowns.core.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import kotlin.math.PI
import kotlin.math.sin

/**
 * A soft, slowly looping landscape — warm sky, a hazy sun, drifting clouds and
 * parallax rolling hills. Drawn entirely with Canvas (no assets) and meant to
 * sit BEHIND a blur + scrim on the menu, so it reads as a dreamy backdrop. All
 * motion is an infinite transition, so it loops forever and is cheap.
 */
@Composable
fun AnimatedLandscape(modifier: Modifier = Modifier) {
    val tr = rememberInfiniteTransition(label = "landscape")
    val drift by tr.animateFloat(
        0f, 1f, infiniteRepeatable(tween(60000, easing = LinearEasing), RepeatMode.Restart),
        label = "drift",
    )
    val sun by tr.animateFloat(
        0f, 1f, infiniteRepeatable(tween(14000), RepeatMode.Reverse), label = "sun",
    )
    val sway by tr.animateFloat(
        0f, 1f, infiniteRepeatable(tween(11000), RepeatMode.Reverse), label = "sway",
    )

    Canvas(modifier) {
        val w = size.width; val h = size.height

        // Sky.
        drawRect(Brush.verticalGradient(listOf(SkyTop, SkyMid, SkyHorizon)))

        // Hazy sun with a soft glow, bobbing gently.
        val sunY = h * (0.30f - 0.04f * sun)
        val sunX = w * (0.50f + 0.06f * (sun - 0.5f))
        drawCircle(SunGlow.copy(alpha = 0.45f), radius = h * 0.18f, center = Offset(sunX, sunY))
        drawCircle(SunGlow.copy(alpha = 0.55f), radius = h * 0.11f, center = Offset(sunX, sunY))
        drawCircle(Sun, radius = h * 0.07f, center = Offset(sunX, sunY))

        // Clouds drifting left→right and wrapping.
        for ((i, c) in CLOUDS.withIndex()) {
            val speed = 0.5f + 0.25f * i
            val frac = (c.x + drift * speed) % 1.3f
            val cx = frac * w - 0.15f * w
            cloud(cx, c.y * h, c.scale * h, Color.White.copy(alpha = 0.75f))
        }

        // Parallax hills, back to front.
        hill(h * 0.60f, h * 0.045f, Hill1, sway * 0.30f, waves = 1.4f)
        hill(h * 0.70f, h * 0.060f, Hill2, 0.4f + sway * 0.20f, waves = 1.8f)
        hill(h * 0.82f, h * 0.050f, Hill3, 0.9f + sway * 0.10f, waves = 2.4f)
    }
}

private class Cloud(val x: Float, val y: Float, val scale: Float)

private val CLOUDS = listOf(
    Cloud(0.10f, 0.18f, 0.07f),
    Cloud(0.55f, 0.12f, 0.05f),
    Cloud(0.85f, 0.26f, 0.09f),
    Cloud(0.35f, 0.30f, 0.06f),
)

private val SkyTop = Color(0xFFA9CBE0)
private val SkyMid = Color(0xFFD6E2DA)
private val SkyHorizon = Color(0xFFF3E7CF)
private val Sun = Color(0xFFFFEBB0)
private val SunGlow = Color(0xFFFFD98A)
private val Hill1 = Color(0xFF9DC07C)
private val Hill2 = Color(0xFF7AAA5E)
private val Hill3 = Color(0xFF5C8C49)

private fun DrawScope.hill(baseY: Float, amp: Float, color: Color, phase: Float, waves: Float) {
    val w = size.width; val h = size.height
    val path = Path().apply {
        moveTo(0f, h)
        lineTo(0f, baseY)
        val steps = 28
        for (i in 0..steps) {
            val f = i / steps.toFloat()
            val y = baseY + amp * sin((phase + f * waves) * 2f * PI).toFloat()
            lineTo(w * f, y)
        }
        lineTo(w, h)
        close()
    }
    drawPath(path, color)
}

private fun DrawScope.cloud(cx: Float, cy: Float, r: Float, color: Color) {
    drawOval(color, topLeft = Offset(cx - r * 1.6f, cy - r * 0.6f), size = Size(r * 3.2f, r * 1.2f))
    drawCircle(color, radius = r * 0.8f, center = Offset(cx - r * 0.6f, cy))
    drawCircle(color, radius = r, center = Offset(cx + r * 0.2f, cy - r * 0.2f))
    drawCircle(color, radius = r * 0.7f, center = Offset(cx + r * 0.9f, cy))
}
