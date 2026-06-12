package com.studio.gladetowns.core.domain.usecase

import com.studio.gladetowns.core.domain.model.town.TownMeta
import com.studio.gladetowns.core.domain.repository.DioramaRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ListDioramasUseCase @Inject constructor(
    private val dioramas: DioramaRepository,
) {
    operator fun invoke(): Flow<List<TownMeta>> = dioramas.observeDioramas()
}
