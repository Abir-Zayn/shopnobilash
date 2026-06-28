package com.shopnobilash.app.presentation.checkout.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shopnobilash.app.data.property.model.Property
import com.shopnobilash.app.domain.property.usecase.GetPropertyByIdUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class CheckoutUiState {
    object Loading : CheckoutUiState()
    data class Success(val property: Property) : CheckoutUiState()
    data class Error(val message: String) : CheckoutUiState()
}

class CheckoutViewModel(
    private val propertyId: String,
    private val getPropertyByIdUseCase: GetPropertyByIdUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<CheckoutUiState>(CheckoutUiState.Loading)
    val uiState: StateFlow<CheckoutUiState> = _uiState.asStateFlow()

    /** Contract length in years — drives Total Pay = monthlyRent * 12 * years. */
    private val _selectedYears = MutableStateFlow(1)
    val selectedYears: StateFlow<Int> = _selectedYears.asStateFlow()

    /** Chosen move-in date as UTC epoch millis (matches Material3 DatePicker), null until picked. */
    private val _moveInDateMillis = MutableStateFlow<Long?>(null)
    val moveInDateMillis: StateFlow<Long?> = _moveInDateMillis.asStateFlow()

    init {
        viewModelScope.launch {
            getPropertyByIdUseCase(propertyId)
                .onSuccess { _uiState.value = CheckoutUiState.Success(it) }
                .onFailure { _uiState.value = CheckoutUiState.Error(it.message ?: "Unknown error") }
        }
    }

    fun selectYears(years: Int) { _selectedYears.value = years }
    fun setMoveInDate(millis: Long?) { _moveInDateMillis.value = millis }
}
