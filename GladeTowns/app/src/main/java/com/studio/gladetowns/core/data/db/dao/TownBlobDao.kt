package com.studio.gladetowns.core.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.studio.gladetowns.core.data.db.entity.TownBlobEntity

@Dao
interface TownBlobDao {

    @Query("SELECT * FROM town_blob WHERE diorama_id = :dioramaId LIMIT 1")
    suspend fun get(dioramaId: String): TownBlobEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: TownBlobEntity)
}
