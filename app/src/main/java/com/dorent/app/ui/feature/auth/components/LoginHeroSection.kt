package com.dorent.app.ui.feature.auth.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.dorent.app.ui.components.AppText
import com.dorent.app.ui.theme.Primary
import com.dorent.app.ui.theme.PrimaryDeep

private val HeroHeight = 290.dp
private val CurveDepth = 52.dp

private class HalfOvalBottomShape(private val curveDepthPx: Float) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val path = Path().apply {
            moveTo(0f, 0f)
            lineTo(size.width, 0f)
            lineTo(size.width, size.height - curveDepthPx)
            arcTo(
                rect = Rect(
                    left   = 0f,
                    top    = size.height - curveDepthPx * 2f,
                    right  = size.width,
                    bottom = size.height,
                ),
                startAngleDegrees = 0f,
                sweepAngleDegrees = 180f,
                forceMoveTo = false,
            )
            close()
        }
        return Outline.Generic(path)
    }
}

@Composable
fun LoginHeroSection() {
    val density      = LocalDensity.current
    val curveDepthPx = with(density) { CurveDepth.toPx() }
    val heroShape    = remember(curveDepthPx) { HalfOvalBottomShape(curveDepthPx) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(HeroHeight)
            .clip(heroShape)
            .background(Brush.linearGradient(listOf(PrimaryDeep, Primary))),
    ) {
        // Left: welcome copy — padded to stay above the curve
        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 28.dp, top = 32.dp, end = 8.dp, bottom = CurveDepth + 8.dp)
                .fillMaxWidth(0.54f),
            verticalArrangement = Arrangement.Center,
        ) {
            AppText(
                text = "Welcome\nBack!",
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
                text = "Sign in and pick up right where you left off.",
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

        // Right: vector — reduced size, stays above curve
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
