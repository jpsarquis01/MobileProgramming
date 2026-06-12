package com.studio.gladetowns.core.domain.model.command

import com.studio.gladetowns.core.domain.model.structure.Structure
import com.studio.gladetowns.core.domain.model.town.TownState

/**
 * Pure, deterministic reducer: (state, command) → state.
 * No randomness, no clocks, no I/O — unit-testable on the JVM and the
 * single source of truth for what a command *means* (TDD §6.3).
 */
object TownReducer {

    fun reduce(state: TownState, command: TownCommand): TownState = when (command) {

        is TownCommand.PlaceStructure -> state.copy(
            structures = state.structures.put(
                command.structureId,
                Structure(
                    id = command.structureId,
                    placementSeq = command.seq,
                    archetype = command.archetype,
                    footprint = command.footprint,
                    structureSeed = command.derivedSeed,
                ),
            ),
            nextLocalSeq = maxOf(state.nextLocalSeq, command.seq + 1),
        )

        is TownCommand.RerollStructure -> {
            val s = state.structures[command.structureId] ?: return state
            state.copy(
                structures = state.structures.put(
                    s.id,
                    s.copy(variantIndex = command.newVariantIndex),
                ),
                nextLocalSeq = maxOf(state.nextLocalSeq, command.seq + 1),
            )
        }

        is TownCommand.Demolish -> state.copy(
            structures = state.structures.remove(command.structureId),
            nextLocalSeq = maxOf(state.nextLocalSeq, command.seq + 1),
        )

        is TownCommand.SetTimeOfDay -> state.copy(
            timeOfDay = command.timeOfDay,
            nextLocalSeq = maxOf(state.nextLocalSeq, command.seq + 1),
        )
    }

    fun replay(initial: TownState, commands: List<TownCommand>): TownState =
        commands.sortedBy { it.seq }.fold(initial, ::reduce)
}
