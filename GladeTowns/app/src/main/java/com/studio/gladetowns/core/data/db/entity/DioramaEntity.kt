package com.studio.gladetowns.core.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Metadata row per town — mirrors TDD §16.1. Town *content* never lives in
 * SQLite; it lives in the blob store with a URI recorded in [TownBlobEntity].
 */
@Entity(
    tableName = "diorama",
    indices = [
        Index(value = ["status", "sealed_at"]),
        Index(value = ["is_deleted", "delete_after"]),
        Index(value = ["updated_at"]),
    ],
)
data class DioramaEntity(
    @PrimaryKey val id: String,
    val name: String,
    val status: String,                                   // DRAFT | SEALED
    @ColumnInfo(name = "grid_size") val gridSize: Int,    // 20 | 50 | 100
    @ColumnInfo(name = "master_seed") val masterSeed: Long,
    @ColumnInfo(name = "generator_version") val generatorVersion: Int,
    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "updated_at") val updatedAt: Long,
    @ColumnInfo(name = "sealed_at") val sealedAt: Long?,
    @ColumnInfo(name = "structure_count") val structureCount: Int,
    @ColumnInfo(name = "cell_coverage") val cellCoverage: Float,
    @ColumnInfo(name = "time_of_day") val timeOfDay: String,
    @ColumnInfo(name = "thumb_version") val thumbVersion: Int,
    @ColumnInfo(name = "is_deleted") val isDeleted: Boolean,
    @ColumnInfo(name = "delete_after") val deleteAfter: Long?,
)
