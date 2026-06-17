package com.shopnobilash.app.presentation.profile_setup.viewmodel

import android.util.Log
import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shopnobilash.app.data.profile.model.Profile
import com.shopnobilash.app.data.profile.model.IdentityType
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
        Log.d("ProfileSetup", "checkExistingProfile() was called.")
        checkSessionUseCase().fold(
            onSuccess = { userId ->
                Log.d("ProfileSetup", "Session verified successfully for user: $userId")
                currentUserId = userId
                getCurrentUserEmailUseCase().fold(
                    onSuccess = { email ->
                        prefillEmail = email
                        getProfileUseCase(userId).fold(
                            onSuccess = { profile ->
                                Log.d("ProfileSetup", "Profile query succeeded. Profile exists: ${profile != null}")
                                _uiState.value = if (profile != null) ProfileSetupUiState.ProfileExists
                                                 else ProfileSetupUiState.ShowForm
                            },
                            onFailure = { 
                                Log.w("ProfileSetup", "Failed to retrieve profile: ${it.message}")
                                _uiState.value = ProfileSetupUiState.ShowForm 
                            }
                        )
                    },
                    onFailure = { 
                        Log.e("ProfileSetup", "Failed to get current user email")
                        _uiState.value = ProfileSetupUiState.ShowForm 
                    }
                )
            },
            onFailure = {
                Log.e("ProfileSetup", "Session check failed: ${it.message}")
                _uiState.value = ProfileSetupUiState.Error("Session expired. Please log in again.")
            }
        )
    }

    fun onProfilePicSelected(uri: Uri, context: Context) {
        viewModelScope.launch {
            Log.d("ProfileSetup", "Profile picture selected: $uri")
            val bytes = context.contentResolver.openInputStream(uri)?.readBytes() ?: return@launch
            profilePicBytes = bytes
            profilePicMime = context.contentResolver.getType(uri) ?: "image/jpeg"
            _profilePicUri.value = uri
        }
    }

    fun onIdentityImageSelected(uri: Uri, context: Context) {
        viewModelScope.launch {
            Log.d("ProfileSetup", "Identity image selected: $uri")
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
        Log.d("ProfileSetup", "saveProfile() called with fullName=$fullName, phoneNumber=$phoneNumber, identityType=$identityType")
        if (listOf(fullName, phoneNumber, permanentAddress, emergencyContact,
                emergencyContactRecipient, identityType, identityNumber).any { it.isBlank() }) {
            Log.w("ProfileSetup", "Validation failed: Some required fields are blank.")
            _uiState.value = ProfileSetupUiState.Error("Please fill in all required fields")
            return
        }

        val cleanPhone = phoneNumber.trim()
        val cleanEmergency = emergencyContact.trim()

        if (cleanPhone.length != 11 || !cleanPhone.all { it.isDigit() } || !cleanPhone.startsWith("01")) {
            Log.w("ProfileSetup", "Validation failed: Phone number (${cleanPhone}) must be exactly 11 digits, numeric, and start with 01.")
            _uiState.value = ProfileSetupUiState.Error("Phone number must be exactly 11 digits and start with 01")
            return
        }
        if (cleanEmergency.length != 11 || !cleanEmergency.all { it.isDigit() } || !cleanEmergency.startsWith("01")) {
            Log.w("ProfileSetup", "Validation failed: Emergency contact number (${cleanEmergency}) must be exactly 11 digits, numeric, and start with 01.")
            _uiState.value = ProfileSetupUiState.Error("Emergency contact number must be exactly 11 digits and start with 01")
            return
        }
        if (permanentAddress.trim().length < 20) {
            Log.w("ProfileSetup", "Validation failed: Permanent address length (${permanentAddress.trim().length}) is less than 20 characters.")
            _uiState.value = ProfileSetupUiState.Error("Permanent address must be at least 20 characters")
            return
        }
        if (emergencyContactRecipient.trim().length < 10) {
            Log.w("ProfileSetup", "Validation failed: Emergency contact person name length (${emergencyContactRecipient.trim().length}) is less than 10 characters.")
            _uiState.value = ProfileSetupUiState.Error("Emergency contact person name must be at least 10 characters")
            return
        }

        val cleanIdentityNumber = identityNumber.trim()
        when (identityType) {
            IdentityType.NID.label -> {
                if (cleanIdentityNumber.length != 10 && cleanIdentityNumber.length != 17) {
                    Log.w("ProfileSetup", "Validation failed: NID length (${cleanIdentityNumber.length}) must be 10 or 17.")
                    _uiState.value = ProfileSetupUiState.Error("NID number must be either 10 or 17 digits")
                    return
                }
                if (!cleanIdentityNumber.all { it.isDigit() }) {
                    Log.w("ProfileSetup", "Validation failed: NID must be numeric.")
                    _uiState.value = ProfileSetupUiState.Error("NID number must contain only digits")
                    return
                }
            }
            IdentityType.PASSPORT.label -> {
                if (cleanIdentityNumber.length != 9) {
                    Log.w("ProfileSetup", "Validation failed: Passport length (${cleanIdentityNumber.length}) must be 9.")
                    _uiState.value = ProfileSetupUiState.Error("Passport number must be exactly 9 characters")
                    return
                }
            }
            IdentityType.BIRTH_CERTIFICATE.label -> {
                if (cleanIdentityNumber.length != 17) {
                    Log.w("ProfileSetup", "Validation failed: Birth Certificate length (${cleanIdentityNumber.length}) must be 17.")
                    _uiState.value = ProfileSetupUiState.Error("Birth Certificate number must be exactly 17 digits")
                    return
                }
                if (!cleanIdentityNumber.all { it.isDigit() }) {
                    Log.w("ProfileSetup", "Validation failed: Birth Certificate must be numeric.")
                    _uiState.value = ProfileSetupUiState.Error("Birth Certificate number must contain only digits")
                    return
                }
            }
        }

        viewModelScope.launch {
            _uiState.value = ProfileSetupUiState.Loading
            Log.d("ProfileSetup", "Saving profile to repository...")

            var profilePictureUrl: String? = null
            profilePicBytes?.let { bytes ->
                Log.d("ProfileSetup", "Uploading profile picture...")
                storageRepository.uploadProfilePicture(currentUserId, bytes, profilePicMime).fold(
                    onSuccess = { fileId ->
                        profilePictureUrl = storageRepository.getProfilePictureUrl(fileId)
                        Log.d("ProfileSetup", "Profile picture uploaded successfully. URL: $profilePictureUrl")
                    },
                    onFailure = {
                        Log.e("ProfileSetup", "Profile picture upload failed", it)
                        _uiState.value = ProfileSetupUiState.Error(it.message ?: "Profile picture upload failed")
                        return@launch
                    },
                )
            }

            var identityImageUrl: String? = null
            identityImageBytes?.let { bytes ->
                Log.d("ProfileSetup", "Uploading identity document...")
                storageRepository.uploadVerificationDoc(currentUserId, bytes, identityImageMime).fold(
                    onSuccess = { fileId ->
                        identityImageUrl = storageRepository.getVerificationDocUrl(fileId)
                        Log.d("ProfileSetup", "Identity document uploaded successfully. URL: $identityImageUrl")
                    },
                    onFailure = {
                        Log.e("ProfileSetup", "Identity document upload failed", it)
                        _uiState.value = ProfileSetupUiState.Error(it.message ?: "Identity document upload failed")
                        return@launch
                    },
                )
            }

            Log.d("ProfileSetup", "Creating profile with CreateProfileUseCase...")
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
                onSuccess = {
                    Log.i("ProfileSetup", "Profile successfully saved and created!")
                    _uiState.value = ProfileSetupUiState.Saved
                },
                onFailure = {
                    Log.e("ProfileSetup", "Failed to save profile via UseCase", it)
                    _uiState.value = ProfileSetupUiState.Error(it.message ?: "Failed to save profile")
                }
            )
        }
    }

    fun clearError() { 
        Log.d("ProfileSetup", "Clearing UI error state")
        _uiState.value = ProfileSetupUiState.ShowForm 
    }
}
