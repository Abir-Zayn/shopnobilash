package com.shopnobilash.app.presentation.wishlist.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KingBed
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
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
fun WishlistCard(
    property: Property,
    onOpen: () -> Unit,
    onDeleteClick: () -> Unit,
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Property Image (rounded corners)
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(14.dp))
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
            }

            Spacer(Modifier.width(14.dp))

            // Details Column
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = property.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = colors.ink,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(4.dp))
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
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "৳ ${"%,d".format(property.price)} / mo",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = colors.accent,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp,
                    ),
                )
            }

            Spacer(Modifier.width(8.dp))

            // Delete Button
            IconButton(
                onClick = onDeleteClick,
                modifier = Modifier.size(40.dp),
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Delete from wishlist",
                    tint = colors.danger,
                    modifier = Modifier.size(24.dp),
                )
            }
        }
    }
}
