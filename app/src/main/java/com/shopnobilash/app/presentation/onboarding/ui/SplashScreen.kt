package com.shopnobilash.app.presentation.onboarding.ui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.shopnobilash.app.presentation.auth.viewmodel.AuthUiState
import com.shopnobilash.app.presentation.auth.viewmodel.AuthViewModel
import com.shopnobilash.app.presentation.theme.Accent
import com.shopnobilash.app.presentation.theme.appColors
import org.koin.androidx.compose.koinViewModel

private data class OnboardingSlide(val t1: String, val t2: String, val subtitle: String)

private val SLIDES = listOf(
    OnboardingSlide("Buy, Sell, Rent", "Every Property Easily", "Discover, list, and manage homes, offices, and properties effortlessly today."),
    OnboardingSlide("Find a place", "That feels like home", "Thousands of verified rentals, with honest prices and real photos."),
    OnboardingSlide("Move in", "Without the hassle", "Chat with owners, tour, and book — all from one simple app."),
)

@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToProfileSetup: () -> Unit,
    viewModel: AuthViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var slideIndex by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) { viewModel.checkSession() }

    LaunchedEffect(state) {
        when (state) {
            is AuthUiState.SessionValid -> onNavigateToHome()
            is AuthUiState.SessionValidNoProfile -> onNavigateToProfileSetup()
            else -> {}
        }
    }

    val showCarousel = state is AuthUiState.SessionInvalid

    if (!showCarousel) {
        // Session check in progress — show branded loading screen
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0F141B)),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(11.dp))
                            .background(Accent),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Filled.Home, null, tint = Color.White, modifier = Modifier.size(22.dp))
                    }
                    Spacer(Modifier.width(10.dp))
                    Text(
                        "DORent",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 22.sp,
                        ),
                    )
                }
                Spacer(Modifier.height(32.dp))
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Accent,
                    strokeWidth = 2.dp,
                )
            }
        }
        return
    }

    // Onboarding carousel — shown only when session is invalid (user not logged in)
    val slide = SLIDES[slideIndex]

    Box(modifier = Modifier
        .fillMaxSize()
        .pointerInput(slideIndex) {
            var totalDrag = 0f
            detectHorizontalDragGestures(
                onDragEnd = {
                    when {
                        totalDrag < -50f -> if (slideIndex < SLIDES.lastIndex) slideIndex++ else onNavigateToLogin()
                        totalDrag > 50f -> if (slideIndex > 0) slideIndex--
                    }
                    totalDrag = 0f
                },
                onHorizontalDrag = { _, delta -> totalDrag += delta },
            )
        }
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data("file:///android_asset/img/app_bg.jpg")
                .crossfade(true)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize().background(Color(0xFF1B2430)),
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colorStops = arrayOf(
                            0.0f to Color(0x2E141C26),
                            0.38f to Color(0x0A121820),
                            0.78f to Color(0xB8121820),
                            1.0f to Color(0xEB0F141B),
                        ),
                    ),
                ),
        )

        Row(
            modifier = Modifier.align(Alignment.TopStart).padding(start = 22.dp, top = 60.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(RoundedCornerShape(9.dp))
                    .background(Accent),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Filled.Home, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
            }
            Spacer(Modifier.width(8.dp))
            Text(
                text = "DORent",
                style = MaterialTheme.typography.titleLarge.copy(
                    color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp,
                ),
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(start = 26.dp, end = 26.dp, bottom = 40.dp),
        ) {
            Text(
                text = "${slide.t1}\n${slide.t2}",
                style = MaterialTheme.typography.headlineLarge.copy(
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 30.sp,
                    lineHeight = 36.sp,
                ),
            )
            Spacer(Modifier.height(14.dp))
            Text(
                text = slide.subtitle,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.White.copy(alpha = 0.82f),
                    fontSize = 14.5.sp,
                    lineHeight = 22.sp,
                ),
            )
            Spacer(Modifier.height(22.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                SLIDES.indices.forEach { k ->
                    val active = k == slideIndex
                    val w by animateDpAsState(if (active) 26.dp else 6.dp, label = "dot")
                    Box(
                        modifier = Modifier
                            .width(w)
                            .height(6.dp)
                            .clip(CircleShape)
                            .background(if (active) Color.White else Color.White.copy(alpha = 0.45f))
                            .clickable { slideIndex = k },
                    )
                }
            }
            Spacer(Modifier.height(26.dp))
            Button(
                onClick = { if (slideIndex < SLIDES.lastIndex) slideIndex++ else onNavigateToLogin() },
                modifier = Modifier.fillMaxWidth().height(60.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 14.dp),
            ) {
                Text(
                    text = if (slideIndex < SLIDES.lastIndex) "Next" else "Get Started",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = Color(0xFF16191C), fontWeight = FontWeight.Bold, fontSize = 16.5.sp,
                    ),
                )
                Spacer(Modifier.width(8.dp))
                Icon(Icons.Filled.ArrowForward, null, tint = Color(0xFF16191C), modifier = Modifier.size(18.dp))
            }
            Spacer(Modifier.height(14.dp))
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(
                    text = "Skip for now",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.White.copy(alpha = 0.78f), fontWeight = FontWeight.SemiBold,
                    ),
                    modifier = Modifier.clickable { onNavigateToLogin() },
                )
            }
        }
    }
}
