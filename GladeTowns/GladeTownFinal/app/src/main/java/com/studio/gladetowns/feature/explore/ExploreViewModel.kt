package com.studio.gladetowns.feature.explore

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studio.gladetowns.core.domain.model.structure.StructurePlan
import com.studio.gladetowns.core.domain.model.time.TimeOfDay
import com.studio.gladetowns.core.domain.model.town.TownId
import com.studio.gladetowns.core.domain.model.town.TownLayout
import com.studio.gladetowns.core.domain.model.town.TownMeta
import com.studio.gladetowns.core.domain.usecase.LoadTownLayoutUseCase
import com.studio.gladetowns.navigation.Routes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Read-only diorama viewer (TDD §13). Loads a grown TownLayout once; the time
 * of day here is a VIEW-ONLY override (no command is written) so a sealed
 * diorama is never mutated by being looked at.
 */
sealed interface ExploreUiState {
    data object Loading : ExploreUiState
    data class Ready(
        val meta: TownMeta,
        val layout: TownLayout,
        val timeOfDay: TimeOfDay,
        val selected: StructurePlan?,
    ) : ExploreUiState
    data class Error(val message: String) : ExploreUiState
}

sealed interface ExploreEvent {
    data class SelectCell(val packedCell: Int) : ExploreEvent
    data object ClearSelection : ExploreEvent
    data class ChangeTime(val timeOfDay: TimeOfDay) : ExploreEvent
}

@HiltViewModel
class ExploreViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val loadLayout: LoadTownLayoutUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<ExploreUiState>(ExploreUiState.Loading)
    val uiState: StateFlow<ExploreUiState> = _uiState.asStateFlow()

    init {
        val id: String? = savedStateHandle[Routes.ARG_TOWN_ID]
        if (id == null) {
            _uiState.value = ExploreUiState.Error("No town specified.")
        } else {
            viewModelScope.launch(Dispatchers.Default) {
                val loaded = loadLayout(TownId(id))
                _uiState.value = if (loaded == null) {
                    ExploreUiState.Error("This town could not be found.")
                } else {
                    ExploreUiState.Ready(loaded.meta, loaded.layout, loaded.timeOfDay, selected = null)
                }
            }
        }
    }

    fun onEvent(event: ExploreEvent) {
        val s = _uiState.value as? ExploreUiState.Ready ?: return
        _uiState.value = when (event) {
            is ExploreEvent.SelectCell -> {
                val ownerId = s.layout.ownerAt(event.packedCell)
                val plan = ownerId?.let { id -> s.layout.structures.firstOrNull { it.structureId.value == id } }
                s.copy(selected = plan) // tapping empty ground -> null -> clears selection
            }
            ExploreEvent.ClearSelection -> s.copy(selected = null)
            is ExploreEvent.ChangeTime -> s.copy(timeOfDay = event.timeOfDay)
        }
    }
}
