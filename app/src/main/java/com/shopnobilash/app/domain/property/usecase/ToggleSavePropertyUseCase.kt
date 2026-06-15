package com.shopnobilash.app.domain.property.usecase

import com.shopnobilash.app.data.property.repository.PropertyRepository

class ToggleSavePropertyUseCase(private val repository: PropertyRepository) {
    suspend operator fun invoke(id: String) = repository.toggleSave(id)
}
