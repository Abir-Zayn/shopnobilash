package com.dorent.app.ui.feature.profile

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
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
import com.dorent.app.ui.components.BottomNavBar
import com.dorent.app.ui.components.InitialsAvatar
import com.dorent.app.ui.components.RoundIconButton
import com.dorent.app.ui.theme.Accent
import com.dorent.app.ui.theme.AccentDeep
import com.dorent.app.ui.theme.AccentSoft
import com.dorent.app.ui.theme.Blue
import com.dorent.app.ui.theme.Danger
import com.dorent.app.ui.theme.Ink
import com.dorent.app.ui.theme.Muted
import com.dorent.app.ui.theme.TagOrange
import com.dorent.app.ui.theme.appColors
import org.koin.androidx.compose.koinViewModel

data class MenuItem(
    val icon: ImageVector,
    val label: String,
    val tint: Color,
    val badge: String? = null,
    val route: String? = null,
)

val MENU_ITEMS = listOf(
    MenuItem(Icons.Filled.Person,       "Personal information", Accent),
    MenuItem(Icons.Filled.Apartment,    "My properties",        Blue,        badge = "2"),
    MenuItem(Icons.Filled.CreditCard,   "Payment methods",      Color(0xFF7C5CFC)),
    MenuItem(Icons.Filled.Notifications,"Notifications",        TagOrange,   route = "notifications"),
    MenuItem(Icons.Filled.Shield,       "Privacy & security",   Color(0xFF3AAFA9)),
    MenuItem(Icons.Filled.HelpOutline,  "Help center",          Muted),
)

@Composable
fun ProfileScreen(
    onNavigateToNotifications: () -> Unit,
    onLogout: () -> Unit,
    onNavigateToTab: (String) -> Unit,
    viewModel: ProfileViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val colors = MaterialTheme.appColors

    Scaffold(
        containerColor = colors.bg,
        bottomBar = { BottomNavBar(currentRoute = "profile", onTabSelected = onNavigateToTab) },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 6.dp),
        ) {
            item { Spacer(Modifier.height(52.dp)) }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("Profile", style = MaterialTheme.typography.headlineMedium.copy(color = colors.ink, fontWeight = FontWeight.ExtraBold))
                    RoundIconButton(icon = Icons.Filled.Settings, onClick = {}, contentDescription = "Settings")
                }
            }

            // Identity card
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(20.dp))
                        .clip(RoundedCornerShape(20.dp))
                        .background(colors.card)
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    InitialsAvatar(name = "Emma Watson", size = 62.dp)
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Emma Watson", style = MaterialTheme.typography.titleLarge.copy(color = colors.ink, fontWeight = FontWeight.ExtraBold, fontSize = 17.sp))
                        Text("emma@dorent.com", style = MaterialTheme.typography.bodySmall.copy(color = colors.muted))
                    }
                    Row(
                        modifier = Modifier
                            .height(38.dp)
                            .clip(RoundedCornerShape(11.dp))
                            .background(AccentSoft)
                            .clickable { }
                            .padding(horizontal = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Icon(Icons.Filled.Edit, null, tint = AccentDeep, modifier = Modifier.size(15.dp))
                        Text("Edit", style = MaterialTheme.typography.labelLarge.copy(color = AccentDeep, fontWeight = FontWeight.Bold, fontSize = 13.sp))
                    }
                }
                Spacer(Modifier.height(14.dp))
            }

            // Stats
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    listOf(
                        Pair("${uiState.listingCount}", "Listings"),
                        Pair("${uiState.savedCount}", "Saved"),
                        Pair("${uiState.rating}", "Rating"),
                    ).forEach { (num, label) ->
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .shadow(4.dp, RoundedCornerShape(16.dp))
                                .clip(RoundedCornerShape(16.dp))
                                .background(colors.card)
                                .padding(vertical = 14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(num, style = MaterialTheme.typography.headlineSmall.copy(color = colors.ink, fontWeight = FontWeight.ExtraBold, fontSize = 19.sp))
                            Spacer(Modifier.height(2.dp))
                            Text(label, style = MaterialTheme.typography.labelSmall.copy(color = colors.muted))
                        }
                    }
                }
                Spacer(Modifier.height(14.dp))
            }

            // Host promo banner
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(Ink)
                        .padding(horizontal = 18.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("List your property", style = MaterialTheme.typography.titleSmall.copy(color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp))
                        Spacer(Modifier.height(4.dp))
                        Text("Put your home up for rent and earn in minutes.", style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.7f), lineHeight = 18.sp))
                    }
                    Box(
                        modifier = Modifier.size(46.dp).clip(RoundedCornerShape(13.dp)).background(Accent),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Filled.Add, null, tint = Color.White, modifier = Modifier.size(24.dp))
                    }
                }
                Spacer(Modifier.height(18.dp))
            }

            // Menu
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(20.dp))
                        .clip(RoundedCornerShape(20.dp))
                        .background(colors.card)
                        .padding(vertical = 6.dp, horizontal = 4.dp),
                ) {
                    MENU_ITEMS.forEachIndexed { i, item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    when (item.route) {
                                        "notifications" -> onNavigateToNotifications()
                                    }
                                }
                                .padding(horizontal = 14.dp, vertical = 13.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(13.dp),
                        ) {
                            Box(
                                modifier = Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(item.tint),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(item.icon, null, tint = Color.White, modifier = Modifier.size(19.dp))
                            }
                            Text(item.label, style = MaterialTheme.typography.titleSmall.copy(color = colors.ink, fontWeight = FontWeight.SemiBold, fontSize = 14.5.sp), modifier = Modifier.weight(1f))
                            if (item.badge != null) {
                                Box(
                                    modifier = Modifier.clip(RoundedCornerShape(50)).background(AccentSoft).padding(horizontal = 6.dp, vertical = 2.dp),
                                ) {
                                    Text(item.badge, style = MaterialTheme.typography.labelSmall.copy(color = AccentDeep, fontWeight = FontWeight.Bold, fontSize = 11.5.sp))
                                }
                            }
                            Icon(Icons.Filled.ChevronRight, null, tint = colors.faint, modifier = Modifier.size(18.dp))
                        }
                        if (i < MENU_ITEMS.lastIndex) {
                            Box(Modifier.fillMaxWidth().height(1.dp).background(colors.line).padding(start = 63.dp))
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
            }

            // Logout
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .shadow(4.dp, RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp))
                        .background(colors.card)
                        .clickable(onClick = onLogout),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Filled.Logout, null, tint = Danger, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(9.dp))
                    Text("Log out", style = MaterialTheme.typography.titleSmall.copy(color = Danger, fontWeight = FontWeight.Bold, fontSize = 15.sp))
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}
