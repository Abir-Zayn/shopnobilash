package com.shopnobilash.app.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.shopnobilash.app.presentation.theme.appColors

@Composable
fun RoundIconButton(
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    active: Boolean = false,
    bgColor: Color = MaterialTheme.colorScheme.surface,
    iconColor: Color = MaterialTheme.appColors.ink2,
    size: Dp = 42.dp,
) {
    val colors = MaterialTheme.appColors
    FilledIconButton(
        onClick = onClick,
        modifier = modifier.size(size),
        shape = RoundedCornerShape(13.dp),
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = if (active) colors.accent else bgColor,
            contentColor = if (active) Color.White else iconColor,
        ),
    ) {
        Icon(imageVector = icon, contentDescription = contentDescription, modifier = Modifier.size(20.dp))
    }
}

@Composable
fun SaveToggleButton(
    saved: Boolean,
    onClick: () -> Unit,
    icon: ImageVector,
    iconFilled: ImageVector,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.appColors
    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(34.dp)
            .clip(RoundedCornerShape(11.dp))
            .background(if (saved) colors.accent else Color.White.copy(alpha = 0.92f)),
    ) {
        Icon(
            imageVector = if (saved) iconFilled else icon,
            contentDescription = if (saved) "Remove from saved" else "Save",
            tint = if (saved) Color.White else colors.ink,
            modifier = Modifier.size(18.dp),
        )
    }
}
