package com.studio.gladetowns.core.domain.model.town

import com.studio.gladetowns.core.domain.model.structure.StructurePlan

/**
 * The derived, render-ready view of a town (TDD §11). Produced purely from the
 * ordered structures by [com.studio.gladetowns.core.domain.procgen.merge.TownGrowthEngine];
 * never persisted. Roads + merged building plans are a deterministic function
 * of placement order, so revisiting a saved town rebuilds an identical layout.
 */
data class TownLayout(
    val gridCells: Int,
    val structures: List<StructurePlan>,
    val roads: Set<Int>,            // packed cells (Footprint.pack)
    val ownerByCell: Map<Int, Long>, // packed cell -> StructureId.value (hit-test)
) {
    fun ownerAt(packedCell: Int): Long? = ownerByCell[packedCell]

    companion object {
        fun empty(gridCells: Int) =
            TownLayout(gridCells, emptyList(), emptySet(), emptyMap())
    }
}
