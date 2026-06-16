package com.shopnobilash.app.presentation.verification.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shopnobilash.app.constants.VERIFICATION_STATUS_APPROVED
import com.shopnobilash.app.constants.VERIFICATION_STATUS_REJECTED
import com.shopnobilash.app.data.verification.model.DocumentType
import com.shopnobilash.app.data.verification.model.Verification
import com.shopnobilash.app.presentation.components.AppSnackbarHost
import com.shopnobilash.app.presentation.components.PrimaryButton
import com.shopnobilash.app.presentation.components.SnackbarMessage
import com.shopnobilash.app.presentation.components.SnackbarType
import com.shopnobilash.app.presentation.components.StackHeader
import com.shopnobilash.app.presentation.theme.Accent
import com.shopnobilash.app.presentation.theme.Blue
import com.shopnobilash.app.presentation.theme.Danger
import com.shopnobilash.app.presentation.theme.Primary
import com.shopnobilash.app.presentation.theme.appColors
import com.shopnobilash.app.presentation.verification.viewmodel.VerificationUiState
import com.shopnobilash.app.presentation.verification.viewmodel.VerificationViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerificationScreen(
    onBack: () -> Unit,
    viewModel: VerificationViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val colors = MaterialTheme.appColors
    var snackbar by remember { mutableStateOf<SnackbarMessage?>(null) }
    var documentType by remember { mutableStateOf(DocumentType.NID) }
    var docTypeExpanded by remember { mutableStateOf(false) }

    val filePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { viewModel.onFileSelected(it, context) }
    }

    LaunchedEffect(uiState) {
        if (uiState is VerificationUiState.Error) {
            snackbar = SnackbarMessage(
                (uiState as VerificationUiState.Error).message,
                SnackbarType.Error,
            )
            viewModel.clearError()
        }
    }

    val isUploading = uiState is VerificationUiState.Uploading
    val isSubmitting = uiState is VerificationUiState.Submitting
    val isLoading = uiState is VerificationUiState.Loading
    val hasFile = (uiState as? VerificationUiState.Idle)?.hasUploadedFile == true

    Scaffold(
        containerColor = colors.bg,
        topBar = { StackHeader(title = "Verify Identity", onBack = onBack) },
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            when (val state = uiState) {
                is VerificationUiState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(color = Accent)
                    }
                }

                is VerificationUiState.AlreadySubmitted -> {
                    VerificationStatusCard(
                        verification = state.verification,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(horizontal = 20.dp, vertical = 16.dp),
                    )
                }

                is VerificationUiState.Success -> {
                    VerificationSuccessContent(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(horizontal = 20.dp, vertical = 16.dp),
                    )
                }

                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 20.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Text(
                            "UPLOAD IDENTITY DOCUMENT",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = colors.muted,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 1.sp,
                            ),
                            modifier = Modifier.padding(top = 4.dp),
                        )

                        ExposedDropdownMenuBox(
                            expanded = docTypeExpanded,
                            onExpandedChange = { if (!isUploading && !isSubmitting) docTypeExpanded = it },
                        ) {
                            OutlinedTextField(
                                value = documentType.label,
                                onValueChange = {},
                                readOnly = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .menuAnchor(),
                                leadingIcon = {
                                    Icon(
                                        Icons.Filled.CreditCard, null,
                                        tint = colors.muted,
                                        modifier = Modifier.size(18.dp),
                                    )
                                },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = docTypeExpanded)
                                },
                                shape = RoundedCornerShape(14.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Primary,
                                    unfocusedBorderColor = Color.Transparent,
                                    focusedContainerColor = colors.card,
                                    unfocusedContainerColor = colors.card,
                                    focusedTextColor = colors.ink,
                                    unfocusedTextColor = colors.ink,
                                ),
                            )
                            ExposedDropdownMenu(
                                expanded = docTypeExpanded,
                                onDismissRequest = { docTypeExpanded = false },
                                modifier = Modifier.background(colors.card),
                            ) {
                                DocumentType.entries.forEach { type ->
                                    DropdownMenuItem(
                                        text = { Text(type.label, color = colors.ink) },
                                        onClick = {
                                            documentType = type
                                            docTypeExpanded = false
                                        },
                                    )
                                }
                            }
                        }

                        // File picker area
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(colors.card)
                                .border(
                                    width = 1.dp,
                                    color = if (hasFile) Accent else colors.line,
                                    shape = RoundedCornerShape(14.dp),
                                )
                                .clickable(enabled = !isUploading && !isSubmitting) {
                                    filePicker.launch("image/*")
                                },
                            contentAlignment = Alignment.Center,
                        ) {
                            if (isUploading) {
                                CircularProgressIndicator(color = Accent, modifier = Modifier.size(28.dp))
                            } else if (hasFile) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(6.dp),
                                ) {
                                    Icon(
                                        Icons.Filled.CheckCircle,
                                        null,
                                        tint = Accent,
                                        modifier = Modifier.size(32.dp),
                                    )
                                    Text(
                                        "Document uploaded",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = Accent,
                                            fontWeight = FontWeight.SemiBold,
                                        ),
                                    )
                                    Text(
                                        "Tap to change",
                                        style = MaterialTheme.typography.labelSmall.copy(color = colors.muted),
                                    )
                                }
                            } else {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(6.dp),
                                ) {
                                    Icon(
                                        Icons.Filled.Add,
                                        null,
                                        tint = colors.muted,
                                        modifier = Modifier.size(32.dp),
                                    )
                                    Text(
                                        "Tap to upload document",
                                        style = MaterialTheme.typography.bodySmall.copy(color = colors.muted),
                                    )
                                    Text(
                                        "JPG or PNG, max 10 MB",
                                        style = MaterialTheme.typography.labelSmall.copy(color = colors.faint),
                                    )
                                }
                            }
                        }

                        // Security notice
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Blue.copy(alpha = 0.08f))
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(Icons.Filled.Lock, null, tint = Blue, modifier = Modifier.size(18.dp))
                            Text(
                                "Your document is encrypted and only visible to our admin team.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Blue,
                                    lineHeight = 18.sp,
                                ),
                            )
                        }

                        Spacer(Modifier.height(4.dp))

                        PrimaryButton(
                            text = "Submit for Verification",
                            isLoading = isSubmitting,
                            enabled = hasFile && !isUploading,
                            onClick = { viewModel.submitVerification(documentType.value) },
                        )

                        Spacer(Modifier.height(16.dp))
                    }
                }
            }

            AppSnackbarHost(
                message = snackbar,
                onDismiss = { snackbar = null },
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }
    }
}

