package com.shopnobilash.app.presentation.wishlist.ui
import com.shopnobilash.app.presentation.wishlist.viewmodel.WishlistViewModel

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shopnobilash.app.constants.AppConstants
import com.shopnobilash.app.data.property.model.Property
import com.shopnobilash.app.navigation.Screen
import com.shopnobilash.app.presentation.components.BottomNavBar
import com.shopnobilash.app.presentation.components.PrimaryButton
import com.shopnobilash.app.presentation.components.PropertyCardHorizontalShimmer
import com.shopnobilash.app.presentation.theme.Accent
import com.shopnobilash.app.presentation.theme.appColors
import com.shopnobilash.app.presentation.wishlist.ui.components.WishlistCard
import org.koin.androidx.compose.koinViewModel

@Composable
fun WishlistScreen(
    onNavigateToDetail: (String) -> Unit,
    onNavigateToTab: (String) -> Unit,
    viewModel: WishlistViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val colors = MaterialTheme.appColors

    Scaffold(
        containerColor = colors.bg,
        bottomBar = { BottomNavBar(currentRoute = Screen.Wishlist.route, onTabSelected = onNavigateToTab) },
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            SavedHeader(savedCount = uiState.savedProperties.size)

            when {
                uiState.isLoading -> SavedLoadingState()
                uiState.savedProperties.isEmpty() -> SavedEmptyState(
                    onBrowse = { onNavigateToTab(Screen.Home.route) },
                )
                else -> SavedList(
                    properties = uiState.savedProperties,
                    onOpen = onNavigateToDetail,
                    onSaveToggle = viewModel::toggleSave,
                )
            }
        }
    }
}

@Composable
private fun SavedHeader(savedCount: Int) {
    val colors = MaterialTheme.appColors
    Spacer(Modifier.height(52.dp))
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            "Saved",
            style = MaterialTheme.typography.headlineMedium.copy(
                color = colors.ink,
                fontWeight = FontWeight.ExtraBold,
            ),
        )
        if (savedCount > 0) {
            Box(
                modifier = Modifier
                    .shadow(4.dp, RoundedCornerShape(10.dp))
                    .clip(RoundedCornerShape(10.dp))
                    .background(colors.card)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
            ) {
                Text(
                    "$savedCount ${if (savedCount == 1) "place" else "places"}",
                    style = MaterialTheme.typography.labelMedium.copy(color = colors.muted),
                )
            }
        }
    }
}

@Composable
private fun SavedLoadingState() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        userScrollEnabled = false,
    ) {
        items(4) {
            PropertyCardHorizontalShimmer()
        }
    }
}

@Composable
private fun SavedEmptyState(onBrowse: () -> Unit) {
    val colors = MaterialTheme.appColors
    Box(modifier = Modifier.fillMaxSize().padding(horizontal = 32.dp), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Icon(
                painter = painterResource(AppConstants.CardsStarIcon),
                contentDescription = null,
                tint = Accent,
                modifier = Modifier.size(56.dp),
            )
            Spacer(Modifier.height(10.dp))
            Text(
                "No saved places yet",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = colors.ink,
                    fontWeight = FontWeight.Bold,
                ),
            )
            Text(
                "Tap the bookmark on any listing to keep it here for later.",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = colors.muted,
                    lineHeight = 18.sp,
                    textAlign = TextAlign.Center,
                ),
            )
            Spacer(Modifier.height(20.dp))
            PrimaryButton(
                text = "Browse listings",
                onClick = onBrowse,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun SavedList(
    properties: List<Property>,
    onOpen: (String) -> Unit,
    onSaveToggle: (String) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        items(properties, key = { it.id }) { prop ->
            WishlistCard(
                property = prop,
                onOpen = { onOpen(prop.id) },
                onDeleteClick = { onSaveToggle(prop.id) },
            )
        }
        item { Spacer(Modifier.height(8.dp)) }
    }
}
