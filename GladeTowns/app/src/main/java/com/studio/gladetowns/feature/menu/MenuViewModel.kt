package com.studio.gladetowns.feature.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studio.gladetowns.core.domain.repository.DioramaRepository
import com.studio.gladetowns.core.domain.usecase.ListDioramasUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * Immutable UI state — the only thing the screen reads (UDF, TDD §5.1).
 */
data class MenuUiState(
    val dioramaCount: Int = 0,
    val lastDraftId: String? = null,
    val lastDraftName: String? = null,
)

@HiltViewModel
class MenuViewModel @Inject constructor(
    listDioramas: ListDioramasUseCase,
    dioramas: DioramaRepository,
) : ViewModel() {

    val uiState: StateFlow<MenuUiState> =
        combine(listDioramas(), dioramas.observeLastDraft()) { all, lastDraft ->
            MenuUiState(
                dioramaCount = all.size,
                lastDraftId = lastDraft?.id?.value,
                lastDraftName = lastDraft?.name,
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), MenuUiState())
}
