package com.shopnobilash.app.data.property.repository

import com.shopnobilash.app.constants.PROPERTY_DATABASE_ID
import com.shopnobilash.app.constants.TABLE_PROPERTIES
import com.shopnobilash.app.constants.TABLE_PROPERTY_OWNERS
import com.shopnobilash.app.constants.TABLE_OWNERS
import com.shopnobilash.app.constants.TABLE_WISHLIST
import com.shopnobilash.app.data.property.model.Property
import com.shopnobilash.app.data.property.model.PropertyDraft
import com.shopnobilash.app.data.property.model.PropertyStatus
import io.appwrite.ID
import io.appwrite.Permission
import io.appwrite.Query
import io.appwrite.Role
import io.appwrite.services.Account
import io.appwrite.services.Databases
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.Instant

class PropertyRepositoryImpl(
    private val databases: Databases,
    private val account: Account,
) : PropertyRepository {

    private val _savedIds = MutableStateFlow<Set<String>>(emptySet())
    private val mutex = Mutex()
    @Volatile
    private var lastFetchedUserId: String? = null

    override fun getSavedIds(): Flow<Set<String>> = flow {
        val userId = getCurrentUserId()
        if (userId != null) {
            mutex.withLock {
                if (lastFetchedUserId != userId) {
                    val saved = fetchSavedIdsFromServer(userId)
                    _savedIds.value = saved
                    lastFetchedUserId = userId
                }
            }
        } else {
            mutex.withLock {
                _savedIds.value = emptySet()
                lastFetchedUserId = null
            }
        }
        emitAll(_savedIds)
    }.flowOn(Dispatchers.IO)

    override suspend fun toggleSave(id: String) {
        withContext(Dispatchers.IO) {
            val userId = getCurrentUserId() ?: return@withContext
            
            // Capture the current saved state
            val wasSaved = _savedIds.value.contains(id)
            
            // 1. Optimistic Update: Update the UI state immediately
            _savedIds.value = _savedIds.value.toMutableSet().apply {
                if (wasSaved) remove(id) else add(id)
            }
            
            // 2. Perform the server update
            val result = runCatching {
                if (wasSaved) {
                    deleteFromWishlist(userId, id)
                } else {
                    addToWishlist(userId, id)
                }
            }
            
            // 3. Rollback on failure
            result.onFailure { exception ->
                exception.printStackTrace()
                _savedIds.value = _savedIds.value.toMutableSet().apply {
                    if (wasSaved) add(id) else remove(id)
                }
            }
        }
    }

    private suspend fun getCurrentUserId(): String? = withContext(Dispatchers.IO) {
        runCatching {
            account.get().id
        }.getOrNull()
    }

    private suspend fun fetchSavedIdsFromServer(userId: String): Set<String> = withContext(Dispatchers.IO) {
        runCatching {
            val response = databases.listDocuments(
                databaseId = PROPERTY_DATABASE_ID,
                collectionId = TABLE_WISHLIST,
                queries = listOf(
                    Query.equal("user_id", userId)
                )
            )
            response.documents.mapNotNull { doc ->
                val propertyData = doc.data["property_id"]
                when (propertyData) {
                    is Map<*, *> -> propertyData["\$id"] as? String
                    is String -> propertyData
                    else -> null
                }
            }.toSet()
        }.getOrDefault(emptySet())
    }

    private suspend fun addToWishlist(userId: String, propertyId: String) = withContext(Dispatchers.IO) {
        val existing = databases.listDocuments(
            databaseId = PROPERTY_DATABASE_ID,
            collectionId = TABLE_WISHLIST,
            queries = listOf(
                Query.equal("user_id", userId),
                Query.equal("property_id", propertyId)
            )
        )
        if (existing.total == 0L) {
            databases.createDocument(
                databaseId = PROPERTY_DATABASE_ID,
                collectionId = TABLE_WISHLIST,
                documentId = ID.unique(),
                data = mapOf(
                    "user_id" to userId,
                    "property_id" to propertyId
                ),
                permissions = listOf(
                    Permission.read(Role.any()),
                    Permission.update(Role.user(userId)),
                    Permission.delete(Role.user(userId))
                )
            )
        }
    }

    private suspend fun deleteFromWishlist(userId: String, propertyId: String) = withContext(Dispatchers.IO) {
        val existing = databases.listDocuments(
            databaseId = PROPERTY_DATABASE_ID,
            collectionId = TABLE_WISHLIST,
            queries = listOf(
                Query.equal("user_id", userId),
                Query.equal("property_id", propertyId)
            )
        )
        existing.documents.forEach { doc ->
            databases.deleteDocument(
                databaseId = PROPERTY_DATABASE_ID,
                collectionId = TABLE_WISHLIST,
                documentId = doc.id
            )
        }
    }

    @Suppress("UNCHECKED_CAST")
    override suspend fun getListings(): Result<List<Property>> = withContext(Dispatchers.IO) {
        runCatching {
            val docs = databases.listDocuments(
                databaseId = PROPERTY_DATABASE_ID,
                collectionId = TABLE_PROPERTIES,
                queries = listOf(Query.orderDesc("ads_created_on")),
            )
            docs.documents.map { doc ->
                val data = doc.data as Map<String, Any>
                data.toProperty(doc.id)
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    override suspend fun getPropertyById(id: String): Result<Property> = withContext(Dispatchers.IO) {
        runCatching {
            val propDoc = databases.getDocument(
                databaseId = PROPERTY_DATABASE_ID,
                collectionId = TABLE_PROPERTIES,
                documentId = id,
            )
            val propData = propDoc.data as Map<String, Any>
            
            var ownerName = ""
            val ownerRole = "Owner"
            
            runCatching {
                val junctionDocs = databases.listDocuments(
                    databaseId = PROPERTY_DATABASE_ID,
                    collectionId = TABLE_PROPERTY_OWNERS,
                    queries = listOf(Query.equal("property_id", id))
                )
                val junctionDoc = junctionDocs.documents.firstOrNull()
                if (junctionDoc != null) {
                    val ownerData = junctionDoc.data["owner_id"]
                    val ownerId = when (ownerData) {
                        is Map<*, *> -> ownerData["\$id"] as? String
                        is String -> ownerData
                        else -> null
                    }
                    if (ownerId != null) {
                        val ownerDoc = databases.getDocument(
                            databaseId = PROPERTY_DATABASE_ID,
                            collectionId = TABLE_OWNERS,
                            documentId = ownerId
                        )
                        ownerName = ownerDoc.data["name"] as? String ?: ""
                    }
                }
            }
            
            propData.toProperty(
                id = propDoc.id,
                ownerName = ownerName.ifEmpty { "Property Owner" },
                ownerRole = ownerRole
            )
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun Map<String, Any>.toProperty(
        id: String,
        ownerName: String = "",
        ownerRole: String = "",
    ) = Property(
        id = id,
        title = this["house_name"] as? String ?: "",
        type = this["property_category"] as? String ?: "",
        city = "",
        address = this["location"] as? String ?: "",
        price = (this["rent"] as? Number)?.toInt() ?: 0,
        period = "mo",
        beds = (this["bed_no"] as? Number)?.toInt() ?: 0,
        baths = (this["bath_no"] as? Number)?.toInt() ?: 0,
        sqft = (this["area_sqft"] as? Number)?.toInt() ?: 0,
        rating = 0.0,
        ownerName = ownerName.ifEmpty { this["ownerName"] as? String ?: "" },
        ownerRole = ownerRole.ifEmpty { this["ownerRole"] as? String ?: "Owner" },
        description = this["description"] as? String ?: "",
        imageUrl = (this["property_img"] as? List<*>)?.firstOrNull() as? String ?: "",
        imageUrls = (this["property_img"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
    )

    override suspend fun createProperty(
        userId: String,
        ownerId: String,
        draft: PropertyDraft,
        imageUrls: List<String>,
    ): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            val now = Instant.now().toString()

            val propDoc = databases.createDocument(
                databaseId = PROPERTY_DATABASE_ID,
                collectionId = TABLE_PROPERTIES,
                documentId = ID.unique(),
                data = buildMap {
                    put("house_name", draft.houseName)
                    put("bed_no", draft.bedNo)
                    put("bath_no", draft.bathNo)
                    put("area_sqft", draft.areaSqft)
                    put("rent", draft.rent)
                    put("status", PropertyStatus.AVAILABLE)
                    put("property_category", draft.category.rawValue)
                    draft.location?.takeIf { it.isNotBlank() }?.let { put("location", it) }
                    draft.floor?.takeIf { it.isNotBlank() }?.let { put("floor", it) }
                    draft.description?.takeIf { it.isNotBlank() }?.let { put("description", it) }
                    draft.contractTerms?.takeIf { it.isNotBlank() }?.let { put("contract_terms", it) }
                    if (imageUrls.isNotEmpty()) put("property_img", imageUrls)
                    put("ads_created_on", now)
                    put("updated_on", now)
                },
                permissions = listOf(
                    Permission.read(Role.any()),
                    Permission.update(Role.user(userId)),
                    Permission.delete(Role.user(userId)),
                ),
            )

            // Junction MUST follow the property insert (FK ordering).
            databases.createDocument(
                databaseId = PROPERTY_DATABASE_ID,
                collectionId = TABLE_PROPERTY_OWNERS,
                documentId = ID.unique(),
                data = mapOf(
                    "property_id" to propDoc.id,
                    "owner_id" to ownerId,
                ),
                permissions = listOf(
                    Permission.read(Role.any()),
                    Permission.update(Role.user(userId)),
                    Permission.delete(Role.user(userId)),
                ),
            )

            propDoc.id
        }
    }
}
