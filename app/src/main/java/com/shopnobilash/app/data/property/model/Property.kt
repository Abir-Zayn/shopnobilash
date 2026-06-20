package com.shopnobilash.app.data.property.model

data class Property(
    val id: String,
    val title: String,
    val type: String,
    val city: String,
    val address: String,
    val price: Int,
    val period: String,
    val beds: Int,
    val baths: Int,
    val sqft: Int,
    val rating: Double,
    val ownerName: String,
    val ownerRole: String,
    val description: String,
    val imageUrl: String,
    val imageUrls: List<String> = emptyList(),
    val meterType: String? = null,
    val officeRoomType: String? = null,
    val advanceAmount: Double? = null,
)

fun formatPrice(price: Int): String = "\$${"%,d".format(price)}"

/** Resolved category, or null if the raw value is unknown. */
val Property.category: PropertyCategory?
    get() = PropertyCategory.fromRaw(type)

/**
 * Read-path room label honoring the `bed_no` reframe — "3 Bedrooms" for a House,
 * "2 Rooms" for Shop/Office/Coaching/Showroom.
 */
val Property.roomCountLabel: String
    get() {
        val noun = category?.roomNoun ?: "Room"
        return "$beds $noun${if (beds == 1) "" else "s"}"
    }
