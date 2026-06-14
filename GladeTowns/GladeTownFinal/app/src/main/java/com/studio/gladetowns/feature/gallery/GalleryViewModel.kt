package com.studio.gladetowns.feature.gallery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studio.gladetowns.core.domain.model.town.TownId
import com.studio.gladetowns.core.domain.model.town.TownMeta
import com.studio.gladetowns.core.domain.model.town.TownPreview
import com.studio.gladetowns.core.domain.usecase.DeleteDioramaUseCase
import com.studio.gladetowns.core.domain.usecase.ListDioramasUseCase
import com.studio.gladetowns.core.domain.usecase.LoadTownLayoutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GalleryUiState(
    val isLoading: Boolean = true,
    val dioramas: List<TownMeta> = emptyList(),
    val previews: Map<String, TownPreview> = emptyMap(),
)

sealed interface GalleryEvent {
    data class Delete(val id: String) : GalleryEvent
}

/**
 * Snow-globe previews are expensive to build (load + replay + grow), so they're
 * generated lazily off the main thread, cached for the session, and streamed in
 * progressively — cards show the seeded fallback until their preview arrives.
 */
@HiltViewModel
class GalleryViewModel @Inject constructor(
    private val listDioramas: ListDioramasUseCase,
    private val deleteDiorama: DeleteDioramaUseCase,
    private val loadLayout: LoadTownLayoutUseCase,
) : ViewModel() {

    private val previews = MutableStateFlow<Map<String, TownPreview>>(emptyMap())
    private val requested = HashSet<String>()

    val uiState: StateFlow<GalleryUiState> =
        combine(listDioramas(), previews) { list, prev ->
            ensurePreviews(list)
            GalleryUiState(isLoading = false, dioramas = list, previews = prev)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), GalleryUiState())

    private fun ensurePreviews(list: List<TownMeta>) {
        for (meta in list) {
            val key = meta.id.value
            if (requested.add(key)) {
                viewModelScope.launch(Dispatchers.Default) {
                    val loaded = loadLayout(meta.id) ?: return@launch
                    val preview = TownPreview.from(loaded.layout)
                    previews.update { it + (key to preview) }
                }
            }
        }
    }

    fun onEvent(event: GalleryEvent) {
        when (event) {
            is GalleryEvent.Delete -> viewModelScope.launch {
                deleteDiorama(TownId(event.id))
                previews.update { it - event.id }
                requested.remove(event.id)
            }
        }
    }
}
