package com.studio.gladetowns.feature.play

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studio.gladetowns.core.domain.model.command.TownCommand
import com.studio.gladetowns.core.domain.model.command.TownReducer
import com.studio.gladetowns.core.domain.model.shape.RawStroke
import com.studio.gladetowns.core.domain.model.shape.ShapeClass
import com.studio.gladetowns.core.domain.model.time.TimeOfDay
import com.studio.gladetowns.core.domain.model.town.GridSize
import com.studio.gladetowns.core.domain.model.town.TownId
import com.studio.gladetowns.core.domain.model.town.TownLayout
import com.studio.gladetowns.core.domain.model.town.TownMeta
import com.studio.gladetowns.core.domain.model.town.TownState
import com.studio.gladetowns.core.domain.procgen.merge.TownGrowthEngine
import com.studio.gladetowns.core.domain.repository.TownRepository
import com.studio.gladetowns.core.domain.usecase.CreateTownUseCase
import com.studio.gladetowns.core.domain.usecase.DrawShapeUseCase
import com.studio.gladetowns.core.domain.usecase.LoadTownUseCase
import com.studio.gladetowns.core.domain.usecase.RenameTownUseCase
import com.studio.gladetowns.core.engine.SceneCommandSink
import com.studio.gladetowns.navigation.Routes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

sealed interface PlayUiState {
    data object Loading : PlayUiState

    data class Setup(
        val selectedSize: GridSize = GridSize.MEDIUM,
        val name: String = "",
    ) : PlayUiState

    data class Building(
        val meta: TownMeta,
        val gridCells: Int,
        val layout: TownLayout,
        val structureCount: Int,
        val canUndo: Boolean,
        val timeOfDay: TimeOfDay,
        val lastShape: ShapeClass? = null,
    ) : PlayUiState

    data class Error(val message: String) : PlayUiState
}

sealed interface PlayEvent {
    data class SelectGridSize(val size: GridSize) : PlayEvent
    data class NameChanged(val name: String) : PlayEvent
    data object ConfirmCreate : PlayEvent
    data class Rename(val name: String) : PlayEvent

    data class StrokeFinished(val stroke: RawStroke) : PlayEvent
    data class RerollAt(val packedCell: Int) : PlayEvent
    data class DemolishAt(val packedCell: Int) : PlayEvent
    data object Undo : PlayEvent
    data class ChangeTime(val timeOfDay: TimeOfDay) : PlayEvent
}

