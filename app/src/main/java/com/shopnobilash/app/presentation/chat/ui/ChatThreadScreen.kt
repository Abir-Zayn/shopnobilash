package com.shopnobilash.app.presentation.chat.ui
import com.shopnobilash.app.presentation.chat.viewmodel.ChatViewModel

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.shopnobilash.app.presentation.components.AppAvatar
import com.shopnobilash.app.presentation.components.PriceText
import com.shopnobilash.app.presentation.components.RoundIconButton
import com.shopnobilash.app.presentation.theme.Accent
import com.shopnobilash.app.presentation.theme.appColors
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun ChatThreadScreen(
    propertyId: String,
    onBack: () -> Unit,
    onNavigateToDetail: () -> Unit,
    viewModel: ChatViewModel = koinViewModel { parametersOf(propertyId) },
) {
    val messages by viewModel.messages.collectAsStateWithLifecycle()
    val inputText by viewModel.inputText.collectAsStateWithLifecycle()
    val property by viewModel.property.collectAsStateWithLifecycle()
    val colors = MaterialTheme.appColors
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) listState.animateScrollToItem(messages.size - 1)
    }

    Scaffold(
        containerColor = colors.bg,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colors.card)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .padding(top = 52.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                RoundIconButton(icon = Icons.AutoMirrored.Filled.ArrowBack, onClick = onBack, bgColor = colors.bg, contentDescription = "Back")
                val owner = property?.ownerName.orEmpty()
                Box {
                    AppAvatar(imageUrl = null, name = owner, size = 42.dp)
                    Box(Modifier.size(11.dp).clip(CircleShape).background(Accent).align(Alignment.BottomEnd))
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(owner, style = MaterialTheme.typography.titleSmall.copy(color = colors.ink, fontWeight = FontWeight.Bold))
                    Text("Online now", style = MaterialTheme.typography.bodySmall.copy(color = Accent))
                }
                RoundIconButton(icon = Icons.Filled.Phone, onClick = {}, bgColor = colors.bg, iconColor = Accent, contentDescription = "Call")
            }
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colors.card)
                    .padding(horizontal = 14.dp, vertical = 10.dp)
                    .padding(bottom = 16.dp)
                    .imePadding(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Box(
                    modifier = Modifier.size(44.dp).clip(RoundedCornerShape(13.dp)).background(colors.bg).clickable { },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Filled.Add, null, tint = colors.muted, modifier = Modifier.size(22.dp))
                }
                OutlinedTextField(
                    value = inputText,
                    onValueChange = viewModel::setInputText,
                    modifier = Modifier.weight(1f).height(46.dp),
                    placeholder = { Text("Message…", style = MaterialTheme.typography.bodyMedium.copy(color = colors.faint)) },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(onSend = { viewModel.sendMessage() }),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = colors.bg,
                        unfocusedContainerColor = colors.bg,
                    ),
                )
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .shadow(8.dp, RoundedCornerShape(14.dp))
                        .clip(RoundedCornerShape(14.dp))
                        .background(Accent)
                        .clickable { viewModel.sendMessage() },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Filled.Send, null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }
        },
    ) { innerPadding ->
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(14.dp))
                        .clip(RoundedCornerShape(14.dp))
                        .background(colors.card)
                        .clickable(onClick = onNavigateToDetail)
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(11.dp),
                ) {
                    val img = property?.imageUrl ?: ""
                    val title = property?.title ?: ""
                    val price = property?.price ?: 0
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current).data(img).crossfade(true).build(),
                        contentDescription = null, contentScale = ContentScale.Crop,
                        modifier = Modifier.size(46.dp).clip(RoundedCornerShape(11.dp)).background(colors.field),
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(title, style = MaterialTheme.typography.labelLarge.copy(color = colors.ink, fontWeight = FontWeight.Bold, fontSize = 13.5.sp))
                        PriceText(price = price, period = "mo", size = 13.dp)
                    }
                    Icon(Icons.Filled.ChevronRight, null, tint = colors.faint, modifier = Modifier.size(18.dp))
                }
                Spacer(Modifier.height(6.dp))
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text("Today", style = MaterialTheme.typography.labelSmall.copy(color = colors.faint, fontSize = 11.5.sp))
                }
            }

            items(messages) { msg ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (msg.isMe) Arrangement.End else Arrangement.Start,
                ) {
                    Column(modifier = Modifier.fillMaxWidth(0.78f)) {
                        Box(
                            modifier = Modifier
                                .clip(
                                    if (msg.isMe) RoundedCornerShape(18.dp, 18.dp, 6.dp, 18.dp)
                                    else RoundedCornerShape(18.dp, 18.dp, 18.dp, 6.dp),
                                )
                                .background(if (msg.isMe) Accent else colors.card)
                                .padding(horizontal = 15.dp, vertical = 11.dp),
                        ) {
                            Text(
                                msg.text,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = if (msg.isMe) Color.White else colors.ink,
                                    lineHeight = 21.sp,
                                ),
                            )
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            msg.time,
                            style = MaterialTheme.typography.labelSmall.copy(color = colors.faint, fontSize = 10.5.sp),
                            modifier = Modifier
                                .align(if (msg.isMe) Alignment.End else Alignment.Start)
                                .padding(horizontal = 4.dp),
                        )
                    }
                }
            }
        }
    }
}
