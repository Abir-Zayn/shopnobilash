package com.shopnobilash.app.presentation.verification.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shopnobilash.app.data.storage.repository.StorageRepository
import com.shopnobilash.app.data.verification.model.Verification
import com.shopnobilash.app.domain.auth.usecase.CheckSessionUseCase
import com.shopnobilash.app.domain.verification.usecase.GetVerificationStatusUseCase
import com.shopnobilash.app.domain.verification.usecase.SubmitVerificationUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class VerificationUiState {
    object Loading : VerificationUiState()
    data class Idle(val hasUploadedFile: Boolean = false) : VerificationUiState()
    object Uploading : VerificationUiState()
    object Submitting : VerificationUiState()
    object Success : VerificationUiState()
    data class AlreadySubmitted(val verification: Verification) : VerificationUiState()
    data class Error(val message: String) : VerificationUiState()
}

class VerificationViewModel(
    private val checkSessionUseCase: CheckSessionUseCase,
    private val getVerificationStatusUseCase: GetVerificationStatusUseCase,
    private val submitVerificationUseCase: SubmitVerificationUseCase,
    private val storageRepository: StorageRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<VerificationUiState>(VerificationUiState.Loading)
    val uiState: StateFlow<VerificationUiState> = _uiState.asStateFlow()

    private var currentUserId = ""
    private var pendingFileId = ""

    init { loadStatus() }

    private fun loadStatus() = viewModelScope.launch {
        checkSessionUseCase().fold(
            onSuccess = { userId ->
                currentUserId = userId
                getVerificationStatusUseCase(userId).fold(
                    onSuccess = { verification ->
                        _uiState.value = if (verification != null)
                            VerificationUiState.AlreadySubmitted(verification)
                        else
                            VerificationUiState.Idle()
                    },
                    onFailure = { _uiState.value = VerificationUiState.Idle() },
                )
            },
            onFailure = {
                _uiState.value = VerificationUiState.Error("Session expired. Please log in again.")
            },
        )
    }

    fun onFileSelected(uri: Uri, context: Context) {
        viewModelScope.launch {
            _uiState.value = VerificationUiState.Uploading
            val bytes = context.contentResolver.openInputStream(uri)?.readBytes()
                ?: run {
                    _uiState.value = VerificationUiState.Error("Could not read the selected file")
                    return@launch
                }
            val mimeType = context.contentResolver.getType(uri) ?: "image/jpeg"
            storageRepository.uploadVerificationDoc(currentUserId, bytes, mimeType).fold(
                onSuccess = { fileId ->
                    pendingFileId = fileId
                    _uiState.value = VerificationUiState.Idle(hasUploadedFile = true)
                },
                onFailure = {
                    _uiState.value = VerificationUiState.Error(it.message ?: "Upload failed")
                },
            )
        }
    }

    fun submitVerification(documentType: String) {
        if (pendingFileId.isBlank()) {
            _uiState.value = VerificationUiState.Error("Please select a document to upload first")
            return
        }
        viewModelScope.launch {
            _uiState.value = VerificationUiState.Submitting
            submitVerificationUseCase(currentUserId, documentType, pendingFileId).fold(
                onSuccess = { _uiState.value = VerificationUiState.Success },
                onFailure = {
                    _uiState.value = VerificationUiState.Error(it.message ?: "Submission failed")
                },
            )
        }
    }

    fun clearError() {
        _uiState.value = VerificationUiState.Idle(hasUploadedFile = pendingFileId.isNotBlank())
    }
}
