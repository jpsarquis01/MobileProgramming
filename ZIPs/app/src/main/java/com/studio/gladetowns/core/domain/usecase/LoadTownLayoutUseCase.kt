package com.studio.gladetowns.core.domain.usecase

import com.studio.gladetowns.core.domain.model.command.TownReducer
import com.studio.gladetowns.core.domain.model.time.TimeOfDay
import com.studio.gladetowns.core.domain.model.town.TownId
import com.studio.gladetowns.core.domain.model.town.TownLayout
import com.studio.gladetowns.core.domain.model.town.TownMeta
import com.studio.gladetowns.core.domain.model.town.TownState
import com.studio.gladetowns.core.domain.procgen.merge.TownGrowthEngine
import com.studio.gladetowns.core.domain.repository.DioramaRepository
import com.studio.gladetowns.core.domain.repository.TownRepository
import javax.inject.Inject

/**
 * Read-only load that yields a fully grown [TownLayout] (meta + replay + grow).
 * Used by Explore and by gallery preview generation. Deterministic: the same
 * saved log always rebuilds the identical layout.
 */
class LoadTownLayoutUseCase @Inject constructor(
    private val dioramas: DioramaRepository,
    private val towns: TownRepository,
) {
    data class LoadedLayout(
        val meta: TownMeta,
        val layout: TownLayout,
        val timeOfDay: TimeOfDay,
    )

    suspend operator fun invoke(id: TownId): LoadedLayout? {
        val meta = dioramas.getMeta(id) ?: return null
        val commands = towns.loadCommands(id)
        val state = TownReducer.replay(TownState.empty(meta.gridSpec), commands)
        val growth = TownGrowthEngine(meta.gridSpec, meta.masterSeed)
        growth.rebuild(state.structuresOrdered())
        growth.setManualPaths(state.paths)
        return LoadedLayout(meta, growth.snapshot(), state.timeOfDay)
    }
}
