package com.dorent.app.ui.feature.onboarding

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.dorent.app.ui.theme.Accent
import com.dorent.app.ui.theme.appColors

private data class OnboardingSlide(val t1: String, val t2: String, val subtitle: String)

private val SLIDES = listOf(
    OnboardingSlide("Buy, Sell, Rent", "Every Property Easily", "Discover, list, and manage homes, offices, and properties effortlessly today."),
    OnboardingSlide("Find a place", "That feels like home", "Thousands of verified rentals, with honest prices and real photos."),
    OnboardingSlide("Move in", "Without the hassle", "Chat with owners, tour, and book — all from one simple app."),
)

@Composable
fun SplashScreen(onNavigateToLogin: () -> Unit) {
    var slideIndex by remember { mutableStateOf(0) }
    val slide = SLIDES[slideIndex]

    Box(modifier = Modifier.fillMaxSize()) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data("https://picsum.photos/seed/dorent-hero/450/900")
                .crossfade(true)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize().background(Color(0xFF1B2430)),
        )
        // Gradient overlay
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

        // Brand mark
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

        // Copy + CTA
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
            // Dot indicators
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
