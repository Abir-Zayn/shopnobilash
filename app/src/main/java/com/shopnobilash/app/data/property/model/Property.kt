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
)

val MOCK_PROPERTIES = listOf(
    Property(
        id = "sherman", title = "Sherman Oaks", type = "House", city = "New York, USA",
        address = "21 Greenfield Ave, New York, USA", price = 2000, period = "mo",
        beds = 4, baths = 3, sqft = 2100, rating = 4.9,
        ownerName = "Emma Watson", ownerRole = "House Owner",
        description = "A warm family house tucked on a quiet, tree-lined street. Open-plan living, a sunlit kitchen and a private backyard make it a calm place to come home to.",
        imageUrl = "https://picsum.photos/seed/sherman/600/400",
    ),
    Property(
        id = "lara", title = "Lara Apartment", type = "Apartment", city = "Los Angeles, USA",
        address = "88 Crescent Blvd, Los Angeles, USA", price = 4000, period = "mo",
        beds = 2, baths = 2, sqft = 1450, rating = 4.7,
        ownerName = "Marcus Lee", ownerRole = "Property Manager",
        description = "A bright modern apartment with floor-to-ceiling windows and a skyline view. Steps from cafés, transit and the riverside park.",
        imageUrl = "https://picsum.photos/seed/lara/600/400",
    ),
    Property(
        id = "minimal", title = "Minimalist Apartment", type = "Apartment", city = "San Francisco, USA",
        address = "2125 Union St #6, San Francisco, USA", price = 4500, period = "mo",
        beds = 3, baths = 3, sqft = 1900, rating = 4.8,
        ownerName = "Sofia Reyes", ownerRole = "House Owner",
        description = "Clean lines, soft light and honest materials. A minimalist three-bed designed for focus, with a chef-grade kitchen and a quiet study nook.",
        imageUrl = "https://picsum.photos/seed/minimal/600/400",
    ),
    Property(
        id = "earth", title = "Earth House", type = "House", city = "Los Angeles, USA",
        address = "332 S Kingsley Dr #105, Los Angeles, USA", price = 3000, period = "mo",
        beds = 3, baths = 2, sqft = 1960, rating = 4.9,
        ownerName = "David Carlos", ownerRole = "House Owner",
        description = "Cozy and homey house with the most affordable price available in the marketplace. Beautifully designed by a world-class architect and built with the warmest natural materials.",
        imageUrl = "https://picsum.photos/seed/earth/600/400",
    ),
    Property(
        id = "aspen", title = "Aspen Villa", type = "Villa", city = "Aspen, USA",
        address = "14 Maroon Creek Rd, Aspen, USA", price = 6200, period = "mo",
        beds = 5, baths = 4, sqft = 3400, rating = 5.0,
        ownerName = "Olivia Park", ownerRole = "Villa Owner",
        description = "A mountainside villa wrapped in glass and timber, with a wellness room, heated pool and uninterrupted views of the ridge line.",
        imageUrl = "https://picsum.photos/seed/aspen/600/400",
    ),
)

fun propertyById(id: String): Property = MOCK_PROPERTIES.find { it.id == id } ?: MOCK_PROPERTIES.first()
fun formatPrice(price: Int): String = "\$${"%,d".format(price)}"
