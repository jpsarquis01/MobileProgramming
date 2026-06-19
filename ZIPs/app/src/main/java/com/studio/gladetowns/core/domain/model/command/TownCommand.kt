package com.studio.gladetowns.core.domain.model.command

import com.studio.gladetowns.core.domain.model.shape.RawStroke
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
 */
@Serializable
sealed interface TownCommand {
    val seq: Int
    val atMs: Long

    /**
     * The drawing pipeline's output. `stroke` is the serialized finger drawing
     * (optional / additive — old logs decode it as null); `footprint`,
     * `archetype` and `derivedSeed` are the recognized RESULT that drives all
     * generation, so replay never depends on re-running float recognition.
     */
    @Serializable
    data class PlaceStructure(
        override val seq: Int,
        override val atMs: Long,
        val structureId: StructureId,
        val archetype: StructureArchetype,
        val footprint: Footprint,
        val derivedSeed: Long,
        val stroke: RawStroke? = null,
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

    /**
     * A player-drawn hallway: the cells the finger traced become walkable path
     * cells (rendered as roads), so the player can connect rooms by hand. Like
     * every command this records only the input cells; the open doorways it
     * creates in adjacent room walls are derived at render time.
     */
    @Serializable
    data class DrawPath(
        override val seq: Int,
        override val atMs: Long,
        val cells: Footprint,
    ) : TownCommand
}
