package com.shopnobilash.app.domain.property.usecase

import com.shopnobilash.app.data.property.model.PropertyDraft
import com.shopnobilash.app.data.property.repository.PropertyRepository

class CreatePropertyUseCase(private val repository: PropertyRepository) {
    suspend operator fun invoke(
        userId: String,
        ownerId: String,
        draft: PropertyDraft,
        imageUrls: List<String>,
    ): Result<String> = repository.createProperty(userId, ownerId, draft, imageUrls)
}
