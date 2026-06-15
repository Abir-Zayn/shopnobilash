package com.shopnobilash.app.presentation.listing.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shopnobilash.app.data.property.model.Property
import com.shopnobilash.app.domain.property.usecase.GetListingsUseCase
import com.shopnobilash.app.domain.property.usecase.GetSavedPropertyIdsUseCase
import com.shopnobilash.app.domain.property.usecase.ToggleSavePropertyUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class ListingUiState {
    object Loading : ListingUiState()
    data class Success(val properties: List<Property>) : ListingUiState()
    data class Error(val message: String) : ListingUiState()
}

class ListingViewModel(
    private val getListingsUseCase: GetListingsUseCase,
    private val getSavedPropertyIdsUseCase: GetSavedPropertyIdsUseCase,
    private val toggleSavePropertyUseCase: ToggleSavePropertyUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<ListingUiState>(ListingUiState.Loading)
    val uiState: StateFlow<ListingUiState> = _uiState.asStateFlow()

    val savedIds: StateFlow<Set<String>> = getSavedPropertyIdsUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptySet())

    init { load() }

    fun load() {
        viewModelScope.launch {
            _uiState.value = ListingUiState.Loading
            getListingsUseCase()
                .onSuccess { _uiState.value = ListingUiState.Success(it) }
                .onFailure { _uiState.value = ListingUiState.Error(it.message ?: "Unknown error") }
        }
    }

    fun toggleSave(id: String) { viewModelScope.launch { toggleSavePropertyUseCase(id) } }
}
