package com.shopnobilash.app.presentation.detail.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shopnobilash.app.data.property.model.Property
import com.shopnobilash.app.domain.property.usecase.GetPropertyByIdUseCase
import com.shopnobilash.app.domain.property.usecase.GetSavedPropertyIdsUseCase
import com.shopnobilash.app.domain.property.usecase.ToggleSavePropertyUseCase
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
    private val getPropertyByIdUseCase: GetPropertyByIdUseCase,
    private val getSavedPropertyIdsUseCase: GetSavedPropertyIdsUseCase,
    private val toggleSavePropertyUseCase: ToggleSavePropertyUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    val savedIds: StateFlow<Set<String>> = getSavedPropertyIdsUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptySet())

    init {
        viewModelScope.launch {
            getPropertyByIdUseCase(propertyId)
                .onSuccess { _uiState.value = DetailUiState.Success(it) }
                .onFailure { _uiState.value = DetailUiState.Error(it.message ?: "Unknown error") }
        }
    }

    fun toggleSave(id: String) { viewModelScope.launch { toggleSavePropertyUseCase(id) } }
}
