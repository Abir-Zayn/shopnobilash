package com.shopnobilash.app.ui.feature.detail

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bathtub
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.KingBed
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.SquareFoot
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.shopnobilash.app.data.model.Property
import com.shopnobilash.app.ui.components.AppTag
import com.shopnobilash.app.ui.components.InitialsAvatar
import com.shopnobilash.app.ui.components.PriceText
import com.shopnobilash.app.ui.components.PrimaryButton
import com.shopnobilash.app.ui.components.RoundIconButton
import com.shopnobilash.app.ui.components.SaveToggleButton
import com.shopnobilash.app.ui.theme.Accent
import com.shopnobilash.app.ui.theme.AccentSoft
import com.shopnobilash.app.ui.theme.appColors
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun DetailScreen(
    propertyId: String,
    onBack: () -> Unit,
    onNavigateToCheckout: () -> Unit,
    onNavigateToChatThread: () -> Unit,
    viewModel: DetailViewModel = koinViewModel { parametersOf(propertyId) },
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val savedIds by viewModel.savedIds.collectAsStateWithLifecycle()
    val colors = MaterialTheme.appColors

    when (val state = uiState) {
        is DetailUiState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Accent)
        }
        is DetailUiState.Error -> Text(state.message, modifier = Modifier.padding(18.dp), color = colors.danger)
        is DetailUiState.Success -> DetailContent(
            property = state.property,
            isSaved = state.property.id in savedIds,
            onBack = onBack,
            onSaveToggle = { viewModel.toggleSave(state.property.id) },
            onCheckout = onNavigateToCheckout,
            onChatThread = onNavigateToChatThread,
        )
    }
}

