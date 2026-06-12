package com.dorent.app.ui.feature.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dorent.app.data.model.Property
import com.dorent.app.data.repository.PropertyRepository
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
    private val repository: PropertyRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<CheckoutUiState>(CheckoutUiState.Loading)
    val uiState: StateFlow<CheckoutUiState> = _uiState.asStateFlow()

    private val _selectedTerm = MutableStateFlow(12)
    val selectedTerm: StateFlow<Int> = _selectedTerm.asStateFlow()

    private val _selectedPayment = MutableStateFlow("visa")
    val selectedPayment: StateFlow<String> = _selectedPayment.asStateFlow()

    private val _bookingDone = MutableStateFlow(false)
    val bookingDone: StateFlow<Boolean> = _bookingDone.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getPropertyById(propertyId)
                .onSuccess { _uiState.value = CheckoutUiState.Success(it) }
                .onFailure { _uiState.value = CheckoutUiState.Error(it.message ?: "Unknown error") }
        }
    }

    fun selectTerm(months: Int) { _selectedTerm.value = months }
    fun selectPayment(id: String) { _selectedPayment.value = id }
    fun confirmBooking() { _bookingDone.value = true }
    fun dismissConfirmation() { _bookingDone.value = false }
}
