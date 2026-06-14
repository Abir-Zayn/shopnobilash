package com.dorent.app.ui.feature.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.dorent.app.ui.feature.auth.components.SignupFormSection
import com.dorent.app.ui.feature.auth.components.SignupHeroSection
import com.dorent.app.ui.theme.appColors

@Composable
fun SignupScreen(onNavigateToHome: () -> Unit, onNavigateToSignIn: () -> Unit) {
    val colors = MaterialTheme.appColors

    Column(
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
            }
            .verticalScroll(rememberScrollState()),
    ) {
        SignupHeroSection()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 24.dp),
        ) {
            SignupFormSection(
                onSignUp           = onNavigateToHome,
                onNavigateToSignIn = onNavigateToSignIn,
            )
            Spacer(Modifier.height(24.dp))
        }
    }
}
