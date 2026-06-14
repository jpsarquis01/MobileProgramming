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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.studio.gladetowns.core.domain.model.shape.RawStroke
import com.studio.gladetowns.core.domain.model.shape.ShapeClass
import com.studio.gladetowns.core.domain.model.shape.StrokePoint
import com.studio.gladetowns.core.domain.model.structure.Footprint
import com.studio.gladetowns.core.domain.model.structure.GridRect
import com.studio.gladetowns.core.domain.model.structure.RoofKind
import com.studio.gladetowns.core.domain.model.town.GridSize
import com.studio.gladetowns.core.domain.model.town.TownLayout
import com.studio.gladetowns.core.ui.components.ChalkButton
import com.studio.gladetowns.core.ui.components.EmptyState
import kotlin.math.min

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
                actions = {
                    (state as? PlayUiState.Building)?.let { b ->
                        IconButton(onClick = { viewModel.onEvent(PlayEvent.Undo) }, enabled = b.canUndo) {
                            Icon(Icons.AutoMirrored.Filled.Undo, contentDescription = "Undo")
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

// --- Build mode: interactive town canvas ------------------------------------

@Composable
private fun BuildingContent(state: PlayUiState.Building, onEvent: (PlayEvent) -> Unit) {
    // Current finger stroke, in screen coords (recomposes as the finger moves).
    var stroke by remember { mutableStateOf<List<Offset>>(emptyList()) }
    // Latest grid mapping captured during draw so we can convert on drag-end.
    val mapping = remember { GridMappingHolder() }

    Box(Modifier.fillMaxSize()) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
                // One-finger drawing.
                .pointerInput(state.gridCells) {
                    detectDragGestures(
                        onDragStart = { stroke = listOf(it) },
                        onDrag = { change, _ -> stroke = stroke + change.position },
                        onDragEnd = {
                            mapping.value?.let { m ->
                                if (stroke.size >= 3) onEvent(PlayEvent.StrokeFinished(m.toRawStroke(stroke)))
                            }
                            stroke = emptyList()
                        },
                        onDragCancel = { stroke = emptyList() },
                    )
                }
                // Tap = re-roll, long-press = demolish.
                .pointerInput(state.layout) {
                    detectTapGestures(
                        onTap = { pos ->
                            mapping.value?.cellAt(pos)?.let { onEvent(PlayEvent.RerollAt(it)) }
                        },
                        onLongPress = { pos ->
                            mapping.value?.cellAt(pos)?.let { onEvent(PlayEvent.DemolishAt(it)) }
                        },
                    )
                },
        ) {
            val m = GridMapping(state.gridCells, size.width, size.height)
            mapping.value = m
            drawTown(state.layout, m)
            drawStroke(stroke)
        }

        // Recognition toast.
        state.lastShape?.let { shape ->
            Surface(
                color = MaterialTheme.colorScheme.secondary,
                shape = MaterialTheme.shapes.large,
                modifier = Modifier.align(Alignment.TopCenter).padding(top = 12.dp),
            ) {
                Text(
                    shape.toLabel(),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                )
            }
        }

        Text(
            text = "${state.structureCount} buildings \u00B7 draw a shape \u00B7 tap to re-roll \u00B7 hold to remove",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.55f),
            modifier = Modifier.align(Alignment.BottomCenter).padding(12.dp),
        )
    }
}

private fun ShapeClass.toLabel() = when (this) {
    ShapeClass.RECTANGLE -> "\uD83C\uDFE0 House"
    ShapeClass.CIRCLE -> "\uD83D\uDDFC Tower"
    ShapeClass.TRIANGLE -> "\u26EA Chapel"
    ShapeClass.IRREGULAR -> "\u2728 Folly"
}

// --- screen <-> grid mapping -------------------------------------------------

private class GridMappingHolder { var value: GridMapping? = null }

private class GridMapping(val cells: Int, viewW: Float, viewH: Float) {
    val side = min(viewW, viewH)
    val cellPx = side / cells
    val originX = (viewW - side) / 2f
    val originY = (viewH - side) / 2f

    fun toGrid(p: Offset): Pair<Float, Float> =
        ((p.x - originX) / cellPx).coerceIn(0f, cells.toFloat()) to
            ((p.y - originY) / cellPx).coerceIn(0f, cells.toFloat())

    fun cellAt(p: Offset): Int? {
        val cx = ((p.x - originX) / cellPx).toInt()
        val cy = ((p.y - originY) / cellPx).toInt()
        if (cx !in 0 until cells || cy !in 0 until cells) return null
        return Footprint.pack(cx, cy)
    }

