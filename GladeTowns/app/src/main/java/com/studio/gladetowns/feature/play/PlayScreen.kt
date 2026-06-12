package com.studio.gladetowns.feature.play

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.studio.gladetowns.core.domain.model.town.GridSize
import com.studio.gladetowns.core.ui.components.ChalkButton
import com.studio.gladetowns.core.ui.components.EmptyState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayScreen(
    onExit: () -> Unit,
    viewModel: PlayViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        when (val s = state) {
                            is PlayUiState.Building -> s.meta.name
                            is PlayUiState.Setup -> "New Town"
                            else -> ""
                        },
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onExit) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when (val s = state) {
                PlayUiState.Loading ->
                    CircularProgressIndicator(Modifier.align(Alignment.Center))

                is PlayUiState.Error ->
                    EmptyState(title = "Oh no", body = s.message)

                is PlayUiState.Setup ->
                    SetupContent(s, viewModel::onEvent)

                is PlayUiState.Building ->
                    BuildingContent(gridCells = s.meta.gridSpec.cellsPerSide)
            }
        }
    }
}

@Composable
private fun SetupContent(state: PlayUiState.Setup, onEvent: (PlayEvent) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text("Choose your glade", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(24.dp))

        Column(
            modifier = Modifier.widthIn(max = 360.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            OutlinedTextField(
                value = state.name,
                onValueChange = { onEvent(PlayEvent.NameChanged(it)) },
                label = { Text("Town name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                GridSize.entries.forEach { size ->
                    FilterChip(
                        selected = state.selectedSize == size,
                        onClick = { onEvent(PlayEvent.SelectGridSize(size)) },
                        label = {
                            Text("${size.cells} \u00D7 ${size.cells} \u2014 ${size.flavor()}")
                        },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }

            ChalkButton(
                text = "Begin",
                onClick = { onEvent(PlayEvent.ConfirmCreate) },
            )
        }
    }
}

private fun GridSize.flavor(): String = when (this) {
    GridSize.SMALL -> "a hamlet"
    GridSize.MEDIUM -> "a village"
    GridSize.LARGE -> "a town"
}

/**
 * Foundation placeholder for the build view.
 *
 * This Compose Canvas grid stands exactly where the Filament SurfaceView
 * will be embedded (via AndroidView) in Phase 1 — see docs/RENDERING.md.
 * Keeping it as a plain composable means the swap touches only this file.
 */
@Composable
private fun BuildingContent(gridCells: Int) {
    Column(Modifier.fillMaxSize()) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(16.dp),
        ) {
            val gridColor = androidx.compose.ui.graphics.Color(0x334A4238)
            val side = minOf(size.width, size.height)
            val originX = (size.width - side) / 2f
            val originY = (size.height - side) / 2f
            // Cap rendered lines so a 100x100 placeholder stays cheap;
            // the real renderer draws the grid as a single shader quad.
            val lines = minOf(gridCells, 50)
            val step = side / lines
            for (i in 0..lines) {
                val o = i * step
                drawLine(
                    color = gridColor,
                    start = Offset(originX + o, originY),
                    end = Offset(originX + o, originY + side),
                    strokeWidth = 1f,
                )
                drawLine(
                    color = gridColor,
                    start = Offset(originX, originY + o),
                    end = Offset(originX + side, originY + o),
                    strokeWidth = 1f,
                )
            }
        }
        Text(
            text = "Drawing arrives in the next phase \u2014 this canvas becomes the 3D engine surface.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(16.dp),
        )
    }
}
