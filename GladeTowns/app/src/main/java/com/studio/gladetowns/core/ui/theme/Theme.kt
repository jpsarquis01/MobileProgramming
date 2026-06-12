package com.studio.gladetowns.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val GladeColorScheme = lightColorScheme(
    primary = MossGreenDeep,
    onPrimary = Parchment,
    primaryContainer = MossGreen,
    secondary = TerracottaRoof,
    background = Parchment,
    onBackground = InkSoft,
    surface = ParchmentDeep,
    onSurface = InkSoft,
    surfaceVariant = ParchmentDeep,
    tertiary = LanternGlow,
)

/**
 * Single light, warm theme by design — the in-world day/night cycle is the
 * game's "dark mode" (TDD §14); system chrome stays cozy and consistent.
 */
@Composable
fun GladeTownsTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = GladeColorScheme,
        typography = GladeTypography,
        content = content,
    )
}
