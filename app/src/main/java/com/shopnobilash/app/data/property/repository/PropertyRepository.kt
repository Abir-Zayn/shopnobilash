package com.shopnobilash.app.data.property.repository

import com.shopnobilash.app.data.property.model.Property
import com.shopnobilash.app.data.property.model.PropertyDraft
import kotlinx.coroutines.flow.Flow

interface PropertyRepository {
    fun getSavedIds(): Flow<Set<String>>
    suspend fun toggleSave(id: String)
    suspend fun getListings(): Result<List<Property>>
    suspend fun getPropertyById(id: String): Result<Property>

    /**
     * Create a `properties` row and the `property_owners` junction linking it to [ownerId].
     * Returns the new property `$id`. The junction insert follows the property insert (FK
     * ordering); on junction failure the property is left listed and the failure surfaces.
     */
    suspend fun createProperty(
        userId: String,
        ownerId: String,
        draft: PropertyDraft,
        imageUrls: List<String>,
    ): Result<String>
}
