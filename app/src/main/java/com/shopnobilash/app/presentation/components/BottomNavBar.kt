package com.shopnobilash.app.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.BookmarkAdded
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.MarkEmailUnread
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.shopnobilash.app.navigation.Screen
import com.shopnobilash.app.presentation.theme.appColors

data class NavTab(
    val route: String,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val selectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
)

val NAV_TABS = listOf(
    NavTab(Screen.Home.route,     "Home",    Icons.Rounded.Home,             Icons.Rounded.Home),
    NavTab(Screen.Wishlist.route, "Saved",   Icons.Rounded.BookmarkAdded,    Icons.Rounded.BookmarkAdded),
    NavTab(Screen.Chat.route,     "Chat",    Icons.Rounded.MarkEmailUnread,  Icons.Rounded.MarkEmailUnread),
    NavTab(Screen.Profile.route,  "Profile", Icons.Rounded.AccountCircle,    Icons.Rounded.AccountCircle),
)

@Composable
fun BottomNavBar(currentRoute: String, onTabSelected: (String) -> Unit) {
    val colors = MaterialTheme.appColors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .clip(RoundedCornerShape(30.dp))
            .background(Color.White.copy(alpha = 0.25f))
            .height(64.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        NAV_TABS.forEach { tab ->
            val selected = currentRoute == tab.route
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) { onTabSelected(tab.route) },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Icon(
                    imageVector = if (selected) tab.selectedIcon else tab.icon,
                    contentDescription = tab.label,
                    tint = if (selected) colors.accent else colors.faint,
                    modifier = Modifier.size(28.dp),
                )
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .width(18.dp)
                        .height(3.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(if (selected) colors.ink else Color.Transparent),
                )
            }
        }
    }
}
