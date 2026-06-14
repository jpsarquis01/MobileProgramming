package com.studio.gladetowns.core.domain.repository

import com.studio.gladetowns.core.domain.model.town.GridSize
import com.studio.gladetowns.core.domain.model.town.TownId
import com.studio.gladetowns.core.domain.model.town.TownMeta
import kotlinx.coroutines.flow.Flow

/**
 * Gallery/metadata operations. Implementations live in core.data; the domain
 * layer depends only on this contract (Clean Architecture boundary, TDD §3.1).
 */
interface DioramaRepository {

    /** All non-deleted towns, newest first. Backed by Room -> reactive. */
    fun observeDioramas(): Flow<List<TownMeta>>

    /** Most recently updated draft, for the menu's Continue chip. */
    fun observeLastDraft(): Flow<TownMeta?>

    suspend fun createTown(name: String, gridSize: GridSize, masterSeed: Long): TownMeta

    suspend fun rename(id: TownId, name: String)

    /** Seal a draft into a permanent diorama (DRAFT -> SEALED). */
    suspend fun seal(id: TownId)

    /** Soft delete: hidden immediately, purged after a grace period (TDD §7.5). */
    suspend fun softDelete(id: TownId)

    suspend fun getMeta(id: TownId): TownMeta?
}
