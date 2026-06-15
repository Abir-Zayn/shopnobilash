package com.shopnobilash.app.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

enum class SnackbarType { Success, Error, Warn }

data class SnackbarMessage(val text: String, val type: SnackbarType)

private val SuccessBg = Color(0xFF1B8A4C)
private val ErrorBg   = Color(0xFFD93025)
private val WarnBg    = Color(0xFFB45309)
private val OnSnackbar = Color.White

private fun bgFor(type: SnackbarType): Color = when (type) {
    SnackbarType.Success -> SuccessBg
    SnackbarType.Error   -> ErrorBg
    SnackbarType.Warn    -> WarnBg
}

private fun iconFor(type: SnackbarType): ImageVector = when (type) {
    SnackbarType.Success -> Icons.Filled.CheckCircle
    SnackbarType.Error   -> Icons.Filled.Info
    SnackbarType.Warn    -> Icons.Filled.Warning
}

@Composable
fun AppSnackbarHost(
    message: SnackbarMessage?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LaunchedEffect(message) {
        if (message != null) {
            delay(1500)
            onDismiss()
        }
    }

    AnimatedVisibility(
        visible = message != null,
        enter   = slideInVertically(initialOffsetY = { it }) + fadeIn(),
        exit    = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
        modifier = modifier,
    ) {
        message ?: return@AnimatedVisibility
        Surface(
            modifier  = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            shape     = RoundedCornerShape(14.dp),
            color     = bgFor(message.type),
            shadowElevation = 6.dp,
        ) {
            Row(
                modifier          = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector        = iconFor(message.type),
                    contentDescription = null,
                    tint               = OnSnackbar,
                    modifier           = Modifier.size(20.dp),
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text       = message.text,
                    color      = OnSnackbar,
                    fontSize   = 14.sp,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 20.sp,
                )
            }
        }
    }
}
