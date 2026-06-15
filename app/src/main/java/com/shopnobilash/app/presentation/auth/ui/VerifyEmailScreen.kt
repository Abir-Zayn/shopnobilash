package com.shopnobilash.app.presentation.auth.ui
import com.shopnobilash.app.presentation.auth.viewmodel.AuthViewModel
import com.shopnobilash.app.presentation.auth.viewmodel.AuthUiState

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shopnobilash.app.presentation.components.AppSnackbarHost
import com.shopnobilash.app.presentation.components.AppText
import com.shopnobilash.app.presentation.components.AppTextField
import com.shopnobilash.app.presentation.components.PrimaryButton
import com.shopnobilash.app.presentation.components.SnackbarMessage
import com.shopnobilash.app.presentation.components.SnackbarType
import com.shopnobilash.app.presentation.theme.Primary
import com.shopnobilash.app.presentation.theme.appColors
import org.koin.androidx.compose.koinViewModel

@Composable
fun VerifyEmailScreen(
    userId: String,
    email: String,
    onNavigateToHome: () -> Unit,
) {
    val viewModel: AuthViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val colors = MaterialTheme.appColors

    var code by remember { mutableStateOf("") }
    var snackbar by remember { mutableStateOf<SnackbarMessage?>(null) }

    LaunchedEffect(uiState) {
        when (val s = uiState) {
            is AuthUiState.EmailVerified -> {
                snackbar = SnackbarMessage("Email verified successfully!", SnackbarType.Success)
                onNavigateToHome()
            }
            is AuthUiState.OtpResent -> {
                snackbar = SnackbarMessage("A new code was sent to your email", SnackbarType.Success)
                viewModel.clearError()
            }
            is AuthUiState.Error -> {
                snackbar = SnackbarMessage(s.message, SnackbarType.Error)
                viewModel.clearError()
            }
            else -> {}
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.bg)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Spacer(Modifier.height(80.dp))
            Icon(
                imageVector        = Icons.Filled.MarkEmailRead,
                contentDescription = null,
                modifier           = Modifier.size(80.dp),
                tint               = Primary,
            )
            Spacer(Modifier.height(24.dp))
            AppText(
                text  = "Verify your email",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color      = MaterialTheme.colorScheme.onBackground,
                ),
            )
            Spacer(Modifier.height(12.dp))
            AppText(
                text  = "We sent a 6-digit code to ${email.ifBlank { "your email" }}. Enter it below to activate your account.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color      = colors.muted,
                    textAlign  = TextAlign.Center,
                    lineHeight = 22.sp,
                ),
            )
            Spacer(Modifier.height(32.dp))

            AppTextField(
                value         = code,
                onValueChange = { if (it.length <= 6 && it.all(Char::isDigit)) code = it },
                placeholder   = "Enter 6-digit code",
                leadingIcon   = Icons.Filled.Pin,
                keyboardType  = KeyboardType.Number,
            )
            Spacer(Modifier.height(28.dp))

            PrimaryButton(
                text      = "Verify",
                isLoading = uiState is AuthUiState.Loading,
                enabled   = code.length == 6,
                onClick   = { viewModel.verifyOtp(userId, code) },
                modifier  = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(8.dp))

            TextButton(
                onClick = { viewModel.resendOtp(userId, email) },
                enabled = uiState !is AuthUiState.Loading,
            ) {
                AppText(
                    text  = "Didn't get a code? Resend",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color      = Primary,
                        fontWeight = FontWeight.SemiBold,
                    ),
                )
            }
            TextButton(onClick = onNavigateToHome) {
                AppText(
                    text  = "Skip for now",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color      = colors.muted,
                        fontWeight = FontWeight.Medium,
                    ),
                )
            }
            Spacer(Modifier.height(40.dp))
        }

        AppSnackbarHost(
            message   = snackbar,
            onDismiss = { snackbar = null },
            modifier  = Modifier.align(Alignment.BottomCenter),
        )
    }
}
