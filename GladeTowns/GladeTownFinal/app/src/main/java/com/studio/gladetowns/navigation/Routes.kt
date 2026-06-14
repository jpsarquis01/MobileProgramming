package com.studio.gladetowns.navigation

/**
 * Centralised route definitions. Arguments are part of the contract so
 * ViewModels can read them from [androidx.lifecycle.SavedStateHandle]
 * (process-death safe — TDD §5.1).
 */
object Routes {
    const val MENU = "menu"
    const val GALLERY = "gallery"

    const val ARG_TOWN_ID = "townId"

    /** `townId` is optional: absent -> "new town" setup flow inside Play. */
    const val PLAY = "play?$ARG_TOWN_ID={$ARG_TOWN_ID}"

    /** Read-only diorama viewer; `townId` required. */
    const val EXPLORE = "explore/{$ARG_TOWN_ID}"

    fun playNew() = "play"
    fun playExisting(townId: String) = "play?$ARG_TOWN_ID=$townId"
    fun explore(townId: String) = "explore/$townId"
}
