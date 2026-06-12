package com.studio.gladetowns.core.domain.model.time

import kotlinx.serialization.Serializable

/** Art-directed lighting states (TDD §14). */
@Serializable
enum class TimeOfDay { MORNING, AFTERNOON, SUNSET, NIGHT }
