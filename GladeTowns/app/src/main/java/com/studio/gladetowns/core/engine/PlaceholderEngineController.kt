package com.studio.gladetowns.core.engine

import com.studio.gladetowns.core.domain.model.time.TimeOfDay
import com.studio.gladetowns.core.domain.model.town.GridSpec
import javax.inject.Inject
import javax.inject.Singleton

/**
 * No-op engine for the foundation phase. The Play screen currently renders
 * its placeholder grid with Compose Canvas; once the Filament host lands
 * (Phase 1, TDD roadmap M0.3/M1), this class is replaced behind the same
 * [SceneCommandSink] interface with zero changes above it.
 *
 * See docs/RENDERING.md for the full rendering architecture recommendation.
 */
@Singleton
class PlaceholderEngineController @Inject constructor() : SceneCommandSink {
    override fun setGrid(gridSpec: GridSpec) = Unit
    override fun setTimeOfDay(timeOfDay: TimeOfDay) = Unit
    override fun clearScene() = Unit
}
