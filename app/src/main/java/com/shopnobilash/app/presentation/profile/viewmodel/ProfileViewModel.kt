package com.shopnobilash.app.presentation.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shopnobilash.app.domain.auth.usecase.CheckSessionUseCase
import com.shopnobilash.app.domain.auth.usecase.GetCurrentUserEmailUseCase
import com.shopnobilash.app.domain.auth.usecase.LogoutUseCase
import com.shopnobilash.app.domain.profile.usecase.GetProfileUseCase
import com.shopnobilash.app.domain.property.usecase.GetSavedPropertyIdsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ProfileUiState(
    val savedCount: Int = 0,
    val listingCount: Int = 0,
    val rating: Double = 0.0,
    val name: String = "",
    val email: String = "",
    val profilePictureUrl: String? = null,
)

class ProfileViewModel(
    private val getSavedPropertyIdsUseCase: GetSavedPropertyIdsUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val checkSessionUseCase: CheckSessionUseCase,
    private val getProfileUseCase: GetProfileUseCase,
    private val getCurrentUserEmailUseCase: GetCurrentUserEmailUseCase,
) : ViewModel() {

    private val _profileData = MutableStateFlow(ProfileUiState())

    init {
        viewModelScope.launch {
            checkSessionUseCase().fold(
                onSuccess = { userId ->
                    getProfileUseCase(userId).fold(
                        onSuccess = { profile ->
                            _profileData.value = _profileData.value.copy(
                                name = profile?.fullName ?: "",
                                email = profile?.gmail ?: "",
                                profilePictureUrl = profile?.profilePictureUrl,
                                listingCount = profile?.totalListings ?: 0,
                                rating = (profile?.ratings ?: 0).toDouble(),
                            )
                            if (profile == null) {
                                getCurrentUserEmailUseCase().fold(
                                    onSuccess = { email ->
                                        _profileData.value = _profileData.value.copy(email = email)
                                    },
                                    onFailure = { /* leave empty */ },
                                )
                            }
                        },
                        onFailure = {
                            getCurrentUserEmailUseCase().fold(
                                onSuccess = { email ->
                                    _profileData.value = _profileData.value.copy(email = email)
                                },
                                onFailure = { /* leave empty */ },
                            )
                        },
                    )
                },
                onFailure = { /* leave empty */ },
            )
        }
    }

    val uiState: StateFlow<ProfileUiState> = combine(
        getSavedPropertyIdsUseCase(),
        _profileData,
    ) { savedIds, profileData ->
        profileData.copy(savedCount = savedIds.size)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ProfileUiState())

    private val _loggedOut = MutableStateFlow(false)
    val loggedOut: StateFlow<Boolean> = _loggedOut.asStateFlow()

    fun logout() = viewModelScope.launch {
        logoutUseCase()
        _loggedOut.value = true
    }
}
