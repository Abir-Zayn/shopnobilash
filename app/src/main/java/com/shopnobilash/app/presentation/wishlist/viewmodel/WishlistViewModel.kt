package com.shopnobilash.app.presentation.wishlist.viewmodel

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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class WishlistUiState(
    val savedProperties: List<Property> = emptyList(),
    val selectedFilter: String = "All",
    val isLoading: Boolean = true,
)

class WishlistViewModel(
    private val getSavedPropertyIdsUseCase: GetSavedPropertyIdsUseCase,
    private val toggleSavePropertyUseCase: ToggleSavePropertyUseCase,
    private val getListingsUseCase: GetListingsUseCase,
) : ViewModel() {

    private val _allProperties = MutableStateFlow<List<Property>>(emptyList())
    private val _filterFlow = MutableStateFlow("All")

    init {
        viewModelScope.launch {
            getListingsUseCase().onSuccess { props ->
                _allProperties.value = props
            }
        }
    }

    val uiState: StateFlow<WishlistUiState> = combine(
        getSavedPropertyIdsUseCase(),
        _allProperties,
        _filterFlow,
    ) { savedIds, allProps, filter ->
        val props = allProps.filter { it.id in savedIds }
        val filtered = if (filter == "All") props else props.filter { it.type == filter }
        WishlistUiState(savedProperties = filtered, selectedFilter = filter, isLoading = false)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), WishlistUiState())

    fun setFilter(filter: String) { _filterFlow.value = filter }
    fun toggleSave(id: String) { viewModelScope.launch { toggleSavePropertyUseCase(id) } }
}
