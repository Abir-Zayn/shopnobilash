package com.shopnobilash.app.domain.property.usecase

import com.shopnobilash.app.data.property.model.Property
import com.shopnobilash.app.data.property.repository.PropertyRepository

class GetListingsUseCase(private val repository: PropertyRepository) {
    suspend operator fun invoke(): Result<List<Property>> = repository.getListings()
}
