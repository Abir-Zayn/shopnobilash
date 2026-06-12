package com.dorent.app.ui.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dorent.app.data.model.MOCK_PROPERTIES
import com.dorent.app.data.repository.PropertyRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class ProfileUiState(
    val savedCount: Int = 0,
    val listingCount: Int = 2,
    val rating: Double = 4.9,
)

class ProfileViewModel(private val repository: PropertyRepository) : ViewModel() {
    val uiState: StateFlow<ProfileUiState> = repository.getSavedIds()
        .map { savedIds -> ProfileUiState(savedCount = savedIds.size) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ProfileUiState())
}
