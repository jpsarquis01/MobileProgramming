package com.studio.gladetowns.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.studio.gladetowns.feature.explore.ExploreScreen
import com.studio.gladetowns.feature.gallery.GalleryScreen
import com.studio.gladetowns.feature.menu.MenuScreen
import com.studio.gladetowns.feature.play.PlayScreen

/**
 * Navigation graph (TDD §15.1).
 *
 * Menu ── Play ──► Play(townId?)            build / resume a town
 *      │        └─ Explore(townId)          preview while building
 *      └─ Dioramas ──► Gallery ─┬─► Play(townId)     reopen a DRAFT
 *                               └─► Explore(townId)  view a SEALED diorama
 */
@Composable
fun GladeNavHost() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.MENU) {

        composable(Routes.MENU) {
            MenuScreen(
                onPlay = { navController.navigate(Routes.playNew()) },
                onContinue = { id -> navController.navigate(Routes.playExisting(id)) },
                onDioramas = { navController.navigate(Routes.GALLERY) },
            )
        }

        composable(
            route = Routes.PLAY,
            arguments = listOf(
                navArgument(Routes.ARG_TOWN_ID) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
            ),
        ) {
            PlayScreen(
                onExit = { navController.popBackStack() },
                onExplore = { id -> navController.navigate(Routes.explore(id)) },
            )
        }

        composable(
            route = Routes.EXPLORE,
            arguments = listOf(
                navArgument(Routes.ARG_TOWN_ID) { type = NavType.StringType },
            ),
        ) {
            ExploreScreen(onExit = { navController.popBackStack() })
        }

        composable(Routes.GALLERY) {
            GalleryScreen(
                onBack = { navController.popBackStack() },
                onOpen = { id, sealed ->
                    if (sealed) navController.navigate(Routes.explore(id))
                    else navController.navigate(Routes.playExisting(id))
                },
            )
        }
    }
}
