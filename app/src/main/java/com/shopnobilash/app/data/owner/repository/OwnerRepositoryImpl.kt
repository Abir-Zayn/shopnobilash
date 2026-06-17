package com.shopnobilash.app.data.owner.repository

import com.shopnobilash.app.constants.PROPERTY_DATABASE_ID
import com.shopnobilash.app.constants.TABLE_OWNERS
import com.shopnobilash.app.data.owner.model.Owner
import com.shopnobilash.app.data.owner.model.OwnerResult
import io.appwrite.ID
import io.appwrite.Permission
import io.appwrite.Query
import io.appwrite.Role
import io.appwrite.exceptions.AppwriteException
import io.appwrite.services.Databases

class OwnerRepositoryImpl(private val databases: Databases) : OwnerRepository {

    override suspend fun resolveOwner(userId: String): Result<Owner?> = runCatching {
        val res = databases.listDocuments(
            databaseId = PROPERTY_DATABASE_ID,
            collectionId = TABLE_OWNERS,
            queries = listOf(
                Query.equal("user_id", userId),
                Query.limit(1),
            ),
        )
        res.documents.firstOrNull()?.let { doc ->
            @Suppress("UNCHECKED_CAST")
            (doc.data as Map<String, Any?>).toOwner(doc.id)
        }
    }

    override suspend fun createOwner(
        userId: String,
        name: String,
        addressLine1: String,
        addressLine2: String?,
        tinCertificateNo: String?,
        profilePictureUrl: String?,
    ): OwnerResult {
        return try {
            val doc = databases.createDocument(
                databaseId = PROPERTY_DATABASE_ID,
                collectionId = TABLE_OWNERS,
                documentId = ID.unique(),
                data = buildMap {
                    put("user_id", userId)
                    put("name", name)
                    put("address_line1", addressLine1)
                    addressLine2?.takeIf { it.isNotBlank() }?.let { put("address_line2", it) }
                    tinCertificateNo?.takeIf { it.isNotBlank() }?.let { put("tin_certificate_no", it) }
                    profilePictureUrl?.takeIf { it.isNotBlank() }?.let { put("profile_picture_url", it) }
                },
                permissions = listOf(
                    Permission.read(Role.any()),
                    Permission.update(Role.user(userId)),
                    Permission.delete(Role.user(userId)),
                ),
            )
            @Suppress("UNCHECKED_CAST")
            OwnerResult.Created((doc.data as Map<String, Any?>).toOwner(doc.id))
        } catch (e: AppwriteException) {
            if (e.code == 409) {
                // Distinguish the two UNIQUE indexes: if an owner row already exists for
                // this account, it was the user_id clash; otherwise the TIN is taken.
                val existing = resolveOwner(userId).getOrNull()
                if (existing != null) OwnerResult.AlreadyExists(existing)
                else OwnerResult.DuplicateTin
            } else {
                OwnerResult.Error(e.message ?: "Could not create owner account")
            }
        } catch (e: Exception) {
            OwnerResult.Error(e.message ?: "Could not create owner account")
        }
    }

    private fun Map<String, Any?>.toOwner(id: String) = Owner(
        id = id,
        userId = this["user_id"] as? String,
        name = this["name"] as? String ?: "",
        addressLine1 = this["address_line1"] as? String ?: "",
        addressLine2 = this["address_line2"] as? String,
        tinCertificateNo = this["tin_certificate_no"] as? String,
        profilePictureUrl = this["profile_picture_url"] as? String,
    )
}