@Composable
private fun DetailContent(
    property: Property,
    isSaved: Boolean,
    onBack: () -> Unit,
    onSaveToggle: () -> Unit,
    onCheckout: () -> Unit,
    onChatThread: () -> Unit,
) {
    val colors = MaterialTheme.appColors
    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = colors.card,
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colors.card)
                    .padding(horizontal = 18.dp, vertical = 14.dp)
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Column {
                    Text("Total price", style = MaterialTheme.typography.bodySmall.copy(color = colors.muted))
                    PriceText(price = property.price, period = "month", size = 21.dp)
                }
                PrimaryButton(text = "Book Now", onClick = onCheckout, modifier = Modifier.weight(1f))
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
        ) {
            // Hero image
            Box {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(property.imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = property.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth().height(310.dp).background(colors.field),
                )
                Row(
                    modifier = Modifier.padding(horizontal = 18.dp).padding(top = 56.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RoundIconButton(
                        icon = Icons.AutoMirrored.Filled.ArrowBack,
                        onClick = onBack,
                        bgColor = Color.White.copy(alpha = 0.92f),
                        contentDescription = "Back",
                    )
                    Spacer(Modifier.weight(1f))
                    SaveToggleButton(
                        saved = isSaved,
                        onClick = onSaveToggle,
                        icon = Icons.Outlined.BookmarkBorder,
                        iconFilled = Icons.Filled.Bookmark,
                    )
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                // Thumbnails
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.padding(bottom = 20.dp),
                ) {
                    val urls = listOf(
                        "https://picsum.photos/seed/${property.id}2/200/150",
                        "https://picsum.photos/seed/${property.id}3/200/150",
                        "https://picsum.photos/seed/${property.id}4/200/150",
                    )
                    urls.forEachIndexed { i, url ->
                        Box(modifier = Modifier.weight(1f)) {
                            AsyncImage(
                                model = url,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxWidth().height(74.dp).clip(RoundedCornerShape(12.dp)).background(colors.field),
                            )
                            if (i > 0) {
                                Box(
                                    modifier = Modifier
                                        .matchParentSize()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0x66101620)),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text("+${i + 1}", style = MaterialTheme.typography.titleMedium.copy(color = Color.White, fontWeight = FontWeight.Bold))
                                }
                            }
                        }
                    }
                }

                // Title row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        AppTag(text = property.type)
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = property.title,
                            style = MaterialTheme.typography.headlineLarge.copy(
                                color = colors.ink, fontWeight = FontWeight.ExtraBold, fontSize = 23.sp,
                            ),
                        )
                        Spacer(Modifier.height(5.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.LocationOn, null, tint = colors.muted, modifier = Modifier.size(15.dp))
                            Text(
                                text = property.address,
                                style = MaterialTheme.typography.bodySmall.copy(color = colors.muted),
                                modifier = Modifier.padding(start = 5.dp),
                            )
                        }
                    }
                    Spacer(Modifier.width(12.dp))
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(AccentSoft)
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Icon(Icons.Filled.Star, null, tint = colors.star, modifier = Modifier.size(15.dp))
                        Text(
                            text = property.rating.toString(),
                            style = MaterialTheme.typography.labelMedium.copy(color = colors.ink, fontWeight = FontWeight.Bold),
                        )
                    }
                }

                Spacer(Modifier.height(18.dp))

                // Meta pills
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    listOf(
                        Triple(Icons.Filled.KingBed,  "${property.beds} bed",  "beds"),
                        Triple(Icons.Filled.Bathtub,  "${property.baths} bath", "baths"),
                        Triple(Icons.Filled.SquareFoot, "${property.sqft} sqft", "area"),
                    ).forEach { (icon, label, _) ->
                        MetaPill(icon = icon, label = label, modifier = Modifier.weight(1f))
                    }
                }

                Spacer(Modifier.height(20.dp))

                // Description
                Text("Description", style = MaterialTheme.typography.titleMedium.copy(color = colors.ink, fontWeight = FontWeight.Bold, fontSize = 17.sp))
                Spacer(Modifier.height(8.dp))
                val desc = property.description
                val shortDesc = if (desc.length > 132) desc.take(132) + "…" else desc
                Text(
                    text = buildAnnotatedString {
                        append(if (expanded) desc else shortDesc)
                        withStyle(SpanStyle(color = Accent, fontWeight = FontWeight.Bold)) {
                            append(if (expanded) " read less" else " read more")
                        }
                    },
                    style = MaterialTheme.typography.bodyMedium.copy(color = colors.muted, lineHeight = 22.sp),
                    modifier = Modifier.clickable { expanded = !expanded },
                )

                Spacer(Modifier.height(22.dp))

                // Owner card
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(colors.bg)
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    InitialsAvatar(name = property.ownerName, size = 50.dp)
                    Column(modifier = Modifier.weight(1f)) {
                        Text(property.ownerName, style = MaterialTheme.typography.titleSmall.copy(color = colors.ink, fontWeight = FontWeight.Bold))
                        Text(property.ownerRole, style = MaterialTheme.typography.bodySmall.copy(color = colors.muted))
                    }
                    ContactChip(icon = Icons.Filled.Phone, onClick = onChatThread)
                    ContactChip(icon = Icons.Filled.MailOutline, onClick = onChatThread)
                }

                Spacer(Modifier.height(22.dp))

                // Map placeholder
                Text("Location on the map", style = MaterialTheme.typography.titleMedium.copy(color = colors.ink, fontWeight = FontWeight.Bold, fontSize = 17.sp))
                Spacer(Modifier.height(12.dp))
                MapPlaceholder()
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun MetaPill(icon: ImageVector, label: String, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.appColors
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(colors.bg)
            .padding(horizontal = 14.dp, vertical = 13.dp),
        verticalArrangement = Arrangement.spacedBy(7.dp),
    ) {
        Icon(icon, null, tint = Accent, modifier = Modifier.size(20.dp))
        Text(label, style = MaterialTheme.typography.labelMedium.copy(color = colors.ink, fontWeight = FontWeight.Bold))
    }
}

@Composable
private fun ContactChip(icon: ImageVector, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(RoundedCornerShape(13.dp))
            .background(AccentSoft)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, null, tint = Accent, modifier = Modifier.size(20.dp))
    }
}

@Composable
private fun MapPlaceholder() {
    val colors = MaterialTheme.appColors
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFE9EEF1))
            .shadow(0.dp),
        contentAlignment = Alignment.Center,
    ) {
        // Simplified map mock with "Here" pin
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(Accent)
                .padding(horizontal = 10.dp, vertical = 5.dp),
        ) {
            Text("📍 Here", style = MaterialTheme.typography.labelMedium.copy(color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp))
        }
    }
}
