package com.shopnobilash.app.domain.owner.usecase

import com.shopnobilash.app.data.owner.model.OwnerResult
import com.shopnobilash.app.data.owner.repository.OwnerRepository

class CreateOwnerUseCase(private val repository: OwnerRepository) {
    suspend operator fun invoke(
        userId: String,
        name: String,
        addressLine1: String,
        addressLine2: String?,
        tinCertificateNo: String?,
        profilePictureUrl: String?,
    ): OwnerResult = repository.createOwner(
        userId = userId,
        name = name,
        addressLine1 = addressLine1,
        addressLine2 = addressLine2,
        tinCertificateNo = tinCertificateNo,
        profilePictureUrl = profilePictureUrl,
    )
}
