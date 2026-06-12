package com.studio.gladetowns.feature.play

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studio.gladetowns.core.domain.model.town.GridSize
import com.studio.gladetowns.core.domain.model.town.TownId
import com.studio.gladetowns.core.domain.model.town.TownMeta
import com.studio.gladetowns.core.domain.model.town.TownState
import com.studio.gladetowns.core.domain.usecase.CreateTownUseCase
import com.studio.gladetowns.core.domain.usecase.LoadTownUseCase
import com.studio.gladetowns.core.domain.usecase.RenameTownUseCase
import com.studio.gladetowns.core.engine.SceneCommandSink
import com.studio.gladetowns.navigation.Routes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Play screen states:
 *  - Setup: no townId argument → player picks grid size + name.
 *  - Building: a town (new or loaded) is open; foundation phase shows the
 *    grid placeholder where the Filament surface + drawing land in Phase 1.
 */
sealed interface PlayUiState {
    data object Loading : PlayUiState

    data class Setup(
        val selectedSize: GridSize = GridSize.MEDIUM,
        val name: String = "",
    ) : PlayUiState

    data class Building(
        val meta: TownMeta,
        val townState: TownState,
    ) : PlayUiState

    data class Error(val message: String) : PlayUiState
}

sealed interface PlayEvent {
    data class SelectGridSize(val size: GridSize) : PlayEvent
    data class NameChanged(val name: String) : PlayEvent
    data object ConfirmCreate : PlayEvent
    data class Rename(val name: String) : PlayEvent
}

@HiltViewModel
class PlayViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val createTown: CreateTownUseCase,
    private val loadTown: LoadTownUseCase,
    private val renameTown: RenameTownUseCase,
    private val scene: SceneCommandSink,
) : ViewModel() {

    private val _uiState = MutableStateFlow<PlayUiState>(PlayUiState.Loading)
    val uiState: StateFlow<PlayUiState> = _uiState.asStateFlow()

    init {
        // Route arg via SavedStateHandle → survives process death (TDD §5.1).
        val townId: String? = savedStateHandle[Routes.ARG_TOWN_ID]
        if (townId == null) {
            _uiState.value = PlayUiState.Setup()
        } else {
            open(TownId(townId))
        }
    }

    fun onEvent(event: PlayEvent) {
        when (event) {
            is PlayEvent.SelectGridSize -> _uiState.update { s ->
                if (s is PlayUiState.Setup) s.copy(selectedSize = event.size) else s
            }

            is PlayEvent.NameChanged -> _uiState.update { s ->
                if (s is PlayUiState.Setup) s.copy(name = event.name) else s
            }

            is PlayEvent.ConfirmCreate -> {
                val setup = _uiState.value as? PlayUiState.Setup ?: return
                viewModelScope.launch {
                    _uiState.value = PlayUiState.Loading
                    val meta = createTown(setup.name, setup.selectedSize)
                    open(meta.id)
                }
            }

            is PlayEvent.Rename -> {
                val building = _uiState.value as? PlayUiState.Building ?: return
                viewModelScope.launch {
                    renameTown(building.meta.id, event.name)
                    _uiState.value = building.copy(
                        meta = building.meta.copy(name = event.name),
                    )
                }
            }
        }
    }

    private fun open(id: TownId) {
        viewModelScope.launch {
            val loaded = loadTown(id)
            if (loaded == null) {
                _uiState.value = PlayUiState.Error("This town could not be found.")
                return@launch
            }
            // Tell the (future) renderer what world to stand up.
            scene.setGrid(loaded.meta.gridSpec)
            scene.setTimeOfDay(loaded.state.timeOfDay)
            _uiState.value = PlayUiState.Building(loaded.meta, loaded.state)
        }
    }
}
