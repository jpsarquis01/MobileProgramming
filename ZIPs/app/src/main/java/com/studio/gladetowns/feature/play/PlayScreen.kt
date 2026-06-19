package com.studio.gladetowns.feature.play

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.studio.gladetowns.core.domain.model.structure.Footprint
import com.studio.gladetowns.core.domain.model.structure.RoomClassifier
import com.studio.gladetowns.core.domain.model.town.GridSize
import com.studio.gladetowns.core.ui.components.ChalkButton
import com.studio.gladetowns.core.ui.components.EmptyState
import com.studio.gladetowns.core.ui.components.RoomGuideDialog
import com.studio.gladetowns.core.ui.render.AtmosphereOverlay
import com.studio.gladetowns.core.ui.render.GridMapping
import com.studio.gladetowns.core.ui.render.GridMappingHolder
import com.studio.gladetowns.core.ui.render.TimeOfDayBar
import com.studio.gladetowns.core.ui.render.TownRoomModel
import com.studio.gladetowns.core.ui.render.drawRooms
import com.studio.gladetowns.core.ui.render.palette
import kotlin.math.abs

/** What a drag draws: a room shape, or a connecting hallway line. */
private enum class BuildMode { ROOM, HALLWAY }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayScreen(
    onExit: () -> Unit,
    onExplore: (String) -> Unit = {},
    viewModel: PlayViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showGuide by remember { mutableStateOf(false) }
    if (showGuide) RoomGuideDialog(onDismiss = { showGuide = false })

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
                    IconButton(onClick = { showGuide = true }) {
                        Icon(Icons.Filled.Info, contentDescription = "How rooms work")
                    }
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
                        label = { Text("${size.cells} × ${size.cells} — ${size.flavor()}") },
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BuildingContent(state: PlayUiState.Building, onEvent: (PlayEvent) -> Unit) {
    // SnapshotStateList: appends are amortised O(1) and invalidate only the
    // drawing Canvas — avoids the O(n^2) churn of rebuilding a List per event.
    val stroke = remember { mutableStateListOf<Offset>() }
    val mapping = remember { GridMappingHolder() }
    val palette = state.timeOfDay.palette()
    var buildMode by remember { mutableStateOf(BuildMode.ROOM) }
    // Zoom/pan baked into the GridMapping so drawing stays aligned when zoomed.
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
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
                // One finger draws; two fingers pinch-zoom + pan. Re-keyed on
                // buildMode so the handler always sees the live mode.
                .pointerInput(state.gridCells, buildMode) {
                    awaitEachGesture {
                        val first = awaitFirstDown(requireUnconsumed = false)
                        var multiTouch = false
                        stroke.clear()
                        stroke.add(first.position)
                        while (true) {
                            val event = awaitPointerEvent()
                            val pressed = event.changes.filter { it.pressed }
                            if (pressed.size >= 2) {
                                if (!multiTouch) { multiTouch = true; stroke.clear() }
                                val zoom = event.calculateZoom()
                                val pan = event.calculatePan()
                                if (zoom != 1f) scale = (scale * zoom).coerceIn(1f, 8f)
                                offset += pan
                                event.changes.forEach { it.consume() }
                            } else if (!multiTouch && pressed.size == 1) {
                                val change = pressed.first()
                                stroke.add(change.position)
                                change.consume()
                            }
                            if (event.changes.none { it.pressed }) break
                        }
                        if (!multiTouch) {
                            mapping.value?.let { m ->
                                when (buildMode) {
                                    BuildMode.ROOM ->
                                        if (stroke.size >= 3) onEvent(PlayEvent.StrokeFinished(m.toRawStroke(stroke)))
                                    BuildMode.HALLWAY -> {
                                        val cells = m.toPathCells(stroke)
                                        if (cells.isNotEmpty()) onEvent(PlayEvent.PathDrawn(cells))
                                    }
                                }
                            }
                        }
                        stroke.clear()
                    }
                }
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { pos -> mapping.value?.cellAt(pos)?.let { onEvent(PlayEvent.RerollAt(it)) } },
                        onLongPress = { pos -> mapping.value?.cellAt(pos)?.let { onEvent(PlayEvent.DemolishAt(it)) } },
                    )
                },
        ) {
            val m = GridMapping(state.gridCells, size.width, size.height, scale, offset.x, offset.y)
            mapping.value = m
            drawRooms(model, m, palette)
            drawStroke(stroke, buildMode)
        }

        // Animated lighting / atmosphere (isolated so it never re-runs drawRooms).
        AtmosphereOverlay(palette, Modifier.matchParentSize())

        // Zoom controls (pinch works too); recenter resets zoom + pan.
        Column(
            modifier = Modifier.align(Alignment.CenterEnd).padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            FilledTonalIconButton(onClick = { scale = (scale * 1.25f).coerceAtMost(8f) }) {
                Icon(Icons.Filled.Add, contentDescription = "Zoom in")
            }
            FilledTonalIconButton(onClick = { scale = (scale / 1.25f).coerceAtLeast(1f) }) {
                Icon(Icons.Filled.Remove, contentDescription = "Zoom out")
            }
            FilledTonalIconButton(onClick = { scale = 1f; offset = Offset.Zero }) {
                Icon(Icons.Filled.CenterFocusStrong, contentDescription = "Recenter")
            }
        }

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
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = buildMode == BuildMode.ROOM,
                    onClick = { buildMode = BuildMode.ROOM },
                    label = { Text("Room") },
                )
                FilterChip(
                    selected = buildMode == BuildMode.HALLWAY,
                    onClick = { buildMode = BuildMode.HALLWAY },
                    label = { Text("Hallway") },
                )
            }
            TimeOfDayBar(selected = state.timeOfDay, onSelect = { onEvent(PlayEvent.ChangeTime(it)) })
            Text(
                text = when (buildMode) {
                    BuildMode.ROOM ->
                        "${state.structureCount} rooms · draw a shape · tap to re-roll · hold to remove"
                    BuildMode.HALLWAY ->
                        "Hallway · drag a line to connect rooms"
                },
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

/**
 * Rasterize a freehand line into the contiguous grid cells it crosses. Gaps
 * between sampled finger points are filled with a Bresenham line so the hallway
 * is unbroken even on a fast swipe.
 */
private fun GridMapping.toPathCells(points: List<Offset>): List<Int> {
    val out = LinkedHashSet<Int>()
    var prev: Pair<Int, Int>? = null
    for (p in points) {
        cellXY(p)?.let { cur ->
            val (px, py) = prev ?: cur
            line(px, py, cur.first, cur.second) { x, y -> out.add(Footprint.pack(x, y)) }
            prev = cur
        }
    }
    return out.toList()
}

private fun GridMapping.cellXY(p: Offset): Pair<Int, Int>? {
    val cx = ((p.x - originX) / cellPx).toInt()
    val cy = ((p.y - originY) / cellPx).toInt()
    if (cx !in 0 until cells || cy !in 0 until cells) return null
    return cx to cy
}

private inline fun line(x0: Int, y0: Int, x1: Int, y1: Int, plot: (Int, Int) -> Unit) {
    var x = x0; var y = y0
    val dx = abs(x1 - x0); val dy = -abs(y1 - y0)
    val sx = if (x0 < x1) 1 else -1
    val sy = if (y0 < y1) 1 else -1
    var err = dx + dy
    while (true) {
        plot(x, y)
        if (x == x1 && y == y1) break
        val e2 = 2 * err
        if (e2 >= dy) { err += dy; x += sx }
        if (e2 <= dx) { err += dx; y += sy }
    }
}

private fun DrawScope.drawStroke(points: List<Offset>, mode: BuildMode) {
    if (points.size < 2) return
    val path = Path().apply {
        moveTo(points.first().x, points.first().y)
        for (i in 1 until points.size) lineTo(points[i].x, points[i].y)
    }
    val color = if (mode == BuildMode.HALLWAY) Color(0xCC9A8463) else Color(0xCC5F7D52)
    drawPath(path, color, style = Stroke(width = 6f))
}
