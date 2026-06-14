package com.studio.gladetowns.core.domain.model.structure

/** Axis-aligned cell rectangle: (x, y) origin with width/height in cells. */
data class GridRect(val x: Int, val y: Int, val w: Int, val h: Int) {
    val centerX: Float get() = x + w / 2f
    val centerY: Float get() = y + h / 2f
}

enum class RoofKind { FLAT, GABLE, HIP, CONE, SPIRE }

/** One box mass of a building. Floors drive perceived height / shading. */
data class BuildingMass(
    val rect: GridRect,
    val floors: Int,
    val roof: RoofKind,
)

/**
 * Fully-resolved, renderer-agnostic building description (TDD §6.1).
 * NOT persisted — regenerated deterministically from (archetype, footprint,
 * seed, variantIndex, neighbor context) at load time, so saves stay tiny.
 */
data class StructurePlan(
    val structureId: StructureId,
    val archetype: StructureArchetype,
    val masses: List<BuildingMass>,
    val paletteIndex: Int,
    val footprint: List<Int>, // packed cells, for hit-testing / coverage
) {
    val maxFloors: Int get() = masses.maxOfOrNull { it.floors } ?: 1
}
