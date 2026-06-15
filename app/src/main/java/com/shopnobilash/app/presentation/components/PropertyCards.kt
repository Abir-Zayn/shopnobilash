package com.shopnobilash.app.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Bathtub
import androidx.compose.material.icons.filled.KingBed
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.SquareFoot
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.shopnobilash.app.data.property.model.Property
import com.shopnobilash.app.presentation.theme.appColors

@Composable
fun PropertyCardVertical(
    property: Property,
    isSaved: Boolean,
    onOpen: () -> Unit,
    onSaveToggle: () -> Unit,
    modifier: Modifier = Modifier,
    width: Int = 200,
) {
    val colors = MaterialTheme.appColors
    Box(
        modifier = modifier
            .width(width.dp)
            .shadow(4.dp, RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .background(colors.card)
            .clickable(onClick = onOpen),
    ) {
        Column {
            Box(modifier = Modifier.padding(8.dp)) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(property.imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = property.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(132.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(colors.field),
                )
                Box(modifier = Modifier.align(Alignment.TopEnd).padding(6.dp)) {
                    SaveToggleButton(
                        saved = isSaved, onClick = onSaveToggle,
                        icon = Icons.Outlined.BookmarkBorder,
                        iconFilled = Icons.Filled.Bookmark,
                    )
                }
            }
            Column(modifier = Modifier.padding(start = 12.dp, end = 12.dp, bottom = 14.dp, top = 4.dp)) {
                AppTag(text = property.type)
                Spacer(Modifier.height(8.dp))
                Text(
                    text = property.title,
                    style = MaterialTheme.typography.titleMedium.copy(color = colors.ink, fontWeight = FontWeight.Bold),
                    maxLines = 1,
                )
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.LocationOn, null, tint = colors.muted, modifier = Modifier.size(14.dp))
                    Text(
                        text = property.city,
                        style = MaterialTheme.typography.bodySmall.copy(color = colors.muted, fontWeight = FontWeight.Medium),
                        modifier = Modifier.padding(start = 4.dp),
                    )
                }
                Spacer(Modifier.height(10.dp))
                PriceText(price = property.price, period = property.period, size = 16.dp)
            }
        }
    }
}

@Composable
fun PropertyCardHorizontal(
    property: Property,
    isSaved: Boolean,
    onOpen: () -> Unit,
    onSaveToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.appColors
    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .background(colors.card)
            .clickable(onClick = onOpen),
    ) {
        Column {
            Box(modifier = Modifier.padding(8.dp)) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(property.imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = property.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(168.dp)
                        .clip(RoundedCornerShape(15.dp))
                        .background(colors.field),
                )
                Box(modifier = Modifier.align(Alignment.TopEnd).padding(6.dp)) {
                    SaveToggleButton(
                        saved = isSaved, onClick = onSaveToggle,
                        icon = Icons.Outlined.BookmarkBorder,
                        iconFilled = Icons.Filled.Bookmark,
                    )
                }
            }
            Column(modifier = Modifier.padding(start = 14.dp, end = 14.dp, bottom = 16.dp, top = 4.dp)) {
                AppTag(text = property.type)
                Spacer(Modifier.height(8.dp))
                PriceText(price = property.price, period = "month", size = 18.dp)
                Spacer(Modifier.height(8.dp))
                Text(
                    text = property.title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = colors.ink, fontWeight = FontWeight.Bold, fontSize = 16.sp,
                    ),
                )
                Spacer(Modifier.height(3.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.LocationOn, null, tint = colors.muted, modifier = Modifier.size(14.dp))
                    Text(
                        text = property.address,
                        style = MaterialTheme.typography.bodySmall.copy(color = colors.muted),
                        maxLines = 1,
                        modifier = Modifier.padding(start = 4.dp),
                    )
                }
                Spacer(Modifier.height(12.dp))
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(colors.line))
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    MetaItem(icon = Icons.Filled.KingBed, label = "${property.beds} bed")
                    MetaItem(icon = Icons.Filled.Bathtub, label = "${property.baths} bath")
                    MetaItem(icon = Icons.Filled.SquareFoot, label = "${property.sqft}sqft")
                }
            }
        }
    }
}

@Composable
private fun MetaItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String) {
    val colors = MaterialTheme.appColors
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
        Icon(icon, null, tint = colors.muted, modifier = Modifier.size(16.dp))
        Text(label, style = MaterialTheme.typography.bodySmall.copy(color = colors.muted, fontWeight = FontWeight.SemiBold))
    }
}
