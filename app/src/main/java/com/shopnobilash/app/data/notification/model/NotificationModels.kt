package com.shopnobilash.app.data.notification.model

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
