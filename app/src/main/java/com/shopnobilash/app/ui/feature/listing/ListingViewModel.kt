package com.shopnobilash.app.ui.feature.listing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shopnobilash.app.data.model.Property
import com.shopnobilash.app.data.repository.PropertyRepository
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

class ListingViewModel(private val repository: PropertyRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<ListingUiState>(ListingUiState.Loading)
    val uiState: StateFlow<ListingUiState> = _uiState.asStateFlow()

    val savedIds: StateFlow<Set<String>> = repository.getSavedIds()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptySet())

    init { load() }

    fun load() {
        viewModelScope.launch {
            _uiState.value = ListingUiState.Loading
            repository.getListings()
                .onSuccess { _uiState.value = ListingUiState.Success(it) }
                .onFailure { _uiState.value = ListingUiState.Error(it.message ?: "Unknown error") }
        }
    }

    fun toggleSave(id: String) { viewModelScope.launch { repository.toggleSave(id) } }
}
