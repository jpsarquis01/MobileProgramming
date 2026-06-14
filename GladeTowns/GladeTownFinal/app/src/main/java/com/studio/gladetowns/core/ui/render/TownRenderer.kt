package com.studio.gladetowns.core.ui.render

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.studio.gladetowns.core.domain.model.structure.Footprint
import com.studio.gladetowns.core.domain.model.structure.GridRect
import com.studio.gladetowns.core.domain.model.structure.RoofKind
import com.studio.gladetowns.core.domain.model.structure.StructurePlan
import com.studio.gladetowns.core.domain.model.town.TownLayout
import com.studio.gladetowns.core.domain.rng.SplitMix64
import kotlin.math.max
import kotlin.math.min

/**
 * Shared, renderer-agnostic top-down drawing for a [TownLayout] (TDD §11/§17).
 *
 * Extracted so Play (edit) and Explore (read-only) draw identically; when the
 * Filament SurfaceView lands it consumes the same TownLayout and this file is
 * the only thing the Compose path keeps.
 */

/** Screen <-> grid mapping for a square board centred in the canvas. */
class GridMapping(val cells: Int, viewW: Float, viewH: Float) {
    val side = min(viewW, viewH)
    val cellPx = side / cells
    val originX = (viewW - side) / 2f
    val originY = (viewH - side) / 2f

    fun toGrid(p: Offset): Pair<Float, Float> =
        ((p.x - originX) / cellPx).coerceIn(0f, cells.toFloat()) to
            ((p.y - originY) / cellPx).coerceIn(0f, cells.toFloat())

    fun cellAt(p: Offset): Int? {
        val cx = ((p.x - originX) / cellPx).toInt()
        val cy = ((p.y - originY) / cellPx).toInt()
        if (cx !in 0 until cells || cy !in 0 until cells) return null
        return Footprint.pack(cx, cy)
    }

    fun cellTopLeft(x: Int, y: Int) = Offset(originX + x * cellPx, originY + y * cellPx)
}

/** Mutable holder so a gesture handler can read the latest mapping from draw. */
class GridMappingHolder { var value: GridMapping? = null }

private val WallColors = listOf(
    Color(0xFFE8D8BE), Color(0xFFD9C7A3), Color(0xFFE2CBB0),
    Color(0xFFCFC2A8), Color(0xFFEAD9C2), Color(0xFFD2BE9B),
)
private val RoofColors = listOf(
    Color(0xFFC97B5A), Color(0xFF8C6E52), Color(0xFFA8584A),
    Color(0xFF6E7D52), Color(0xFFB5734A), Color(0xFF7C6A8A),
)
private val WindowColor = Color(0xFFFFE6A0)

fun DrawScope.drawTown(layout: TownLayout, m: GridMapping, palette: TimeOfDayPalette) {
    // Ground.
    drawRect(palette.ground, topLeft = Offset(m.originX, m.originY), size = Size(m.side, m.side))

    // Grid (line count capped so a 100x100 board stays cheap).
    val lines = min(layout.gridCells, 50)
    val step = m.side / lines
    for (i in 0..lines) {
        val o = i * step
        drawLine(palette.gridLine, Offset(m.originX + o, m.originY), Offset(m.originX + o, m.originY + m.side))
        drawLine(palette.gridLine, Offset(m.originX, m.originY + o), Offset(m.originX + m.side, m.originY + o))
    }

    // Roads (under buildings).
    for (cell in layout.roads) {
        val x = Footprint.unpackX(cell); val y = Footprint.unpackY(cell)
        drawRect(palette.road, topLeft = m.cellTopLeft(x, y), size = Size(m.cellPx, m.cellPx))
    }

    // Buildings: floor-scaled drop shadow, wall fill, roof hint, optional windows.
    for (plan in layout.structures) {
        val wall = WallColors[plan.paletteIndex % WallColors.size]
        val roof = RoofColors[plan.paletteIndex % RoofColors.size]
        for (mass in plan.masses) {
            val tl = m.cellTopLeft(mass.rect.x, mass.rect.y)
            val w = mass.rect.w * m.cellPx
            val h = mass.rect.h * m.cellPx
            val shadow = (mass.floors * 1.5f).coerceAtMost(8f)
            drawRect(Color.Black.copy(alpha = palette.shadowAlpha), topLeft = Offset(tl.x + shadow, tl.y + shadow), size = Size(w, h))
            drawRect(wall, topLeft = tl, size = Size(w, h))
            drawRoof(mass.rect, mass.roof, roof, m)
        }
        if (palette.showWindows) drawWindows(plan, m)
    }
}

