package com.shopnobilash.app.data.profile.repository

import com.shopnobilash.app.constants.PROPERTY_DATABASE_ID
import com.shopnobilash.app.constants.TABLE_PROFILES
import com.shopnobilash.app.data.profile.model.Profile
import io.appwrite.Permission
import io.appwrite.Role
import io.appwrite.exceptions.AppwriteException
import io.appwrite.services.Databases

class ProfileRepositoryImpl(private val databases: Databases) : ProfileRepository {

    override suspend fun createProfile(profile: Profile): Result<Unit> = runCatching {
        databases.createDocument(
            databaseId = PROPERTY_DATABASE_ID,
            collectionId = TABLE_PROFILES,
            documentId = profile.id,
            data = buildMap {
                put("full_name", profile.fullName)
                put("phone_number", profile.phoneNumber)
                put("gmail", profile.gmail)
                put("permanent_address", profile.permanentAddress)
                put("emergency_contact", profile.emergencyContact)
                put("emergency_contact_recipient", profile.emergencyContactRecipient)
                put("identity_type", profile.identityType)
                put("identity_number", profile.identityNumber)
                put("is_verified", false)
                profile.profilePictureUrl?.let { put("profile_picture_url", it) }
                profile.identityImageUrl?.let { put("identity_image_url", it) }
                profile.bloodGroup?.let { put("blood_group", it) }
                profile.occupation?.let { put("occupation", it) }
            },
            // Owner can read/update/delete own profile
            permissions = listOf(
                Permission.read(Role.user(profile.id)),
                Permission.update(Role.user(profile.id)),
                Permission.delete(Role.user(profile.id)),
            ),
        )
    }.map { }

    override suspend fun getProfile(userId: String): Result<Profile?> = runCatching {
        try {
            val doc = databases.getDocument(
                databaseId = PROPERTY_DATABASE_ID,
                collectionId = TABLE_PROFILES,
                documentId = userId,
            )
            @Suppress("UNCHECKED_CAST")
            (doc.data as Map<String, Any>).toProfile(doc.id)
        } catch (e: AppwriteException) {
            if (e.code == 404) null else throw e
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun Map<String, Any>.toProfile(id: String) = Profile(
        id = id,
        fullName = this["full_name"] as String,
        phoneNumber = this["phone_number"] as String,
        gmail = this["gmail"] as String,
        profilePictureUrl = this["profile_picture_url"] as? String,
        permanentAddress = this["permanent_address"] as String,
        bloodGroup = this["blood_group"] as? String,
        occupation = this["occupation"] as? String,
        emergencyContact = this["emergency_contact"] as String,
        emergencyContactRecipient = this["emergency_contact_recipient"] as String,
        identityType = this["identity_type"] as String,
        identityNumber = this["identity_number"] as String,
        identityImageUrl = this["identity_image_url"] as? String,
        isVerified = this["is_verified"] as? Boolean ?: false,
    )
}
