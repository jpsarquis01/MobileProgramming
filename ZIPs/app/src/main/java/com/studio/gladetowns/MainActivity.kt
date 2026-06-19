package com.studio.gladetowns

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.studio.gladetowns.core.audio.MusicManager
import com.studio.gladetowns.core.ui.theme.GladeTownsTheme
import com.studio.gladetowns.navigation.GladeNavHost
import dagger.hilt.android.AndroidEntryPoint

/**
 * Single-activity host (TDD §4). All screens are composables managed by
 * Navigation-Compose; the future Filament SurfaceView will be embedded
 * inside the Play/Explore screens via AndroidView, not separate activities.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GladeTownsTheme {
                GladeNavHost()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        MusicManager.start(this) // no-op until a res/raw/chill_music track is added
    }

    override fun onStop() {
        super.onStop()
        MusicManager.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isFinishing) MusicManager.release()
    }
}
