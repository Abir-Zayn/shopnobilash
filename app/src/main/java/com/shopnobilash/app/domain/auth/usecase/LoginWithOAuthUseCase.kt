package com.shopnobilash.app.domain.auth.usecase

import com.shopnobilash.app.data.auth.repository.AuthRepository

class LoginWithOAuthUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(activity: Any, provider: String): Result<Unit> =
        repository.loginWithOAuth(activity, provider)
}
