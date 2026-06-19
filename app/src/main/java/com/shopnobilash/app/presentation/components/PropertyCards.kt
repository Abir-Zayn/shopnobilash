package com.shopnobilash.app.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.style.TextOverflow
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
    width: Int = 260,
) {
    val colors = MaterialTheme.appColors
    Box(
        modifier = modifier
            .width(width.dp)
            .shadow(4.dp, RoundedCornerShape(24.dp))
            .clip(RoundedCornerShape(24.dp))
            .background(colors.card)
            .clickable(onClick = onOpen),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(colors.field),
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(property.imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = property.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
                // Translucent price tag overlaid at top-left of image
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp)
                        .background(
                            color = Color.White.copy(alpha = 0.25f),
                            shape = RoundedCornerShape(50)
                        )
                        .border(
                            width = 1.dp,
                            color = Color.White.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(50)
                        )
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                ) {
                    Text(
                        text = "৳ ${"%,d".format(property.price)}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = colors.ink,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp,
                        ),
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = property.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = colors.ink,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                Spacer(Modifier.width(8.dp))
                Icon(
                    imageVector = if (isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                    contentDescription = if (isSaved) "Remove from saved" else "Save",
                    tint = if (isSaved) colors.accent else colors.muted,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable(onClick = onSaveToggle),
                )
            }
            Spacer(Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(
                    imageVector = Icons.Filled.LocationOn,
                    contentDescription = null,
                    tint = colors.muted,
                    modifier = Modifier.size(16.dp),
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = property.address,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = colors.muted,
                        fontWeight = FontWeight.Normal,
                        fontSize = 13.5.sp,
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
            }
            Spacer(Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Icon(
                        imageVector = Icons.Filled.KingBed,
                        contentDescription = null,
                        tint = colors.muted,
                        modifier = Modifier.size(16.dp),
                    )
                    Text(
                        text = "${property.beds} Bed${if (property.beds > 1) "rooms" else "room"}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = colors.muted,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.5.sp,
                        ),
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Icon(
                        imageVector = Icons.Filled.Bathtub,
                        contentDescription = null,
                        tint = colors.muted,
                        modifier = Modifier.size(16.dp),
                    )
                    Text(
                        text = "${property.baths} Bath${if (property.baths > 1) "s" else ""}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = colors.muted,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.5.sp,
                        ),
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Icon(
                        imageVector = Icons.Filled.SquareFoot,
                        contentDescription = null,
                        tint = colors.muted,
                        modifier = Modifier.size(16.dp),
                    )
                    Text(
                        text = "${"%,d".format(property.sqft)} sqft",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = colors.muted,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.5.sp,
                        ),
                    )
                }
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
            .shadow(4.dp, RoundedCornerShape(24.dp))
            .clip(RoundedCornerShape(24.dp))
            .background(colors.card)
            .clickable(onClick = onOpen),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(colors.field),
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(property.imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = property.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
                // Translucent price tag overlaid at top-left of image
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp)
                        .background(
                            color = Color.White.copy(alpha = 0.45f),
                            shape = RoundedCornerShape(50)
                        )
                        .border(
                            width = 1.dp,
                            color = Color.White.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(50)
                        )
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                ) {
                    Text(
                        text = "৳ ${"%,d".format(property.price)}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = colors.ink,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp,
                        ),
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = property.title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = colors.ink,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                Spacer(Modifier.width(8.dp))
                Icon(
                    imageVector = if (isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                    contentDescription = if (isSaved) "Remove from saved" else "Save",
                    tint = if (isSaved) colors.accent else colors.muted,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable(onClick = onSaveToggle),
                )
            }
            Spacer(Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(
                    imageVector = Icons.Filled.LocationOn,
                    contentDescription = null,
                    tint = colors.muted,
                    modifier = Modifier.size(16.dp),
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = property.address,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = colors.muted,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
            }
            Spacer(Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Icon(
                        imageVector = Icons.Filled.KingBed,
                        contentDescription = null,
                        tint = colors.muted,
                        modifier = Modifier.size(16.dp),
                    )
                    Text(
                        text = "${property.beds} Bed${if (property.beds > 1) "rooms" else "room"}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = colors.muted,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                        ),
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Icon(
                        imageVector = Icons.Filled.Bathtub,
                        contentDescription = null,
                        tint = colors.muted,
                        modifier = Modifier.size(16.dp),
                    )
                    Text(
                        text = "${property.baths} Bath${if (property.baths > 1) "s" else ""}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = colors.muted,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                        ),
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Icon(
                        imageVector = Icons.Filled.SquareFoot,
                        contentDescription = null,
                        tint = colors.muted,
                        modifier = Modifier.size(16.dp),
                    )
                    Text(
                        text = "${"%,d".format(property.sqft)} sqft",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = colors.muted,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                        ),
                    )
                }
            }
        }
    }
}
