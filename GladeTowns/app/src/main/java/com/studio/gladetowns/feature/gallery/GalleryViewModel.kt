package com.studio.gladetowns.feature.gallery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studio.gladetowns.core.domain.model.town.TownId
import com.studio.gladetowns.core.domain.model.town.TownMeta
import com.studio.gladetowns.core.domain.usecase.DeleteDioramaUseCase
import com.studio.gladetowns.core.domain.usecase.ListDioramasUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GalleryUiState(
    val isLoading: Boolean = true,
    val dioramas: List<TownMeta> = emptyList(),
)

sealed interface GalleryEvent {
    data class Delete(val id: String) : GalleryEvent
}

@HiltViewModel
class GalleryViewModel @Inject constructor(
    listDioramas: ListDioramasUseCase,
    private val deleteDiorama: DeleteDioramaUseCase,
) : ViewModel() {

    val uiState: StateFlow<GalleryUiState> = listDioramas()
        .map { GalleryUiState(isLoading = false, dioramas = it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), GalleryUiState())

    fun onEvent(event: GalleryEvent) {
        when (event) {
            is GalleryEvent.Delete -> viewModelScope.launch {
                deleteDiorama(TownId(event.id))
            }
        }
    }
}
