package com.shopnobilash.app.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shopnobilash.app.presentation.theme.appColors

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
fun StackHeader(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    actions: @Composable () -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
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
    AppAvatar(imageUrl = null, name = name, size = size)
}
