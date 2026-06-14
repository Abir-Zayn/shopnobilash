package com.shopnobilash.app.ui.feature.auth

import android.content.Context
import android.content.ContextWrapper
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.shopnobilash.app.ui.components.AppSnackbarHost
import com.shopnobilash.app.ui.components.AppText
import com.shopnobilash.app.ui.components.SnackbarMessage
import com.shopnobilash.app.ui.components.SnackbarType
import com.shopnobilash.app.ui.feature.auth.components.LoginFormSection
import com.shopnobilash.app.ui.feature.auth.components.LoginHeroSection
import com.shopnobilash.app.ui.theme.Primary
import com.shopnobilash.app.ui.theme.appColors
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginScreen(onNavigateToHome: () -> Unit, onNavigateToRegister: () -> Unit) {
    val viewModel: AuthViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val colors = MaterialTheme.appColors

    val context = LocalContext.current
    val activity = remember(context) { context.findActivity() }

    var snackbar by remember { mutableStateOf<SnackbarMessage?>(null) }

    LaunchedEffect(uiState) {
        when (val s = uiState) {
            is AuthUiState.LoginSuccess -> {
                snackbar = SnackbarMessage("Login successful", SnackbarType.Success)
                kotlinx.coroutines.delay(1000)
                onNavigateToHome()
            }
            is AuthUiState.Error -> {
                snackbar = SnackbarMessage("Failed to login", SnackbarType.Error)
                viewModel.clearError()
            }
            else -> {}
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.bg)
            .pointerInput(Unit) {
                val edgeZonePx = 48.dp.toPx()
                val thresholdPx = 60.dp.toPx()
                var dragTotal = 0f
                var edgeSwipe = false
                detectHorizontalDragGestures(
                    onDragStart  = { offset ->
                        dragTotal = 0f
                        edgeSwipe = offset.x > size.width - edgeZonePx
                    },
                    onDragCancel = { dragTotal = 0f; edgeSwipe = false },
                    onDragEnd    = {
                        if (edgeSwipe && kotlin.math.abs(dragTotal) > thresholdPx) onNavigateToRegister()
                        dragTotal = 0f; edgeSwipe = false
                    },
                    onHorizontalDrag = { change, amount ->
                        change.consume()
                        dragTotal += amount
                    },
                )
            },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            LoginHeroSection()

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 24.dp),
            ) {
                LoginFormSection(
                    isLoading = uiState is AuthUiState.Loading,
                    onSignIn = { email, password -> viewModel.login(email, password) },
                    onGoogleSignIn = {
                        activity?.let { viewModel.loginWithGoogle(it) }
                    },
                )

                Spacer(Modifier.height(36.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    AppText(
                        text = "Don't have an account? ",
                        style = MaterialTheme.typography.bodyMedium.copy(color = colors.muted),
                    )
                    TextButton(onClick = onNavigateToRegister) {
                        AppText(
                            text = "Sign up",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Primary,
                                fontWeight = FontWeight.Bold,
                            ),
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))
            }
        }

        AppSnackbarHost(
            message   = snackbar,
            onDismiss = { snackbar = null },
            modifier  = Modifier.align(Alignment.BottomCenter),
        )
    }
}

private tailrec fun Context.findActivity(): ComponentActivity? {
    if (this is ComponentActivity) return this
    if (this is ContextWrapper) return baseContext.findActivity()
    return null
}
