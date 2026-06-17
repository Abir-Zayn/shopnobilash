package com.shopnobilash.app.presentation.home.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shopnobilash.app.presentation.components.PropertyCardShimmer
import com.shopnobilash.app.presentation.theme.appColors

/**
 * Section header with title and optional "See all" action.
 */
@Composable
fun HomeSectionHeader(
    title: String,
    onSeeAll: (() -> Unit)? = null,
) {
    val colors = MaterialTheme.appColors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            title,
            style = MaterialTheme.typography.headlineSmall.copy(
                color = colors.ink,
                fontWeight = FontWeight.Bold,
            ),
        )
        if (onSeeAll != null) {
            Text(
                "See all",
                style = MaterialTheme.typography.labelLarge.copy(
                    color = colors.accent,
                    fontWeight = FontWeight.Bold,
                ),
                modifier = Modifier.clickable(onClick = onSeeAll).padding(start = 8.dp),
            )
        }
    }
    Spacer(Modifier.height(14.dp))
}

/**
 * Renders shimmer placeholders during loading.
 */
@Composable
fun HomeLoadingState() {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 18.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        items(4) {
            PropertyCardShimmer(
                modifier = Modifier.padding(bottom = 8.dp),
            )
        }
    }
}

/**
 * Empty-state placeholder shown when no properties are available.
 */
@Composable
fun HomeEmptyState(
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.appColors
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            "—",
            style = MaterialTheme.typography.headlineLarge.copy(
                color = colors.faint,
                fontSize = 48.sp,
            ),
        )
        Spacer(Modifier.height(12.dp))
        Text(
            "All properties are currently booked.",
            style = MaterialTheme.typography.titleMedium.copy(
                color = colors.ink,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
            ),
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "New listings are added daily \u2014 check back soon or browse different categories.",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = colors.muted,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp,
            ),
        )
    }
}
