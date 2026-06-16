package com.shopnobilash.app.domain.verification.usecase

import com.shopnobilash.app.data.verification.repository.VerificationRepository

class SubmitVerificationUseCase(private val repository: VerificationRepository) {
    suspend operator fun invoke(userId: String, documentType: String, fileId: String): Result<Unit> =
        repository.submitVerification(userId, documentType, fileId)
}
