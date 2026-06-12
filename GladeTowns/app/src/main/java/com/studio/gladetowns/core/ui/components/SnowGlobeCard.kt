package com.studio.gladetowns.core.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlin.math.abs

/**
 * Gallery card showing a town as a tiny "snow globe".
 *
 * Foundation phase: no thumbnails exist yet (sealing arrives with the Seal
 * flow, TDD §12.2), so the dome is filled with a deterministic gradient and
 * "hill" silhouette derived from the town's master seed — every town already
 * looks unique, and the real WebP render simply replaces the dome contents
 * later behind the same composable signature.
 */
@Composable
fun SnowGlobeCard(
    name: String,
    subtitle: String,
    seed: Long,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = MaterialTheme.shapes.extraLarge,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .padding(12.dp),
            ) {
                val sky = seededColor(seed, saturationShift = 0)
                val ground = seededColor(seed, saturationShift = 1)
                val r = size.minDimension / 2f
                val center = Offset(size.width / 2f, size.height / 2f)

                // Glass dome
                drawCircle(
                    brush = Brush.verticalGradient(listOf(sky, ground)),
                    radius = r,
                    center = center,
                )
                // Seeded hill silhouette
                val hillShift = ((seed ushr 8) % 40L).toFloat() - 20f
                drawCircle(
                    color = ground.copy(alpha = 0.85f),
                    radius = r * 0.55f,
                    center = Offset(center.x + hillShift, center.y + r * 0.55f),
                )
                // Dome highlight
                drawCircle(
                    color = Color.White.copy(alpha = 0.25f),
                    radius = r * 0.18f,
                    center = Offset(center.x - r * 0.4f, center.y - r * 0.45f),
                )
                // Base
                drawRect(
                    color = Color(0xFF8A6E52),
                    topLeft = Offset(center.x - r * 0.7f, center.y + r * 0.92f),
                    size = androidx.compose.ui.geometry.Size(r * 1.4f, r * 0.16f),
                )
            }
            Text(
                text = name,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 14.dp),
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.padding(start = 14.dp, end = 14.dp, bottom = 14.dp),
            )
        }
    }
}

private fun seededColor(seed: Long, saturationShift: Int): Color {
    val h = abs((seed * 31 + saturationShift * 97) % 360L).toFloat()
    return Color.hsv(hue = h, saturation = 0.28f, value = 0.92f)
}
