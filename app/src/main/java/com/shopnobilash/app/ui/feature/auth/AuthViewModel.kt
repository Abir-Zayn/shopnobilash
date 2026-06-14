package com.shopnobilash.app.ui.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.appwrite.ID
import io.appwrite.exceptions.AppwriteException
import io.appwrite.services.Account
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class OtpSent(val userId: String, val email: String) : AuthUiState()  // navigate to verify screen
    object OtpResent : AuthUiState()      // resend success → toast only
    object EmailVerified : AuthUiState()  // OTP correct → navigate to Home
    data class Error(val message: String) : AuthUiState()
}

class AuthViewModel(private val account: Account) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun signUp(name: String, email: String, password: String, confirmPassword: String) {
        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            _uiState.value = AuthUiState.Error("All fields are required")
            return
        }
        if (password != confirmPassword) {
            _uiState.value = AuthUiState.Error("Passwords do not match")
            return
        }
        if (password.length < 8) {
            _uiState.value = AuthUiState.Error("Password must be at least 8 characters")
            return
        }
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                // Clear any leftover session — Email OTP requires no active session
                try { account.deleteSession(sessionId = "current") } catch (_: Exception) {}

                val user = account.create(
                    userId = ID.unique(),
                    email = email.trim(),
                    password = password,
                    name = name.trim(),
                )
                // Send a 6-digit OTP code to the email
                account.createEmailToken(userId = user.id, email = email.trim())
                _uiState.value = AuthUiState.OtpSent(user.id, email.trim())
            } catch (e: AppwriteException) {
                _uiState.value = AuthUiState.Error(e.message ?: "Sign up failed. Try again.")
            }
        }
    }

    fun verifyOtp(userId: String, code: String) {
        if (code.isBlank()) {
            _uiState.value = AuthUiState.Error("Enter the code from your email")
            return
        }
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                // Completing the Email OTP creates a session AND marks the email verified
                account.createSession(userId = userId, secret = code.trim())
                _uiState.value = AuthUiState.EmailVerified
            } catch (e: AppwriteException) {
                _uiState.value = AuthUiState.Error(e.message ?: "Invalid or expired code. Try again.")
            }
        }
    }

    fun resendOtp(userId: String, email: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                account.createEmailToken(userId = userId, email = email)
                _uiState.value = AuthUiState.OtpResent
            } catch (e: AppwriteException) {
                _uiState.value = AuthUiState.Error(e.message ?: "Failed to resend code. Try again.")
            }
        }
    }

    fun clearError() { _uiState.value = AuthUiState.Idle }
}
