package com.studio.gladetowns.core.domain.model.town

import com.studio.gladetowns.core.domain.model.time.TimeOfDay

@JvmInline
value class TownId(val value: String)

enum class TownStatus { DRAFT, SEALED }

/** Aggregate display statistics, denormalised into the diorama table. */
data class TownStats(
    val structureCount: Int = 0,
    val cellCoverage: Float = 0f,
)

/**
 * Everything the gallery and menu need to know about a town *without*
 * touching its blob. Mirrors the `diorama` Room table (TDD §16.1).
 */
data class TownMeta(
    val id: TownId,
    val name: String,
    val status: TownStatus,
    val gridSpec: GridSpec,
    val masterSeed: Long,
    val generatorVersion: Int,
    val createdAtMs: Long,
    val updatedAtMs: Long,
    val sealedAtMs: Long?,
    val timeOfDay: TimeOfDay,
    val stats: TownStats,
)
