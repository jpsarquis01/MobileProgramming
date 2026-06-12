package com.studio.gladetowns.core.domain.repository

import com.studio.gladetowns.core.domain.model.command.TownCommand
import com.studio.gladetowns.core.domain.model.town.TownId

/**
 * Town-content operations over the event-sourced blob store (TDD §7).
 * Append-only by design: autosave is "append the commands since last flush".
 */
interface TownRepository {

    /** Full ordered command log for a town (empty list for a fresh town). */
    suspend fun loadCommands(id: TownId): List<TownCommand>

    /** Atomically append commands and bump the diorama's updatedAt. */
    suspend fun appendCommands(id: TownId, commands: List<TownCommand>)

    /**
     * Rewrite the entire log (used by undo-trim on exit: discarded redo tail
     * must not be resurrected on next load).
     */
    suspend fun rewriteCommands(id: TownId, commands: List<TownCommand>)
}
