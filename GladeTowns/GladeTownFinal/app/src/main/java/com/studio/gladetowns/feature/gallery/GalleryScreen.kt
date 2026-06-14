package com.studio.gladetowns.feature.gallery

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.studio.gladetowns.core.domain.model.town.TownMeta
import com.studio.gladetowns.core.domain.model.town.TownStatus
import com.studio.gladetowns.core.ui.components.EmptyState
import com.studio.gladetowns.core.ui.components.SnowGlobeCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryScreen(
    onBack: () -> Unit,
    onOpen: (id: String, sealed: Boolean) -> Unit,
    viewModel: GalleryViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var pendingDelete by remember { mutableStateOf<TownMeta?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dioramas") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { padding ->
        if (!state.isLoading && state.dioramas.isEmpty()) {
            EmptyState(
                title = "An empty shelf",
                body = "Towns you build will live here forever.\nTap Play to sketch your first one.",
                modifier = Modifier.padding(padding),
            )
            return@Scaffold
        }

        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 160.dp),
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            items(state.dioramas, key = { it.id.value }) { meta ->
                val sealed = meta.status == TownStatus.SEALED
                val statusLabel = if (sealed) "Sealed" else "In progress"
                SnowGlobeCard(
                    name = meta.name,
                    subtitle = "$statusLabel \u00B7 ${meta.gridSpec.cellsPerSide}\u00D7" +
                        "${meta.gridSpec.cellsPerSide} \u00B7 ${meta.stats.structureCount} buildings",
                    seed = meta.masterSeed,
                    preview = state.previews[meta.id.value],
                    onClick = { onOpen(meta.id.value, sealed) },
                    onLongClick = { pendingDelete = meta },
                )
            }
        }
    }

    pendingDelete?.let { meta ->
        AlertDialog(
            onDismissRequest = { pendingDelete = null },
            title = { Text("Delete \u201C${meta.name}\u201D?") },
            text = { Text("It will be hidden now and permanently removed after 30 days.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.onEvent(GalleryEvent.Delete(meta.id.value))
                    pendingDelete = null
                }) { Text("Delete") }
            },
            dismissButton = { TextButton(onClick = { pendingDelete = null }) { Text("Keep") } },
        )
    }
}
