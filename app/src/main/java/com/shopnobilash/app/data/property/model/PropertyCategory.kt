package com.shopnobilash.app.data.property.model

/** `properties.property_category` enum — intended use of the property. */
enum class PropertyCategory(val rawValue: String) {
    HOUSE("House"),
    COACHING("Coaching"),
    OFFICE("Office"),
    SHOP("Shop"),
    SHOWROOM("Showroom");

    /**
     * UI label for the generic `bed_no` room count. `bed_no` is NOT literally
     * bedrooms — only `House` shows "Bedrooms"; every other category shows "Rooms".
     */
    val roomLabel: String
        get() = if (this == HOUSE) "Bedrooms" else "Rooms"

    /** Singular room noun for read-path labels ("Bedroom" vs "Room"). */
    val roomNoun: String
        get() = if (this == HOUSE) "Bedroom" else "Room"

    /**
     * Soft room-count hint per category (warn, do NOT hard-block). `null` = no cap.
     * House/Showroom have no expected range.
     */
    val roomRange: IntRange?
        get() = when (this) {
            COACHING, OFFICE -> 1..4
            SHOP -> 1..2
            else -> null
        }

    /** Shop lists an electricity meter type. */
    val showsMeterType: Boolean get() = this == SHOP

    /** Office distinguishes private vs shared / co-working space. */
    val showsOfficeRoomType: Boolean get() = this == OFFICE

    /** Shop + Office advertise a listing-level advance; form-required for these. */
    val showsAdvanceAmount: Boolean get() = this == SHOP || this == OFFICE

    companion object {
        fun fromRaw(raw: String?): PropertyCategory? = entries.find { it.rawValue == raw }
    }
}

/** `properties.meter_type` enum — electricity meter for commercial (Shop) listings. */
enum class MeterType(val rawValue: String, val label: String) {
    COMMERCIAL("Commercial", "Own dedicated meter"),
    SUBMETER("SubMeter", "Shared meter (billed by landlord)");

    companion object {
        fun fromRaw(raw: String?): MeterType? = entries.find { it.rawValue == raw }
    }
}

/** `properties.office_room_type` enum — private office vs shared / co-working. */
enum class OfficeRoomType(val rawValue: String, val label: String) {
    PRIVATE("Private", "Private office"),
    SHARED("Shared", "Shared / co-working");

    companion object {
        fun fromRaw(raw: String?): OfficeRoomType? = entries.find { it.rawValue == raw }
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
 *
 * `bedNo` is a generic room count (see [PropertyCategory.roomLabel]). The commercial
 * fields are nullable in the schema; the form requires [advanceAmount] for Shop/Office.
 */
data class PropertyDraft(
    val category: PropertyCategory,
    val houseName: String,
    val location: String? = null,
    val floor: String? = null,
    val bedNo: Int,
    val bathNo: Int,
    val areaSqft: Int,
    val rent: Double,
    val description: String? = null,
    val contractTerms: String? = null,
    val meterType: MeterType? = null,
    val officeRoomType: OfficeRoomType? = null,
    val advanceAmount: Double? = null,
)
