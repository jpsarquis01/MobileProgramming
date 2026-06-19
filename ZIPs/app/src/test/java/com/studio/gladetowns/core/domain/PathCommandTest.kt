package com.studio.gladetowns.core.domain

import com.studio.gladetowns.core.domain.model.command.TownCommand
import com.studio.gladetowns.core.domain.model.command.TownReducer
import com.studio.gladetowns.core.domain.model.structure.Footprint
import com.studio.gladetowns.core.domain.model.town.GridSize
import com.studio.gladetowns.core.domain.model.town.GridSpec
import com.studio.gladetowns.core.domain.model.town.TownState
import com.studio.gladetowns.core.domain.procgen.merge.TownGrowthEngine
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PathCommandTest {

    private val spec = GridSpec(GridSize.SMALL)

    @Test fun `DrawPath records hallway cells in state`() {
        val cells = listOf(Footprint.pack(2, 2), Footprint.pack(3, 2), Footprint.pack(4, 2))
        val cmd = TownCommand.DrawPath(seq = 0, atMs = 0L, cells = Footprint(cells))

        val state = TownReducer.reduce(TownState.empty(spec), cmd)

        assertTrue(state.paths.containsAll(cells))
        assertTrue(state.nextLocalSeq >= 1)
    }

    @Test fun `manual paths appear as roads in the grown layout`() {
        val growth = TownGrowthEngine(spec, masterSeed = 42L)
        growth.rebuild(emptyList())
        val path = setOf(Footprint.pack(5, 5), Footprint.pack(6, 5))
        growth.setManualPaths(path)

        val layout = growth.snapshot()

        assertTrue(layout.roads.containsAll(path))
    }

    @Test fun `manual paths are clipped to the board`() {
        val growth = TownGrowthEngine(spec, masterSeed = 1L)
        growth.rebuild(emptyList())
        val offBoard = Footprint.pack(GridSize.SMALL.cells + 4, 0)
        growth.setManualPaths(setOf(offBoard))

        assertFalse(growth.snapshot().roads.contains(offBoard))
    }
}
