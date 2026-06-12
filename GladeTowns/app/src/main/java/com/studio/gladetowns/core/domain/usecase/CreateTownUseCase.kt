package com.studio.gladetowns.core.domain.usecase

import com.studio.gladetowns.core.domain.model.town.GridSize
import com.studio.gladetowns.core.domain.model.town.TownMeta
import com.studio.gladetowns.core.domain.repository.DioramaRepository
import javax.inject.Inject
import kotlin.random.Random

class CreateTownUseCase @Inject constructor(
    private val dioramas: DioramaRepository,
) {
    /**
     * The master seed is rolled exactly once, here, and persisted in metadata.
     * Every other random decision in the town's life derives from it
     * (SplitMix64 tree, TDD §8.1) — this is the determinism root.
     */
    suspend operator fun invoke(name: String, gridSize: GridSize): TownMeta =
        dioramas.createTown(
            name = name.ifBlank { "Untitled Glade" },
            gridSize = gridSize,
            masterSeed = Random.nextLong(),
        )
}
