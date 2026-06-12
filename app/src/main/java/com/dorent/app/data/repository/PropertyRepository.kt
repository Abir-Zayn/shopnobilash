package com.dorent.app.data.repository

import com.dorent.app.data.model.Property
import kotlinx.coroutines.flow.Flow

interface PropertyRepository {
    fun getSavedIds(): Flow<Set<String>>
    suspend fun toggleSave(id: String)
    suspend fun getListings(): Result<List<Property>>
    suspend fun getPropertyById(id: String): Result<Property>
}
