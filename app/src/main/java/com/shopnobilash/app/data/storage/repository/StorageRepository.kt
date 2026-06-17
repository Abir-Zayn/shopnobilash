package com.shopnobilash.app.data.storage.repository

interface StorageRepository {
    suspend fun uploadProfilePicture(userId: String, bytes: ByteArray, mimeType: String): Result<String>
    suspend fun uploadPropertyImage(userId: String, bytes: ByteArray, mimeType: String): Result<String>
    suspend fun uploadVerificationDoc(userId: String, bytes: ByteArray, mimeType: String): Result<String>
    fun getProfilePictureUrl(fileId: String): String
    fun getVerificationDocUrl(fileId: String): String
    suspend fun deleteVerificationDoc(fileId: String): Result<Unit>
}
