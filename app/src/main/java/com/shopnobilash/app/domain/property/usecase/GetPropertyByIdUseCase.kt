package com.shopnobilash.app.domain.property.usecase

import com.shopnobilash.app.data.property.model.Property
import com.shopnobilash.app.data.property.repository.PropertyRepository

class GetPropertyByIdUseCase(private val repository: PropertyRepository) {
    suspend operator fun invoke(id: String): Result<Property> = repository.getPropertyById(id)
}
