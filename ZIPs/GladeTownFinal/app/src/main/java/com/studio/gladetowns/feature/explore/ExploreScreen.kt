package com.studio.gladetowns.feature.explore

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import com.studio.gladetowns.core.domain.model.structure.Footprint
import com.studio.gladetowns.core.domain.model.structure.RoofKind
import com.studio.gladetowns.core.domain.model.structure.StructureArchetype
import com.studio.gladetowns.core.domain.model.structure.StructurePlan
import com.studio.gladetowns.core.ui.components.EmptyState
import com.studio.gladetowns.core.ui.render.AtmosphereOverlay
import com.studio.gladetowns.core.ui.render.GridMapping
import com.studio.gladetowns.core.ui.render.GridMappingHolder
import com.studio.gladetowns.core.ui.render.TimeOfDayBar
import com.studio.gladetowns.core.ui.render.drawSelection
import com.studio.gladetowns.core.ui.render.drawTown
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
        Box(Modifier.fillMaxWidth().padding(padding)) {
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

    Box(Modifier.fillMaxWidth()) {
        Canvas(
            modifier = Modifier
                .matchParentSize()
                // Visual transform only — does NOT recompose the draw body.
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
            drawTown(s.layout, m, palette)
            s.selected?.let { drawSelection(it, m) }
        }

        // Sky/atmosphere stays fixed (not pan/zoomed).
        AtmosphereOverlay(palette, Modifier.matchParentSize())

        // Zoom controls.
        Column(
            modifier = Modifier.align(Alignment.TopEnd).padding(12.dp),
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

        TimeOfDayBar(
            selected = s.timeOfDay,
            onSelect = { onEvent(ExploreEvent.ChangeTime(it)) },
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 12.dp),
        )

        s.selected?.let { plan ->
            InspectorCard(
                plan = plan,
                onClose = { onEvent(ExploreEvent.ClearSelection) },
                modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp).fillMaxWidth(),
            )
        } ?: Text(
            text = "Pinch to zoom \u00B7 drag to pan \u00B7 tap a building to inspect",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.55f),
            modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp),
        )
    }
}

@Composable
private fun InspectorCard(plan: StructurePlan, onClose: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.large,
        tonalElevation = 3.dp,
        modifier = modifier,
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(plan.archetype.label(), style = MaterialTheme.typography.titleLarge)
            Text(
                "Footprint ${plan.footprint.size} cells \u00B7 ${plan.maxFloors} floors \u00B7 ${plan.masses.size} mass(es)",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            )
            Text(
                "Roofs: ${plan.masses.map { it.roof.label() }.distinct().joinToString()}",
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

private fun StructureArchetype.label() = when (this) {
    StructureArchetype.HOUSE -> "\uD83C\uDFE0 House"
    StructureArchetype.WAREHOUSE -> "\uD83C\uDFED Warehouse"
    StructureArchetype.TOWER -> "\uD83D\uDDFC Tower"
    StructureArchetype.CHAPEL -> "\u26EA Chapel"
    StructureArchetype.FOLLY -> "\u2728 Folly"
    StructureArchetype.L_SHAPE -> "L-shaped building"
    StructureArchetype.PROP -> "Prop"
}

private fun RoofKind.label() = when (this) {
    RoofKind.FLAT -> "flat"
    RoofKind.GABLE -> "gable"
    RoofKind.HIP -> "hip"
    RoofKind.CONE -> "cone"
    RoofKind.SPIRE -> "spire"
}
