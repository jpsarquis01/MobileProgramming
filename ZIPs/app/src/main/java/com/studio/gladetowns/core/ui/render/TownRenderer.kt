package com.studio.gladetowns.core.ui.render

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.studio.gladetowns.core.domain.model.structure.Footprint
import com.studio.gladetowns.core.domain.model.structure.RoomClassifier
import com.studio.gladetowns.core.domain.model.structure.RoomType
import com.studio.gladetowns.core.domain.model.structure.StructurePlan
import com.studio.gladetowns.core.domain.model.town.TownLayout
import kotlin.math.max
import kotlin.math.min

/**
 * Shared top-down ROOM renderer. Each drawn shape is a room: its border tiles
 * are walls (with a beveled tile texture) unless they face a hallway — another
 * room or a path — and its interior is a room-typed floor. Wall/room
 * classification is precomputed once in [TownRoomModel] (off the redraw path).
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

// ---- Precomputed room model ------------------------------------------------

private val DIRS = arrayOf(intArrayOf(1, 0), intArrayOf(-1, 0), intArrayOf(0, 1), intArrayOf(0, -1))

class RoomCell(val x: Int, val y: Int, val wall: Boolean)
class RoomRender(val type: RoomType, val cells: List<RoomCell>)

/**
 * Derived, render-ready rooms. Built once per [TownLayout] (remember-keyed in
 * the screens) so panning, zooming and selecting never re-run the O(cells)
 * wall analysis.
 */
class TownRoomModel(
    val gridCells: Int,
    val rooms: List<RoomRender>,
    val roads: Set<Int>,
) {
    companion object {
        fun from(layout: TownLayout): TownRoomModel {
            val owner = layout.ownerByCell
            val roads = layout.roads
            val n = layout.gridCells
            val rooms = layout.structures.map { plan ->
                val type = RoomClassifier.classify(plan)
                val open = type == RoomType.GARDEN || type == RoomType.DOOR
                val self = plan.structureId.value
                val cells = plan.footprint.map { packed ->
                    val x = Footprint.unpackX(packed); val y = Footprint.unpackY(packed)
                    val wall = if (open) false else isWall(x, y, self, owner, roads, n)
                    RoomCell(x, y, wall)
                }
                RoomRender(type, cells)
            }
            return TownRoomModel(n, rooms, roads)
        }

        /** Border tile facing only empty exterior = wall; facing a room/path = hallway. */
        private fun isWall(
            x: Int, y: Int, self: Long,
            owner: Map<Int, Long>, roads: Set<Int>, n: Int,
        ): Boolean {
            var facesEmpty = false
            var facesHallway = false
            for (d in DIRS) {
                val nx = x + d[0]; val ny = y + d[1]
                if (nx < 0 || ny < 0 || nx >= n || ny >= n) { facesEmpty = true; continue }
                val np = Footprint.pack(nx, ny)
                val no = owner[np]
                when {
                    no == null -> if (np in roads) facesHallway = true else facesEmpty = true
                    no != self -> facesHallway = true
                    // same room -> interior edge
                }
            }
            return facesEmpty && !facesHallway
        }
    }
}

// ---- Painting --------------------------------------------------------------

private val WallBase = Color(0xFFB7A488)
private val WallHi = Color(0xFFDBCEB5)
private val WallShadow = Color(0xFF8A7A63)

private fun floorColor(type: RoomType): Color = when (type) {
    RoomType.BATHROOM -> Color(0xFFAFC9CC)
    RoomType.BEDROOM -> Color(0xFFC9A9C4)
    RoomType.KITCHEN -> Color(0xFFE6B566)
    RoomType.COMMON_AREA -> Color(0xFFA9C58C)
    RoomType.TV_AREA -> Color(0xFF8E97B8)
    RoomType.LOUNGE -> Color(0xFFD9C2A0)
    RoomType.GARDEN -> Color(0xFF86B06A)
    RoomType.DOOR -> Color(0xFFB07A4E)
}

/** Convenience that builds the model inline (use the model path in hot screens). */
fun DrawScope.drawTown(layout: TownLayout, m: GridMapping, palette: TimeOfDayPalette) =
    drawRooms(TownRoomModel.from(layout), m, palette)

