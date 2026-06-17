package com.shopnobilash.app.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shopnobilash.app.presentation.theme.appColors

@Composable
fun AppTile(
    icon: ImageVector,
    iconBg: Color,
    text: String,
    modifier: Modifier = Modifier,
    iconContainerSize: Dp = 36.dp,
    appTextSize: TextUnit = 14.5.sp,
    hasNotification: Boolean = false,
    notificationContainerSize: Dp = 20.dp,
    notificationContainerBg: Color = Color.Unspecified,
    notificationNumber: String? = null,
    notificationNumberColor: Color = Color.Unspecified,
    onClick: () -> Unit = {},
    trailing: @Composable (() -> Unit)? = {
        val colors = MaterialTheme.appColors
        Icon(Icons.Filled.ChevronRight, null, tint = colors.faint, modifier = Modifier.size(18.dp))
    },
) {
    val colors = MaterialTheme.appColors

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(13.dp),
    ) {
        // Icon container
        Box(
            modifier = Modifier
                .size(iconContainerSize)
                .clip(RoundedCornerShape(10.dp))
                .background(iconBg),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, null, tint = Color.White, modifier = Modifier.size(iconContainerSize * 0.53f))
        }

        // Label
        AppText(
            text = text,
            modifier = Modifier.weight(1f),
            color = colors.ink,
            fontSize = appTextSize,
            fontWeight = FontWeight.SemiBold,
        )

        // Notification badge
        if (hasNotification && notificationNumber != null) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(
                        if (notificationContainerBg == Color.Unspecified)
                            colors.accentSoft
                        else notificationContainerBg,
                    )
                    .padding(horizontal = 6.dp, vertical = 2.dp)
                    .then(
                        if (notificationContainerSize > 0.dp)
                            Modifier.size(notificationContainerSize)
                        else Modifier
                    ),
                contentAlignment = Alignment.Center,
            ) {
                AppText(
                    text = notificationNumber,
                    color = if (notificationNumberColor == Color.Unspecified)
                        colors.accentDeep
                    else notificationNumberColor,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }

        // Trailing
        trailing?.invoke()
    }
}

@Composable
fun AppTileDivider() {
    val colors = MaterialTheme.appColors
    Box(
        Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(colors.line)
            .padding(start = 63.dp),
    )
}
