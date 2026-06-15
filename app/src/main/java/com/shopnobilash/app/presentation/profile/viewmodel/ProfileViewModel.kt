package com.shopnobilash.app.presentation.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shopnobilash.app.domain.auth.usecase.LogoutUseCase
import com.shopnobilash.app.domain.property.usecase.GetSavedPropertyIdsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ProfileUiState(
    val savedCount: Int = 0,
    val listingCount: Int = 2,
    val rating: Double = 4.9,
)

class ProfileViewModel(
    private val getSavedPropertyIdsUseCase: GetSavedPropertyIdsUseCase,
    private val logoutUseCase: LogoutUseCase,
) : ViewModel() {

    val uiState: StateFlow<ProfileUiState> = getSavedPropertyIdsUseCase()
        .map { savedIds -> ProfileUiState(savedCount = savedIds.size) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ProfileUiState())

    private val _loggedOut = MutableStateFlow(false)
    val loggedOut: StateFlow<Boolean> = _loggedOut.asStateFlow()

    fun logout() = viewModelScope.launch {
        logoutUseCase()
        _loggedOut.value = true
    }
}
