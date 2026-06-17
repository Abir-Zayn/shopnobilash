package com.shopnobilash.app.presentation.owner.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shopnobilash.app.presentation.components.AppSnackbarHost
import com.shopnobilash.app.presentation.components.AppText
import com.shopnobilash.app.presentation.components.RoundIconButton
import com.shopnobilash.app.presentation.components.SnackbarMessage
import com.shopnobilash.app.presentation.components.SnackbarType
import com.shopnobilash.app.presentation.owner.viewmodel.OwnerDashboardViewModel
import com.shopnobilash.app.presentation.owner.viewmodel.OwnerStep
import com.shopnobilash.app.presentation.theme.Accent
import com.shopnobilash.app.presentation.theme.appColors
import org.koin.androidx.compose.koinViewModel

@Composable
fun OwnerDashboardScreen(
    onClose: () -> Unit,
    onReviewProfile: () -> Unit,
    onSessionExpired: () -> Unit,
    onViewListing: (String) -> Unit,
    viewModel: OwnerDashboardViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val colors = MaterialTheme.appColors
    var snackbar by remember { mutableStateOf<SnackbarMessage?>(null) }

    LaunchedEffect(state.sessionExpired) {
        if (state.sessionExpired) onSessionExpired()
    }

    LaunchedEffect(state.error) {
        state.error?.let {
            snackbar = SnackbarMessage(it, SnackbarType.Error)
            viewModel.clearError()
        }
    }

    Scaffold(
        containerColor = colors.bg,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 18.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                AppText(
                    "Owner Dashboard",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = colors.ink,
                        fontWeight = FontWeight.Bold,
                    ),
                )
                RoundIconButton(
                    icon = Icons.Filled.Close,
                    onClick = onClose,
                    contentDescription = "Close",
                )
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            OwnerStepper(
                currentIndex = state.step.index.coerceAtMost(2),
                modifier = Modifier.padding(horizontal = 36.dp, vertical = 12.dp),
            )

            Box(modifier = Modifier.fillMaxSize()) {
                if (state.isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Accent)
                    }
                } else {
                    when (state.step) {
                        OwnerStep.Verify ->
                            VerificationPendingScreen(viewModel = viewModel, onReviewProfile = onReviewProfile)
                        OwnerStep.OwnerInfo ->
                            OwnerOnboardingScreen(state = state, viewModel = viewModel)
                        OwnerStep.PropertyForm ->
                            AddPropertyScreen(state = state, viewModel = viewModel)
                        OwnerStep.Done ->
                            PropertyCreatedScreen(
                                state = state,
                                onViewListing = onViewListing,
                                onAddAnother = { viewModel.addAnother() },
                                onGoToDashboard = onClose,
                            )
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
}

private data class OwnerStepInfo(val line1: String, val line2: String)

private val OWNER_STEPS = listOf(
    OwnerStepInfo("Verify", "Identity"),
    OwnerStepInfo("Owner", "Info"),
    OwnerStepInfo("Add", "Property"),
)

/**
 * Non-interactive numbered stepper. Completed steps show a check, the current step
 * its number, upcoming steps an outlined number. Advances only via ViewModel state.
 */
@Composable
private fun OwnerStepper(currentIndex: Int, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.appColors
    val last = OWNER_STEPS.lastIndex

    Column(modifier = modifier.semantics { stateDescription = "Step ${currentIndex + 1} of 3" }) {
        // Circles joined by connector lines.
        Row(verticalAlignment = Alignment.CenterVertically) {
            OWNER_STEPS.forEachIndexed { i, _ ->
                StepCircle(index = i, currentIndex = currentIndex)
                if (i < last) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(2.dp)
                            .background(if (i < currentIndex) Accent else colors.line),
                    )
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        // Two-line labels aligned under each circle.
        Row(modifier = Modifier.fillMaxWidth()) {
            OWNER_STEPS.forEachIndexed { i, step ->
                val active = i <= currentIndex
                val align = when (i) {
                    0 -> Alignment.Start
                    last -> Alignment.End
                    else -> Alignment.CenterHorizontally
                }
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = align,
                ) {
                    AppText(
                        step.line1,
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = if (active) colors.ink else colors.faint,
                            fontWeight = if (i == currentIndex) FontWeight.Bold else FontWeight.SemiBold,
                            fontSize = 12.sp,
                        ),
                    )
                    AppText(
                        step.line2,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (active) colors.muted else colors.faint,
                            fontSize = 11.sp,
                        ),
                    )
                }
            }
        }
    }
}

@Composable
private fun StepCircle(index: Int, currentIndex: Int) {
    val colors = MaterialTheme.appColors
    val completed = index < currentIndex
    val current = index == currentIndex
    val filled = completed || current

    Box(
        modifier = Modifier
            .size(30.dp)
            .clip(CircleShape)
            .background(if (filled) Accent else Color.Transparent)
            .then(if (filled) Modifier else Modifier.border(1.5.dp, colors.line, CircleShape)),
        contentAlignment = Alignment.Center,
    ) {
        if (completed) {
            Icon(Icons.Filled.Check, null, tint = Color.White, modifier = Modifier.size(16.dp))
        } else {
            AppText(
                "${index + 1}",
                style = MaterialTheme.typography.labelLarge.copy(
                    color = if (current) Color.White else colors.faint,
                    fontWeight = FontWeight.Bold,
                ),
            )
        }
    }
}
