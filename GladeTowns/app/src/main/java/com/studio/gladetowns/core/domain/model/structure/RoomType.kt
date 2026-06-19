package com.studio.gladetowns.core.domain.model.structure

import kotlin.math.max
import kotlin.math.min

/**
 * Interior room kinds a drawn shape resolves into (TDD §10 extension).
 *
 * A room is a pure function of the drawn shape (recovered from its archetype)
 * and the footprint's bounding box — so it's derived at render time and needs
 * NO change to the event-sourced save format.
 */
enum class RoomType { BATHROOM, BEDROOM, KITCHEN, COMMON_AREA, TV_AREA, LOUNGE, GARDEN, DOOR }

object RoomClassifier {

    // Size bands by the bounding box's larger side, in tiles. Scaled up so rooms
    // are comfortable to draw on a 100x100 board (tweak here to rebalance).
    const val BATHROOM_MAX = 8
    const val BEDROOM_MAX = 14
    const val KITCHEN_MAX = 22

    /** (width, height) of the footprint's bounding box, in cells. */
    fun boundingBox(footprint: List<Int>): Pair<Int, Int> {
        if (footprint.isEmpty()) return 0 to 0
        var minX = Int.MAX_VALUE; var minY = Int.MAX_VALUE
        var maxX = Int.MIN_VALUE; var maxY = Int.MIN_VALUE
        for (p in footprint) {
            val x = Footprint.unpackX(p); val y = Footprint.unpackY(p)
            minX = min(minX, x); maxX = max(maxX, x)
            minY = min(minY, y); maxY = max(maxY, y)
        }
        return (maxX - minX + 1) to (maxY - minY + 1)
    }

    fun classify(plan: StructurePlan): RoomType {
        val (w, h) = boundingBox(plan.footprint)
        return classify(plan.archetype, w, h)
    }

    /**
     * Deterministic classification — SHAPE first, then size. A circle is always
     * a common area and a triangle always a TV area (at any size), so those
     * rooms are reliable to make. Everything else — rectangles AND irregular
     * blobs — becomes a room sized by its bounding box, so any drawing resolves
     * to a usable room instead of dead-ending on "irregular":
     *
     *  - COMMON_AREA circle/oval, any size
     *  - TV_AREA     triangle, any size
     *  - DOOR        thin (min side ≤ 1) or tiny (s ≤ 2)
     *  - BATHROOM    s ≤ 8
     *  - BEDROOM     9 ≤ s ≤ 14
     *  - KITCHEN     15 ≤ s ≤ 22
     *  - GARDEN      s ≥ 23   (a large freeform area)
     *
     * `s` is the larger side. Note size only decides among the rectangle-family
     * rooms; to control which one, draw smaller (use pinch-zoom on big grids).
     */
    fun classify(archetype: StructureArchetype, w: Int, h: Int): RoomType {
        val s = max(w, h)
        val minSide = min(w, h)
        val isCircle = archetype == StructureArchetype.TOWER
        val isTriangle = archetype == StructureArchetype.CHAPEL
        return when {
            isCircle -> RoomType.COMMON_AREA
            isTriangle -> RoomType.TV_AREA
            minSide <= 1 || s <= 2 -> RoomType.DOOR
            s <= BATHROOM_MAX -> RoomType.BATHROOM
            s <= BEDROOM_MAX -> RoomType.BEDROOM
            s <= KITCHEN_MAX -> RoomType.KITCHEN
            else -> RoomType.GARDEN
        }
    }

    fun label(type: RoomType): String = when (type) {
        RoomType.BATHROOM -> "Bathroom"
        RoomType.BEDROOM -> "Bedroom"
        RoomType.KITCHEN -> "Kitchen"
        RoomType.COMMON_AREA -> "Common area"
        RoomType.TV_AREA -> "TV area"
        RoomType.LOUNGE -> "Lounge"
        RoomType.GARDEN -> "Garden"
        RoomType.DOOR -> "Doorway"
    }
}
