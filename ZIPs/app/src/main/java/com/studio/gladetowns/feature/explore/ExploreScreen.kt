package com.studio.gladetowns.feature.explore

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.studio.gladetowns.core.domain.model.structure.RoomClassifier
import com.studio.gladetowns.core.domain.model.structure.StructurePlan
import com.studio.gladetowns.core.ui.components.EmptyState
import com.studio.gladetowns.core.ui.render.AtmosphereOverlay
import com.studio.gladetowns.core.ui.render.GridMapping
import com.studio.gladetowns.core.ui.render.GridMappingHolder
import com.studio.gladetowns.core.ui.render.TimeOfDayBar
import com.studio.gladetowns.core.ui.render.TownRoomModel
import com.studio.gladetowns.core.ui.render.drawRooms
import com.studio.gladetowns.core.ui.render.drawSelection
import com.studio.gladetowns.core.ui.render.palette

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    onExit: () -> Unit,
    viewModel: ExploreViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text((state as? ExploreUiState.Ready)?.meta?.name ?: "Explore") },
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
                ExploreUiState.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                is ExploreUiState.Error -> EmptyState(title = "Oh no", body = s.message)
                is ExploreUiState.Ready -> ExploreContent(s, viewModel::onEvent)
            }
        }
    }
}

@Composable
private fun ExploreContent(s: ExploreUiState.Ready, onEvent: (ExploreEvent) -> Unit) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val mapping = remember { GridMappingHolder() }
    val palette = s.timeOfDay.palette()
    // Wall/room analysis runs once per layout, not per pan/zoom/selection.
    val model = remember(s.layout) { TownRoomModel.from(s.layout) }

    Column(Modifier.fillMaxSize()) {
        // Time selector gets its own row — never overlaps the viewport.
        Surface(tonalElevation = 2.dp) {
            TimeOfDayBar(
                selected = s.timeOfDay,
                onSelect = { onEvent(ExploreEvent.ChangeTime(it)) },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
            )
        }

        // The viewport takes ALL remaining height.
        Box(Modifier.fillMaxWidth().weight(1f)) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = scale; scaleY = scale
                        translationX = offset.x; translationY = offset.y
                    }
                    .pointerInput(Unit) {
                        detectTransformGestures { _, pan, zoom, _ ->
                            scale = (scale * zoom).coerceIn(0.6f, 6f)
                            offset += pan
                        }
                    }
                    .pointerInput(Unit) {
                        detectTapGestures { pos -> mapping.value?.cellAt(pos)?.let { onEvent(ExploreEvent.SelectCell(it)) } }
                    },
            ) {
                val m = GridMapping(s.layout.gridCells, size.width, size.height)
                mapping.value = m
                drawRooms(model, m, palette)
                s.selected?.let { drawSelection(it, m) }
            }

            AtmosphereOverlay(palette, Modifier.matchParentSize())

            Column(
                modifier = Modifier.align(Alignment.CenterEnd).padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                FilledTonalIconButton(onClick = { scale = (scale * 1.25f).coerceAtMost(6f) }) {
                    Icon(Icons.Filled.Add, contentDescription = "Zoom in")
                }
                FilledTonalIconButton(onClick = { scale = (scale / 1.25f).coerceAtLeast(0.6f) }) {
                    Icon(Icons.Filled.Remove, contentDescription = "Zoom out")
                }
                FilledTonalIconButton(onClick = { scale = 1f; offset = Offset.Zero }) {
                    Icon(Icons.Filled.CenterFocusStrong, contentDescription = "Recenter")
                }
            }

            s.selected?.let { plan ->
                InspectorCard(
                    plan = plan,
                    onClose = { onEvent(ExploreEvent.ClearSelection) },
                    modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp).fillMaxWidth(),
                )
            } ?: Text(
                text = "Pinch to zoom \u00B7 drag to pan \u00B7 tap a room to inspect",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.55f),
                modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp),
            )
        }
    }
}

@Composable
private fun InspectorCard(plan: StructurePlan, onClose: () -> Unit, modifier: Modifier = Modifier) {
    val type = RoomClassifier.classify(plan)
    val (w, h) = RoomClassifier.boundingBox(plan.footprint)
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.large,
        tonalElevation = 3.dp,
        modifier = modifier,
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(RoomClassifier.label(type), style = MaterialTheme.typography.titleLarge)
            Text(
                "Drawn ${w}\u00D7$h \u00B7 ${plan.footprint.size} tiles",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            )
            Text(
                "tap empty ground to dismiss",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
            )
        }
    }
}
