package com.shopnobilash.app.presentation.notifications.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shopnobilash.app.data.notification.model.NotificationGroup
import com.shopnobilash.app.data.notification.model.NotificationItem
import com.shopnobilash.app.domain.auth.usecase.CheckSessionUseCase
import com.shopnobilash.app.domain.notification.usecase.GetNotificationsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime

class NotificationsViewModel(
    private val checkSessionUseCase: CheckSessionUseCase,
    private val getNotificationsUseCase: GetNotificationsUseCase,
) : ViewModel() {

    private val _notifications = MutableStateFlow<List<NotificationGroup>>(emptyList())
    val notifications: StateFlow<List<NotificationGroup>> = _notifications.asStateFlow()

    init { loadNotifications() }

    private fun loadNotifications() = viewModelScope.launch {
        checkSessionUseCase().fold(
            onSuccess = { userId ->
                getNotificationsUseCase(userId).fold(
                    onSuccess = { items -> _notifications.value = groupByDate(items) },
                    onFailure = { /* empty list — notifications require live data from backend */ },
                )
            },
            onFailure = { /* session expired — caller navigates to login */ },
        )
    }

    fun markAllRead() {
        _notifications.value = _notifications.value.map { group ->
            group.copy(items = group.items.map { it.copy(isUnread = false) })
        }
    }

    private fun groupByDate(items: List<NotificationItem>): List<NotificationGroup> {
        val today = LocalDate.now()
        val groups = linkedMapOf<String, MutableList<NotificationItem>>()
        items.forEach { item ->
            val label = try {
                val date = ZonedDateTime.parse(item.isoTimestamp)
                    .withZoneSameInstant(ZoneId.systemDefault())
                    .toLocalDate()
                when {
                    date == today -> "Today"
                    date == today.minusDays(1) -> "Yesterday"
                    date.isAfter(today.minusDays(7)) -> "This week"
                    else -> "Earlier"
                }
            } catch (e: Exception) {
                "Recent"
            }
            groups.getOrPut(label) { mutableListOf() }.add(item)
        }
        return groups.map { (label, items) -> NotificationGroup(label, items) }
    }
}
