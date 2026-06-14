package com.studio.gladetowns.core.domain

import com.studio.gladetowns.core.domain.model.structure.Footprint
import com.studio.gladetowns.core.domain.model.structure.Structure
import com.studio.gladetowns.core.domain.model.structure.StructureArchetype
import com.studio.gladetowns.core.domain.model.structure.StructureId
import com.studio.gladetowns.core.domain.model.town.GridSize
import com.studio.gladetowns.core.domain.model.town.GridSpec
import com.studio.gladetowns.core.domain.procgen.merge.TownGrowthEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TownGrowthEngineTest {

    private val spec = GridSpec(GridSize.MEDIUM)

    private fun house(id: Long, seq: Int, x: Int, y: Int) = Structure(
        id = StructureId(id),
        placementSeq = seq,
        archetype = StructureArchetype.HOUSE,
        footprint = Footprint((x..x + 1).flatMap { cx -> (y..y + 1).map { cy -> Footprint.pack(cx, cy) } }),
        structureSeed = id * 7919L,
    )

    @Test
    fun `incremental append equals full rebuild`() {
        val structures = listOf(house(1, 0, 5, 5), house(2, 1, 12, 5), house(3, 2, 9, 11))

        val incremental = TownGrowthEngine(spec, masterSeed = 99L)
        structures.forEach { incremental.append(it) }

        val rebuilt = TownGrowthEngine(spec, masterSeed = 99L)
        rebuilt.rebuild(structures)

        assertEquals(rebuilt.snapshot(), incremental.snapshot())
    }

    @Test
    fun `placing a second building creates a connecting road`() {
        val engine = TownGrowthEngine(spec, masterSeed = 1L)
        engine.append(house(1, 0, 5, 5))
        engine.append(house(2, 1, 12, 5))
        assertTrue(engine.snapshot().roads.isNotEmpty())
    }
}
