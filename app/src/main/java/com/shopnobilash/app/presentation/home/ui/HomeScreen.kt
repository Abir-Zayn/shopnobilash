package com.shopnobilash.app.presentation.home.ui
import com.shopnobilash.app.presentation.home.viewmodel.HomeViewModel
import com.shopnobilash.app.presentation.home.viewmodel.HomeUiState

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.CircularProgressIndicator
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
import com.shopnobilash.app.presentation.components.PropertyCardHorizontal
import com.shopnobilash.app.presentation.components.PropertyCardVertical
import com.shopnobilash.app.presentation.components.RoundIconButton
import com.shopnobilash.app.presentation.theme.Accent
import com.shopnobilash.app.presentation.theme.appColors
import org.koin.androidx.compose.koinViewModel

data class Category(val id: String, val icon: ImageVector, val label: String)

val CATEGORIES = listOf(
    Category("house",     Icons.Filled.Home,          "House"),
    Category("villa",     Icons.Filled.Business,      "Villa"),
    Category("apartment", Icons.Filled.Apartment,     "Apartment"),
    Category("office",    Icons.Filled.Business,      "Office"),
    Category("studio",    Icons.Filled.Hotel,          "Studio"),
)

@Composable
fun HomeScreen(
    onNavigateToDetail: (String) -> Unit,
    onNavigateToNewlyAdded: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToTab: (String) -> Unit,
    viewModel: HomeViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val savedIds by viewModel.savedIds.collectAsStateWithLifecycle()
    val selectedCat by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val colors = MaterialTheme.appColors

    Scaffold(
        containerColor = colors.bg,
        bottomBar = { BottomNavBar(currentRoute = "home", onTabSelected = onNavigateToTab) },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentPadding = PaddingValues(bottom = 24.dp),
        ) {
            // Status bar spacer
            item { Spacer(Modifier.height(52.dp)) }

            // Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RoundIconButton(
                        icon = Icons.Filled.Menu,
                        onClick = { onNavigateToTab("profile") },
                        contentDescription = "Menu",
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Location", style = MaterialTheme.typography.labelSmall.copy(color = colors.muted, fontWeight = FontWeight.SemiBold))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.LocationOn, null, tint = Accent, modifier = Modifier.size(15.dp))
                            Spacer(Modifier.width(5.dp))
                            Text("New York, USA", style = MaterialTheme.typography.titleSmall.copy(color = colors.ink, fontWeight = FontWeight.Bold))
                            Icon(Icons.Filled.KeyboardArrowDown, null, tint = colors.ink2, modifier = Modifier.size(13.dp))
                        }
                    }
                    Box {
                        RoundIconButton(icon = Icons.Filled.Notifications, onClick = onNavigateToNotifications, contentDescription = "Notifications")
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(RoundedCornerShape(50))
                                .background(colors.danger)
                                .align(Alignment.TopEnd)
                                .padding(1.dp),
                        )
                    }
                }
            }

            // Search bar
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 4.dp).padding(bottom = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(11.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .shadow(4.dp, RoundedCornerShape(15.dp))
                            .clip(RoundedCornerShape(15.dp))
                            .background(colors.card)
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Icon(Icons.Filled.Search, null, tint = colors.muted, modifier = Modifier.size(20.dp))
                        Text("Search location, type…", style = MaterialTheme.typography.bodyMedium.copy(color = colors.faint, fontSize = 14.5.sp))
                    }
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(15.dp))
                            .background(Accent)
                            .clickable { },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Filled.Tune, null, tint = Color.White, modifier = Modifier.size(22.dp))
                    }
                }
            }

            // Categories
            item {
                Text(
                    "Categories",
                    style = MaterialTheme.typography.headlineSmall.copy(color = colors.ink, fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(horizontal = 18.dp),
                )
                Spacer(Modifier.height(14.dp))
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()).padding(horizontal = 18.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    CATEGORIES.forEach { cat ->
                        val on = selectedCat == cat.id
                        Row(
                            modifier = Modifier
                                .height(44.dp)
                                .shadow(if (on) 6.dp else 4.dp, RoundedCornerShape(13.dp))
                                .clip(RoundedCornerShape(13.dp))
                                .background(if (on) Accent else colors.card)
                                .clickable { viewModel.setCategory(cat.id) }
                                .padding(horizontal = 18.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Icon(cat.icon, null, tint = if (on) Color.White else colors.muted, modifier = Modifier.size(18.dp))
                            Text(cat.label, style = MaterialTheme.typography.labelLarge.copy(color = if (on) Color.White else colors.ink2, fontWeight = FontWeight.Bold))
                        }
                    }
                }
                Spacer(Modifier.height(22.dp))
            }

            when (val state = uiState) {
                is HomeUiState.Loading -> item {
                    Box(Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Accent)
                    }
                }
                is HomeUiState.Error -> item {
                    Text(state.message, modifier = Modifier.padding(18.dp), color = colors.danger)
                }
                is HomeUiState.Success -> {
                    // Recommend section
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text("Recommend for you", style = MaterialTheme.typography.headlineSmall.copy(color = colors.ink, fontWeight = FontWeight.Bold))
                            Text("See all", style = MaterialTheme.typography.labelLarge.copy(color = Accent, fontWeight = FontWeight.Bold),
                                modifier = Modifier.clickable { onNavigateToNewlyAdded() })
                        }
                        Spacer(Modifier.height(14.dp))
                    }
                    item {
                        val recommended = state.properties.filter { it.id in listOf("sherman", "lara", "minimal") }
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 18.dp),
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                        ) {
                            items(recommended) { prop ->
                                PropertyCardVertical(
                                    property = prop,
                                    isSaved = prop.id in savedIds,
                                    onOpen = { onNavigateToDetail(prop.id) },
                                    onSaveToggle = { viewModel.toggleSave(prop.id) },
                                )
                            }
                        }
                        Spacer(Modifier.height(24.dp))
                    }

                    // Newly Added section
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text("Newly Added", style = MaterialTheme.typography.headlineSmall.copy(color = colors.ink, fontWeight = FontWeight.Bold))
                            Text("See all", style = MaterialTheme.typography.labelLarge.copy(color = Accent, fontWeight = FontWeight.Bold),
                                modifier = Modifier.clickable { onNavigateToNewlyAdded() })
                        }
                        Spacer(Modifier.height(14.dp))
                    }
                    val newlyAdded = state.properties.filter { it.id in listOf("minimal", "earth") }
                    items(newlyAdded) { prop ->
                        PropertyCardHorizontal(
                            property = prop,
                            isSaved = prop.id in savedIds,
                            onOpen = { onNavigateToDetail(prop.id) },
                            onSaveToggle = { viewModel.toggleSave(prop.id) },
                            modifier = Modifier.padding(horizontal = 18.dp).padding(bottom = 16.dp),
                        )
                    }
                }
            }
        }
    }
}
