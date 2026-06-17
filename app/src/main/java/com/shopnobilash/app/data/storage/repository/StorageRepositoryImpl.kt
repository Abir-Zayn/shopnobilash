package com.shopnobilash.app.data.storage.repository

import com.shopnobilash.app.constants.APPWRITE_PROJECT_ID
import com.shopnobilash.app.constants.APPWRITE_PUBLIC_ENDPOINT
import com.shopnobilash.app.constants.BUCKET_PROPERTY_IMAGES
import com.shopnobilash.app.constants.BUCKET_USER_PROFILES
import com.shopnobilash.app.constants.BUCKET_VERIFICATION_DOCS
import io.appwrite.ID
import io.appwrite.Permission
import io.appwrite.Role
import io.appwrite.models.InputFile
import io.appwrite.services.Storage

class StorageRepositoryImpl(private val storage: Storage) : StorageRepository {

    override suspend fun uploadProfilePicture(userId: String, bytes: ByteArray, mimeType: String): Result<String> =
        runCatching {
            storage.createFile(
                bucketId = BUCKET_USER_PROFILES,
                fileId = ID.unique(),
                file = InputFile.fromBytes(bytes, "avatar.jpg", mimeType),
                permissions = listOf(
                    Permission.read(Role.any()),
                    Permission.write(Role.user(userId)),
                ),
            ).id
        }

    override suspend fun uploadPropertyImage(userId: String, bytes: ByteArray, mimeType: String): Result<String> =
        runCatching {
            storage.createFile(
                bucketId = BUCKET_PROPERTY_IMAGES,
                fileId = ID.unique(),
                file = InputFile.fromBytes(bytes, "property.jpg", mimeType),
                permissions = listOf(
                    Permission.read(Role.any()),
                    Permission.write(Role.user(userId)),
                ),
            ).id
        }

    override suspend fun uploadVerificationDoc(userId: String, bytes: ByteArray, mimeType: String): Result<String> =
        runCatching {
            storage.createFile(
                bucketId = BUCKET_VERIFICATION_DOCS,
                fileId = ID.unique(),
                file = InputFile.fromBytes(bytes, "document", mimeType),
                // No public read — only owner and admin can access
                permissions = listOf(
                    Permission.read(Role.user(userId)),
                    Permission.read(Role.label("admin")),
                ),
            ).id
        }

    override fun getProfilePictureUrl(fileId: String): String =
        "$APPWRITE_PUBLIC_ENDPOINT/storage/buckets/$BUCKET_USER_PROFILES/files/$fileId/preview?project=$APPWRITE_PROJECT_ID"

    override fun getVerificationDocUrl(fileId: String): String =
        "$APPWRITE_PUBLIC_ENDPOINT/storage/buckets/$BUCKET_VERIFICATION_DOCS/files/$fileId/view?project=$APPWRITE_PROJECT_ID"

    override suspend fun deleteVerificationDoc(fileId: String): Result<Unit> =
        runCatching {
            storage.deleteFile(bucketId = BUCKET_VERIFICATION_DOCS, fileId = fileId)
        }.map { }
}
