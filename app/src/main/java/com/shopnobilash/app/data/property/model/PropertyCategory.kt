package com.shopnobilash.app.data.property.model

/** `properties.property_category` enum — intended use of the property. */
enum class PropertyCategory(val rawValue: String) {
    HOUSE("House"),
    COACHING("Coaching"),
    OFFICE("Office"),
    SHOP("Shop"),
    SHOWROOM("Showroom");

    companion object {
        fun fromRaw(raw: String?): PropertyCategory? = entries.find { it.rawValue == raw }
    }
}

/** Property listing status. `Available` is the default for new listings. */
object PropertyStatus {
    const val AVAILABLE = "Available"
    const val RENTED = "Rented"
    const val MAINTENANCE = "Maintenance"
}

/**
 * App-side draft of a new listing. Maps 1:1 to the `properties` table columns the
 * UI captures; auto-set fields (`status`, `ads_created_on`, `updated_on`) are filled
 * by the repository.
 */
data class PropertyDraft(
    val category: PropertyCategory,
    val houseName: String,
    val floor: String? = null,
    val bedNo: Int,
    val bathNo: Int,
    val areaSqft: Int,
    val rent: Double,
    val description: String? = null,
    val contractTerms: String? = null,
)
