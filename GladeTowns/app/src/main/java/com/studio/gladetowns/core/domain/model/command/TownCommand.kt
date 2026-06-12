package com.studio.gladetowns.core.domain.model.command

import com.studio.gladetowns.core.domain.model.structure.Footprint
import com.studio.gladetowns.core.domain.model.structure.StructureArchetype
import com.studio.gladetowns.core.domain.model.structure.StructureId
import com.studio.gladetowns.core.domain.model.time.TimeOfDay
import kotlinx.serialization.Serializable

/**
 * The event-sourced heart of the save system (TDD §6.3, §7).
 *
 * A town IS its command log: TownState = fold(commands). Commands record the
 * player's *inputs plus derived seeds*, never generated output geometry —
 * which keeps saves tiny and replays bit-deterministic forever.
 *
 * Sealed + @Serializable gives us polymorphic CBOR encoding for free.
 * Every new command type MUST be appended with a new serial name and must
 * never change shape once shipped (append-only schema; see BlobVersions).
 */
@Serializable
sealed interface TownCommand {
    val seq: Int
    val atMs: Long

    /**
     * Placeholder for the Phase 1 drawing pipeline: carries the classified
     * shape result rather than raw strokes so foundation persistence and the
     * reducer are final from day one. The stroke itself is added as an extra
     * field in Phase 1 (additive, backward-compatible).
     */
    @Serializable
    data class PlaceStructure(
        override val seq: Int,
        override val atMs: Long,
        val structureId: StructureId,
        val archetype: StructureArchetype,
        val footprint: Footprint,
        val derivedSeed: Long,
    ) : TownCommand

    @Serializable
    data class RerollStructure(
        override val seq: Int,
        override val atMs: Long,
        val structureId: StructureId,
        val newVariantIndex: Int,
    ) : TownCommand

    @Serializable
    data class Demolish(
        override val seq: Int,
        override val atMs: Long,
        val structureId: StructureId,
    ) : TownCommand

    @Serializable
    data class SetTimeOfDay(
        override val seq: Int,
        override val atMs: Long,
        val timeOfDay: TimeOfDay,
    ) : TownCommand
}
