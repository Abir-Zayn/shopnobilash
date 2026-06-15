package com.shopnobilash.app.presentation.profile_setup.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shopnobilash.app.data.profile.model.Profile
import com.shopnobilash.app.domain.auth.usecase.CheckSessionUseCase
import com.shopnobilash.app.domain.auth.usecase.GetCurrentUserEmailUseCase
import com.shopnobilash.app.domain.profile.usecase.CreateProfileUseCase
import com.shopnobilash.app.domain.profile.usecase.GetProfileUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ProfileSetupUiState {
    object Loading : ProfileSetupUiState()
    object ShowForm : ProfileSetupUiState()
    object ProfileExists : ProfileSetupUiState()
    object Saved : ProfileSetupUiState()
    data class Error(val message: String) : ProfileSetupUiState()
}

class ProfileSetupViewModel(
    private val checkSessionUseCase: CheckSessionUseCase,
    private val getCurrentUserEmailUseCase: GetCurrentUserEmailUseCase,
    private val getProfileUseCase: GetProfileUseCase,
    private val createProfileUseCase: CreateProfileUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileSetupUiState>(ProfileSetupUiState.Loading)
    val uiState: StateFlow<ProfileSetupUiState> = _uiState.asStateFlow()

    private var currentUserId = ""
    var prefillEmail = ""
        private set

    init { checkExistingProfile() }

    private fun checkExistingProfile() = viewModelScope.launch {
        checkSessionUseCase().fold(
            onSuccess = { userId ->
                currentUserId = userId
                getCurrentUserEmailUseCase().fold(
                    onSuccess = { email ->
                        prefillEmail = email
                        getProfileUseCase(userId).fold(
                            onSuccess = { profile ->
                                _uiState.value = if (profile != null) ProfileSetupUiState.ProfileExists
                                                 else ProfileSetupUiState.ShowForm
                            },
                            onFailure = { _uiState.value = ProfileSetupUiState.ShowForm }
                        )
                    },
                    onFailure = {
                        _uiState.value = ProfileSetupUiState.ShowForm
                    }
                )
            },
            onFailure = {
                _uiState.value = ProfileSetupUiState.Error("Session expired. Please log in again.")
            }
        )
    }

    fun saveProfile(
        fullName: String,
        phoneNumber: String,
        permanentAddress: String,
        emergencyContact: String,
        emergencyContactRecipient: String,
        identityType: String,
        identityNumber: String,
    ) {
        if (listOf(fullName, phoneNumber, permanentAddress, emergencyContact,
                emergencyContactRecipient, identityType, identityNumber).any { it.isBlank() }) {
            _uiState.value = ProfileSetupUiState.Error("Please fill in all required fields")
            return
        }
        viewModelScope.launch {
            _uiState.value = ProfileSetupUiState.Loading
            createProfileUseCase(
                Profile(
                    id = currentUserId,
                    fullName = fullName.trim(),
                    phoneNumber = phoneNumber.trim(),
                    gmail = prefillEmail,
                    permanentAddress = permanentAddress.trim(),
                    emergencyContact = emergencyContact.trim(),
                    emergencyContactRecipient = emergencyContactRecipient.trim(),
                    identityType = identityType,
                    identityNumber = identityNumber.trim(),
                )
            ).fold(
                onSuccess = { _uiState.value = ProfileSetupUiState.Saved },
                onFailure = { _uiState.value = ProfileSetupUiState.Error(it.message ?: "Failed to save profile") }
            )
        }
    }

    fun clearError() { _uiState.value = ProfileSetupUiState.ShowForm }
}
