package com.shopnobilash.app.ui.feature.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import com.shopnobilash.app.ui.components.AppSnackbarHost
import com.shopnobilash.app.ui.components.SnackbarMessage
import com.shopnobilash.app.ui.components.SnackbarType
import com.shopnobilash.app.ui.feature.auth.components.SignupFormSection
import com.shopnobilash.app.ui.feature.auth.components.SignupHeroSection
import com.shopnobilash.app.ui.theme.appColors
import org.koin.androidx.compose.koinViewModel

@Composable
fun SignupScreen(
    onNavigateToVerify: (userId: String, email: String) -> Unit,
    onNavigateToSignIn: () -> Unit,
) {
    val viewModel: AuthViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val colors = MaterialTheme.appColors

    var snackbar by remember { mutableStateOf<SnackbarMessage?>(null) }

    LaunchedEffect(uiState) {
        when (val s = uiState) {
            is AuthUiState.OtpSent -> onNavigateToVerify(s.userId, s.email)
            is AuthUiState.Error   -> {
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
                .pointerInput(Unit) {
                    val edgeZonePx  = 48.dp.toPx()
                    val thresholdPx = 60.dp.toPx()
                    var dragTotal = 0f
                    var edgeSwipe = false
                    detectHorizontalDragGestures(
                        onDragStart  = { offset ->
                            dragTotal = 0f
                            edgeSwipe = offset.x < edgeZonePx
                        },
                        onDragCancel = { dragTotal = 0f; edgeSwipe = false },
                        onDragEnd    = {
                            if (edgeSwipe && kotlin.math.abs(dragTotal) > thresholdPx) onNavigateToSignIn()
                            dragTotal = 0f; edgeSwipe = false
                        },
                        onHorizontalDrag = { change, amount ->
                            change.consume()
                            dragTotal += amount
                        },
                    )
                },
        ) {
            SignupHeroSection()

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 24.dp),
            ) {
                SignupFormSection(
                    isLoading          = uiState is AuthUiState.Loading,
                    onSignUp           = { name, email, password, confirmPassword ->
                        viewModel.signUp(name, email, password, confirmPassword)
                    },
                    onNavigateToSignIn = onNavigateToSignIn,
                )
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
