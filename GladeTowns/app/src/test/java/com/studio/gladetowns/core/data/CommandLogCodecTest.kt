package com.studio.gladetowns.core.data

import com.studio.gladetowns.core.data.blob.CommandLogCodec
import com.studio.gladetowns.core.domain.model.command.TownCommand
import com.studio.gladetowns.core.domain.model.structure.Footprint
import com.studio.gladetowns.core.domain.model.structure.StructureArchetype
import com.studio.gladetowns.core.domain.model.structure.StructureId
import com.studio.gladetowns.core.domain.model.time.TimeOfDay
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class CommandLogCodecTest {

    private val commands: List<TownCommand> = listOf(
        TownCommand.PlaceStructure(
            seq = 0, atMs = 1000L,
            structureId = StructureId(1),
            archetype = StructureArchetype.TOWER,
            footprint = Footprint(listOf(Footprint.pack(5, 5))),
            derivedSeed = -42L,
        ),
        TownCommand.SetTimeOfDay(1, 2000L, TimeOfDay.SUNSET),
        TownCommand.Demolish(2, 3000L, StructureId(1)),
    )

    @Test
    fun `round trip preserves the full polymorphic command list`() {
        val decoded = CommandLogCodec.decode(CommandLogCodec.encode(commands))
        assertEquals(commands, decoded.commands)
        assertEquals(CommandLogCodec.CURRENT_GENERATOR_VERSION, decoded.generatorVersion)
    }

    @Test
    fun `corrupted payload fails CRC instead of half-deserializing`() {
        val bytes = CommandLogCodec.encode(commands)
        bytes[bytes.size - 3] = (bytes[bytes.size - 3] + 1).toByte() // flip a payload byte
        assertThrows(CommandLogCodec.CorruptBlobException::class.java) {
            CommandLogCodec.decode(bytes)
        }
    }

    @Test
    fun `bad magic is rejected`() {
        val bytes = CommandLogCodec.encode(commands)
        bytes[0] = 0
        assertThrows(CommandLogCodec.CorruptBlobException::class.java) {
            CommandLogCodec.decode(bytes)
        }
    }
}
