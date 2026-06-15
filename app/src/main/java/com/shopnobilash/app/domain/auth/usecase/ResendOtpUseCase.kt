package com.shopnobilash.app.domain.auth.usecase

import com.shopnobilash.app.data.auth.repository.AuthRepository

class ResendOtpUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(userId: String, email: String): Result<Unit> =
        repository.resendOtp(userId, email)
}
