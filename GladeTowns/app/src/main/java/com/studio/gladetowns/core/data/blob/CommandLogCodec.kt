package com.studio.gladetowns.core.data.blob

import com.studio.gladetowns.core.domain.model.command.TownCommand
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.util.zip.CRC32
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

/**
 * Binary format for the town command log (TDD §7, §16.2).
 *
 *   [magic:int][blobVersion:int][generatorVersion:int]
 *   [payloadLength:int][crc32:long][gzip(cbor(List<TownCommand>))]
 *
 * Rules:
 *  - The header is fixed forever; only blobVersion gates payload evolution.
 *  - CBOR + sealed-class polymorphism means new command types are additive.
 *  - CRC over the compressed payload detects torn/corrupt files before
 *    deserialization can crash or, worse, half-succeed.
 */
object CommandLogCodec {

    const val MAGIC = 0x474C4144            // "GLAD"
    const val BLOB_VERSION = 1

    /** Bumped whenever generation rules change visibly (TDD §7.4). */
    const val CURRENT_GENERATOR_VERSION = 1

    @OptIn(ExperimentalSerializationApi::class)
    private val cbor = Cbor { ignoreUnknownKeys = true }

    class CorruptBlobException(message: String) : Exception(message)

    data class DecodedLog(
        val generatorVersion: Int,
        val commands: List<TownCommand>,
    )

    @OptIn(ExperimentalSerializationApi::class)
    fun encode(
        commands: List<TownCommand>,
        generatorVersion: Int = CURRENT_GENERATOR_VERSION,
    ): ByteArray {
        val raw = cbor.encodeToByteArray(commands)
        val compressed = ByteArrayOutputStream().use { bos ->
            GZIPOutputStream(bos).use { it.write(raw) }
            bos.toByteArray()
        }
        val crc = CRC32().apply { update(compressed) }.value

        val out = ByteArrayOutputStream(compressed.size + 24)
        DataOutputStream(out).use { dos ->
            dos.writeInt(MAGIC)
            dos.writeInt(BLOB_VERSION)
            dos.writeInt(generatorVersion)
            dos.writeInt(compressed.size)
            dos.writeLong(crc)
            dos.write(compressed)
        }
        return out.toByteArray()
    }

    @OptIn(ExperimentalSerializationApi::class)
    fun decode(bytes: ByteArray): DecodedLog {
        DataInputStream(ByteArrayInputStream(bytes)).use { dis ->
            if (dis.readInt() != MAGIC) throw CorruptBlobException("Bad magic")
            val blobVersion = dis.readInt()
            if (blobVersion > BLOB_VERSION) {
                throw CorruptBlobException("Blob from a newer app version: $blobVersion")
            }
            val generatorVersion = dis.readInt()
            val length = dis.readInt()
            val expectedCrc = dis.readLong()
            val compressed = ByteArray(length)
            dis.readFully(compressed)

            val actualCrc = CRC32().apply { update(compressed) }.value
            if (actualCrc != expectedCrc) throw CorruptBlobException("CRC mismatch")

            val raw = GZIPInputStream(ByteArrayInputStream(compressed)).use { it.readBytes() }
            val commands: List<TownCommand> = cbor.decodeFromByteArray(raw)
            return DecodedLog(generatorVersion, commands)
        }
    }

    fun crcOf(bytes: ByteArray): Long = CRC32().apply { update(bytes) }.value
}
