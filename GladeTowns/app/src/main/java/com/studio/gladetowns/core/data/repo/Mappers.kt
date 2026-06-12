package com.studio.gladetowns.core.data.repo

import com.studio.gladetowns.core.data.db.entity.DioramaEntity
import com.studio.gladetowns.core.domain.model.time.TimeOfDay
import com.studio.gladetowns.core.domain.model.town.GridSize
import com.studio.gladetowns.core.domain.model.town.GridSpec
import com.studio.gladetowns.core.domain.model.town.TownId
import com.studio.gladetowns.core.domain.model.town.TownMeta
import com.studio.gladetowns.core.domain.model.town.TownStats
import com.studio.gladetowns.core.domain.model.town.TownStatus

/** Entity ↔ domain mapping lives in one place; ViewModels never see entities. */
fun DioramaEntity.toDomain(): TownMeta = TownMeta(
    id = TownId(id),
    name = name,
    status = TownStatus.valueOf(status),
    gridSpec = GridSpec(GridSize.fromCells(gridSize)),
    masterSeed = masterSeed,
    generatorVersion = generatorVersion,
    createdAtMs = createdAt,
    updatedAtMs = updatedAt,
    sealedAtMs = sealedAt,
    timeOfDay = TimeOfDay.valueOf(timeOfDay),
    stats = TownStats(structureCount = structureCount, cellCoverage = cellCoverage),
)
