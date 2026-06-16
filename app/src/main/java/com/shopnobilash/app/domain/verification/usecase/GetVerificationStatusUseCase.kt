package com.shopnobilash.app.domain.verification.usecase

import com.shopnobilash.app.data.verification.model.Verification
import com.shopnobilash.app.data.verification.repository.VerificationRepository

class GetVerificationStatusUseCase(private val repository: VerificationRepository) {
    suspend operator fun invoke(userId: String): Result<Verification?> =
        repository.getVerificationStatus(userId)
}
