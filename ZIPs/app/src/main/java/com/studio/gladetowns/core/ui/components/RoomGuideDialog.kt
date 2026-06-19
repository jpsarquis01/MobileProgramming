package com.studio.gladetowns.core.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

/**
 * In-app tutorial: which drawn shape becomes which room, with a little drawn
 * "picture" per entry. Pure UI — the room rules mirror RoomClassifier so the
 * guide and the gameplay never drift. Pictures are drawn with Canvas (no image
 * assets needed), so each thumbnail is the actual room floor + wall colour.
 */

private enum class GuideShape { SMALL_RECT, RECT, BIG_RECT, CIRCLE, TRIANGLE, BLOB, FILL, THIN, LINE }

private data class GuideEntry(
    val shape: GuideShape,
    val room: String,
    val rule: String,
    val floor: Color,
)

private val WallInk = Color(0xFF8A7A63)

private val entries = listOf(
    GuideEntry(GuideShape.CIRCLE, "Common area", "Draw a circle or oval — any size", Color(0xFFA9C58C)),
    GuideEntry(GuideShape.TRIANGLE, "TV area", "Draw a triangle — any size", Color(0xFF8E97B8)),
    GuideEntry(GuideShape.SMALL_RECT, "Bathroom", "A small shape, up to 8×8 tiles", Color(0xFFAFC9CC)),
    GuideEntry(GuideShape.RECT, "Bedroom", "A shape 9×9 to 14×14 tiles", Color(0xFFC9A9C4)),
    GuideEntry(GuideShape.BIG_RECT, "Kitchen", "A shape 15×15 to 22×22 tiles", Color(0xFFE6B566)),
    GuideEntry(GuideShape.BLOB, "Garden", "A large freeform area, 23×23 or bigger", Color(0xFF86B06A)),
    GuideEntry(GuideShape.THIN, "Doorway", "A thin or tiny rectangle", Color(0xFFB07A4E)),
    GuideEntry(GuideShape.LINE, "Hallway", "Switch to Hallway, then drag a line to connect rooms", Color(0xFFD8B98E)),
)

@Composable
fun RoomGuideDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = { TextButton(onClick = onDismiss) { Text("Got it") } },
        title = { Text("How rooms grow") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(
                    "The shape you draw decides the room. A circle or triangle always " +
                        "makes the same room at any size; other shapes become rooms by " +
                        "how many tiles they cover. Pinch to zoom so you can draw small " +
                        "rooms on a big grid.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                )
                Spacer(Modifier.height(2.dp))
                entries.forEach { GuideRow(it) }
            }
        },
    )
}

@Composable
private fun GuideRow(e: GuideEntry) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.size(48.dp),
        ) {
            Box(Modifier.padding(6.dp)) {
                Canvas(Modifier.fillMaxWidth().height(36.dp)) { drawGuideShape(e.shape, e.floor) }
            }
        }
        Column(Modifier.fillMaxWidth()) {
            Text(e.room, style = MaterialTheme.typography.titleSmall)
            Text(
                e.rule,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
            )
        }
    }
}

private fun DrawScope.drawGuideShape(shape: GuideShape, floor: Color) {
    val w = size.width; val h = size.height
    val cx = w / 2f; val cy = h / 2f
    val wall = Stroke(width = 3f)
    when (shape) {
        GuideShape.SMALL_RECT -> filledRect(floor, w * 0.30f, w * 0.30f, h * 0.30f, h * 0.40f, wall)
        GuideShape.RECT -> filledRect(floor, w * 0.18f, w * 0.18f, h * 0.18f, h * 0.64f, wall)
        GuideShape.BIG_RECT -> filledRect(floor, w * 0.08f, w * 0.08f, h * 0.08f, h * 0.84f, wall)
        GuideShape.FILL -> filledRect(floor, 0f, 0f, 0f, h, Stroke(width = 1f))
        GuideShape.THIN -> filledRect(floor, w * 0.10f, w * 0.10f, h * 0.40f, h * 0.20f, wall)
        GuideShape.CIRCLE -> {
            drawCircle(floor, radius = minOf(w, h) * 0.42f, center = Offset(cx, cy))
            drawCircle(WallInk, radius = minOf(w, h) * 0.42f, center = Offset(cx, cy), style = wall)
        }
        GuideShape.TRIANGLE -> {
            val p = Path().apply {
                moveTo(cx, h * 0.10f); lineTo(w * 0.10f, h * 0.90f); lineTo(w * 0.90f, h * 0.90f); close()
            }
            drawPath(p, floor); drawPath(p, WallInk, style = wall)
        }
        GuideShape.BLOB -> {
            val p = Path().apply {
                moveTo(w * 0.20f, h * 0.45f)
                cubicTo(w * 0.10f, h * 0.10f, w * 0.55f, h * 0.05f, w * 0.62f, h * 0.30f)
                cubicTo(w * 0.95f, h * 0.30f, w * 0.88f, h * 0.80f, w * 0.55f, h * 0.82f)
                cubicTo(w * 0.30f, h * 0.95f, w * 0.18f, h * 0.75f, w * 0.20f, h * 0.45f)
                close()
            }
            drawPath(p, floor); drawPath(p, WallInk, style = wall)
        }
        GuideShape.LINE -> {
            val p = Path().apply {
                moveTo(w * 0.10f, h * 0.80f); lineTo(w * 0.40f, h * 0.30f)
                lineTo(w * 0.65f, h * 0.65f); lineTo(w * 0.90f, h * 0.20f)
            }
            drawPath(p, floor, style = Stroke(width = 7f))
        }
    }
}

private fun DrawScope.filledRect(floor: Color, l: Float, r: Float, t: Float, sideH: Float, wall: Stroke) {
    val w = size.width
    val tl = Offset(l, t)
    val sz = Size(w - l - r, sideH)
    drawRect(floor, topLeft = tl, size = sz)
    drawRect(WallInk, topLeft = tl, size = sz, style = wall)
}
