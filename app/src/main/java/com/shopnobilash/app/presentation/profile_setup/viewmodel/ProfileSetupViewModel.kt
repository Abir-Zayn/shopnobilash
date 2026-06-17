package com.shopnobilash.app.presentation.profile_setup.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shopnobilash.app.data.profile.model.Profile
import com.shopnobilash.app.data.storage.repository.StorageRepository
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
    private val storageRepository: StorageRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileSetupUiState>(ProfileSetupUiState.Loading)
    val uiState: StateFlow<ProfileSetupUiState> = _uiState.asStateFlow()

    private var currentUserId = ""
    var prefillEmail = ""
        private set

    private var profilePicBytes: ByteArray? = null
    private var profilePicMime = "image/jpeg"
    private var identityImageBytes: ByteArray? = null
    private var identityImageMime = "image/jpeg"

    private val _profilePicUri = MutableStateFlow<Uri?>(null)
    val profilePicUri: StateFlow<Uri?> = _profilePicUri.asStateFlow()

    private val _identityImageUri = MutableStateFlow<Uri?>(null)
    val identityImageUri: StateFlow<Uri?> = _identityImageUri.asStateFlow()

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
                    onFailure = { _uiState.value = ProfileSetupUiState.ShowForm }
                )
            },
            onFailure = {
                _uiState.value = ProfileSetupUiState.Error("Session expired. Please log in again.")
            }
        )
    }

    fun onProfilePicSelected(uri: Uri, context: Context) {
        viewModelScope.launch {
            val bytes = context.contentResolver.openInputStream(uri)?.readBytes() ?: return@launch
            profilePicBytes = bytes
            profilePicMime = context.contentResolver.getType(uri) ?: "image/jpeg"
            _profilePicUri.value = uri
        }
    }

    fun onIdentityImageSelected(uri: Uri, context: Context) {
        viewModelScope.launch {
            val bytes = context.contentResolver.openInputStream(uri)?.readBytes() ?: return@launch
            identityImageBytes = bytes
            identityImageMime = context.contentResolver.getType(uri) ?: "image/jpeg"
            _identityImageUri.value = uri
        }
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

            var profilePictureUrl: String? = null
            profilePicBytes?.let { bytes ->
                storageRepository.uploadProfilePicture(currentUserId, bytes, profilePicMime).fold(
                    onSuccess = { fileId ->
                        profilePictureUrl = storageRepository.getProfilePictureUrl(fileId)
                    },
                    onFailure = {
                        _uiState.value = ProfileSetupUiState.Error(it.message ?: "Profile picture upload failed")
                        return@launch
                    },
                )
            }

            var identityImageUrl: String? = null
            identityImageBytes?.let { bytes ->
                storageRepository.uploadVerificationDoc(currentUserId, bytes, identityImageMime).fold(
                    onSuccess = { fileId ->
                        identityImageUrl = storageRepository.getVerificationDocUrl(fileId)
                    },
                    onFailure = {
                        _uiState.value = ProfileSetupUiState.Error(it.message ?: "Identity document upload failed")
                        return@launch
                    },
                )
            }

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
                    profilePictureUrl = profilePictureUrl,
                    identityImageUrl = identityImageUrl,
                )
            ).fold(
                onSuccess = { _uiState.value = ProfileSetupUiState.Saved },
                onFailure = { _uiState.value = ProfileSetupUiState.Error(it.message ?: "Failed to save profile") }
            )
        }
    }

    fun clearError() { _uiState.value = ProfileSetupUiState.ShowForm }
}
