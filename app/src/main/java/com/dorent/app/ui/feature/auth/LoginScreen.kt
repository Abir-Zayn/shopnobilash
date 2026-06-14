package com.dorent.app.ui.feature.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dorent.app.ui.components.AppText
import com.dorent.app.ui.feature.auth.components.LoginFormSection
import com.dorent.app.ui.feature.auth.components.LoginHeroSection
import com.dorent.app.ui.theme.Primary
import com.dorent.app.ui.theme.appColors

@Composable
fun LoginScreen(onNavigateToHome: () -> Unit) {
    val colors = MaterialTheme.appColors

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.bg),
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
                LoginFormSection(onSignIn = onNavigateToHome)

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
                    TextButton(onClick = onNavigateToHome) {
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
    }
}
