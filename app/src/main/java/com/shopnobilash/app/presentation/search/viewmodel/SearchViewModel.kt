package com.shopnobilash.app.presentation.search.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shopnobilash.app.data.property.model.Property
import com.shopnobilash.app.data.property.model.PropertyCategory
import com.shopnobilash.app.data.property.model.PropertyFilter
import com.shopnobilash.app.data.property.model.SearchException
import com.shopnobilash.app.domain.property.usecase.GetSavedPropertyIdsUseCase
import com.shopnobilash.app.domain.property.usecase.SearchPropertiesUseCase
import com.shopnobilash.app.domain.property.usecase.ToggleSavePropertyUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class SearchUiState {
    object Loading : SearchUiState()

    /** [total] is the server match count; [properties] is the (possibly capped) returned page. */
    data class Success(val properties: List<Property>, val total: Long) : SearchUiState()
    data class Error(val message: String) : SearchUiState()
}

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
class SearchViewModel(
    private val searchPropertiesUseCase: SearchPropertiesUseCase,
    private val getSavedPropertyIdsUseCase: GetSavedPropertyIdsUseCase,
    private val toggleSavePropertyUseCase: ToggleSavePropertyUseCase,
) : ViewModel() {

    private val _filter = MutableStateFlow(PropertyFilter())
    val filter: StateFlow<PropertyFilter> = _filter.asStateFlow()

    // Each filter change cancels the in-flight search (flatMapLatest) and re-queries.
    // Text typing is debounced; structural changes (badge/sheet) apply immediately.
    val uiState: StateFlow<SearchUiState> = _filter
        .debounce { if (it.query.isBlank()) 0L else SEARCH_DEBOUNCE_MS }
        .distinctUntilChanged()
        .flatMapLatest { activeFilter ->
            flow {
                emit(SearchUiState.Loading)
                val result = searchPropertiesUseCase(activeFilter)
                emit(
                    result.fold(
                        onSuccess = { SearchUiState.Success(it.items, it.total) },
                        onFailure = { SearchUiState.Error(it.toUserMessage()) },
                    ),
                )
            }
        }
        .catch { emit(SearchUiState.Error(it.toUserMessage())) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SearchUiState.Loading)

    val savedIds: StateFlow<Set<String>> = getSavedPropertyIdsUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptySet())

    fun onQueryChange(query: String) {
        _filter.value = _filter.value.copy(query = query)
    }

    /** Toggle a badge — selecting the active category clears it (back to "All"). */
    fun onCategorySelected(category: PropertyCategory?) {
        _filter.value = _filter.value.copy(
            category = if (_filter.value.category == category) null else category,
        )
    }

    /** Apply the advanced criteria captured by the filter sheet; query + category survive. */
    fun applyAdvancedFilters(
        minRent: Int?,
        maxRent: Int?,
        minArea: Int?,
        maxArea: Int?,
        bathrooms: Set<Int>,
        bedrooms: Set<Int>,
        newlyAdded: Boolean,
    ) {
        _filter.value = _filter.value.copy(
            minRent = minRent,
            maxRent = maxRent,
            minArea = minArea,
            maxArea = maxArea,
            bathrooms = bathrooms,
            bedrooms = bedrooms,
            newlyAdded = newlyAdded,
        )
    }

    fun clearAdvancedFilters() {
        _filter.value = _filter.value.copy(
            minRent = null,
            maxRent = null,
            minArea = null,
            maxArea = null,
            bathrooms = emptySet(),
            bedrooms = emptySet(),
            newlyAdded = false,
        )
    }

    fun toggleSave(id: String) {
        viewModelScope.launch { toggleSavePropertyUseCase(id) }
    }

    private companion object {
        const val SEARCH_DEBOUNCE_MS = 300L
    }
}

/** Resolve a search failure to user-facing copy, honoring the typed [SearchException]. */
private fun Throwable.toUserMessage(): String =
    (this as? SearchException)?.error?.message ?: "Couldn't load properties. Please try again."
