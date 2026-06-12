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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.studio.gladetowns.core.domain.model.town.TownStatus
import com.studio.gladetowns.core.ui.components.EmptyState
import com.studio.gladetowns.core.ui.components.SnowGlobeCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryScreen(
    onBack: () -> Unit,
    onOpenTown: (String) -> Unit,
    viewModel: GalleryViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

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
                val statusLabel =
                    if (meta.status == TownStatus.SEALED) "Sealed" else "In progress"
                SnowGlobeCard(
                    name = meta.name,
                    subtitle = "$statusLabel \u00B7 ${meta.gridSpec.cellsPerSide}\u00D7" +
                        "${meta.gridSpec.cellsPerSide} \u00B7 ${meta.stats.structureCount} buildings",
                    seed = meta.masterSeed,
                    onClick = { onOpenTown(meta.id.value) },
                )
            }
        }
    }
}
