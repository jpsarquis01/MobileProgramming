package com.studio.gladetowns.core.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.studio.gladetowns.core.domain.model.town.TownPreview
import kotlin.math.abs

/**
 * Gallery card showing a town as a tiny "snow globe".
 *
 * When a [preview] is available it paints the ACTUAL downsampled town inside
 * the glass dome; otherwise it falls back to a deterministic seeded gradient
 * (so cards look unique even before their preview has been computed).
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SnowGlobeCard(
    name: String,
    subtitle: String,
    seed: Long,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    preview: TownPreview? = null,
    onLongClick: () -> Unit = {},
) {
    Card(
        modifier = modifier.combinedClickable(onClick = onClick, onLongClick = onLongClick),
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

                // Glass dome backdrop.
                drawCircle(brush = Brush.verticalGradient(listOf(sky, ground)), radius = r, center = center)

                if (preview != null) {
                    drawPreview(preview, center, r)
                } else {
                    // Seeded hill silhouette fallback.
                    val hillShift = ((seed ushr 8) % 40L).toFloat() - 20f
                    drawCircle(
                        color = ground.copy(alpha = 0.85f),
                        radius = r * 0.55f,
                        center = Offset(center.x + hillShift, center.y + r * 0.55f),
                    )
                }

                // Dome highlight + base (kept regardless of preview).
                drawCircle(Color.White.copy(alpha = 0.25f), radius = r * 0.18f, center = Offset(center.x - r * 0.4f, center.y - r * 0.45f))
                drawRect(Color(0xFF8A6E52), topLeft = Offset(center.x - r * 0.7f, center.y + r * 0.92f), size = Size(r * 1.4f, r * 0.16f))
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

private val PreviewWall = listOf(
    Color(0xFFE8D8BE), Color(0xFFD9C7A3), Color(0xFFE2CBB0),
    Color(0xFFCFC2A8), Color(0xFFEAD9C2), Color(0xFFD2BE9B),
)
private val PreviewRoad = Color(0xFFCFC3A6)

private fun DrawScope.drawPreview(preview: TownPreview, center: Offset, r: Float) {
    val n = preview.sample
    val box = r * 1.42f                 // inscribe the sample grid inside the dome
    val cell = box / n
    val left = center.x - box / 2f
    val top = center.y - box / 2f
    val circle = Path().apply { addOval(androidx.compose.ui.geometry.Rect(center.x - r, center.y - r, center.x + r, center.y + r)) }

    clipPath(circle) {
        for (sy in 0 until n) {
            for (sx in 0 until n) {
                val i = sy * n + sx
                val tl = Offset(left + sx * cell, top + sy * cell)
                if (preview.roads[i]) {
                    drawRect(PreviewRoad, topLeft = tl, size = Size(cell, cell))
                }
                val pal = preview.palette[i]
                if (pal >= 0) {
                    drawRect(PreviewWall[pal % PreviewWall.size], topLeft = tl, size = Size(cell, cell))
                }
            }
        }
    }
}

private fun seededColor(seed: Long, saturationShift: Int): Color {
    val h = abs((seed * 31 + saturationShift * 97) % 360L).toFloat()
    return Color.hsv(hue = h, saturation = 0.28f, value = 0.92f)
}
