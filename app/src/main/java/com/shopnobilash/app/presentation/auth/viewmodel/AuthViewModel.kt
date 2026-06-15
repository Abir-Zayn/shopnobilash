package com.shopnobilash.app.presentation.auth.viewmodel

import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shopnobilash.app.domain.auth.usecase.CheckSessionUseCase
import com.shopnobilash.app.domain.auth.usecase.LoginUseCase
import com.shopnobilash.app.domain.auth.usecase.LoginWithOAuthUseCase
import com.shopnobilash.app.domain.auth.usecase.LogoutUseCase
import com.shopnobilash.app.domain.auth.usecase.ResendOtpUseCase
import com.shopnobilash.app.domain.auth.usecase.SignUpUseCase
import com.shopnobilash.app.domain.auth.usecase.VerifyOtpUseCase
import com.shopnobilash.app.domain.profile.usecase.GetProfileUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    object SessionChecking : AuthUiState()
    object SessionValid : AuthUiState()          // session + profile → Home
    object SessionValidNoProfile : AuthUiState() // session, no profile → ProfileSetup
    object SessionInvalid : AuthUiState()        // no session → onboarding carousel
    data class OtpSent(val userId: String, val email: String) : AuthUiState()
    object OtpResent : AuthUiState()
    object EmailVerified : AuthUiState()
    object LoginSuccess : AuthUiState()
    object LoggedOut : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

class AuthViewModel(
    private val checkSessionUseCase: CheckSessionUseCase,
    private val signUpUseCase: SignUpUseCase,
    private val loginUseCase: LoginUseCase,
    private val loginWithOAuthUseCase: LoginWithOAuthUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val verifyOtpUseCase: VerifyOtpUseCase,
    private val resendOtpUseCase: ResendOtpUseCase,
    private val getProfileUseCase: GetProfileUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun checkSession() {
        viewModelScope.launch {
            _uiState.value = AuthUiState.SessionChecking
            checkSessionUseCase().fold(
                onSuccess = { userId ->
                    val hasProfile = getProfileUseCase(userId).getOrNull() != null
                    _uiState.value = if (hasProfile) AuthUiState.SessionValid
                                     else AuthUiState.SessionValidNoProfile
                },
                onFailure = {
                    _uiState.value = AuthUiState.SessionInvalid
                }
            )
        }
    }

    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
            _uiState.value = AuthUiState.LoggedOut
        }
    }

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
            signUpUseCase(name, email, password).fold(
                onSuccess = { userId ->
                    _uiState.value = AuthUiState.OtpSent(userId, email.trim())
                },
                onFailure = { e ->
                    _uiState.value = AuthUiState.Error(e.message ?: "Sign up failed. Try again.")
                }
            )
        }
    }

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = AuthUiState.Error("Email and password are required")
            return
        }
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            loginUseCase(email, password).fold(
                onSuccess = {
                    _uiState.value = AuthUiState.LoginSuccess
                },
                onFailure = { e ->
                    _uiState.value = AuthUiState.Error(e.message ?: "Invalid email or password. Try again.")
                }
            )
        }
    }

    fun loginWithGoogle(activity: ComponentActivity) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            loginWithOAuthUseCase(activity, "google").fold(
                onSuccess = {
                    _uiState.value = AuthUiState.LoginSuccess
                },
                onFailure = { e ->
                    _uiState.value = AuthUiState.Error(e.message ?: "Google Sign In failed. Try again.")
                }
            )
        }
    }

    fun loginWithFacebook(activity: ComponentActivity) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            loginWithOAuthUseCase(activity, "facebook").fold(
                onSuccess = {
                    _uiState.value = AuthUiState.LoginSuccess
                },
                onFailure = { e ->
                    _uiState.value = AuthUiState.Error(e.message ?: "Facebook Sign In failed. Try again.")
                }
            )
        }
    }

    fun verifyOtp(userId: String, code: String) {
        if (code.isBlank()) {
            _uiState.value = AuthUiState.Error("Enter the code from your email")
            return
        }
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            verifyOtpUseCase(userId, code).fold(
                onSuccess = {
                    _uiState.value = AuthUiState.EmailVerified
                },
                onFailure = { e ->
                    _uiState.value = AuthUiState.Error(e.message ?: "Invalid or expired code. Try again.")
                }
            )
        }
    }

    fun resendOtp(userId: String, email: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            resendOtpUseCase(userId, email).fold(
                onSuccess = {
                    _uiState.value = AuthUiState.OtpResent
                },
                onFailure = { e ->
                    _uiState.value = AuthUiState.Error(e.message ?: "Failed to resend code. Try again.")
                }
            )
        }
    }

    fun clearError() { _uiState.value = AuthUiState.Idle }
}
