package com.shopnobilash.app.presentation.profile_setup.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
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
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.shopnobilash.app.data.profile.model.IdentityType
import com.shopnobilash.app.presentation.components.AppSnackbarHost
import com.shopnobilash.app.presentation.components.AppTextField
import com.shopnobilash.app.presentation.components.PrimaryButton
import com.shopnobilash.app.presentation.components.SnackbarMessage
import com.shopnobilash.app.presentation.components.SnackbarType
import com.shopnobilash.app.presentation.profile_setup.viewmodel.ProfileSetupUiState
import com.shopnobilash.app.presentation.profile_setup.viewmodel.ProfileSetupViewModel
import com.shopnobilash.app.presentation.theme.Accent
import com.shopnobilash.app.presentation.theme.Primary
import com.shopnobilash.app.presentation.theme.appColors
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSetupScreen(
    onNavigateToHome: () -> Unit,
    onSessionExpired: () -> Unit,
    viewModel: ProfileSetupViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val profilePicUri by viewModel.profilePicUri.collectAsStateWithLifecycle()
    val identityImageUri by viewModel.identityImageUri.collectAsStateWithLifecycle()
    var snackbar by remember { mutableStateOf<SnackbarMessage?>(null) }
    val context = LocalContext.current

    var fullName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var permanentAddress by remember { mutableStateOf("") }
    var emergencyContact by remember { mutableStateOf("") }
    var emergencyContactRecipient by remember { mutableStateOf("") }
    var identityType by remember { mutableStateOf("") }
    var identityNumber by remember { mutableStateOf("") }
    var identityTypeExpanded by remember { mutableStateOf(false) }

    val isLoading = state is ProfileSetupUiState.Loading

    val profilePicLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> uri?.let { viewModel.onProfilePicSelected(it, context) } }

    val identityImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> uri?.let { viewModel.onIdentityImageSelected(it, context) } }

    LaunchedEffect(state) {
        when (val s = state) {
            is ProfileSetupUiState.Saved, ProfileSetupUiState.ProfileExists -> onNavigateToHome()
            is ProfileSetupUiState.Error -> {
                if (s.message.contains("Session expired", ignoreCase = true)) {
                    onSessionExpired()
                } else {
                    snackbar = SnackbarMessage(s.message, SnackbarType.Error)
                    viewModel.clearError()
                }
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Set Up Profile",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.appColors.bg,
                    titleContentColor = MaterialTheme.appColors.ink,
                ),
            )
        },
        containerColor = MaterialTheme.appColors.bg,
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {

                // ── Avatar picker ──────────────────────────────────────────────
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.appColors.card)
                        .border(2.dp, if (profilePicUri != null) Accent else MaterialTheme.appColors.line, CircleShape)
                        .clickable(enabled = !isLoading) { profilePicLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center,
                ) {
                    if (profilePicUri != null) {
                        AsyncImage(
                            model = profilePicUri,
                            contentDescription = "Profile picture",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize().clip(CircleShape),
                        )
                    } else {
                        Icon(
                            Icons.Filled.CameraAlt,
                            contentDescription = "Add photo",
                            tint = MaterialTheme.appColors.muted,
                            modifier = Modifier.size(32.dp),
                        )
                    }
                }
                Text(
                    text = if (profilePicUri != null) "Tap to change photo" else "Add profile photo",
                    style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.appColors.muted),
                )

                // ── Personal Information ───────────────────────────────────────
                SectionLabel("Personal Information")

                AppTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    placeholder = "Full Name",
                    leadingIcon = Icons.Filled.Person,
                )
                AppTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    placeholder = "Phone Number",
                    leadingIcon = Icons.Filled.Phone,
                    keyboardType = KeyboardType.Phone,
                )

                // Read-only email pre-filled from auth
                OutlinedTextField(
                    value = viewModel.prefillEmail,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    placeholder = {
                        Text("Email", color = MaterialTheme.appColors.faint, fontSize = 15.sp)
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Filled.Email, null,
                            tint = MaterialTheme.appColors.muted,
                            modifier = Modifier.size(18.dp),
                        )
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = MaterialTheme.appColors.card.copy(alpha = 0.5f),
                        unfocusedContainerColor = MaterialTheme.appColors.card.copy(alpha = 0.5f),
                        focusedTextColor = MaterialTheme.appColors.muted,
                        unfocusedTextColor = MaterialTheme.appColors.muted,
                    ),
                )

                // Multiline address field
                OutlinedTextField(
                    value = permanentAddress,
                    onValueChange = { permanentAddress = it },
                    modifier = Modifier.fillMaxWidth().height(96.dp),
                    placeholder = {
                        Text("Permanent Address", color = MaterialTheme.appColors.faint, fontSize = 15.sp)
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Filled.Home, null,
                            tint = MaterialTheme.appColors.muted,
                            modifier = Modifier.size(18.dp),
                        )
                    },
                    singleLine = false,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = MaterialTheme.appColors.card,
                        unfocusedContainerColor = MaterialTheme.appColors.card,
                        focusedTextColor = MaterialTheme.appColors.ink,
                        unfocusedTextColor = MaterialTheme.appColors.ink,
                        cursorColor = Primary,
                    ),
                )

                // ── Emergency Contact ──────────────────────────────────────────
                Spacer(Modifier.height(4.dp))
                SectionLabel("Emergency Contact")

                AppTextField(
                    value = emergencyContact,
                    onValueChange = { emergencyContact = it },
                    placeholder = "Emergency Contact Number",
                    leadingIcon = Icons.Filled.Phone,
                    keyboardType = KeyboardType.Phone,
                )
                AppTextField(
                    value = emergencyContactRecipient,
                    onValueChange = { emergencyContactRecipient = it },
                    placeholder = "Contact Person (e.g. Father, Spouse)",
                    leadingIcon = Icons.Filled.Person,
                )

                // ── Identity Verification ──────────────────────────────────────
                Spacer(Modifier.height(4.dp))
                SectionLabel("Identity Verification")

                ExposedDropdownMenuBox(
                    expanded = identityTypeExpanded,
                    onExpandedChange = { identityTypeExpanded = it },
                ) {
                    OutlinedTextField(
                        value = identityType.ifBlank { "Select Identity Type" },
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth().height(56.dp).menuAnchor(),
                        leadingIcon = {
                            Icon(
                                Icons.Filled.Lock, null,
                                tint = MaterialTheme.appColors.muted,
                                modifier = Modifier.size(18.dp),
                            )
                        },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = identityTypeExpanded) },
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            unfocusedBorderColor = Color.Transparent,
                            focusedContainerColor = MaterialTheme.appColors.card,
                            unfocusedContainerColor = MaterialTheme.appColors.card,
                            focusedTextColor = if (identityType.isBlank()) MaterialTheme.appColors.faint
                                              else MaterialTheme.appColors.ink,
                            unfocusedTextColor = if (identityType.isBlank()) MaterialTheme.appColors.faint
                                                else MaterialTheme.appColors.ink,
                        ),
                    )
                    ExposedDropdownMenu(
                        expanded = identityTypeExpanded,
                        onDismissRequest = { identityTypeExpanded = false },
                        modifier = Modifier.background(MaterialTheme.appColors.card),
                    ) {
                        IdentityType.entries.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type.label, color = MaterialTheme.appColors.ink) },
                                onClick = {
                                    identityType = type.label
                                    identityTypeExpanded = false
                                },
                            )
                        }
                    }
                }

                AppTextField(
                    value = identityNumber,
                    onValueChange = { identityNumber = it },
                    placeholder = "Identity Number",
                    leadingIcon = Icons.Filled.Lock,
                    keyboardType = KeyboardType.Number,
                )

                // Identity document image upload
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.appColors.card)
                        .border(
                            width = 1.dp,
                            color = if (identityImageUri != null) Accent else MaterialTheme.appColors.line,
                            shape = RoundedCornerShape(14.dp),
                        )
                        .clickable(enabled = !isLoading) { identityImageLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center,
                ) {
                    if (identityImageUri != null) {
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
                                "Identity document uploaded",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Accent,
                                    fontWeight = FontWeight.SemiBold,
                                ),
                            )
                            Text(
                                "Tap to change",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.appColors.muted,
                                ),
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
                                tint = MaterialTheme.appColors.muted,
                                modifier = Modifier.size(32.dp),
                            )
                            Text(
                                "Upload identity document photo",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.appColors.muted,
                                ),
                            )
                            Text(
                                "JPG or PNG — optional",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.appColors.faint,
                                ),
                            )
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                PrimaryButton(
                    text = "Save & Continue",
                    isLoading = isLoading,
                    onClick = {
                        viewModel.saveProfile(
                            fullName = fullName,
                            phoneNumber = phoneNumber,
                            permanentAddress = permanentAddress,
                            emergencyContact = emergencyContact,
                            emergencyContactRecipient = emergencyContactRecipient,
                            identityType = identityType,
                            identityNumber = identityNumber,
                        )
                    },
                )

                Spacer(Modifier.height(16.dp))
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
private fun SectionLabel(text: String) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelMedium.copy(
            color = MaterialTheme.appColors.muted,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.sp,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
    )
}
