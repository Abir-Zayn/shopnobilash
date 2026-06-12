package com.dorent.app.ui.feature.onboarding

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dorent.app.ui.components.PrimaryButton
import com.dorent.app.ui.theme.Accent
import com.dorent.app.ui.theme.appColors

@Composable
fun LoginScreen(onNavigateToHome: () -> Unit) {
    val colors = MaterialTheme.appColors
    var email by remember { mutableStateOf("emma@dorent.com") }
    var password by remember { mutableStateOf("password") }
    var showPassword by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.card)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 24.dp),
    ) {
        Spacer(Modifier.height(32.dp))
        // Logo
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Accent),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Filled.Home, null, tint = Color.White, modifier = Modifier.size(28.dp))
        }
        Spacer(Modifier.height(26.dp))
        Text(
            text = "Welcome back",
            style = MaterialTheme.typography.headlineLarge.copy(
                color = colors.ink, fontWeight = FontWeight.ExtraBold, fontSize = 27.sp, letterSpacing = (-0.4).sp,
            ),
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Sign in to keep finding your next home.",
            style = MaterialTheme.typography.bodyMedium.copy(color = colors.muted, fontSize = 14.5.sp),
        )
        Spacer(Modifier.height(28.dp))

        // Email field
        LabelledField(label = "Email address") {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                placeholder = { Text("you@email.com", style = MaterialTheme.typography.bodyMedium.copy(color = colors.faint)) },
                leadingIcon = { Icon(Icons.Filled.MailOutline, null, tint = colors.muted, modifier = Modifier.size(20.dp)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                shape = RoundedCornerShape(15.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Accent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = colors.field,
                    unfocusedContainerColor = colors.field,
                ),
            )
        }
        Spacer(Modifier.height(18.dp))

        // Password field
        LabelledField(label = "Password") {
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                placeholder = { Text("••••••••", style = MaterialTheme.typography.bodyMedium.copy(color = colors.faint)) },
                leadingIcon = { Icon(Icons.Filled.Lock, null, tint = colors.muted, modifier = Modifier.size(20.dp)) },
                trailingIcon = {
                    IconButton(onClick = { showPassword = !showPassword }) {
                        Icon(
                            if (showPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            null, tint = colors.muted, modifier = Modifier.size(20.dp),
                        )
                    }
                },
                singleLine = true,
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                shape = RoundedCornerShape(15.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Accent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = colors.field,
                    unfocusedContainerColor = colors.field,
                ),
            )
        }
        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier,
            ) {
                Box(
                    modifier = Modifier
                        .size(19.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (rememberMe) Accent else colors.field)
                        .then(if (!rememberMe) Modifier.border(1.dp, colors.line2, RoundedCornerShape(6.dp)) else Modifier)
                        .align(Alignment.CenterVertically),
                    contentAlignment = Alignment.Center,
                ) {
                    if (rememberMe) {
                        Icon(Icons.Filled.Check, null, tint = Color.White, modifier = Modifier.size(13.dp))
                    }
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Remember me",
                    style = MaterialTheme.typography.labelMedium.copy(color = colors.ink2, fontWeight = FontWeight.SemiBold),
                )
            }
            TextButton(onClick = {}) {
                Text(
                    text = "Forgot password?",
                    style = MaterialTheme.typography.labelMedium.copy(color = Accent, fontWeight = FontWeight.Bold),
                )
            }
        }
        Spacer(Modifier.height(24.dp))
        PrimaryButton(text = "Sign In", onClick = onNavigateToHome)
        Spacer(Modifier.height(24.dp))

        // Divider
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.weight(1f).height(1.dp).background(colors.line2))
            Text(
                text = "or continue with",
                style = MaterialTheme.typography.labelMedium.copy(color = colors.faint),
                modifier = Modifier.padding(horizontal = 14.dp),
            )
            Box(Modifier.weight(1f).height(1.dp).background(colors.line2))
        }
        Spacer(Modifier.height(24.dp))

        // Social buttons
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            listOf(
                Pair("Google", Icons.Filled.Language),
                Pair("Apple", Icons.Filled.PhoneAndroid),
            ).forEach { (label, icon) ->
                Button(
                    onClick = onNavigateToHome,
                    modifier = Modifier.weight(1f).height(54.dp),
                    shape = RoundedCornerShape(15.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.card,
                        contentColor = colors.ink,
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, colors.line2),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
                ) {
                    Icon(icon, null, modifier = Modifier.size(21.dp))
                    Spacer(Modifier.width(9.dp))
                    Text(label, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                }
            }
        }
        Spacer(Modifier.height(26.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Don't have an account? ",
                style = MaterialTheme.typography.bodyMedium.copy(color = colors.muted),
            )
            TextButton(onClick = onNavigateToHome) {
                Text(
                    text = "Sign up",
                    style = MaterialTheme.typography.bodyMedium.copy(color = Accent, fontWeight = FontWeight.Bold),
                )
            }
        }
    }
}

@Composable
private fun LabelledField(label: String, content: @Composable () -> Unit) {
    val colors = MaterialTheme.appColors
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(color = colors.ink2, fontWeight = FontWeight.SemiBold),
            modifier = Modifier.padding(bottom = 8.dp),
        )
        content()
    }
}
