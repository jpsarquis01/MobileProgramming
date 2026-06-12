package com.studio.gladetowns.core.engine

import com.studio.gladetowns.core.domain.model.time.TimeOfDay
import com.studio.gladetowns.core.domain.model.town.GridSpec

/**
 * The ONLY surface through which presentation/domain talk to the renderer
 * (TDD §5.1, §17). ViewModels depend on this interface — never on Filament —
 * keeping every ViewModel JVM-unit-testable with a fake sink.
 *
 * Foundation phase: implemented by [PlaceholderEngineController].
 * Phase 1+: implemented by the Filament-backed EngineController, consuming
 * TownDiff batches on the render thread.
 */
interface SceneCommandSink {
    fun setGrid(gridSpec: GridSpec)
    fun setTimeOfDay(timeOfDay: TimeOfDay)
    fun clearScene()
}
