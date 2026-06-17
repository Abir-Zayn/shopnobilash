package com.shopnobilash.app.domain.owner.usecase

import com.shopnobilash.app.data.owner.model.Owner
import com.shopnobilash.app.data.owner.repository.OwnerRepository

class ResolveOwnerUseCase(private val repository: OwnerRepository) {
    suspend operator fun invoke(userId: String): Result<Owner?> = repository.resolveOwner(userId)
}
