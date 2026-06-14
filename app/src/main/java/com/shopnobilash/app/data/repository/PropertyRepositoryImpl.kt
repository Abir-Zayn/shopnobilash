package com.shopnobilash.app.data.repository

import com.shopnobilash.app.data.model.MOCK_PROPERTIES
import com.shopnobilash.app.data.model.Property
import com.shopnobilash.app.data.model.propertyById
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class PropertyRepositoryImpl : PropertyRepository {

    private val _savedIds = MutableStateFlow(setOf("earth", "minimal"))

    override fun getSavedIds(): Flow<Set<String>> = _savedIds.asStateFlow()

    override suspend fun toggleSave(id: String) {
        val current = _savedIds.value.toMutableSet()
        if (current.contains(id)) current.remove(id) else current.add(id)
        _savedIds.value = current
    }

    override suspend fun getListings(): Result<List<Property>> =
        Result.success(MOCK_PROPERTIES)

    override suspend fun getPropertyById(id: String): Result<Property> =
        Result.success(propertyById(id))
}
