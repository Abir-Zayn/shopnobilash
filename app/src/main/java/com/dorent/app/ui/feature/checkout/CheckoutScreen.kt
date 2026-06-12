package com.dorent.app.ui.feature.checkout

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.dorent.app.data.model.Property
import com.dorent.app.data.model.formatPrice
import com.dorent.app.ui.components.AppTag
import com.dorent.app.ui.components.PriceText
import com.dorent.app.ui.components.PrimaryButton
import com.dorent.app.ui.components.StackHeader
import com.dorent.app.ui.theme.Accent
import com.dorent.app.ui.theme.AccentDeep
import com.dorent.app.ui.theme.AccentSoft
import com.dorent.app.ui.theme.appColors
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

data class PaymentMethod(val id: String, val label: String, val sub: String, val icon: ImageVector, val tint: Color)

val PAYMENT_METHODS = listOf(
    PaymentMethod("visa",   "Visa",           "•••• 4921",    Icons.Filled.CreditCard,          Color(0xFF1A1F71)),
    PaymentMethod("mc",     "Mastercard",     "•••• 8830",    Icons.Filled.CreditCard,          Color(0xFFEB601B)),
    PaymentMethod("wallet", "DORent Wallet",  "Balance \$5,400", Icons.Filled.AccountBalanceWallet, Accent),
)

@Composable
fun CheckoutScreen(
    propertyId: String,
    onBack: () -> Unit,
    onBookingConfirmed: () -> Unit,
    onMessageOwner: () -> Unit,
    viewModel: CheckoutViewModel = koinViewModel { parametersOf(propertyId) },
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val selectedTerm by viewModel.selectedTerm.collectAsStateWithLifecycle()
    val selectedPayment by viewModel.selectedPayment.collectAsStateWithLifecycle()
    val bookingDone by viewModel.bookingDone.collectAsStateWithLifecycle()
    val colors = MaterialTheme.appColors

    when (val state = uiState) {
        is CheckoutUiState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Accent)
        }
        is CheckoutUiState.Error -> Text(state.message, modifier = Modifier.padding(18.dp), color = colors.danger)
        is CheckoutUiState.Success -> {
            val property = state.property
            val service = 120
            val deposit = property.price
            val total = property.price + service + deposit

            Scaffold(
                containerColor = colors.bg,
                topBar = { StackHeader(title = "Checkout", onBack = onBack) },
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
                            Text("Due today", style = MaterialTheme.typography.bodySmall.copy(color = colors.muted))
                            Text(formatPrice(total), style = MaterialTheme.typography.headlineSmall.copy(color = colors.ink, fontWeight = FontWeight.ExtraBold, fontSize = 21.sp))
                        }
                        PrimaryButton(
                            text = "Confirm & Pay",
                            onClick = { viewModel.confirmBooking() },
                            modifier = Modifier.weight(1f),
                            icon = Icons.Filled.Lock,
                        )
                    }
                },
            ) { innerPadding ->
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 18.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(0.dp),
                ) {
                    item { Spacer(Modifier.height(8.dp)) }
                    item { PropertySummaryCard(property) }
                    item { Spacer(Modifier.height(22.dp)) }

                    item {
                        Text("Booking details", style = MaterialTheme.typography.titleMedium.copy(color = colors.ink, fontWeight = FontWeight.Bold, fontSize = 16.sp))
                        Spacer(Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            BookingDetailCard("Move-in", "Jul 1, 2026", Icons.Filled.CalendarMonth, Modifier.weight(1f))
                            BookingDetailCard("Lease term", "$selectedTerm months", Icons.Filled.Shield, Modifier.weight(1f))
                        }
                        Spacer(Modifier.height(14.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf(6, 12, 24).forEach { term ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(42.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (selectedTerm == term) AccentSoft else colors.card)
                                        .border(1.5.dp, if (selectedTerm == term) Accent else colors.line2, RoundedCornerShape(12.dp))
                                        .clickable { viewModel.selectTerm(term) },
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text("$term mo", style = MaterialTheme.typography.labelLarge.copy(color = if (selectedTerm == term) AccentDeep else colors.ink2, fontWeight = FontWeight.Bold))
                                }
                            }
                        }
                        Spacer(Modifier.height(24.dp))
                    }

                    item {
                        Text("Payment method", style = MaterialTheme.typography.titleMedium.copy(color = colors.ink, fontWeight = FontWeight.Bold, fontSize = 16.sp))
                        Spacer(Modifier.height(12.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            PAYMENT_METHODS.forEach { method ->
                                PaymentMethodRow(method, selectedPayment == method.id) { viewModel.selectPayment(method.id) }
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .border(1.5.dp, colors.line2, RoundedCornerShape(14.dp))
                                    .clickable { },
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                            ) {
                                Icon(Icons.Filled.Add, null, tint = Accent, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Add new card", style = MaterialTheme.typography.labelLarge.copy(color = Accent, fontWeight = FontWeight.Bold))
                            }
                        }
                        Spacer(Modifier.height(14.dp))
                    }

                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(4.dp, RoundedCornerShape(18.dp))
                                .clip(RoundedCornerShape(18.dp))
                                .background(colors.card)
                                .padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(13.dp),
                        ) {
                            SummaryRow("Monthly rent", formatPrice(property.price))
                            SummaryRow("Service fee", formatPrice(service))
                            SummaryRow("Security deposit", formatPrice(deposit))
                            Box(Modifier.fillMaxWidth().height(1.dp).background(colors.line))
                            SummaryRow("Due today", formatPrice(total), strong = true)
                        }
                        Spacer(Modifier.height(16.dp))
                    }
                }
            }

            if (bookingDone) {
                Dialog(onDismissRequest = { viewModel.dismissConfirmation(); onBookingConfirmed() }) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                            .background(colors.card)
                            .padding(horizontal = 24.dp, vertical = 30.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Box(
                            modifier = Modifier.size(78.dp).clip(CircleShape).background(AccentSoft),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(Icons.Filled.CheckCircle, null, tint = Accent, modifier = Modifier.size(50.dp))
                        }
                        Spacer(Modifier.height(18.dp))
                        Text("Booking Confirmed!", style = MaterialTheme.typography.headlineMedium.copy(color = colors.ink, fontWeight = FontWeight.ExtraBold))
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "You've booked ${property.title}. The owner has been notified.",
                            style = MaterialTheme.typography.bodyMedium.copy(color = colors.muted, lineHeight = 22.sp),
                        )
                        Spacer(Modifier.height(24.dp))
                        PrimaryButton("Back to Home", onClick = { viewModel.dismissConfirmation(); onBookingConfirmed() })
                        Spacer(Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .clip(RoundedCornerShape(15.dp))
                                .background(AccentSoft)
                                .clickable(onClick = onMessageOwner),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text("Message owner", style = MaterialTheme.typography.titleSmall.copy(color = AccentDeep, fontWeight = FontWeight.Bold, fontSize = 15.sp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PropertySummaryCard(property: Property) {
    val colors = MaterialTheme.appColors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(18.dp))
            .clip(RoundedCornerShape(18.dp))
            .background(colors.card)
            .padding(10.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current).data(property.imageUrl).crossfade(true).build(),
            contentDescription = null, contentScale = ContentScale.Crop,
            modifier = Modifier.size(78.dp).clip(RoundedCornerShape(13.dp)).background(colors.field),
        )
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            AppTag(text = property.type)
            Text(property.title, style = MaterialTheme.typography.titleSmall.copy(color = colors.ink, fontWeight = FontWeight.Bold, fontSize = 15.5.sp))
            PriceText(price = property.price, period = "month", size = 15.dp)
        }
    }
}

