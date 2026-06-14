package com.studio.gladetowns.core.domain.usecase

import com.studio.gladetowns.core.domain.model.town.TownId
import com.studio.gladetowns.core.domain.repository.DioramaRepository
import javax.inject.Inject

/**
 * Seal a town into a permanent diorama (DRAFT -> SEALED, TDD §12.2). Content is
 * already autosaved; sealing only flips status + stamps sealedAt so the gallery
 * can present it as a finished keepsake and route it to Explore.
 */
class SealTownUseCase @Inject constructor(
    private val dioramas: DioramaRepository,
) {
    suspend operator fun invoke(id: TownId) = dioramas.seal(id)
}
