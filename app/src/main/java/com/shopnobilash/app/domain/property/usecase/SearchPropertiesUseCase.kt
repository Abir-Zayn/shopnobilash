package com.shopnobilash.app.domain.property.usecase

import com.shopnobilash.app.data.property.model.PropertyFilter
import com.shopnobilash.app.data.property.model.PropertyPage
import com.shopnobilash.app.data.property.repository.PropertyRepository

class SearchPropertiesUseCase(private val repository: PropertyRepository) {
    suspend operator fun invoke(filter: PropertyFilter): Result<PropertyPage> =
        repository.searchProperties(filter)
}
