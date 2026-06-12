package com.dorent.app.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dorent.app.navigation.Screen
import com.dorent.app.ui.theme.appColors

data class NavTab(
    val route: String,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val selectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
)

val NAV_TABS = listOf(
    NavTab(Screen.Home.route,     "Home",    Icons.Outlined.Home,             Icons.Filled.Home),
    NavTab(Screen.Wishlist.route, "Saved",   Icons.Outlined.BookmarkBorder,   Icons.Filled.Bookmark),
    NavTab(Screen.Chat.route,     "Chat",    Icons.Outlined.ChatBubbleOutline, Icons.Filled.ChatBubble),
    NavTab(Screen.Profile.route,  "Profile", Icons.Outlined.Person,           Icons.Filled.Person),
)

@Composable
fun BottomNavBar(currentRoute: String, onTabSelected: (String) -> Unit) {
    val colors = MaterialTheme.appColors
    NavigationBar(
        containerColor = colors.card,
        tonalElevation = 0.dp,
    ) {
        NAV_TABS.forEach { tab ->
            val selected = currentRoute == tab.route
            NavigationBarItem(
                selected = selected,
                onClick = { onTabSelected(tab.route) },
                icon = {
                    Icon(
                        imageVector = if (selected) tab.selectedIcon else tab.icon,
                        contentDescription = tab.label,
                    )
                },
                label = {
                    Text(
                        text = tab.label,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold,
                        ),
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor   = colors.accent,
                    selectedTextColor   = colors.accent,
                    unselectedIconColor = colors.faint,
                    unselectedTextColor = colors.faint,
                    indicatorColor      = Color.Transparent,
                ),
            )
        }
    }
}