fun DrawScope.drawRooms(model: TownRoomModel, m: GridMapping, palette: TimeOfDayPalette) {
    // Ground.
    drawRect(palette.ground, topLeft = Offset(m.originX, m.originY), size = Size(m.side, m.side))

    // Grid (capped).
    val lines = min(model.gridCells, 50)
    val step = m.side / lines
    for (i in 0..lines) {
        val o = i * step
        drawLine(palette.gridLine, Offset(m.originX + o, m.originY), Offset(m.originX + o, m.originY + m.side))
        drawLine(palette.gridLine, Offset(m.originX, m.originY + o), Offset(m.originX + m.side, m.originY + o))
    }

    // Paths (hallways between rooms).
    for (cell in model.roads) {
        val x = Footprint.unpackX(cell); val y = Footprint.unpackY(cell)
        drawRect(palette.road, topLeft = m.cellTopLeft(x, y), size = Size(m.cellPx, m.cellPx))
    }

    // Floors first, then walls on top so the wall ring reads cleanly.
    for (room in model.rooms) {
        val floor = floorColor(room.type)
        for (c in room.cells) drawFloorTile(room.type, floor, m.cellTopLeft(c.x, c.y), m.cellPx)
    }
    for (room in model.rooms) {
        for (c in room.cells) if (c.wall) drawWallTile(m.cellTopLeft(c.x, c.y), m.cellPx)
    }
}

private fun DrawScope.drawFloorTile(type: RoomType, base: Color, tl: Offset, c: Float) {
    drawRect(base, topLeft = tl, size = Size(c, c))
    val inset = c * 0.16f
    when (type) {
        RoomType.BATHROOM, RoomType.KITCHEN -> {
            // Tile seams.
            val line = darken(base, 0.12f)
            drawLine(line, Offset(tl.x + c / 2f, tl.y), Offset(tl.x + c / 2f, tl.y + c), strokeWidth = 1f)
            drawLine(line, Offset(tl.x, tl.y + c / 2f), Offset(tl.x + c, tl.y + c / 2f), strokeWidth = 1f)
        }
        RoomType.GARDEN -> {
            drawCircle(darken(base, 0.18f), radius = c * 0.16f, center = Offset(tl.x + c * 0.5f, tl.y + c * 0.5f))
        }
        RoomType.TV_AREA -> {
            drawRect(darken(base, 0.30f), topLeft = Offset(tl.x + inset, tl.y + inset), size = Size(c - 2 * inset, c * 0.22f))
        }
        RoomType.DOOR -> {
            drawLine(darken(base, 0.22f), Offset(tl.x, tl.y + c / 2f), Offset(tl.x + c, tl.y + c / 2f), strokeWidth = 1.5f)
        }
        else -> {
            drawRect(lighten(base, 0.10f), topLeft = Offset(tl.x + inset, tl.y + inset), size = Size(c - 2 * inset, c - 2 * inset))
        }
    }
}

private fun DrawScope.drawWallTile(tl: Offset, c: Float) {
    drawRect(WallBase, topLeft = tl, size = Size(c, c))
    val t = max(1f, c * 0.10f)
    // Bevel: light top/left, dark bottom/right.
    drawRect(WallHi, topLeft = tl, size = Size(c, t))
    drawRect(WallHi, topLeft = tl, size = Size(t, c))
    drawRect(WallShadow, topLeft = Offset(tl.x, tl.y + c - t), size = Size(c, t))
    drawRect(WallShadow, topLeft = Offset(tl.x + c - t, tl.y), size = Size(t, c))
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

private fun darken(c: Color, f: Float) = Color(
    red = (c.red * (1 - f)).coerceIn(0f, 1f),
    green = (c.green * (1 - f)).coerceIn(0f, 1f),
    blue = (c.blue * (1 - f)).coerceIn(0f, 1f),
    alpha = c.alpha,
)

private fun lighten(c: Color, f: Float) = Color(
    red = (c.red + (1 - c.red) * f).coerceIn(0f, 1f),
    green = (c.green + (1 - c.green) * f).coerceIn(0f, 1f),
    blue = (c.blue + (1 - c.blue) * f).coerceIn(0f, 1f),
    alpha = c.alpha,
)
