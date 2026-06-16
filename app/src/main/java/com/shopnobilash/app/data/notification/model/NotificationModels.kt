package com.shopnobilash.app.data.notification.model

data class NotificationItem(
    val id: String,
    val targetId: String,
    val type: String,
    val title: String,
    val body: String,
    val time: String,
    val isoTimestamp: String,
    val isUnread: Boolean,
    val actionRoute: String? = null,
    val ctaLabel: String? = null,
)

data class NotificationGroup(
    val group: String,
    val items: List<NotificationItem>,
)
