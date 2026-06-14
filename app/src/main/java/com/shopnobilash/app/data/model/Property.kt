package com.shopnobilash.app.data.model

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

data class Conversation(
    val propertyId: String,
    val lastMessage: String,
    val time: String,
    val unreadCount: Int,
    val isOnline: Boolean,
)

data class ChatMessage(
    val isMe: Boolean,
    val text: String,
    val time: String,
)

data class NotificationItem(
    val propertyId: String,
    val type: String,
    val title: String,
    val body: String,
    val time: String,
    val isUnread: Boolean,
    val ctaLabel: String? = null,
)

data class NotificationGroup(
    val group: String,
    val items: List<NotificationItem>,
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

val MOCK_CONVERSATIONS = listOf(
    Conversation("earth",   "Sure, the place is available from July 1st.", "2m",  2, true),
    Conversation("minimal", "I can send over a few more photos if you'd like.", "1h", 0, true),
    Conversation("lara",    "Thanks for your interest in the apartment!", "Tue", 0, false),
    Conversation("aspen",   "Your villa tour is confirmed for Saturday ✓", "Mon", 0, false),
)

val MOCK_NOTIFICATIONS = listOf(
    NotificationGroup(
        group = "Today", items = listOf(
            NotificationItem("earth",   "message",  "David Carlos sent you a message", "\"Sure, the place is available from July 1st.\"", "2m",  true),
            NotificationItem("minimal", "wishlist",  "Price dropped on a saved place",  "Minimalist Apartment is now \$4,500/mo",          "1h",  true),
            NotificationItem("lara",    "review",    "How was your stay?",               "Add a review for Lara Apartment",                 "3h",  true, "Add a Review"),
        ),
    ),
    NotificationGroup(
        group = "This week", items = listOf(
            NotificationItem("aspen",   "booking",   "Booking confirmed",                "Your tour for Aspen Villa is set for Saturday",   "Mon", false),
            NotificationItem("sherman", "wishlist",  "New place matches your search",    "Sherman Oaks was just added in New York",          "Sun", false),
        ),
    ),
)

fun propertyById(id: String): Property = MOCK_PROPERTIES.find { it.id == id } ?: MOCK_PROPERTIES.first()
fun formatPrice(price: Int): String = "\$${"%,d".format(price)}"
