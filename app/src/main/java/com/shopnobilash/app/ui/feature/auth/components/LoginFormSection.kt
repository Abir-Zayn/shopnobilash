package com.shopnobilash.app.ui.feature.auth.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.shopnobilash.app.ui.components.AppText
import com.shopnobilash.app.ui.components.AppTextField
import com.shopnobilash.app.ui.components.AppTextFieldSecure
import com.shopnobilash.app.ui.components.PrimaryButton
import com.shopnobilash.app.ui.theme.Primary
import com.shopnobilash.app.ui.theme.appColors

@Composable
fun LoginFormSection(onSignIn: () -> Unit) {
    val colors = MaterialTheme.appColors
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(false) }

    LoginField(label = "Email address") {
        AppTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = "you@email.com",
            leadingIcon = Icons.Filled.MailOutline,
            keyboardType = KeyboardType.Email,
        )
    }
    Spacer(Modifier.height(28.dp))

    LoginField(label = "Password") {
        AppTextFieldSecure(
            value = password,
            onValueChange = { password = it },
        )
    }
    Spacer(Modifier.height(14.dp))

    // Remember me + Forgot password
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { rememberMe = !rememberMe },
        ) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (rememberMe) Primary else colors.field)
                    .then(
                        if (!rememberMe) Modifier.border(1.5.dp, colors.line2, RoundedCornerShape(6.dp))
                        else Modifier,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                if (rememberMe) {
                    Icon(Icons.Filled.Check, null, tint = Color.White, modifier = Modifier.size(13.dp))
                }
            }
            Spacer(Modifier.width(8.dp))
            AppText(
                text = "Remember me",
                style = MaterialTheme.typography.labelMedium.copy(
                    color = colors.ink2,
                    fontWeight = FontWeight.SemiBold,
                ),
            )
        }
        TextButton(onClick = {}) {
            AppText(
                text = "Forgot password?",
                style = MaterialTheme.typography.labelMedium.copy(
                    color = Primary,
                    fontWeight = FontWeight.Bold,
                ),
            )
        }
    }
    Spacer(Modifier.height(22.dp))

    PrimaryButton(text = "Sign In", onClick = onSignIn)
    Spacer(Modifier.height(22.dp))

    // Divider
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.weight(1f).height(1.dp).background(colors.line2))
        AppText(
            text = "or continue with",
            style = MaterialTheme.typography.labelMedium.copy(color = colors.faint),
            modifier = Modifier.padding(horizontal = 14.dp),
        )
        Box(Modifier.weight(1f).height(1.dp).background(colors.line2))
    }
    Spacer(Modifier.height(20.dp))

    // Social buttons — circular icon only, centered
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        listOf(
            "file:///android_asset/img/google.png",
            "file:///android_asset/img/facebook.png",
        ).forEachIndexed { index, imagePath ->
            Button(
                onClick = onSignIn,
                modifier = Modifier.size(56.dp),
                shape = androidx.compose.foundation.shape.CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.card,
                    contentColor = colors.ink,
                ),
                border = BorderStroke(1.5.dp, colors.line),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
            ) {
                AsyncImage(
                    model = imagePath,
                    contentDescription = null,
                    modifier = Modifier.size(26.dp),
                )
            }
            if (index == 0) Spacer(Modifier.width(20.dp))
        }
    }
}
