package com.studio.gladetowns.core.domain.model.structure

import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class StructureId(val value: Long)

/**
 * Building archetypes the shape classifier can emit (TDD §9.2).
 * Foundation phase defines the vocabulary; generators arrive in Phase 2.
 */
@Serializable
enum class StructureArchetype { HOUSE, WAREHOUSE, TOWER, L_SHAPE, FOLLY, PROP }

/**
 * A structure footprint: occupied cells as (x, y) pairs packed into one Int
 * each (x in high 16 bits, y in low 16) — compact, allocation-light, and
 * deterministic to sort. Helper accessors keep call sites readable.
 */
@Serializable
data class Footprint(val packedCells: List<Int>) {
    val cellCount: Int get() = packedCells.size

    companion object {
        fun pack(x: Int, y: Int): Int = (x shl 16) or (y and 0xFFFF)
        fun unpackX(packed: Int): Int = packed shr 16
        fun unpackY(packed: Int): Int = packed and 0xFFFF
    }
}

/**
 * Minimal structure record for the foundation phase. The resolved
 * `StructurePlan` (module placements, roof runs, props — TDD §6.1) is added
 * in Phase 2; nothing in persistence needs to change because plans are
 * regenerated from (archetype, footprint, seed) at load time.
 */
@Serializable
data class Structure(
    val id: StructureId,
    val placementSeq: Int,
    val archetype: StructureArchetype,
    val footprint: Footprint,
    val structureSeed: Long,
    val variantIndex: Int = 0,
)
