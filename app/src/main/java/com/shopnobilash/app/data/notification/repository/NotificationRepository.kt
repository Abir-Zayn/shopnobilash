package com.shopnobilash.app.data.notification.repository

import com.shopnobilash.app.data.notification.model.NotificationItem

interface NotificationRepository {
    suspend fun getNotificationsForUser(userId: String): Result<List<NotificationItem>>
}
