package com.studio.gladetowns.core.data.repo

import com.studio.gladetowns.core.data.db.dao.DioramaDao
import com.studio.gladetowns.core.data.db.entity.DioramaEntity
import com.studio.gladetowns.core.data.blob.CommandLogCodec
import com.studio.gladetowns.core.domain.model.time.TimeOfDay
import com.studio.gladetowns.core.domain.model.town.GridSize
import com.studio.gladetowns.core.domain.model.town.TownId
import com.studio.gladetowns.core.domain.model.town.TownMeta
import com.studio.gladetowns.core.domain.repository.DioramaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DioramaRepositoryImpl @Inject constructor(
    private val dao: DioramaDao,
) : DioramaRepository {

    // Field, not a constructor param: Dagger cannot inject defaulted lambdas.
    private val clock: () -> Long = System::currentTimeMillis

    override fun observeDioramas(): Flow<List<TownMeta>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }

    override fun observeLastDraft(): Flow<TownMeta?> =
        dao.observeLastDraft().map { it?.toDomain() }

    override suspend fun createTown(
        name: String,
        gridSize: GridSize,
        masterSeed: Long,
    ): TownMeta {
        val now = clock()
        val entity = DioramaEntity(
            id = UUID.randomUUID().toString(),
            name = name,
            status = "DRAFT",
            gridSize = gridSize.cells,
            masterSeed = masterSeed,
            generatorVersion = CommandLogCodec.CURRENT_GENERATOR_VERSION,
            createdAt = now,
            updatedAt = now,
            sealedAt = null,
            structureCount = 0,
            cellCoverage = 0f,
            timeOfDay = TimeOfDay.AFTERNOON.name,
            thumbVersion = 0,
            isDeleted = false,
            deleteAfter = null,
        )
        dao.insert(entity)
        return entity.toDomain()
    }

    override suspend fun rename(id: TownId, name: String) =
        dao.rename(id.value, name, clock())

    override suspend fun seal(id: TownId) =
        dao.seal(id.value, clock())

    override suspend fun softDelete(id: TownId) =
        dao.softDelete(id.value, purgeAt = clock() + PURGE_GRACE_MS)

    override suspend fun getMeta(id: TownId): TownMeta? =
        dao.getById(id.value)?.toDomain()

    private companion object {
        const val PURGE_GRACE_MS = 30L * 24 * 60 * 60 * 1000 // 30 days (TDD §7.5)
    }
}
