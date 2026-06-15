package com.shopnobilash.app.data.property.repository

import com.shopnobilash.app.data.property.model.Property
import kotlinx.coroutines.flow.Flow

interface PropertyRepository {
    fun getSavedIds(): Flow<Set<String>>
    suspend fun toggleSave(id: String)
    suspend fun getListings(): Result<List<Property>>
    suspend fun getPropertyById(id: String): Result<Property>
}
