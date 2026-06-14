package com.studio.gladetowns.core.domain

import com.studio.gladetowns.core.domain.model.structure.Footprint
import com.studio.gladetowns.core.domain.model.structure.Structure
import com.studio.gladetowns.core.domain.model.structure.StructureArchetype
import com.studio.gladetowns.core.domain.model.structure.StructureId
import com.studio.gladetowns.core.domain.procgen.generate.BuildingGenerator
import org.junit.Assert.assertEquals
import org.junit.Test

class BuildingGeneratorTest {

    private fun structure(variant: Int) = Structure(
        id = StructureId(1),
        placementSeq = 0,
        archetype = StructureArchetype.HOUSE,
        footprint = Footprint(
            (3..6).flatMap { x -> (3..6).map { y -> Footprint.pack(x, y) } },
        ),
        structureSeed = 0xBEEFL,
        variantIndex = variant,
    )

    private val ctx = BuildingGenerator.GenContext(neighborMaxFloors = 0, paletteIndex = 2)

    @Test
    fun `generation is deterministic for identical inputs`() {
        val a = BuildingGenerator.generate(structure(0), ctx)
        val b = BuildingGenerator.generate(structure(0), ctx)
        assertEquals(a, b)
    }

    @Test
    fun `palette index comes from context`() {
        assertEquals(2, BuildingGenerator.generate(structure(0), ctx).paletteIndex)
    }
}
