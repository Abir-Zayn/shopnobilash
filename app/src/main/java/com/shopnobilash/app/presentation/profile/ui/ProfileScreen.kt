package com.shopnobilash.app.presentation.profile.ui
import com.shopnobilash.app.presentation.profile.viewmodel.ProfileViewModel
import com.shopnobilash.app.presentation.profile.viewmodel.ProfileUiState

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
import androidx.compose.material.icons.filled.House
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VerifiedUser
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
import com.shopnobilash.app.presentation.components.BottomNavBar
import com.shopnobilash.app.presentation.components.AppAvatar
import com.shopnobilash.app.presentation.components.AppTile
import com.shopnobilash.app.presentation.components.AppTileDivider
import com.shopnobilash.app.presentation.components.RoundIconButton
import com.shopnobilash.app.presentation.theme.Accent
import com.shopnobilash.app.presentation.theme.AccentDeep
import com.shopnobilash.app.presentation.theme.AccentSoft
import com.shopnobilash.app.presentation.theme.Blue
import com.shopnobilash.app.presentation.theme.Danger
import com.shopnobilash.app.presentation.theme.Ink
import com.shopnobilash.app.presentation.theme.Muted
import com.shopnobilash.app.presentation.theme.TagOrange
import com.shopnobilash.app.presentation.theme.appColors
import org.koin.androidx.compose.koinViewModel

data class MenuItem(
    val icon: ImageVector,
    val label: String,
    val tint: Color,
    val badge: String? = null,
    val onClick: (() -> Unit)? = null,
)

@Composable
fun ProfileScreen(
    onNavigateToNotifications: () -> Unit,
    onNavigateToVerification: () -> Unit,
    onLogout: () -> Unit,
    onNavigateToTab: (String) -> Unit,
    viewModel: ProfileViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val colors = MaterialTheme.appColors

    val menuItems = listOf(
        MenuItem(Icons.Filled.Person,       "Personal information", Accent),
        MenuItem(Icons.Filled.VerifiedUser, "Verify Identity",      Blue,        onClick = onNavigateToVerification),
        MenuItem(Icons.Filled.House,        "Provide Rental",       Accent),
        MenuItem(Icons.Filled.Apartment,    "My properties",        Blue,        badge = "2"),
        MenuItem(Icons.Filled.CreditCard,   "Payment methods",      Color(0xFF7C5CFC)),
        MenuItem(Icons.Filled.Notifications,"Notifications",        TagOrange,   onClick = onNavigateToNotifications),
        MenuItem(Icons.Filled.Shield,       "Privacy & security",   Color(0xFF3AAFA9)),
        MenuItem(Icons.Filled.HelpOutline,  "Help center",          Muted),
    )

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
                    AppAvatar(imageUrl = uiState.profilePictureUrl, name = uiState.name, size = 62.dp)
                    Column(modifier = Modifier.weight(1f)) {
                        Text(uiState.name.ifEmpty { "User" }, style = MaterialTheme.typography.titleLarge.copy(color = colors.ink, fontWeight = FontWeight.ExtraBold, fontSize = 17.sp))
                        Text(uiState.email, style = MaterialTheme.typography.bodySmall.copy(color = colors.muted))
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
                    menuItems.forEachIndexed { i, item ->
                        AppTile(
                            icon = item.icon,
                            iconBg = item.tint,
                            text = item.label,
                            hasNotification = item.badge != null,
                            notificationNumber = item.badge,
                            onClick = item.onClick ?: {},
                        )
                        if (i < menuItems.lastIndex) {
                            AppTileDivider()
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
