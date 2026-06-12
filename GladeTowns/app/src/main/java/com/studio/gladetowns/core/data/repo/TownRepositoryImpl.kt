package com.studio.gladetowns.core.data.repo

import com.studio.gladetowns.core.data.blob.CommandLogCodec
import com.studio.gladetowns.core.data.blob.TownBlobStore
import com.studio.gladetowns.core.data.db.dao.DioramaDao
import com.studio.gladetowns.core.data.db.dao.TownBlobDao
import com.studio.gladetowns.core.data.db.entity.TownBlobEntity
import com.studio.gladetowns.core.domain.model.command.TownCommand
import com.studio.gladetowns.core.domain.model.town.TownId
import com.studio.gladetowns.core.domain.repository.TownRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TownRepositoryImpl @Inject constructor(
    private val blobStore: TownBlobStore,
    private val blobDao: TownBlobDao,
    private val dioramaDao: DioramaDao,
) : TownRepository {

    // Field, not a constructor param: Dagger cannot inject defaulted lambdas.
    private val clock: () -> Long = System::currentTimeMillis

    override suspend fun loadCommands(id: TownId): List<TownCommand> =
        blobStore.read(id.value)

    override suspend fun appendCommands(id: TownId, commands: List<TownCommand>) {
        if (commands.isEmpty()) return
        val merged = (blobStore.read(id.value) + commands)
            .distinctBy { it.seq }
            .sortedBy { it.seq }
        persist(id, merged)
    }

    override suspend fun rewriteCommands(id: TownId, commands: List<TownCommand>) {
        persist(id, commands.sortedBy { it.seq })
    }

    private suspend fun persist(id: TownId, commands: List<TownCommand>) {
        val result = blobStore.write(id.value, commands)
        blobDao.upsert(
            TownBlobEntity(
                dioramaId = id.value,
                logRelativePath = result.relativePath,
                logCrc = result.crc,
                blobVersion = CommandLogCodec.BLOB_VERSION,
                sizeBytes = result.sizeBytes,
            ),
        )
        val structures = commands.count { it is TownCommand.PlaceStructure } -
            commands.count { it is TownCommand.Demolish }
        dioramaDao.touch(id.value, clock(), structures.coerceAtLeast(0))
    }
}
