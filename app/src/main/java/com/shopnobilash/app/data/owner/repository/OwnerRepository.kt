package com.shopnobilash.app.data.owner.repository

import com.shopnobilash.app.data.owner.model.Owner
import com.shopnobilash.app.data.owner.model.OwnerResult

interface OwnerRepository {
    /** Resolve the owner row bound to [userId], or null if the account is not yet an owner. */
    suspend fun resolveOwner(userId: String): Result<Owner?>

    /** Create the owner row for [userId]. Maps Appwrite 409s to typed [OwnerResult] cases. */
    suspend fun createOwner(
        userId: String,
        name: String,
        addressLine1: String,
        addressLine2: String?,
        tinCertificateNo: String?,
        profilePictureUrl: String?,
    ): OwnerResult
}
