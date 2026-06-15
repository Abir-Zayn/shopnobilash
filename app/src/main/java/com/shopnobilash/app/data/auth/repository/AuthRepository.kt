package com.shopnobilash.app.data.auth.repository

interface AuthRepository {
    suspend fun checkSession(): Result<String>
    suspend fun getCurrentUserEmail(): Result<String>
    suspend fun logout(): Result<Unit>
    suspend fun signUp(name: String, email: String, password: String): Result<String>
    suspend fun login(email: String, password: String): Result<Unit>
    suspend fun loginWithOAuth(activity: Any, provider: String): Result<Unit>
    suspend fun verifyOtp(userId: String, code: String): Result<Unit>
    suspend fun resendOtp(userId: String, email: String): Result<Unit>
}
