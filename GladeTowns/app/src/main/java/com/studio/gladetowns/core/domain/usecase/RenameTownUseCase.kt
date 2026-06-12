package com.studio.gladetowns.core.domain.usecase

import com.studio.gladetowns.core.domain.model.town.TownId
import com.studio.gladetowns.core.domain.repository.DioramaRepository
import javax.inject.Inject

class RenameTownUseCase @Inject constructor(
    private val dioramas: DioramaRepository,
) {
    suspend operator fun invoke(id: TownId, name: String) {
        if (name.isNotBlank()) dioramas.rename(id, name.trim())
    }
}
