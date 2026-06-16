package com.shopnobilash.app.domain.notification.usecase

import com.shopnobilash.app.data.notification.model.NotificationItem
import com.shopnobilash.app.data.notification.repository.NotificationRepository

class GetNotificationsUseCase(private val repository: NotificationRepository) {
    suspend operator fun invoke(userId: String): Result<List<NotificationItem>> =
        repository.getNotificationsForUser(userId)
}
