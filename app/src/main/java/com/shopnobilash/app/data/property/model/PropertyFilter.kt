package com.shopnobilash.app.data.property.model

/**
 * Criteria for the property search / filter screen. All fields are optional —
 * an empty [PropertyFilter] means "all available listings, newest first".
 *
 * `query` is the free-text name search (matched against `house_name`). The rest
 * map to server-side Appwrite queries on the `properties` table.
 */
data class PropertyFilter(
    val query: String = "",
    val category: PropertyCategory? = null,
    val minRent: Int? = null,
    val maxRent: Int? = null,
    val minArea: Int? = null,
    val maxArea: Int? = null,
    val bathrooms: Set<Int> = emptySet(),
    val bedrooms: Set<Int> = emptySet(),
    val newlyAdded: Boolean = false,
) {
    /** True when any criterion from the filter sheet (not the badge/search row) is active. */
    val hasAdvancedFilters: Boolean
        get() = minRent != null || maxRent != null || minArea != null || maxArea != null ||
            bathrooms.isNotEmpty() || bedrooms.isNotEmpty() || newlyAdded

    /** Count of active advanced criteria — drives the badge on the filter button. */
    val advancedFilterCount: Int
        get() = listOf(
            minRent != null || maxRent != null,
            minArea != null || maxArea != null,
            bathrooms.isNotEmpty(),
            bedrooms.isNotEmpty(),
            newlyAdded,
        ).count { it }
}

/** Categories surfaced as filter badges on the Search screen (per product spec). */
val FILTERABLE_CATEGORIES: List<PropertyCategory> = listOf(
    PropertyCategory.HOUSE,
    PropertyCategory.OFFICE,
    PropertyCategory.COACHING,
    PropertyCategory.SHOP,
)

/** Soft bounds for the range sliders / room chips in the filter sheet. */
object PropertyFilterRanges {
    const val RENT_MIN = 0
    const val RENT_MAX = 100_000
    const val AREA_MIN = 400
    const val AREA_MAX = 3_500
    val ROOM_OPTIONS = listOf(1, 2, 3, 4)
}
