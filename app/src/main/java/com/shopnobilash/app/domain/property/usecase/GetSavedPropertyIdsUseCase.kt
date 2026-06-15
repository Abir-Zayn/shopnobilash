package com.shopnobilash.app.domain.property.usecase

import com.shopnobilash.app.data.property.repository.PropertyRepository
import kotlinx.coroutines.flow.Flow

class GetSavedPropertyIdsUseCase(private val repository: PropertyRepository) {
    operator fun invoke(): Flow<Set<String>> = repository.getSavedIds()
}
