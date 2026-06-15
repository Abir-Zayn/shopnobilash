package com.shopnobilash.app.domain.auth.usecase

import com.shopnobilash.app.data.auth.repository.AuthRepository

class VerifyOtpUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(userId: String, code: String): Result<Unit> =
        repository.verifyOtp(userId, code)
}
