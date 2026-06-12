package com.studio.gladetowns.core.domain.usecase

import com.studio.gladetowns.core.domain.model.command.TownReducer
import com.studio.gladetowns.core.domain.model.town.TownId
import com.studio.gladetowns.core.domain.model.town.TownMeta
import com.studio.gladetowns.core.domain.model.town.TownState
import com.studio.gladetowns.core.domain.repository.DioramaRepository
import com.studio.gladetowns.core.domain.repository.TownRepository
import javax.inject.Inject

data class LoadedTown(val meta: TownMeta, val state: TownState)

class LoadTownUseCase @Inject constructor(
    private val dioramas: DioramaRepository,
    private val towns: TownRepository,
) {
    /** Load = read metadata + replay the command log (TDD §7.3). */
    suspend operator fun invoke(id: TownId): LoadedTown? {
        val meta = dioramas.getMeta(id) ?: return null
        val commands = towns.loadCommands(id)
        val state = TownReducer.replay(TownState.empty(meta.gridSpec), commands)
        return LoadedTown(meta, state)
    }
}