/** Selection outline used by Explore's structure inspection. */
fun DrawScope.drawSelection(plan: StructurePlan, m: GridMapping) {
    if (plan.footprint.isEmpty()) return
    var minX = Int.MAX_VALUE; var minY = Int.MAX_VALUE
    var maxX = Int.MIN_VALUE; var maxY = Int.MIN_VALUE
    for (p in plan.footprint) {
        val x = Footprint.unpackX(p); val y = Footprint.unpackY(p)
        minX = min(minX, x); maxX = max(maxX, x)
        minY = min(minY, y); maxY = max(maxY, y)
    }
    val tl = m.cellTopLeft(minX, minY)
    val w = (maxX - minX + 1) * m.cellPx
    val h = (maxY - minY + 1) * m.cellPx
    drawRect(Color(0x33FFFFFF), topLeft = tl, size = Size(w, h))
    drawRect(Color(0xFFFFFFFF), topLeft = tl, size = Size(w, h), style = Stroke(width = 3f))
}

private fun DrawScope.drawRoof(rect: GridRect, kind: RoofKind, color: Color, m: GridMapping) {
    val tl = m.cellTopLeft(rect.x, rect.y)
    val w = rect.w * m.cellPx; val h = rect.h * m.cellPx
    val cx = tl.x + w / 2f; val cy = tl.y + h / 2f
    when (kind) {
        RoofKind.FLAT -> drawRect(
            color.copy(alpha = 0.55f),
            topLeft = Offset(tl.x + w * 0.18f, tl.y + h * 0.18f), size = Size(w * 0.64f, h * 0.64f),
        )
        RoofKind.GABLE -> drawPath(
            Path().apply { moveTo(tl.x, tl.y + h); lineTo(cx, tl.y); lineTo(tl.x + w, tl.y + h); close() }, color,
        )
        RoofKind.HIP -> drawPath(
            Path().apply {
                moveTo(tl.x, tl.y + h); lineTo(cx, tl.y); lineTo(tl.x + w, tl.y + h); lineTo(cx, tl.y + h * 0.7f); close()
            }, color,
        )
        RoofKind.CONE -> drawCircle(color, radius = min(w, h) / 2f, center = Offset(cx, cy))
        RoofKind.SPIRE -> {
            drawPath(
                Path().apply { moveTo(cx, tl.y); lineTo(tl.x + w, tl.y + h); lineTo(tl.x, tl.y + h); close() }, color,
            )
            drawCircle(color, radius = m.cellPx * 0.12f, center = Offset(cx, tl.y))
        }
    }
}

private fun DrawScope.drawWindows(plan: StructurePlan, m: GridMapping) {
    var seed = SplitMix64.mix(plan.structureId.value)
    val r = (m.cellPx * 0.10f).coerceAtMost(4f)
    for (mass in plan.masses) {
        val tl = m.cellTopLeft(mass.rect.x, mass.rect.y)
        val w = mass.rect.w * m.cellPx; val h = mass.rect.h * m.cellPx
        val count = (mass.rect.w * mass.rect.h).coerceIn(1, 4)
        repeat(count) {
            seed = SplitMix64.mix(seed); val fx = ((seed ushr 11) % 1000) / 1000f
            seed = SplitMix64.mix(seed); val fy = ((seed ushr 11) % 1000) / 1000f
            drawCircle(
                WindowColor.copy(alpha = 0.9f), radius = r,
                center = Offset(tl.x + 0.15f * w + fx * 0.7f * w, tl.y + 0.15f * h + fy * 0.7f * h),
            )
        }
    }
}
