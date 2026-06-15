package com.shopnobilash.app.data.auth.repository

import androidx.activity.ComponentActivity
import io.appwrite.ID
import io.appwrite.enums.OAuthProvider
import io.appwrite.services.Account

class AuthRepositoryImpl(private val account: Account) : AuthRepository {

    override suspend fun checkSession(): Result<String> = runCatching {
        val user = account.get()
        user.id
    }

    override suspend fun getCurrentUserEmail(): Result<String> = runCatching {
        val user = account.get()
        user.email
    }

    override suspend fun logout(): Result<Unit> = runCatching {
        account.deleteSession("current")
    }.map { }

    override suspend fun signUp(name: String, email: String, password: String): Result<String> = runCatching {
        try { account.deleteSession(sessionId = "current") } catch (_: Exception) {}
        
        val user = account.create(
            userId = ID.unique(),
            email = email.trim(),
            password = password,
            name = name.trim(),
        )
        account.createEmailToken(userId = user.id, email = email.trim())
        user.id
    }

    override suspend fun login(email: String, password: String): Result<Unit> = runCatching {
        try { account.deleteSession(sessionId = "current") } catch (_: Exception) {}
        
        account.createEmailPasswordSession(email = email.trim(), password = password)
    }.map { }

    override suspend fun loginWithOAuth(activity: Any, provider: String): Result<Unit> = runCatching {
        try { account.deleteSession(sessionId = "current") } catch (_: Exception) {}
        
        val appwriteProvider = when (provider.lowercase()) {
            "google" -> OAuthProvider.GOOGLE
            "facebook" -> OAuthProvider.FACEBOOK
            else -> throw IllegalArgumentException("Unsupported OAuth provider: $provider")
        }
        val componentActivity = activity as? ComponentActivity
            ?: throw IllegalArgumentException("Activity must be an instance of ComponentActivity")
            
        account.createOAuth2Session(
            activity = componentActivity,
            provider = appwriteProvider
        )
    }.map { }

    override suspend fun verifyOtp(userId: String, code: String): Result<Unit> = runCatching {
        account.createSession(userId = userId, secret = code.trim())
    }.map { }

    override suspend fun resendOtp(userId: String, email: String): Result<Unit> = runCatching {
        account.createEmailToken(userId = userId, email = email)
    }.map { }
}
