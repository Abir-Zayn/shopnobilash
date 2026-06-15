package com.shopnobilash.app.presentation.home.viewmodel

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

sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Success(val properties: List<Property>) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}

class HomeViewModel(
    private val getListingsUseCase: GetListingsUseCase,
    private val getSavedPropertyIdsUseCase: GetSavedPropertyIdsUseCase,
    private val toggleSavePropertyUseCase: ToggleSavePropertyUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    val savedIds: StateFlow<Set<String>> = getSavedPropertyIdsUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptySet())

    private val _selectedCategory = MutableStateFlow("house")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            getListingsUseCase()
                .onSuccess { _uiState.value = HomeUiState.Success(it) }
                .onFailure { _uiState.value = HomeUiState.Error(it.message ?: "Unknown error") }
        }
    }

    fun setCategory(id: String) { _selectedCategory.value = id }

    fun toggleSave(id: String) { viewModelScope.launch { toggleSavePropertyUseCase(id) } }
}
