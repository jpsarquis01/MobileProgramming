package com.studio.gladetowns.core.data.repo

import com.studio.gladetowns.core.data.db.dao.SettingsDao
import com.studio.gladetowns.core.data.db.entity.SettingsEntity
import com.studio.gladetowns.core.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val dao: SettingsDao,
) : SettingsRepository {

    override fun observe(key: String, default: String): Flow<String> =
        dao.observe(key).map { it ?: default }

    override suspend fun put(key: String, value: String) =
        dao.put(SettingsEntity(key, value))
}
