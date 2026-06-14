package com.shopnobilash.app.ui.feature.auth.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.shopnobilash.app.ui.components.AppText
import com.shopnobilash.app.ui.components.AppTextField
import com.shopnobilash.app.ui.components.AppTextFieldSecure
import com.shopnobilash.app.ui.components.PrimaryButton
import com.shopnobilash.app.ui.theme.Primary
import com.shopnobilash.app.ui.theme.PrimaryDeep
import com.shopnobilash.app.ui.theme.appColors

private val HeroHeight = 290.dp
private val CurveDepth = 52.dp

private class SignupOvalShape(private val curveDepthPx: Float) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val path = Path().apply {
            moveTo(0f, 0f)
            lineTo(size.width, 0f)
            lineTo(size.width, size.height)
            // arc counterclockwise: bottom-right → rises to center → bottom-left (concave / scooped)
            arcTo(
                rect = Rect(
                    left   = 0f,
                    top    = size.height - curveDepthPx,
                    right  = size.width,
                    bottom = size.height + curveDepthPx,
                ),
                startAngleDegrees = 0f,
                sweepAngleDegrees = -180f,
                forceMoveTo = false,
            )
            close()
        }
        return Outline.Generic(path)
    }
}

@Composable
fun SignupHeroSection() {
    val density      = LocalDensity.current
    val curveDepthPx = with(density) { CurveDepth.toPx() }
    val heroShape    = remember(curveDepthPx) { SignupOvalShape(curveDepthPx) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(HeroHeight)
            .clip(heroShape)
            .background(Brush.linearGradient(listOf(PrimaryDeep, Primary))),
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 28.dp, top = 32.dp, end = 8.dp, bottom = 72.dp)
                .fillMaxWidth(0.54f),
            verticalArrangement = Arrangement.Center,
        ) {
            AppText(
                text = "Create\nAccount!",
                style = MaterialTheme.typography.headlineMedium.copy(
                    color         = Color.White,
                    fontWeight    = FontWeight.ExtraBold,
                    fontSize      = 32.sp,
                    letterSpacing = (-0.5).sp,
                    lineHeight    = 38.sp,
                ),
            )
            Spacer(Modifier.height(10.dp))
            AppText(
                text = "Fill in your details and start exploring.",
                style = MaterialTheme.typography.bodySmall.copy(
                    color      = Color.White.copy(alpha = 0.82f),
                    fontSize   = 12.5.sp,
                    lineHeight = 18.sp,
                ),
            )
            Spacer(Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White.copy(alpha = 0.18f))
                    .padding(horizontal = 14.dp, vertical = 6.dp),
            ) {
                AppText(
                    text = "Shop · Explore · Save",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color         = Color.White,
                        fontWeight    = FontWeight.SemiBold,
                        letterSpacing = 0.6.sp,
                        fontSize      = 11.sp,
                    ),
                )
            }
        }

        AsyncImage(
            model              = "file:///android_asset/img/login-screen-vector.png",
            contentDescription = null,
            modifier           = Modifier
                .align(Alignment.TopEnd)
                .fillMaxWidth(0.40f)
                .height(HeroHeight - CurveDepth),
            contentScale = ContentScale.Fit,
        )
    }
}

@Composable
fun SignupFormSection(
    isLoading: Boolean = false,
    onSignUp: (name: String, email: String, password: String, confirmPassword: String) -> Unit,
    onNavigateToSignIn: () -> Unit,
) {
    var fullName        by remember { mutableStateOf("") }
    var email           by remember { mutableStateOf("") }
    var password        by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    val colors = MaterialTheme.appColors

    LoginField(label = "Full Name") {
        AppTextField(
            value         = fullName,
            onValueChange = { fullName = it },
            placeholder   = "John Doe",
            leadingIcon   = Icons.Filled.Person,
            keyboardType  = KeyboardType.Text,
        )
    }
    Spacer(Modifier.height(22.dp))

    LoginField(label = "Email Address") {
        AppTextField(
            value         = email,
            onValueChange = { email = it },
            placeholder   = "you@email.com",
            leadingIcon   = Icons.Filled.MailOutline,
            keyboardType  = KeyboardType.Email,
        )
    }
    Spacer(Modifier.height(22.dp))

    LoginField(label = "Password") {
        AppTextFieldSecure(
            value         = password,
            onValueChange = { password = it },
        )
    }
    Spacer(Modifier.height(22.dp))

    LoginField(label = "Confirm Password") {
        AppTextFieldSecure(
            value         = confirmPassword,
            onValueChange = { confirmPassword = it },
            placeholder   = "••••••••",
        )
    }

    Spacer(Modifier.height(28.dp))

    PrimaryButton(
        text      = "Sign Up",
        isLoading = isLoading,
        onClick   = { onSignUp(fullName, email, password, confirmPassword) },
    )
    Spacer(Modifier.height(28.dp))

    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment     = Alignment.CenterVertically,
    ) {
        AppText(
            text  = "Already have an account? ",
            style = MaterialTheme.typography.bodyMedium.copy(color = colors.muted),
        )
        TextButton(onClick = onNavigateToSignIn) {
            AppText(
                text  = "Sign In",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color      = Primary,
                    fontWeight = FontWeight.Bold,
                ),
            )
        }
    }
}
