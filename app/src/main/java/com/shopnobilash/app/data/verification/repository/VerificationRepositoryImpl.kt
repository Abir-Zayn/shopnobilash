package com.shopnobilash.app.data.verification.repository

import com.shopnobilash.app.constants.PROPERTY_DATABASE_ID
import com.shopnobilash.app.constants.TABLE_VERIFICATIONS
import com.shopnobilash.app.constants.VERIFICATION_STATUS_PENDING
import com.shopnobilash.app.data.verification.model.Verification
import io.appwrite.ID
import io.appwrite.Permission
import io.appwrite.Query
import io.appwrite.Role
import io.appwrite.services.Databases

class VerificationRepositoryImpl(private val databases: Databases) : VerificationRepository {

    override suspend fun submitVerification(
        userId: String,
        documentType: String,
        fileId: String,
    ): Result<Unit> = runCatching {
        databases.createDocument(
            databaseId = PROPERTY_DATABASE_ID,
            collectionId = TABLE_VERIFICATIONS,
            documentId = ID.unique(),
            data = mapOf(
                "userId" to userId,
                "documentType" to documentType,
                "fileId" to fileId,
                "verificationStatus" to VERIFICATION_STATUS_PENDING,
            ),
            // Owner can read own submission; only admin can update status
            permissions = listOf(
                Permission.read(Role.user(userId)),
                Permission.read(Role.label("admin")),
                Permission.update(Role.label("admin")),
            ),
        )
    }.map { }

    override suspend fun getVerificationStatus(userId: String): Result<Verification?> = runCatching {
        val result = databases.listDocuments(
            databaseId = PROPERTY_DATABASE_ID,
            collectionId = TABLE_VERIFICATIONS,
            queries = listOf(
                Query.equal("userId", userId),
                Query.orderDesc("\$createdAt"),
                Query.limit(1),
            ),
        )
        result.documents.firstOrNull()?.let { doc ->
            @Suppress("UNCHECKED_CAST")
            val data = doc.data as Map<String, Any>
            Verification(
                id = doc.id,
                userId = data["userId"] as String,
                documentType = data["documentType"] as String,
                fileId = data["fileId"] as String,
                verificationStatus = data["verificationStatus"] as String,
                rejectReason = data["rejectReason"] as? String,
                reviewedAt = data["reviewedAt"] as? String,
                reviewedBy = data["reviewedBy"] as? String,
                createdAt = doc.createdAt,
            )
        }
    }
}
