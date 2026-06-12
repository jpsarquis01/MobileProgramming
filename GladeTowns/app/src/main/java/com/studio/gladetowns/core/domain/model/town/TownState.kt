package com.studio.gladetowns.core.domain.model.town

import com.studio.gladetowns.core.domain.model.structure.Structure
import com.studio.gladetowns.core.domain.model.structure.StructureId
import com.studio.gladetowns.core.domain.model.time.TimeOfDay
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.persistentMapOf

/**
 * Pure reduction of the command log (TDD §6.3). Persistent collections give
 * cheap structural sharing between consecutive states — undo keeps previous
 * states alive at near-zero memory cost.
 *
 * NOTE: `structures` uses a persistent map but all *iteration for decisions*
 * must go through [structuresOrdered] — deterministic order is a hard rule
 * of the domain layer (TDD §8.1).
 */
data class TownState(
    val gridSpec: GridSpec,
    val structures: PersistentMap<StructureId, Structure> = persistentMapOf(),
    val timeOfDay: TimeOfDay = TimeOfDay.AFTERNOON,
    val nextLocalSeq: Int = 0,
) {
    /** Stable, placement-ordered view. The only sanctioned iteration order. */
    fun structuresOrdered(): List<Structure> =
        structures.values.sortedBy { it.placementSeq }

    companion object {
        fun empty(gridSpec: GridSpec) = TownState(gridSpec)
    }
}
