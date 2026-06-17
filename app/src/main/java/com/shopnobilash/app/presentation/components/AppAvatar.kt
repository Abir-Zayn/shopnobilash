package com.shopnobilash.app.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest

enum class AvatarShape { Circle, Rectangle }

@Composable
fun AppAvatar(
    imageUrl: String?,
    name: String,
    modifier: Modifier = Modifier,
    size: Dp? = 48.dp,
    width: Dp? = null,
    height: Dp? = null,
    shape: AvatarShape = AvatarShape.Circle,
    cornerRadius: Dp = 0.dp,
    border: Boolean = false,
    borderColor: Color = Color.White,
    borderWidth: Dp = 2.dp,
) {
    val resolvedWidth = size ?: width ?: 48.dp
    val resolvedHeight = size ?: height ?: 48.dp

    val resolvedShape: Shape = when (shape) {
        AvatarShape.Circle -> CircleShape
        AvatarShape.Rectangle -> RoundedCornerShape(cornerRadius)
    }

    val borderModifier = if (border) {
        Modifier.border(borderWidth, borderColor, resolvedShape)
    } else {
        Modifier
    }

    if (!imageUrl.isNullOrBlank()) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUrl)
                .crossfade(true)
                .build(),
            contentDescription = "$name avatar",
            contentScale = ContentScale.Crop,
            modifier = modifier
                .then(borderModifier)
                .clip(resolvedShape)
                .size(width = resolvedWidth, height = resolvedHeight),
        )
    } else {
        val palette = listOf(
            Color(0xFF1FAE84), Color(0xFF2F6BE3), Color(0xFF7C5CFC),
            Color(0xFFE27A38), Color(0xFFEA5A8B), Color(0xFF2BA8A1), Color(0xFFE0A52B),
        )
        val hash = name.fold(0L) { acc, c -> (acc * 31 + c.code) and 0xFFFFFFFFL }
        val bg = palette[(hash % palette.size).toInt()]
        val initials = name.split(" ").filter { it.isNotBlank() }.take(2).joinToString("") { it[0].uppercase() }

        Box(
            modifier = modifier
                .then(borderModifier)
                .clip(resolvedShape)
                .size(width = resolvedWidth, height = resolvedHeight)
                .background(bg),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = initials,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = (resolvedWidth.value * 0.38f).sp,
                ),
            )
        }
    }
}
