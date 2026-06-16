package com.shopnobilash.app.data.notification.repository

import com.shopnobilash.app.constants.PROPERTY_DATABASE_ID
import com.shopnobilash.app.constants.TABLE_NOTIFICATIONS
import com.shopnobilash.app.data.notification.model.NotificationItem
import io.appwrite.Query
import io.appwrite.services.Databases
import java.time.ZonedDateTime
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Locale

class NotificationRepositoryImpl(private val databases: Databases) : NotificationRepository {

    override suspend fun getNotificationsForUser(userId: String): Result<List<NotificationItem>> =
        runCatching {
            val result = databases.listDocuments(
                databaseId = PROPERTY_DATABASE_ID,
                collectionId = TABLE_NOTIFICATIONS,
                queries = listOf(
                    Query.equal("user_id", userId),
                    Query.orderDesc("\$createdAt"),
                    Query.limit(50),
                ),
            )
            result.documents.map { doc ->
                @Suppress("UNCHECKED_CAST")
                val data = doc.data as Map<String, Any>
                NotificationItem(
                    id = doc.id,
                    targetId = data["action_target_id"] as? String ?: "",
                    type = (data["notification_type"] as? String ?: "System").lowercase(),
                    title = data["title"] as String,
                    body = data["message"] as String,
                    time = formatRelativeTime(doc.createdAt),
                    isoTimestamp = doc.createdAt,
                    isUnread = !(data["is_read"] as? Boolean ?: false),
                    actionRoute = data["action_route"] as? String,
                )
            }
        }

    private fun formatRelativeTime(isoDateTime: String): String = try {
        val zdt = ZonedDateTime.parse(isoDateTime)
        val instant = zdt.toInstant()
        val now = java.time.Instant.now()
        val diffMin = (now.toEpochMilli() - instant.toEpochMilli()) / 60_000
        when {
            diffMin < 60 -> "${diffMin.coerceAtLeast(1)}m"
            diffMin < 24 * 60 -> "${diffMin / 60}h"
            else -> {
                zdt.withZoneSameInstant(ZoneId.systemDefault())
                    .dayOfWeek
                    .getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
            }
        }
    } catch (e: Exception) {
        ""
    }
}
