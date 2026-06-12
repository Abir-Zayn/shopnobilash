package com.dorent.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dorent.app.ui.theme.appColors

@Composable
fun AppTag(text: String, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.appColors
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(7.dp))
            .background(colors.tagSoft)
            .padding(horizontal = 9.dp, vertical = 3.dp),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall.copy(
                color = colors.tag,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
            ),
        )
    }
}

@Composable
fun PriceText(price: Int, period: String, size: Dp = 17.dp) {
    val colors = MaterialTheme.appColors
    Text(
        text = buildAnnotatedString {
            withStyle(SpanStyle(color = colors.accent, fontWeight = FontWeight.ExtraBold, fontSize = size.value.sp)) {
                append("\$${"%,d".format(price)}")
            }
            withStyle(SpanStyle(color = colors.muted, fontWeight = FontWeight.SemiBold, fontSize = (size.value * 0.66f).sp)) {
                append("/$period")
            }
        },
    )
}

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
) {
    val colors = MaterialTheme.appColors
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = colors.accent),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
    ) {
        if (icon != null) {
            Icon(imageVector = icon, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium.copy(color = Color.White, fontWeight = FontWeight.Bold),
        )
    }
}

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
fun StackHeader(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    actions: @Composable () -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        RoundIconButton(
            icon = Icons.AutoMirrored.Filled.ArrowBack,
            onClick = onBack,
            contentDescription = "Back",
        )
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(
                color = MaterialTheme.appColors.ink,
                fontWeight = FontWeight.Bold,
            ),
        )
        Box(modifier = Modifier.width(42.dp), contentAlignment = Alignment.Center) {
            actions()
        }
    }
}

@Composable
fun InitialsAvatar(name: String, size: Dp = 48.dp) {
    val palette = listOf(
        Color(0xFF1FAE84), Color(0xFF2F6BE3), Color(0xFF7C5CFC),
        Color(0xFFE27A38), Color(0xFFEA5A8B), Color(0xFF2BA8A1), Color(0xFFE0A52B),
    )
    val hash = name.fold(0L) { acc, c -> (acc * 31 + c.code) and 0xFFFFFFFFL }
    val bg = palette[(hash % palette.size).toInt()]
    val initials = name.split(" ").filter { it.isNotBlank() }.take(2).joinToString("") { it[0].uppercase() }

    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(bg),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = initials,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = (size.value * 0.38f).sp,
            ),
        )
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
