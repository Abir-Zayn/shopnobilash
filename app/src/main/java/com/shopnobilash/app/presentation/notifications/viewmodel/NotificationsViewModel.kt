package com.shopnobilash.app.presentation.notifications.viewmodel

import androidx.lifecycle.ViewModel
import com.shopnobilash.app.data.notification.model.MOCK_NOTIFICATIONS
import com.shopnobilash.app.data.notification.model.NotificationGroup
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class NotificationsViewModel : ViewModel() {
    private val _notifications = MutableStateFlow(MOCK_NOTIFICATIONS)
    val notifications: StateFlow<List<NotificationGroup>> = _notifications.asStateFlow()

    fun markAllRead() {
        _notifications.value = _notifications.value.map { group ->
            group.copy(items = group.items.map { it.copy(isUnread = false) })
        }
    }
}
