package com.studio.gladetowns.feature.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.studio.gladetowns.core.audio.MusicManager
import com.studio.gladetowns.core.ui.components.AnimatedLandscape
import com.studio.gladetowns.core.ui.components.ChalkButton

@Composable
fun MenuScreen(
    onPlay: () -> Unit,
    onContinue: (String) -> Unit,
    onDioramas: () -> Unit,
    viewModel: MenuViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var muted by remember { mutableStateOf(MusicManager.isMuted()) }

    Scaffold { padding ->
        Box(Modifier.fillMaxSize()) {
            // Dreamy looping backdrop, blurred (no-op below API 31) + a soft
            // scrim/gradient so the title and buttons stay readable.
            AnimatedLandscape(Modifier.fillMaxSize().blur(20.dp))
            Box(
                Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.Black.copy(alpha = 0.06f),
                                Color.Black.copy(alpha = 0.02f),
                                MaterialTheme.colorScheme.background.copy(alpha = 0.55f),
                            ),
                        ),
                    ),
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text("Glade Towns", style = MaterialTheme.typography.displayMedium)
                Text(
                    "draw a little world",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                )
                Spacer(Modifier.height(48.dp))

                Column(
                    modifier = Modifier.widthIn(max = 360.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    ChalkButton(text = "Play", onClick = onPlay)

                    if (state.lastDraftId != null) {
                        ChalkButton(
                            text = "Continue “${state.lastDraftName}”",
                            onClick = { onContinue(state.lastDraftId!!) },
                            container = MaterialTheme.colorScheme.secondary,
                        )
                    }

                    ChalkButton(
                        text = if (state.dioramaCount > 0) {
                            "Dioramas (${state.dioramaCount})"
                        } else {
                            "Dioramas"
                        },
                        onClick = onDioramas,
                        container = MaterialTheme.colorScheme.primaryContainer,
                    )
                }
            }

            // Music on/off.
            IconButton(
                onClick = { muted = MusicManager.toggleMute(context) },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .statusBarsPadding()
                    .padding(8.dp),
            ) {
                Icon(
                    if (muted) Icons.AutoMirrored.Filled.VolumeOff else Icons.AutoMirrored.Filled.VolumeUp,
                    contentDescription = if (muted) "Unmute music" else "Mute music",
                )
            }
        }
    }
}