@HiltViewModel
class PlayViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val createTown: CreateTownUseCase,
    private val loadTown: LoadTownUseCase,
    private val renameTown: RenameTownUseCase,
    private val drawShape: DrawShapeUseCase,
    private val towns: TownRepository,
    private val scene: SceneCommandSink,
) : ViewModel() {

    private val _uiState = MutableStateFlow<PlayUiState>(PlayUiState.Loading)
    val uiState: StateFlow<PlayUiState> = _uiState.asStateFlow()

    /** In-memory authoritative town session; all mutation is mutex-serialized. */
    private class Session(
        val meta: TownMeta,
        val commands: MutableList<TownCommand>,
        var state: TownState,
        val growth: TownGrowthEngine,
        val redo: ArrayDeque<TownCommand> = ArrayDeque(),
    )

    private var session: Session? = null
    private val sessionMutex = Mutex()
    private var lastShape: ShapeClass? = null

    init {
        val townId: String? = savedStateHandle[Routes.ARG_TOWN_ID]
        if (townId == null) _uiState.value = PlayUiState.Setup() else open(TownId(townId))
    }

    fun onEvent(event: PlayEvent) {
        when (event) {
            is PlayEvent.SelectGridSize -> _uiState.update {
                if (it is PlayUiState.Setup) it.copy(selectedSize = event.size) else it
            }
            is PlayEvent.NameChanged -> _uiState.update {
                if (it is PlayUiState.Setup) it.copy(name = event.name) else it
            }
            is PlayEvent.ConfirmCreate -> {
                val setup = _uiState.value as? PlayUiState.Setup ?: return
                viewModelScope.launch {
                    _uiState.value = PlayUiState.Loading
                    val meta = createTown(setup.name, setup.selectedSize)
                    open(meta.id)
                }
            }
            is PlayEvent.Rename -> viewModelScope.launch {
                val s = session ?: return@launch
                renameTown(s.meta.id, event.name)
            }
            is PlayEvent.StrokeFinished -> onStroke(event.stroke)
            is PlayEvent.RerollAt -> onReroll(event.packedCell)
            is PlayEvent.DemolishAt -> onDemolish(event.packedCell)
            PlayEvent.Undo -> onUndo()
            is PlayEvent.ChangeTime -> onChangeTime(event.timeOfDay)
        }
    }

    // --- session lifecycle ---------------------------------------------------

    private fun open(id: TownId) {
        viewModelScope.launch(Dispatchers.Default) {
            sessionMutex.withLock {
                val loaded = loadTown(id)
                if (loaded == null) {
                    _uiState.value = PlayUiState.Error("This town could not be found.")
                    return@withLock
                }
                val growth = TownGrowthEngine(loaded.meta.gridSpec, loaded.meta.masterSeed)
                growth.rebuild(loaded.state.structuresOrdered())
                val s = Session(loaded.meta, loaded.commands.toMutableList(), loaded.state, growth)
                session = s
                scene.setGrid(loaded.meta.gridSpec)
                scene.setTimeOfDay(loaded.state.timeOfDay)
                emit(s)
            }
        }
    }

    // --- draw ----------------------------------------------------------------

    private fun onStroke(stroke: RawStroke) = viewModelScope.launch(Dispatchers.Default) {
        sessionMutex.withLock {
            val s = session ?: return@withLock
            val result = drawShape(stroke, s.state, s.meta.masterSeed, now()) ?: return@withLock
            s.state = TownReducer.reduce(s.state, result.command)
            s.commands.add(result.command)
            s.redo.clear()
            s.state.structures[result.command.structureId]?.let { s.growth.append(it) }
            lastShape = result.shapeClass
            towns.appendCommands(s.meta.id, listOf(result.command))
            emit(s)
        }
    }

    private fun onReroll(packedCell: Int) = viewModelScope.launch(Dispatchers.Default) {
        sessionMutex.withLock {
            val s = session ?: return@withLock
            val target = s.state.structures.values.firstOrNull { packedCell in it.footprint.packedCells }
                ?: return@withLock
            val cmd = TownCommand.RerollStructure(
                seq = s.state.nextLocalSeq, atMs = now(),
                structureId = target.id, newVariantIndex = target.variantIndex + 1,
            )
            s.state = TownReducer.reduce(s.state, cmd)
            s.commands.add(cmd); s.redo.clear()
            s.growth.rebuild(s.state.structuresOrdered()) // variant affects cohesion → full rebuild
            towns.appendCommands(s.meta.id, listOf(cmd))
            emit(s)
        }
    }

    private fun onDemolish(packedCell: Int) = viewModelScope.launch(Dispatchers.Default) {
        sessionMutex.withLock {
            val s = session ?: return@withLock
            val target = s.state.structures.values.firstOrNull { packedCell in it.footprint.packedCells }
                ?: return@withLock
            val cmd = TownCommand.Demolish(s.state.nextLocalSeq, now(), target.id)
            s.state = TownReducer.reduce(s.state, cmd)
            s.commands.add(cmd); s.redo.clear()
            s.growth.rebuild(s.state.structuresOrdered())
            towns.appendCommands(s.meta.id, listOf(cmd))
            emit(s)
        }
    }

    private fun onUndo() = viewModelScope.launch(Dispatchers.Default) {
        sessionMutex.withLock {
            val s = session ?: return@withLock
            if (s.commands.isEmpty()) return@withLock
            val last = s.commands.removeAt(s.commands.lastIndex)
            s.redo.addLast(last)
            s.state = TownReducer.replay(TownState.empty(s.meta.gridSpec), s.commands)
            s.growth.rebuild(s.state.structuresOrdered())
            lastShape = null
            towns.rewriteCommands(s.meta.id, s.commands) // trim the discarded tail (TDD §7.2)
            emit(s)
        }
    }

    private fun onChangeTime(t: TimeOfDay) = viewModelScope.launch(Dispatchers.Default) {
        sessionMutex.withLock {
            val s = session ?: return@withLock
            val cmd = TownCommand.SetTimeOfDay(s.state.nextLocalSeq, now(), t)
            s.state = TownReducer.reduce(s.state, cmd)
            s.commands.add(cmd); s.redo.clear()
            scene.setTimeOfDay(t)
            towns.appendCommands(s.meta.id, listOf(cmd))
            emit(s)
        }
    }

    // --- emit ----------------------------------------------------------------

    private fun emit(s: Session) {
        _uiState.value = PlayUiState.Building(
            meta = s.meta,
            gridCells = s.meta.gridSpec.cellsPerSide,
            layout = s.growth.snapshot(),
            structureCount = s.state.structures.size,
            canUndo = s.commands.isNotEmpty(),
            timeOfDay = s.state.timeOfDay,
            lastShape = lastShape,
        )
    }

    private fun now() = System.currentTimeMillis()
}
