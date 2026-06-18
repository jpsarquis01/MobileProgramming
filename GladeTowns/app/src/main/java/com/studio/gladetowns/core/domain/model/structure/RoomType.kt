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
     * Deterministic, total classification by shape + bounding-box size.
     * `s` is the larger side. Bands are made non-overlapping and exhaustive:
     *
     *  - GARDEN      any shape, s ≥ 10                       (drawn 10×10 or more)
     *  - DOOR        rectangle, thin (min side ≤ 1) or s ≤ 2 (small rectangle)
     *  - BATHROOM    rectangle, s ≤ 4
     *  - BEDROOM     rectangle, 5 ≤ s ≤ 7                    ("no more than 7")
     *  - KITCHEN     rectangle, 8 ≤ s ≤ 9                    (10 → garden)
     *  - COMMON_AREA circle/oval, s ≤ 9
     *  - TV_AREA     triangle, s ≤ 9
     *  - LOUNGE      irregular / other, s ≤ 9               (fills the unspecified slot)
     */
    fun classify(archetype: StructureArchetype, w: Int, h: Int): RoomType {
        val s = max(w, h)
        val minSide = min(w, h)
        val isRect = archetype == StructureArchetype.HOUSE ||
            archetype == StructureArchetype.WAREHOUSE ||
            archetype == StructureArchetype.L_SHAPE
        val isCircle = archetype == StructureArchetype.TOWER
        val isTriangle = archetype == StructureArchetype.CHAPEL
        return when {
            s >= 10 -> RoomType.GARDEN
            isRect && (minSide <= 1 || s <= 2) -> RoomType.DOOR
            isRect && s <= 4 -> RoomType.BATHROOM
            isRect && s <= 7 -> RoomType.BEDROOM
            isRect -> RoomType.KITCHEN
            isCircle -> RoomType.COMMON_AREA
            isTriangle -> RoomType.TV_AREA
            else -> RoomType.LOUNGE
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
