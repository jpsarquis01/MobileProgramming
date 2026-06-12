package com.studio.gladetowns.core.domain.repository

import kotlinx.coroutines.flow.Flow

/** Small typed key-value settings (quality tier, audio…), Room-backed. */
interface SettingsRepository {
    fun observe(key: String, default: String): Flow<String>
    suspend fun put(key: String, value: String)

    companion object Keys {
        const val QUALITY_TIER = "quality_tier"   // AUTO | HIGH | MID | LOW
        const val HAPTICS = "haptics_enabled"
    }
}
