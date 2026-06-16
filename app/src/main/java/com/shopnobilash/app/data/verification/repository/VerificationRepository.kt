package com.shopnobilash.app.data.verification.repository

import com.shopnobilash.app.data.verification.model.Verification

interface VerificationRepository {
    suspend fun submitVerification(userId: String, documentType: String, fileId: String): Result<Unit>
    suspend fun getVerificationStatus(userId: String): Result<Verification?>
}
