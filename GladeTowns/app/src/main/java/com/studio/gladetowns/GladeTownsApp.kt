package com.studio.gladetowns

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application entry point. Hosts the Hilt dependency graph.
 *
 * Foundation phase keeps startup trivial; later phases add:
 *  - Filament engine warm-up (TDD §15.1 SplashWarmup)
 *  - Module catalog preload (TDD §4 core.data.assets)
 */
@HiltAndroidApp
class GladeTownsApp : Application()
