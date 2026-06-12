package com.dorent.app.ui.feature.notifications

import androidx.lifecycle.ViewModel
import com.dorent.app.data.model.MOCK_NOTIFICATIONS
import com.dorent.app.data.model.NotificationGroup
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
