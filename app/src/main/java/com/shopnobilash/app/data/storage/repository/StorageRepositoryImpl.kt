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
            val extension = when (mimeType) {
                "image/png" -> "png"
                "image/webp" -> "webp"
                "image/gif" -> "gif"
                "application/pdf" -> "pdf"
                else -> "jpg"
            }
            storage.createFile(
                bucketId = BUCKET_VERIFICATION_DOCS,
                fileId = ID.unique(),
                file = InputFile.fromBytes(bytes, "document.$extension", mimeType),
                // No public read — only owner can read. Admins bypass permissions via server-side/admin API keys anyway.
                permissions = listOf(
                    Permission.read(Role.user(userId)),
                ),
            ).id
        }

    override fun getProfilePictureUrl(fileId: String): String =
        "$APPWRITE_PUBLIC_ENDPOINT/storage/buckets/$BUCKET_USER_PROFILES/files/$fileId/preview?project=$APPWRITE_PROJECT_ID"

    override fun getPropertyImageUrl(fileId: String): String =
        "$APPWRITE_PUBLIC_ENDPOINT/storage/buckets/$BUCKET_PROPERTY_IMAGES/files/$fileId/view?project=$APPWRITE_PROJECT_ID"

    override fun getVerificationDocUrl(fileId: String): String =
        "$APPWRITE_PUBLIC_ENDPOINT/storage/buckets/$BUCKET_VERIFICATION_DOCS/files/$fileId/view?project=$APPWRITE_PROJECT_ID"

    override suspend fun deleteVerificationDoc(fileId: String): Result<Unit> =
        runCatching {
            storage.deleteFile(bucketId = BUCKET_VERIFICATION_DOCS, fileId = fileId)
        }.map { }
}