@Composable
private fun VerificationStatusCard(verification: Verification, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.appColors
    val (statusColor, statusLabel, statusIcon) = statusStyle(verification.verificationStatus)

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Spacer(Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(statusColor.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(statusIcon, null, tint = statusColor, modifier = Modifier.size(40.dp))
        }

        Text(
            statusLabel,
            style = MaterialTheme.typography.headlineSmall.copy(
                color = statusColor,
                fontWeight = FontWeight.ExtraBold,
            ),
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(colors.card)
                .padding(16.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                val docLabel = DocumentType.entries
                    .find { it.value == verification.documentType }
                    ?.label ?: verification.documentType
                StatusInfoRow("Document Type", docLabel, colors.ink)
                StatusInfoRow("Submitted", verification.createdAt.take(10), colors.muted)
                if (verification.verificationStatus == VERIFICATION_STATUS_REJECTED
                    && verification.rejectReason != null
                ) {
                    StatusInfoRow("Reason", verification.rejectReason, Danger)
                }
            }
        }

        if (verification.verificationStatus == VERIFICATION_STATUS_REJECTED) {
            Text(
                "Please resubmit with a clearer, valid document.",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = colors.muted,
                    lineHeight = 18.sp,
                ),
                modifier = Modifier.padding(horizontal = 8.dp),
            )
        }
    }
}

@Composable
private fun StatusInfoRow(label: String, value: String, valueColor: Color) {
    val colors = MaterialTheme.appColors
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall.copy(color = colors.muted))
        Text(
            value,
            style = MaterialTheme.typography.bodySmall.copy(
                color = valueColor,
                fontWeight = FontWeight.SemiBold,
            ),
        )
    }
}

@Composable
private fun VerificationSuccessContent(modifier: Modifier = Modifier) {
    val colors = MaterialTheme.appColors
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Spacer(Modifier.height(48.dp))
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Blue.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Filled.HourglassEmpty, null, tint = Blue, modifier = Modifier.size(40.dp))
        }
        Text(
            "Submitted!",
            style = MaterialTheme.typography.headlineSmall.copy(
                color = colors.ink,
                fontWeight = FontWeight.ExtraBold,
            ),
        )
        Text(
            "Your document is under review. You will be notified once verification is complete.",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = colors.muted,
                lineHeight = 22.sp,
            ),
            modifier = Modifier.padding(horizontal = 16.dp),
        )
    }
}

private fun statusStyle(status: String): Triple<Color, String, ImageVector> = when (status) {
    VERIFICATION_STATUS_APPROVED -> Triple(Color(0xFF4CAF50), "Verified", Icons.Filled.Check)
    VERIFICATION_STATUS_REJECTED -> Triple(Color(0xFFE53935), "Rejected", Icons.Filled.Cancel)
    else -> Triple(Color(0xFF2196F3), "Pending Review", Icons.Filled.HourglassEmpty)
}
