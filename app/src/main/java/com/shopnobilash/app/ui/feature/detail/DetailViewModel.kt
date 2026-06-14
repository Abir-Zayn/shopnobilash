package com.shopnobilash.app.ui.feature.detail

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

sealed class DetailUiState {
    object Loading : DetailUiState()
    data class Success(val property: Property) : DetailUiState()
    data class Error(val message: String) : DetailUiState()
}

class DetailViewModel(
    private val propertyId: String,
    private val repository: PropertyRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    val savedIds: StateFlow<Set<String>> = repository.getSavedIds()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptySet())

    init {
        viewModelScope.launch {
            repository.getPropertyById(propertyId)
                .onSuccess { _uiState.value = DetailUiState.Success(it) }
                .onFailure { _uiState.value = DetailUiState.Error(it.message ?: "Unknown error") }
        }
    }

    fun toggleSave(id: String) { viewModelScope.launch { repository.toggleSave(id) } }
}
