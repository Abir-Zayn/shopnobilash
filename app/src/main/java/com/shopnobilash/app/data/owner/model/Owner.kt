package com.shopnobilash.app.data.owner.model

/**
 * Listing/seller identity of an app account — bound to the Appwrite Auth user via [userId].
 * One owner record per account ([userId] is UNIQUE-indexed in the `owners` table).
 */
data class Owner(
    val id: String,
    val userId: String?,
    val name: String,
    val addressLine1: String,
    val addressLine2: String? = null,
    val tinCertificateNo: String? = null,
    val profilePictureUrl: String? = null,
)

/** Result of attempting to create an owner row. */
sealed interface OwnerResult {
    data class Created(val owner: Owner) : OwnerResult
    /** 409 on `idx_user_id_unique` — account already has an owner row. */
    data class AlreadyExists(val owner: Owner) : OwnerResult
    /** 409 on `idx_tin_unique` — TIN belongs to another owner. */
    object DuplicateTin : OwnerResult
    data class Error(val message: String) : OwnerResult
}
