package com.studio.gladetowns.core.domain

import com.studio.gladetowns.core.domain.model.structure.BuildingMass
import com.studio.gladetowns.core.domain.model.structure.Footprint
import com.studio.gladetowns.core.domain.model.structure.GridRect
import com.studio.gladetowns.core.domain.model.structure.RoofKind
import com.studio.gladetowns.core.domain.model.structure.StructureArchetype
import com.studio.gladetowns.core.domain.model.structure.StructureId
import com.studio.gladetowns.core.domain.model.structure.StructurePlan
import com.studio.gladetowns.core.domain.model.town.TownLayout
import com.studio.gladetowns.core.domain.model.town.TownPreview
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TownPreviewTest {

    private fun plan(id: Long, x: Int, y: Int, w: Int, h: Int, palette: Int): StructurePlan {
        val cells = buildList { for (cy in y until y + h) for (cx in x until x + w) add(Footprint.pack(cx, cy)) }
        return StructurePlan(
            structureId = StructureId(id),
            archetype = StructureArchetype.HOUSE,
            masses = listOf(BuildingMass(GridRect(x, y, w, h), floors = 2, roof = RoofKind.GABLE)),
            paletteIndex = palette,
            footprint = cells,
        )
    }

    @Test
    fun `from downsamples buildings and roads into a fixed grid`() {
        val cells = 50
        val road = setOf(Footprint.pack(0, 0), Footprint.pack(10, 10))
        val layout = TownLayout(
            gridCells = cells,
            structures = listOf(plan(1L, 2, 2, 4, 4, palette = 3)),
            roads = road,
            ownerByCell = emptyMap(),
        )

        val preview = TownPreview.from(layout)

        assertEquals(TownPreview.SAMPLE, preview.sample)
        assertEquals(preview.sample * preview.sample, preview.palette.size)
        // The building footprint mapped to at least one painted sample cell.
        assertTrue("expected at least one painted cell", preview.palette.any { it == 3 })
        // Roads were recorded.
        assertTrue("expected at least one road cell", preview.roads.any { it })
    }

    @Test
    fun `empty layout yields an empty preview`() {
        val preview = TownPreview.from(
            TownLayout(gridCells = 20, structures = emptyList(), roads = emptySet(), ownerByCell = emptyMap()),
        )
        assertTrue(preview.palette.all { it == -1 })
        assertTrue(preview.roads.none { it })
    }
}
