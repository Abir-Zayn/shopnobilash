package com.shopnobilash.app.presentation.owner.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.shopnobilash.app.presentation.components.AppAvatar
import com.shopnobilash.app.presentation.components.AppText
import com.shopnobilash.app.presentation.components.PrimaryButton
import com.shopnobilash.app.presentation.owner.viewmodel.OwnerDashboardState
import com.shopnobilash.app.presentation.theme.Accent
import com.shopnobilash.app.presentation.theme.appColors

/** STEP 3 success — "Property has been added successfully." */
@Composable
fun PropertyCreatedScreen(
    state: OwnerDashboardState,
    onViewListing: (String) -> Unit,
    onAddAnother: () -> Unit,
    onGoToDashboard: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.appColors
    val created = state.createdProperty

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Spacer(Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(Accent.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Filled.CheckCircle, null, tint = Accent, modifier = Modifier.size(52.dp))
        }

        AppText(
            "Property has been added successfully.",
            style = MaterialTheme.typography.headlineSmall.copy(
                color = colors.ink,
                fontWeight = FontWeight.ExtraBold,
            ),
            textAlign = TextAlign.Center,
        )

        if (created != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(colors.card)
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                if (created.firstImageUrl != null) {
                    AsyncImage(
                        model = created.firstImageUrl,
                        contentDescription = created.houseName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(colors.field),
                    )
                } else {
                    AppAvatar(imageUrl = null, name = created.houseName, size = 72.dp)
                }
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    AppText(
                        created.houseName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = colors.ink,
                            fontWeight = FontWeight.Bold,
                        ),
                    )
                    AppText(
                        "\$${"%,.0f".format(created.rent)}/mo · ${created.areaSqft} sqft · ${created.bedNo} bd",
                        style = MaterialTheme.typography.bodySmall.copy(color = colors.muted, fontSize = 13.sp),
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        PrimaryButton(
            text = "View Listing",
            onClick = { created?.let { onViewListing(it.id) } },
            enabled = created != null,
            cornerRadius = 28.dp,
        )

        // Add Another (ghost)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(colors.card)
                .clickable(onClick = onAddAnother),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AppText(
                "Add Another",
                style = MaterialTheme.typography.titleSmall.copy(
                    color = colors.ink,
                    fontWeight = FontWeight.Bold,
                ),
            )
        }

        // Go to Dashboard (text)
        AppText(
            "Go to Dashboard",
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable(onClick = onGoToDashboard)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            style = MaterialTheme.typography.titleSmall.copy(
                color = colors.muted,
                fontWeight = FontWeight.SemiBold,
            ),
        )
    }
}
