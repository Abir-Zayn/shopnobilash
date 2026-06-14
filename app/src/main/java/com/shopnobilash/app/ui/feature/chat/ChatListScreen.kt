package com.shopnobilash.app.ui.feature.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shopnobilash.app.data.model.Conversation
import com.shopnobilash.app.data.model.propertyById
import com.shopnobilash.app.ui.components.BottomNavBar
import com.shopnobilash.app.ui.components.InitialsAvatar
import com.shopnobilash.app.ui.theme.Accent
import com.shopnobilash.app.ui.theme.appColors
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun ChatListScreen(
    onNavigateToChatThread: (String) -> Unit,
    onNavigateToTab: (String) -> Unit,
    viewModel: ChatViewModel = koinViewModel { parametersOf(null as String?) },
) {
    val colors = MaterialTheme.appColors

    Scaffold(
        containerColor = colors.bg,
        bottomBar = { BottomNavBar(currentRoute = "chat", onTabSelected = onNavigateToTab) },
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            Spacer(Modifier.height(52.dp))
            Text(
                "Messages",
                style = MaterialTheme.typography.headlineMedium.copy(color = colors.ink, fontWeight = FontWeight.ExtraBold),
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 4.dp),
            )
            Spacer(Modifier.height(8.dp))
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(viewModel.conversations, key = { it.propertyId }) { convo ->
                    ConversationRow(conversation = convo, onClick = { onNavigateToChatThread(convo.propertyId) })
                }
            }
        }
    }
}

@Composable
private fun ConversationRow(conversation: Conversation, onClick: () -> Unit) {
    val colors = MaterialTheme.appColors
    val property = propertyById(conversation.propertyId)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box {
            InitialsAvatar(name = property.ownerName, size = 52.dp)
            if (conversation.isOnline) {
                Box(
                    modifier = Modifier
                        .size(13.dp)
                        .clip(CircleShape)
                        .background(Accent)
                        .align(Alignment.BottomEnd),
                )
            }
        }
        Spacer(Modifier.width(13.dp))
        Column(modifier = Modifier.weight(1f).padding(bottom = 14.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    property.ownerName,
                    style = MaterialTheme.typography.titleSmall.copy(color = colors.ink, fontWeight = FontWeight.Bold),
                    modifier = Modifier.weight(1f),
                )
                Text(
                    conversation.time,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = if (conversation.unreadCount > 0) Accent else colors.faint,
                        fontSize = 11.5.sp,
                    ),
                )
            }
            Spacer(Modifier.height(3.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    conversation.lastMessage,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = if (conversation.unreadCount > 0) colors.ink2 else colors.muted,
                        fontWeight = if (conversation.unreadCount > 0) FontWeight.SemiBold else FontWeight.Normal,
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                if (conversation.unreadCount > 0) {
                    Spacer(Modifier.width(10.dp))
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Accent)
                            .padding(horizontal = 5.dp, vertical = 2.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            "${conversation.unreadCount}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = androidx.compose.ui.graphics.Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                            ),
                        )
                    }
                }
            }
        }
    }
    Box(modifier = Modifier.fillMaxWidth().height(1.dp).padding(start = 77.dp).background(colors.line))
}
