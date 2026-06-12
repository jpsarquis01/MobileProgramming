package com.studio.gladetowns.core.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * Pointer + integrity record for a town's on-disk command log (TDD §16.1).
 * Keeping blobs on the filesystem keeps SQLite rows tiny and export trivial.
 */
@Entity(
    tableName = "town_blob",
    foreignKeys = [
        ForeignKey(
            entity = DioramaEntity::class,
            parentColumns = ["id"],
            childColumns = ["diorama_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class TownBlobEntity(
    @PrimaryKey @ColumnInfo(name = "diorama_id") val dioramaId: String,
    @ColumnInfo(name = "log_relative_path") val logRelativePath: String,
    @ColumnInfo(name = "log_crc") val logCrc: Long,
    @ColumnInfo(name = "blob_version") val blobVersion: Int,
    @ColumnInfo(name = "size_bytes") val sizeBytes: Long,
)
