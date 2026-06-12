package com.studio.gladetowns.core.domain

import com.studio.gladetowns.core.domain.model.command.TownCommand
import com.studio.gladetowns.core.domain.model.command.TownReducer
import com.studio.gladetowns.core.domain.model.structure.Footprint
import com.studio.gladetowns.core.domain.model.structure.StructureArchetype
import com.studio.gladetowns.core.domain.model.structure.StructureId
import com.studio.gladetowns.core.domain.model.time.TimeOfDay
import com.studio.gladetowns.core.domain.model.town.GridSize
import com.studio.gladetowns.core.domain.model.town.GridSpec
import com.studio.gladetowns.core.domain.model.town.TownState
import org.junit.Assert.assertEquals
import org.junit.Test

class TownReducerTest {

    private val spec = GridSpec(GridSize.SMALL)

    private fun place(seq: Int, id: Long) = TownCommand.PlaceStructure(
        seq = seq,
        atMs = 0L,
        structureId = StructureId(id),
        archetype = StructureArchetype.HOUSE,
        footprint = Footprint(listOf(Footprint.pack(1, 1), Footprint.pack(1, 2))),
        derivedSeed = 777L,
    )

    @Test
    fun `place then demolish leaves an empty town`() {
        val state = TownReducer.replay(
            TownState.empty(spec),
            listOf(place(0, 1), TownCommand.Demolish(1, 0L, StructureId(1))),
        )
        assertEquals(0, state.structures.size)
        assertEquals(2, state.nextLocalSeq)
    }

    @Test
    fun `replay is order independent because commands sort by seq`() {
        val cmds = listOf(
            TownCommand.SetTimeOfDay(2, 0L, TimeOfDay.NIGHT),
            place(0, 1),
            place(1, 2),
        )
        val a = TownReducer.replay(TownState.empty(spec), cmds)
        val b = TownReducer.replay(TownState.empty(spec), cmds.reversed())
        assertEquals(a, b)
        assertEquals(TimeOfDay.NIGHT, a.timeOfDay)
        assertEquals(2, a.structures.size)
    }

    @Test
    fun `reroll only touches the targeted structure`() {
        val state = TownReducer.replay(
            TownState.empty(spec),
            listOf(
                place(0, 1),
                place(1, 2),
                TownCommand.RerollStructure(2, 0L, StructureId(1), newVariantIndex = 3),
            ),
        )
        assertEquals(3, state.structures[StructureId(1)]!!.variantIndex)
        assertEquals(0, state.structures[StructureId(2)]!!.variantIndex)
    }
}
