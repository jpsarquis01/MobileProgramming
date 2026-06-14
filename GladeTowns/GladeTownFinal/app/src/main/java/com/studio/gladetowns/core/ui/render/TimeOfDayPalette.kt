package com.studio.gladetowns.core.ui.render

import androidx.compose.ui.graphics.Color
import com.studio.gladetowns.core.domain.model.time.TimeOfDay

/**
 * Art-directed palette + atmosphere parameters per time of day (TDD §14).
 *
 * The renderer paints the town with these ground/road colours; screens then
 * overlay an animated [ambientScrim] + vignette (see AtmosphereOverlay) so the
 * heavy town Canvas is never invalidated by the lighting animation.
 */
data class TimeOfDayPalette(
    val ground: Color,
    val gridLine: Color,
    val road: Color,
    val shadowAlpha: Float,
    val ambientScrim: Color,
    val scrimAlpha: Float,
    val vignette: Color,
    val showWindows: Boolean,
    val showFireflies: Boolean,
)

fun TimeOfDay.palette(): TimeOfDayPalette = when (this) {
    TimeOfDay.MORNING -> TimeOfDayPalette(
        ground = Color(0xFFEFE9DA),
        gridLine = Color(0x224A4238),
        road = Color(0xFFD8CCAE),
        shadowAlpha = 0.16f,
        ambientScrim = Color(0xFFBFD2E0),
        scrimAlpha = 0.16f,
        vignette = Color(0x14283A4A),
        showWindows = false,
        showFireflies = false,
    )
    TimeOfDay.AFTERNOON -> TimeOfDayPalette(
        ground = Color(0xFFEFE7D6),
        gridLine = Color(0x224A4238),
        road = Color(0xFFCFC3A6),
        shadowAlpha = 0.20f,
        ambientScrim = Color(0xFFFFF4D6),
        scrimAlpha = 0.06f,
        vignette = Color(0x10000000),
        showWindows = false,
        showFireflies = false,
    )
    TimeOfDay.SUNSET -> TimeOfDayPalette(
        ground = Color(0xFFEAD8C2),
        gridLine = Color(0x2255402F),
        road = Color(0xFFD8B98E),
        shadowAlpha = 0.26f,
        ambientScrim = Color(0xFFFF9E5E),
        scrimAlpha = 0.22f,
        vignette = Color(0x33421E0E),
        showWindows = true,
        showFireflies = false,
    )
    TimeOfDay.NIGHT -> TimeOfDayPalette(
        ground = Color(0xFF2C3550),
        gridLine = Color(0x1AA9B6D8),
        road = Color(0xFF3C4666),
        shadowAlpha = 0.10f,
        ambientScrim = Color(0xFF14182E),
        scrimAlpha = 0.45f,
        vignette = Color(0x55070A16),
        showWindows = true,
        showFireflies = true,
    )
}
