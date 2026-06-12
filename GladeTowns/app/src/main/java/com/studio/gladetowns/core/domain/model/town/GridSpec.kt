package com.studio.gladetowns.core.domain.model.town

import kotlinx.serialization.Serializable

/** Supported board sizes (cells per side). */
@Serializable
enum class GridSize(val cells: Int) {
    SMALL(20),
    MEDIUM(50),
    LARGE(100);

    companion object {
        fun fromCells(cells: Int): GridSize =
            entries.firstOrNull { it.cells == cells }
                ?: error("Unsupported grid size: $cells")
    }
}

/**
 * Immutable description of the playfield. `cellWorldSize` is expressed in
 * 1/64ths of a world unit (fixed-point) so that any value persisted or used
 * in generation decisions is bit-identical across devices (TDD §6, §8.1).
 */
@Serializable
data class GridSpec(
    val size: GridSize,
    val cellWorldSizeFp: Int = FP_ONE,
) {
    val cellsPerSide: Int get() = size.cells
    val totalCells: Int get() = size.cells * size.cells

    fun isInside(x: Int, y: Int): Boolean =
        x in 0 until cellsPerSide && y in 0 until cellsPerSide

    companion object {
        /** Fixed-point scale: 64 == 1.0 world unit. */
        const val FP_ONE = 64
    }
}
