package com.studio.gladetowns.core.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.studio.gladetowns.core.data.db.entity.DioramaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DioramaDao {

    @Query("SELECT * FROM diorama WHERE is_deleted = 0 ORDER BY updated_at DESC")
    fun observeAll(): Flow<List<DioramaEntity>>

    @Query(
        "SELECT * FROM diorama WHERE is_deleted = 0 AND status = 'DRAFT' " +
            "ORDER BY updated_at DESC LIMIT 1",
    )
    fun observeLastDraft(): Flow<DioramaEntity?>

    @Query("SELECT * FROM diorama WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): DioramaEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(entity: DioramaEntity)

    @Query("UPDATE diorama SET name = :name, updated_at = :now WHERE id = :id")
    suspend fun rename(id: String, name: String, now: Long)

    @Query("UPDATE diorama SET updated_at = :now, structure_count = :structures WHERE id = :id")
    suspend fun touch(id: String, now: Long, structures: Int)

    @Query("UPDATE diorama SET is_deleted = 1, delete_after = :purgeAt WHERE id = :id")
    suspend fun softDelete(id: String, purgeAt: Long)

    @Query("DELETE FROM diorama WHERE is_deleted = 1 AND delete_after < :now")
    suspend fun purgeExpired(now: Long): Int
}
