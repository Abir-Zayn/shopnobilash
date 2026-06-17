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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shopnobilash.app.presentation.components.AppText
import com.shopnobilash.app.presentation.components.PrimaryButton
import com.shopnobilash.app.presentation.owner.viewmodel.OwnerDashboardViewModel
import com.shopnobilash.app.presentation.theme.Blue
import com.shopnobilash.app.presentation.theme.appColors

/** STEP 1 block — shown when `profiles.is_verified == false`. Cannot proceed. */
@Composable
fun VerificationPendingScreen(
    viewModel: OwnerDashboardViewModel,
    onReviewProfile: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.appColors

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Spacer(Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(Blue.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Filled.HourglassEmpty, null, tint = Blue, modifier = Modifier.size(48.dp))
        }

        AppText(
            "Verification Pending",
            style = MaterialTheme.typography.headlineSmall.copy(
                color = colors.ink,
                fontWeight = FontWeight.ExtraBold,
            ),
        )

        AppText(
            "Your profile is under review. Once an admin verifies your identity, you can list your properties.",
            style = MaterialTheme.typography.bodyMedium.copy(color = colors.muted, lineHeight = 22.sp),
            textAlign = TextAlign.Center,
        )

        // Status pill
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(Blue.copy(alpha = 0.10f))
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(Blue),
            )
            AppText(
                "Pending Review",
                style = MaterialTheme.typography.labelLarge.copy(
                    color = Blue,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                ),
            )
        }

        Spacer(Modifier.height(8.dp))

        PrimaryButton(text = "Review My Profile", onClick = onReviewProfile, cornerRadius = 28.dp)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(colors.card)
                .clickable { viewModel.refresh() }
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Icons.Filled.Refresh, null, tint = colors.muted, modifier = Modifier.size(18.dp))
            Spacer(Modifier.size(8.dp))
            AppText(
                "Refresh",
                style = MaterialTheme.typography.titleSmall.copy(
                    color = colors.ink,
                    fontWeight = FontWeight.SemiBold,
                ),
            )
        }

        Spacer(Modifier.height(4.dp))
        AppText(
            "Tap Refresh after an admin approves your verification.",
            style = MaterialTheme.typography.labelSmall.copy(color = colors.faint),
            textAlign = TextAlign.Center,
        )
    }
}
