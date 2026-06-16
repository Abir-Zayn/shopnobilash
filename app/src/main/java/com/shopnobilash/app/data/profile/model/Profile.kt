package com.shopnobilash.app.data.profile.model

data class Profile(
    val id: String,
    val fullName: String,
    val phoneNumber: String,
    val gmail: String,
    val profilePictureUrl: String? = null,
    val permanentAddress: String,
    val bloodGroup: String? = null,
    val occupation: String? = null,
    val emergencyContact: String,
    val emergencyContactRecipient: String,
    val identityType: String,
    val identityNumber: String,
    val isVerified: Boolean = false,
)

enum class IdentityType(val label: String) {
    NID("NID"),
    PASSPORT("Passport"),
    BIRTH_CERTIFICATE("Birth Certificate"),
}
