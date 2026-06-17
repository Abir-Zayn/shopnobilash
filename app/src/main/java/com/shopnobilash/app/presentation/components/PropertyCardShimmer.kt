package com.shopnobilash.app.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

/**
 * Shimmer placeholder matching PropertyCardVertical dimensions.
 * Used during loading state on the Home screen.
 */
@Composable
fun PropertyCardShimmer(
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        // Image placeholder
        ShimmerBox(
            modifier = Modifier
                .fillMaxWidth()
                .height(132.dp)
                .clip(RoundedCornerShape(14.dp)),
        )

        Spacer(Modifier.height(12.dp))

        // Tag placeholder
        ShimmerBox(
            modifier = Modifier
                .fillMaxWidth(0.35f)
                .height(16.dp)
                .clip(RoundedCornerShape(7.dp)),
        )

        Spacer(Modifier.height(8.dp))

        // Title placeholder
        ShimmerBox(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(18.dp)
                .clip(RoundedCornerShape(6.dp)),
        )

        Spacer(Modifier.height(6.dp))

        // City placeholder
        ShimmerBox(
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(14.dp)
                .clip(RoundedCornerShape(6.dp)),
        )

        Spacer(Modifier.height(10.dp))

        // Price placeholder
        ShimmerBox(
            modifier = Modifier
                .fillMaxWidth(0.45f)
                .height(20.dp)
                .clip(RoundedCornerShape(6.dp)),
        )
    }
}

/**
 * Shimmer placeholder matching PropertyCardHorizontal dimensions.
 */
@Composable
fun PropertyCardHorizontalShimmer(
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        // Image placeholder
        ShimmerBox(
            modifier = Modifier
                .fillMaxWidth()
                .height(168.dp)
                .clip(RoundedCornerShape(15.dp)),
        )

        Spacer(Modifier.height(12.dp))

        // Tag
        ShimmerBox(
            modifier = Modifier
                .fillMaxWidth(0.3f)
                .height(16.dp)
                .clip(RoundedCornerShape(7.dp)),
        )

        Spacer(Modifier.height(8.dp))

        // Price
        ShimmerBox(
            modifier = Modifier
                .fillMaxWidth(0.4f)
                .height(22.dp)
                .clip(RoundedCornerShape(6.dp)),
        )

        Spacer(Modifier.height(8.dp))

        // Title
        ShimmerBox(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .height(18.dp)
                .clip(RoundedCornerShape(6.dp)),
        )

        Spacer(Modifier.height(6.dp))

        // Address
        ShimmerBox(
            modifier = Modifier
                .fillMaxWidth(0.65f)
                .height(14.dp)
                .clip(RoundedCornerShape(6.dp)),
        )

        Spacer(Modifier.height(12.dp))

        // Meta row (beds/baths/sqft)
        ShimmerBox(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(16.dp)
                .clip(RoundedCornerShape(6.dp)),
        )
    }
}
