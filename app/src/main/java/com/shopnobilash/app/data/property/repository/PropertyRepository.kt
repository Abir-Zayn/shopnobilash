package com.shopnobilash.app.data.property.repository

import com.shopnobilash.app.data.property.model.Property
import com.shopnobilash.app.data.property.model.PropertyDraft
import com.shopnobilash.app.data.property.model.PropertyFilter
import com.shopnobilash.app.data.property.model.PropertyPage
import kotlinx.coroutines.flow.Flow

interface PropertyRepository {
    fun getSavedIds(): Flow<Set<String>>
    suspend fun toggleSave(id: String)
    suspend fun getListings(): Result<List<Property>>
    suspend fun getPropertyById(id: String): Result<Property>

    /**
     * Search / filter the `properties` table — fully server-side. Name matching
     * (`filter.query`) uses the fulltext index on `house_name`; category, rent / area
     * ranges, bath / bed counts and newly-added are Appwrite queries. Results are
     * newest-first. Returns a [PropertyPage] carrying the server `total` for an honest
     * "showing X of N" contract. Failure carries a typed
     * [com.shopnobilash.app.data.property.model.SearchException].
     */
    suspend fun searchProperties(filter: PropertyFilter): Result<PropertyPage>

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
