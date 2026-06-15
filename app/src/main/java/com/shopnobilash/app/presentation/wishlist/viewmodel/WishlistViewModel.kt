package com.shopnobilash.app.presentation.wishlist.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shopnobilash.app.data.property.model.MOCK_PROPERTIES
import com.shopnobilash.app.data.property.model.Property
import com.shopnobilash.app.domain.property.usecase.GetSavedPropertyIdsUseCase
import com.shopnobilash.app.domain.property.usecase.ToggleSavePropertyUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class WishlistUiState(
    val savedProperties: List<Property> = emptyList(),
    val selectedFilter: String = "All",
)

class WishlistViewModel(
    private val getSavedPropertyIdsUseCase: GetSavedPropertyIdsUseCase,
    private val toggleSavePropertyUseCase: ToggleSavePropertyUseCase,
) : ViewModel() {

    private val _filterFlow = kotlinx.coroutines.flow.MutableStateFlow("All")

    val uiState: StateFlow<WishlistUiState> = combine(
        getSavedPropertyIdsUseCase(),
        _filterFlow,
    ) { savedIds, filter ->
        val props = MOCK_PROPERTIES.filter { it.id in savedIds }
        val filtered = if (filter == "All") props else props.filter { it.type == filter }
        WishlistUiState(savedProperties = filtered, selectedFilter = filter)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), WishlistUiState())

    fun setFilter(filter: String) { _filterFlow.value = filter }
    fun toggleSave(id: String) { viewModelScope.launch { toggleSavePropertyUseCase(id) } }
}
