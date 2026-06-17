package com.shopnobilash.app.presentation.owner.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shopnobilash.app.presentation.components.AppAvatar
import com.shopnobilash.app.presentation.components.AppText
import com.shopnobilash.app.presentation.components.AppTextField
import com.shopnobilash.app.presentation.components.PrimaryButton
import com.shopnobilash.app.presentation.components.RoundIconButton
import com.shopnobilash.app.presentation.owner.viewmodel.OwnerDashboardState
import com.shopnobilash.app.presentation.owner.viewmodel.OwnerDashboardViewModel
import com.shopnobilash.app.presentation.theme.Danger
import com.shopnobilash.app.presentation.theme.appColors

/** STEP 2 — capture the `owners` row. Pre-fills name/address/picture from the profile. */
@Composable
fun OwnerOnboardingScreen(
    state: OwnerDashboardState,
    viewModel: OwnerDashboardViewModel,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.appColors
    val context = LocalContext.current
    val profile = state.profile

    var name by rememberSaveable { mutableStateOf(profile?.fullName.orEmpty()) }
    var addr1 by rememberSaveable { mutableStateOf(profile?.permanentAddress.orEmpty()) }
    var addr2 by rememberSaveable { mutableStateOf("") }
    var tin by rememberSaveable { mutableStateOf("") }
    var newPicUri by remember { mutableStateOf<Uri?>(null) }

    val picker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { newPicUri = it }
    }

    val nameValid = name.trim().isNotEmpty() && name.length <= 255
    val addr1Valid = addr1.trim().isNotEmpty() && addr1.length <= 255
    val canSubmit = nameValid && addr1Valid && !state.isSubmittingOwner

    val displayPic = newPicUri?.toString() ?: profile?.profilePictureUrl

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 8.dp),
    ) {
        // Avatar (left) with header text below it.
        Box(contentAlignment = Alignment.BottomEnd) {
            AppAvatar(imageUrl = displayPic, name = name.ifEmpty { "Owner" }, size = 72.dp)
            RoundIconButton(
                icon = Icons.Filled.Edit,
                onClick = { picker.launch("image/*") },
                contentDescription = "Change picture",
                active = true,
                size = 26.dp,
            )
        }
        Spacer(Modifier.height(14.dp))
        AppText(
            "Become an Owner",
            style = MaterialTheme.typography.titleLarge,
            color = colors.ink,
            fontWeight = FontWeight.ExtraBold,
        )
        Spacer(Modifier.height(4.dp))
        AppText(
            "Confirm your details to start listing properties.",
            style = MaterialTheme.typography.bodySmall,
            color = colors.muted,
        )

        // Gap before the form section.
        Spacer(Modifier.height(28.dp))

        AppText(
            "OWNER INFORMATION",
            style = MaterialTheme.typography.labelMedium,
            color = colors.muted,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.sp,
        )

        Spacer(Modifier.height(20.dp))

        FieldGroup(
            label = "Full Name *",
            helper = "Make sure it matches with the NID / Passport / Birth Certificate.",
        ) {
            AppTextField(value = name, onValueChange = { name = it }, placeholder = "Full name", cornerRadius = 20.dp)
        }

        Spacer(Modifier.height(24.dp))

        FieldGroup(label = "Address Line 1 *") {
            AppTextField(value = addr1, onValueChange = { addr1 = it }, placeholder = "Primary address", cornerRadius = 20.dp)
        }

        Spacer(Modifier.height(24.dp))

        FieldGroup(label = "Address Line 2") {
            AppTextField(value = addr2, onValueChange = { addr2 = it }, placeholder = "Apt, floor (optional)", cornerRadius = 20.dp)
        }

        Spacer(Modifier.height(24.dp))

        FieldGroup(
            label = "TIN Certificate No",
            helper = "It should match the govt.-issued TIN certificate from NBR.",
        ) {
            AppTextField(value = tin, onValueChange = { tin = it }, placeholder = "Tax ID (optional)", cornerRadius = 20.dp)
            if (state.tinError != null) {
                Spacer(Modifier.height(6.dp))
                AppText(
                    state.tinError,
                    style = MaterialTheme.typography.labelSmall,
                    color = Danger,
                )
            }
        }

        Spacer(Modifier.height(32.dp))

        PrimaryButton(
            text = "Next",
            onClick = {
                viewModel.submitOwner(
                    name = name.trim(),
                    addressLine1 = addr1.trim(),
                    addressLine2 = addr2.trim().ifEmpty { null },
                    tin = tin.trim().ifEmpty { null },
                    prefilledProfilePicUrl = profile?.profilePictureUrl,
                    newProfilePicUri = newPicUri,
                    context = context,
                )
            },
            enabled = canSubmit,
            isLoading = state.isSubmittingOwner,
            cornerRadius = 28.dp,
        )

        Spacer(Modifier.height(16.dp))
    }
}

/**
 * Label (+ optional helper right beneath it) followed by the field, with a clear
 * gap between the label block and the input.
 */
@Composable
private fun FieldGroup(
    label: String,
    helper: String? = null,
    content: @Composable () -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        FieldLabel(label)
        if (helper != null) {
            Spacer(Modifier.height(4.dp))
            FieldHelper(helper)
        }
        Spacer(Modifier.height(12.dp))
        content()
    }
}

@Composable
private fun FieldHelper(text: String) {
    AppText(
        text,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.appColors.muted,
        fontSize = 11.sp,
    )
}

@Composable
private fun FieldLabel(text: String) {
    AppText(
        text,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.appColors.ink,
        fontWeight = FontWeight.SemiBold,
        fontSize = 13.sp,
    )
}
