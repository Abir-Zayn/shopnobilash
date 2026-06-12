package com.dorent.app.ui.feature.wishlist

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BookmarkBorder
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dorent.app.ui.components.BottomNavBar
import com.dorent.app.ui.components.PropertyCardHorizontal
import com.dorent.app.ui.theme.Accent
import com.dorent.app.ui.theme.appColors
import org.koin.androidx.compose.koinViewModel

@Composable
fun WishlistScreen(
    onNavigateToDetail: (String) -> Unit,
    onNavigateToTab: (String) -> Unit,
    viewModel: WishlistViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val colors = MaterialTheme.appColors
    val filters = listOf("All", "House", "Apartment", "Villa")

    Scaffold(
        containerColor = colors.bg,
        bottomBar = { BottomNavBar(currentRoute = "wishlist", onTabSelected = onNavigateToTab) },
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            Spacer(Modifier.height(52.dp))
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Saved", style = MaterialTheme.typography.headlineMedium.copy(color = colors.ink, fontWeight = FontWeight.ExtraBold))
                Box(
                    modifier = Modifier
                        .shadow(4.dp, RoundedCornerShape(10.dp))
                        .clip(RoundedCornerShape(10.dp))
                        .background(colors.card)
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                ) {
                    Text("${uiState.savedProperties.size} places", style = MaterialTheme.typography.labelMedium.copy(color = colors.muted))
                }
            }

            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 18.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(9.dp),
            ) {
                filters.forEach { filter ->
                    val selected = uiState.selectedFilter == filter
                    Box(
                        modifier = Modifier
                            .height(38.dp)
                            .shadow(if (selected) 6.dp else 4.dp, RoundedCornerShape(11.dp))
                            .clip(RoundedCornerShape(11.dp))
                            .background(if (selected) Accent else colors.card)
                            .clickable { viewModel.setFilter(filter) }
                            .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(filter, style = MaterialTheme.typography.labelLarge.copy(color = if (selected) Color.White else colors.ink2, fontWeight = FontWeight.Bold))
                    }
                }
            }

            if (uiState.savedProperties.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(76.dp)
                                .shadow(4.dp, RoundedCornerShape(22.dp))
                                .clip(RoundedCornerShape(22.dp))
                                .background(colors.card),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(Icons.Outlined.BookmarkBorder, null, tint = colors.faint, modifier = Modifier.size(32.dp))
                        }
                        Spacer(Modifier.height(16.dp))
                        Text("No saved places yet", style = MaterialTheme.typography.titleMedium.copy(color = colors.ink, fontWeight = FontWeight.Bold))
                        Spacer(Modifier.height(6.dp))
                        Text("Tap the bookmark on any listing to keep it here.", style = MaterialTheme.typography.bodySmall.copy(color = colors.muted))
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 18.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    items(uiState.savedProperties, key = { it.id }) { prop ->
                        PropertyCardHorizontal(
                            property = prop,
                            isSaved = true,
                            onOpen = { onNavigateToDetail(prop.id) },
                            onSaveToggle = { viewModel.toggleSave(prop.id) },
                        )
                    }
                    item { Spacer(Modifier.height(8.dp)) }
                }
            }
        }
    }
}