    fun toRawStroke(points: List<Offset>): RawStroke = RawStroke(
        points = points.map { val (gx, gy) = toGrid(it); StrokePoint.of(gx, gy) },
        gridCells = cells,
    )

    fun cellTopLeft(x: Int, y: Int) = Offset(originX + x * cellPx, originY + y * cellPx)
}

// --- rendering ---------------------------------------------------------------

private val WallColors = listOf(
    Color(0xFFE8D8BE), Color(0xFFD9C7A3), Color(0xFFE2CBB0),
    Color(0xFFCFC2A8), Color(0xFFEAD9C2), Color(0xFFD2BE9B),
)
private val RoofColors = listOf(
    Color(0xFFC97B5A), Color(0xFF8C6E52), Color(0xFFA8584A),
    Color(0xFF6E7D52), Color(0xFFB5734A), Color(0xFF7C6A8A),
)
private val RoadColor = Color(0xFFCFC3A6)
private val GroundColor = Color(0xFFEFE7D6)
private val GridLine = Color(0x224A4238)

private fun DrawScope.drawTown(layout: TownLayout, m: GridMapping) {
    // Ground + light grid.
    drawRect(GroundColor, topLeft = Offset(m.originX, m.originY), size = Size(m.side, m.side))
    val lines = min(layout.gridCells, 50)
    val step = m.side / lines
    for (i in 0..lines) {
        val o = i * step
        drawLine(GridLine, Offset(m.originX + o, m.originY), Offset(m.originX + o, m.originY + m.side))
        drawLine(GridLine, Offset(m.originX, m.originY + o), Offset(m.originX + m.side, m.originY + o))
    }

    // Roads (under buildings).
    for (cell in layout.roads) {
        val x = Footprint.unpackX(cell); val y = Footprint.unpackY(cell)
        drawRect(RoadColor, topLeft = m.cellTopLeft(x, y), size = Size(m.cellPx, m.cellPx))
    }

    // Buildings: drop shadow by floors, wall fill, roof hint.
    for (plan in layout.structures) {
        val wall = WallColors[plan.paletteIndex % WallColors.size]
        val roof = RoofColors[plan.paletteIndex % RoofColors.size]
        for (mass in plan.masses) {
            val tl = m.cellTopLeft(mass.rect.x, mass.rect.y)
            val w = mass.rect.w * m.cellPx
            val h = mass.rect.h * m.cellPx
            val shadow = (mass.floors * 1.5f).coerceAtMost(8f)
            drawRect(Color(0x33000000), topLeft = Offset(tl.x + shadow, tl.y + shadow), size = Size(w, h))
            drawRect(wall, topLeft = tl, size = Size(w, h))
            drawRoof(mass.rect, mass.roof, roof, m)
        }
    }
}

private fun DrawScope.drawRoof(rect: GridRect, kind: RoofKind, color: Color, m: GridMapping) {
    val tl = m.cellTopLeft(rect.x, rect.y)
    val w = rect.w * m.cellPx; val h = rect.h * m.cellPx
    val cx = tl.x + w / 2f; val cy = tl.y + h / 2f
    when (kind) {
        RoofKind.FLAT -> drawRect(color.copy(alpha = 0.55f),
            topLeft = Offset(tl.x + w * 0.18f, tl.y + h * 0.18f), size = Size(w * 0.64f, h * 0.64f))
        RoofKind.GABLE -> {
            val p = Path().apply {
                moveTo(tl.x, tl.y + h); lineTo(cx, tl.y); lineTo(tl.x + w, tl.y + h); close()
            }
            drawPath(p, color)
        }
        RoofKind.HIP -> {
            val p = Path().apply {
                moveTo(tl.x, tl.y + h); lineTo(cx, tl.y); lineTo(tl.x + w, tl.y + h)
                lineTo(cx, tl.y + h * 0.7f); close()
            }
            drawPath(p, color)
        }
        RoofKind.CONE -> drawCircle(color, radius = min(w, h) / 2f, center = Offset(cx, cy))
        RoofKind.SPIRE -> {
            val p = Path().apply {
                moveTo(cx, tl.y); lineTo(tl.x + w, tl.y + h); lineTo(tl.x, tl.y + h); close()
            }
            drawPath(p, color)
            drawCircle(color, radius = m.cellPx * 0.12f, center = Offset(cx, tl.y))
        }
    }
}

private fun DrawScope.drawStroke(points: List<Offset>) {
    if (points.size < 2) return
    val path = Path().apply {
        moveTo(points.first().x, points.first().y)
        for (i in 1 until points.size) lineTo(points[i].x, points[i].y)
    }
    drawPath(path, Color(0xCC5F7D52), style = Stroke(width = 6f))
}
