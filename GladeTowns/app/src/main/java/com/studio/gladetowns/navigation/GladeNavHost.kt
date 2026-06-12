package com.studio.gladetowns.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.studio.gladetowns.feature.gallery.GalleryScreen
import com.studio.gladetowns.feature.menu.MenuScreen
import com.studio.gladetowns.feature.play.PlayScreen

/**
 * Navigation graph (TDD §15.1).
 *
 * Menu ── Play ──► Play(townId?)        build / resume a town
 *      └─ Dioramas ──► Gallery ──► Play(townId)   revisit a saved town
 *
 * Explore is intentionally absent in the foundation phase; it will join the
 * graph as a sibling of Play sharing a nav-graph-scoped TownSessionHolder.
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
            PlayScreen(onExit = { navController.popBackStack() })
        }

        composable(Routes.GALLERY) {
            GalleryScreen(
                onBack = { navController.popBackStack() },
                onOpenTown = { id ->
                    navController.navigate(Routes.playExisting(id))
                },
            )
        }
    }
}
