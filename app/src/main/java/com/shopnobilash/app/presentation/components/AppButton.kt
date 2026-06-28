package com.shopnobilash.app.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.shopnobilash.app.presentation.theme.appColors

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    isLoading: Boolean = false,
    enabled: Boolean = true,
    cornerRadius: Dp = 16.dp,
) {
    val colors = MaterialTheme.appColors
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(cornerRadius),
        colors = ButtonDefaults.buttonColors(containerColor = colors.accent),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
        enabled = enabled && !isLoading,
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(22.dp),
                color = Color.White,
                strokeWidth = 2.5.dp,
            )
        } else {
            if (icon != null) {
                Icon(imageVector = icon, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium.copy(color = Color.White, fontWeight = FontWeight.Bold),
            )
        }
    }
}

@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    cornerRadius: Dp = 16.dp,
) {
    val colors = MaterialTheme.appColors
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(cornerRadius),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.accent),
        border = BorderStroke(1.5.dp, colors.accent),
        enabled = enabled,
    ) {
        if (icon != null) {
            Icon(imageVector = icon, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium.copy(color = colors.accent, fontWeight = FontWeight.Bold),
        )
    }
}
