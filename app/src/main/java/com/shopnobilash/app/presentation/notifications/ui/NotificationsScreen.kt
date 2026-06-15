package com.shopnobilash.app.presentation.notifications.ui
import com.shopnobilash.app.presentation.notifications.viewmodel.NotificationsViewModel

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shopnobilash.app.data.notification.model.NotificationItem
import com.shopnobilash.app.presentation.components.StackHeader
import com.shopnobilash.app.presentation.theme.Accent
import com.shopnobilash.app.presentation.theme.Blue
import com.shopnobilash.app.presentation.theme.Danger
import com.shopnobilash.app.presentation.theme.StarYellow
import com.shopnobilash.app.presentation.theme.appColors
import org.koin.androidx.compose.koinViewModel

@Composable
fun NotificationsScreen(
    onBack: () -> Unit,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToChatThread: (String) -> Unit,
    viewModel: NotificationsViewModel = koinViewModel(),
) {
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    val colors = MaterialTheme.appColors

    Scaffold(
        containerColor = colors.bg,
        topBar = {
            StackHeader(
                title = "Notifications",
                onBack = onBack,
                actions = {
                    Text(
                        "Mark all",
                        style = MaterialTheme.typography.labelMedium.copy(color = Accent, fontWeight = FontWeight.Bold),
                        modifier = Modifier.clickable { viewModel.markAllRead() },
                    )
                },
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 4.dp),
        ) {
            notifications.forEach { group ->
                item {
                    Text(
                        group.group,
                        style = MaterialTheme.typography.labelLarge.copy(color = colors.muted, fontWeight = FontWeight.Bold, fontSize = 13.sp),
                        modifier = Modifier.padding(start = 4.dp, top = 14.dp, bottom = 10.dp),
                    )
                }
                items(group.items, key = { it.propertyId + it.type + it.time }) { notif ->
                    NotificationRow(
                        item = notif,
                        onClick = {
                            if (notif.type == "message") onNavigateToChatThread(notif.propertyId)
                            else onNavigateToDetail(notif.propertyId)
                        },
                    )
                    Spacer(Modifier.height(10.dp))
                }
            }
        }
    }
}

@Composable
private fun NotificationRow(item: NotificationItem, onClick: () -> Unit) {
    val colors = MaterialTheme.appColors
    val (icon, tint) = notificationIconAndTint(item.type)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (item.isUnread) Modifier.shadow(4.dp, RoundedCornerShape(16.dp)) else Modifier)
            .clip(RoundedCornerShape(16.dp))
            .background(if (item.isUnread) colors.card else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier.size(42.dp).clip(RoundedCornerShape(12.dp)).background(tint),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, null, tint = Color.White, modifier = Modifier.size(20.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    item.title,
                    style = MaterialTheme.typography.labelLarge.copy(color = colors.ink, fontWeight = FontWeight.Bold, lineHeight = 19.sp),
                    modifier = Modifier.weight(1f),
                )
                Spacer(Modifier.width(8.dp))
                Text(item.time, style = MaterialTheme.typography.labelSmall.copy(color = colors.faint, fontSize = 11.sp))
            }
            Spacer(Modifier.height(3.dp))
            Text(item.body, style = MaterialTheme.typography.bodySmall.copy(color = colors.muted, lineHeight = 18.sp))
            if (item.ctaLabel != null) {
                Spacer(Modifier.height(9.dp))
                Box(
                    modifier = Modifier.clip(RoundedCornerShape(10.dp)).background(Accent).padding(horizontal = 14.dp, vertical = 7.dp),
                ) {
                    Text(item.ctaLabel, style = MaterialTheme.typography.labelMedium.copy(color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp))
                }
            }
        }
        if (item.isUnread && item.ctaLabel == null) {
            Box(modifier = Modifier.size(8.dp).clip(RoundedCornerShape(50)).background(Accent).padding(top = 6.dp))
        }
    }
}

private fun notificationIconAndTint(type: String): Pair<ImageVector, Color> = when (type) {
    "message"  -> Pair(Icons.Filled.Mail, Accent)
    "wishlist" -> Pair(Icons.Filled.Favorite, Danger)
    "review"   -> Pair(Icons.Filled.Star, StarYellow)
    "booking"  -> Pair(Icons.Filled.Check, Accent)
    else       -> Pair(Icons.Filled.Notifications, Blue)
}