@Composable
private fun BookingDetailCard(label: String, value: String, icon: ImageVector, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.appColors
    Column(
        modifier = modifier
            .shadow(4.dp, RoundedCornerShape(14.dp))
            .clip(RoundedCornerShape(14.dp))
            .background(colors.card)
            .padding(12.dp, 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(7.dp)) {
            Icon(icon, null, tint = Accent, modifier = Modifier.size(17.dp))
            Text(label, style = MaterialTheme.typography.labelSmall.copy(color = colors.muted, fontWeight = FontWeight.SemiBold))
        }
        Text(value, style = MaterialTheme.typography.titleSmall.copy(color = colors.ink, fontWeight = FontWeight.Bold, fontSize = 14.5.sp))
    }
}

@Composable
private fun PaymentMethodRow(method: PaymentMethod, selected: Boolean, onClick: () -> Unit) {
    val colors = MaterialTheme.appColors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(15.dp))
            .background(if (selected) AccentSoft else colors.card)
            .border(1.5.dp, if (selected) Accent else colors.line2, RoundedCornerShape(15.dp))
            .clickable(onClick = onClick)
            .padding(13.dp, 13.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(13.dp),
    ) {
        Box(
            modifier = Modifier.size(42.dp).clip(RoundedCornerShape(11.dp)).background(method.tint),
            contentAlignment = Alignment.Center,
        ) {
            Icon(method.icon, null, tint = Color.White, modifier = Modifier.size(22.dp))
        }
        Column(Modifier.weight(1f)) {
            Text(method.label, style = MaterialTheme.typography.titleSmall.copy(color = colors.ink, fontWeight = FontWeight.Bold, fontSize = 14.5.sp))
            Text(method.sub, style = MaterialTheme.typography.bodySmall.copy(color = colors.muted))
        }
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .border(2.dp, if (selected) Accent else colors.line2, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            if (selected) Box(Modifier.size(11.dp).clip(CircleShape).background(Accent))
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String, strong: Boolean = false) {
    val colors = MaterialTheme.appColors
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodyMedium.copy(
            color = if (strong) colors.ink else colors.muted,
            fontWeight = if (strong) FontWeight.Bold else FontWeight.Normal,
            fontSize = if (strong) 15.sp else 14.sp,
        ))
        Text(value, style = MaterialTheme.typography.bodyMedium.copy(
            color = if (strong) colors.ink else colors.ink2,
            fontWeight = if (strong) FontWeight.ExtraBold else FontWeight.Bold,
            fontSize = if (strong) 17.sp else 14.sp,
        ))
    }
}
