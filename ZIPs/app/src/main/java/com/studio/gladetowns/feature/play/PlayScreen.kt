package com.studio.gladetowns.feature.play

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.studio.gladetowns.core.domain.model.shape.RawStroke
import com.studio.gladetowns.core.domain.model.shape.StrokePoint
import com.studio.gladetowns.core.domain.model.structure.RoomClassifier
import com.studio.gladetowns.core.domain.model.town.GridSize
import com.studio.gladetowns.core.ui.components.ChalkButton
import com.studio.gladetowns.core.ui.components.EmptyState
import com.studio.gladetowns.core.ui.render.AtmosphereOverlay
import com.studio.gladetowns.core.ui.render.GridMapping
import com.studio.gladetowns.core.ui.render.GridMappingHolder
import com.studio.gladetowns.core.ui.render.TimeOfDayBar
import com.studio.gladetowns.core.ui.render.TownRoomModel
import com.studio.gladetowns.core.ui.render.drawRooms
import com.studio.gladetowns.core.ui.render.palette

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayScreen(
    onExit: () -> Unit,
    onExplore: (String) -> Unit = {},
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
                actions = {
                    (state as? PlayUiState.Building)?.let { b ->
                        IconButton(onClick = { viewModel.onEvent(PlayEvent.Undo) }, enabled = b.canUndo) {
                            Icon(Icons.AutoMirrored.Filled.Undo, contentDescription = "Undo")
                        }
                        IconButton(onClick = { onExplore(b.meta.id.value) }) {
                            Icon(Icons.Filled.Visibility, contentDescription = "Explore")
                        }
                        IconButton(onClick = { viewModel.onEvent(PlayEvent.Seal) }, enabled = !b.sealed) {
                            Icon(Icons.Filled.Lock, contentDescription = "Seal")
                        }
                    }
                },
            )
        },
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when (val s = state) {
                PlayUiState.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                is PlayUiState.Error -> EmptyState(title = "Oh no", body = s.message)
                is PlayUiState.Setup -> SetupContent(s, viewModel::onEvent)
                is PlayUiState.Building -> BuildingContent(s, viewModel::onEvent)
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
        Column(modifier = Modifier.widthIn(max = 360.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
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
                        label = { Text("${size.cells} \u00D7 ${size.cells} \u2014 ${size.flavor()}") },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
            ChalkButton(text = "Begin", onClick = { onEvent(PlayEvent.ConfirmCreate) })
        }
    }
}

private fun GridSize.flavor(): String = when (this) {
    GridSize.SMALL -> "a hamlet"
    GridSize.MEDIUM -> "a village"
    GridSize.LARGE -> "a town"
}

// --- Build mode: interactive room canvas ------------------------------------

@Composable
private fun BuildingContent(state: PlayUiState.Building, onEvent: (PlayEvent) -> Unit) {
    // SnapshotStateList: appends are amortised O(1) and invalidate only the
    // drawing Canvas — avoids the O(n^2) churn of rebuilding a List per event.
    val stroke = remember { mutableStateListOf<Offset>() }
    val mapping = remember { GridMappingHolder() }
    val palette = state.timeOfDay.palette()
    // Wall/room analysis runs once per layout change, off the per-frame path.
    val model = remember(state.layout) { TownRoomModel.from(state.layout) }

    // Name the room that was just placed.
    val lastRoom = remember(state.layout, state.lastShape) {
        if (state.lastShape != null) {
            state.layout.structures.lastOrNull()?.let { RoomClassifier.label(RoomClassifier.classify(it)) }
        } else {
            null
        }
    }

    Box(Modifier.fillMaxSize()) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
                .pointerInput(state.gridCells) {
                    detectDragGestures(
                        onDragStart = { stroke.clear(); stroke.add(it) },
                        onDrag = { change, _ -> stroke.add(change.position) },
                        onDragEnd = {
                            mapping.value?.let { m ->
                                if (stroke.size >= 3) onEvent(PlayEvent.StrokeFinished(m.toRawStroke(stroke)))
                            }
                            stroke.clear()
                        },
                        onDragCancel = { stroke.clear() },
                    )
                }
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { pos -> mapping.value?.cellAt(pos)?.let { onEvent(PlayEvent.RerollAt(it)) } },
                        onLongPress = { pos -> mapping.value?.cellAt(pos)?.let { onEvent(PlayEvent.DemolishAt(it)) } },
                    )
                },
        ) {
            val m = GridMapping(state.gridCells, size.width, size.height)
            mapping.value = m
            drawRooms(model, m, palette)
            drawStroke(stroke)
        }

        // Animated lighting / atmosphere (isolated so it never re-runs drawRooms).
        AtmosphereOverlay(palette, Modifier.matchParentSize())

        lastRoom?.let { label ->
            Surface(
                color = MaterialTheme.colorScheme.secondary,
                shape = MaterialTheme.shapes.large,
                modifier = Modifier.align(Alignment.TopCenter).padding(top = 12.dp),
            ) {
                Text(
                    label,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                )
            }
        }

        Column(
            modifier = Modifier.align(Alignment.BottomCenter).padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            TimeOfDayBar(selected = state.timeOfDay, onSelect = { onEvent(PlayEvent.ChangeTime(it)) })
            Text(
                text = "${state.structureCount} rooms \u00B7 draw a shape \u00B7 tap to re-roll \u00B7 hold to remove",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.55f),
            )
        }
    }
}

private fun GridMapping.toRawStroke(points: List<Offset>): RawStroke = RawStroke(
    points = points.map { val (gx, gy) = toGrid(it); StrokePoint.of(gx, gy) },
    gridCells = cells,
)

private fun DrawScope.drawStroke(points: List<Offset>) {
    if (points.size < 2) return
    val path = Path().apply {
        moveTo(points.first().x, points.first().y)
        for (i in 1 until points.size) lineTo(points[i].x, points[i].y)
    }
    drawPath(path, Color(0xCC5F7D52), style = Stroke(width = 6f))
}
