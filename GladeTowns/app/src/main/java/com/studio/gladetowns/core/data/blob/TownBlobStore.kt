package com.studio.gladetowns.core.data.blob

import com.studio.gladetowns.core.domain.model.command.TownCommand
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

/**
 * On-disk home of town content (TDD §16.2):
 *
 *   files/towns/{dioramaId}/log.bin
 *
 * Safety properties (TDD §7.5):
 *  - **Atomic writes**: write to log.bin.tmp then rename. A crash mid-write
 *    leaves the previous good file untouched.
 *  - **Last-known-good fallback**: before replacing, the previous log is
 *    rotated to log.bak; if log.bin fails CRC on read we recover from .bak.
 *  - **Single-writer**: a per-store mutex serialises writes; callers are the
 *    repository layer only.
 *
 * Foundation note: v1 rewrites the full log per flush — logs are tiny
 * (KBs) so this is fine. The segmented append format (log.000, log.001…)
 * from TDD §7.2 is a drop-in upgrade behind this same API.
 */
@Singleton
class TownBlobStore @Inject constructor(
    @Named("filesDir") private val filesDir: File,
    @Named("io") private val io: CoroutineDispatcher,
) {
    private val mutex = Mutex()

    fun logFile(dioramaId: String): File =
        File(File(File(filesDir, "towns"), dioramaId), "log.bin")

    suspend fun write(dioramaId: String, commands: List<TownCommand>): WriteResult =
        withContext(io) {
            mutex.withLock {
                val target = logFile(dioramaId)
                target.parentFile?.mkdirs()
                val bytes = CommandLogCodec.encode(commands)

                val tmp = File(target.parentFile, target.name + ".tmp")
                tmp.writeBytes(bytes)

                val bak = File(target.parentFile, target.name + ".bak")
                if (target.exists()) {
                    bak.delete()
                    target.renameTo(bak)
                }
                check(tmp.renameTo(target)) { "Atomic rename failed for $target" }

                WriteResult(
                    relativePath = "towns/$dioramaId/log.bin",
                    crc = CommandLogCodec.crcOf(bytes),
                    sizeBytes = bytes.size.toLong(),
                )
            }
        }

    suspend fun read(dioramaId: String): List<TownCommand> = withContext(io) {
        val target = logFile(dioramaId)
        if (!target.exists()) return@withContext emptyList()
        try {
            CommandLogCodec.decode(target.readBytes()).commands
        } catch (e: CommandLogCodec.CorruptBlobException) {
            // Recovery path: last-known-good backup.
            val bak = File(target.parentFile, target.name + ".bak")
            if (bak.exists()) CommandLogCodec.decode(bak.readBytes()).commands
            else throw e
        }
    }

    data class WriteResult(val relativePath: String, val crc: Long, val sizeBytes: Long)
}
