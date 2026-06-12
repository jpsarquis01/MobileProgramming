package com.studio.gladetowns.core.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.studio.gladetowns.core.data.db.dao.DioramaDao
import com.studio.gladetowns.core.data.db.dao.SettingsDao
import com.studio.gladetowns.core.data.db.dao.TownBlobDao
import com.studio.gladetowns.core.data.db.entity.DioramaEntity
import com.studio.gladetowns.core.data.db.entity.SettingsEntity
import com.studio.gladetowns.core.data.db.entity.TownBlobEntity

/**
 * Schema v1 (TDD §16). Schemas are exported to app/schemas by KSP; CI diffs
 * them so accidental schema changes are caught. Destructive migration is
 * forbidden — every future version ships an explicit Migration.
 */
@Database(
    entities = [DioramaEntity::class, TownBlobEntity::class, SettingsEntity::class],
    version = 1,
    exportSchema = true,
)
abstract class GladeDatabase : RoomDatabase() {
    abstract fun dioramaDao(): DioramaDao
    abstract fun townBlobDao(): TownBlobDao
    abstract fun settingsDao(): SettingsDao

    companion object {
        const val NAME = "glade_towns.db"
    }
}
