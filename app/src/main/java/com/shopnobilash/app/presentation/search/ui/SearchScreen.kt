package com.shopnobilash.app.presentation.search.ui

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shopnobilash.app.data.property.model.FILTERABLE_CATEGORIES
import com.shopnobilash.app.data.property.model.PropertyCategory
import com.shopnobilash.app.presentation.components.AppTextField
import com.shopnobilash.app.presentation.components.PropertyCardHorizontal
import com.shopnobilash.app.presentation.components.PropertyCardShimmer
import com.shopnobilash.app.presentation.components.RoundIconButton
import com.shopnobilash.app.presentation.search.viewmodel.SearchUiState
import com.shopnobilash.app.presentation.search.viewmodel.SearchViewModel
import com.shopnobilash.app.presentation.theme.Accent
import com.shopnobilash.app.presentation.theme.appColors
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onBack: () -> Unit,
    onNavigateToDetail: (String) -> Unit,
    initialCategory: PropertyCategory? = null,
    viewModel: SearchViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val filter by viewModel.filter.collectAsStateWithLifecycle()
    val savedIds by viewModel.savedIds.collectAsStateWithLifecycle()
    val colors = MaterialTheme.appColors

    var showSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    // Seed the pre-selected category once (e.g. tapped from a Home badge).
    LaunchedEffect(Unit) { initialCategory?.let { viewModel.onCategorySelected(it) } }

    Scaffold(containerColor = colors.bg) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            // ── Search row: back · field · filter ─────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RoundIconButton(
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    onClick = onBack,
                    contentDescription = "Back",
                )
                AppTextField(
                    value = filter.query,
                    onValueChange = viewModel::onQueryChange,
                    placeholder = "Search by property name…",
                    leadingIcon = Icons.Filled.Search,
                    trailingIcon = if (filter.query.isNotEmpty()) Icons.Filled.Close else null,
                    onTrailingIconClick = { viewModel.onQueryChange("") },
                    modifier = Modifier.weight(1f),
                )
                Box {
                    RoundIconButton(
                        icon = Icons.Filled.Tune,
                        onClick = { showSheet = true },
                        contentDescription = "Filter",
                        active = filter.hasAdvancedFilters,
                    )
                    if (filter.advancedFilterCount > 0) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .size(18.dp)
                                .clip(RoundedCornerShape(50))
                                .background(colors.danger),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                "${filter.advancedFilterCount}",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                }
            }

            // ── Category badges ────────────────────────────────────────────────
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                item {
                    CategoryBadge(
                        label = "All",
                        selected = filter.category == null,
                        onClick = { viewModel.onCategorySelected(null) },
                    )
                }
                items(FILTERABLE_CATEGORIES) { category ->
                    CategoryBadge(
                        label = category.rawValue,
                        selected = filter.category == category,
                        onClick = { viewModel.onCategorySelected(category) },
                    )
                }
            }

            // ── Results ────────────────────────────────────────────────────────
            when (val state = uiState) {
                is SearchUiState.Loading -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(18.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    items(4) { PropertyCardShimmer(modifier = Modifier.fillMaxWidth()) }
                }

                is SearchUiState.Error -> SearchMessage(
                    title = "Something went wrong",
                    subtitle = state.message,
                )

                is SearchUiState.Success -> {
                    if (state.properties.isEmpty()) {
                        SearchMessage(
                            title = "No properties found",
                            subtitle = "Try a different name, category, or loosen your filters.",
                        )
                    } else {
                        val shown = state.properties.size
                        val countText = if (state.total > shown) {
                            "Showing $shown of ${state.total}"
                        } else {
                            "$shown ${if (shown == 1) "result" else "results"}"
                        }
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            item {
                                Text(
                                    countText,
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        color = colors.muted,
                                        fontWeight = FontWeight.SemiBold,
                                    ),
                                )
                            }
                            items(state.properties, key = { it.id }) { prop ->
                                PropertyCardHorizontal(
                                    property = prop,
                                    isSaved = prop.id in savedIds,
                                    onOpen = { onNavigateToDetail(prop.id) },
                                    onSaveToggle = { viewModel.toggleSave(prop.id) },
                                    modifier = Modifier.fillMaxWidth(),
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState = sheetState,
            containerColor = colors.card,
        ) {
            FilterSheetContent(
                current = filter,
                onApply = { minRent, maxRent, minArea, maxArea, baths, beds, newly ->
                    viewModel.applyAdvancedFilters(minRent, maxRent, minArea, maxArea, baths, beds, newly)
                    scope.launch { sheetState.hide() }.invokeOnCompletion { showSheet = false }
                },
                onReset = { viewModel.clearAdvancedFilters() },
            )
        }
    }
}

@Composable
private fun CategoryBadge(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val colors = MaterialTheme.appColors
    Box(
        modifier = Modifier
            .height(40.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) Accent else colors.card)
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            label,
            style = MaterialTheme.typography.labelLarge.copy(
                color = if (selected) Color.White else colors.ink2,
                fontWeight = FontWeight.Bold,
            ),
        )
    }
}

@Composable
private fun SearchMessage(title: String, subtitle: String) {
    val colors = MaterialTheme.appColors
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            Icons.Filled.SearchOff,
            contentDescription = null,
            tint = colors.faint,
            modifier = Modifier.size(56.dp),
        )
        Spacer(Modifier.height(14.dp))
        Text(
            title,
            style = MaterialTheme.typography.titleMedium.copy(
                color = colors.ink,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
            ),
        )
        Spacer(Modifier.height(6.dp))
        Text(
            subtitle,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = colors.muted,
                textAlign = TextAlign.Center,
            ),
        )
    }
}
