package com.shopnobilash.app.presentation.checkout.ui
import com.shopnobilash.app.presentation.checkout.viewmodel.CheckoutViewModel
import com.shopnobilash.app.presentation.checkout.viewmodel.CheckoutUiState

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.shopnobilash.app.data.property.model.Property
import com.shopnobilash.app.data.property.model.formatPrice
import com.shopnobilash.app.presentation.components.AppTag
import com.shopnobilash.app.presentation.components.PriceText
import com.shopnobilash.app.presentation.components.PrimaryButton
import com.shopnobilash.app.presentation.components.SecondaryButton
import com.shopnobilash.app.presentation.components.StackHeader
import com.shopnobilash.app.presentation.theme.Accent
import com.shopnobilash.app.presentation.theme.AccentDeep
import com.shopnobilash.app.presentation.theme.AccentSoft
import com.shopnobilash.app.presentation.theme.appColors
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

/** Width past which we switch to the two-pane (tablet / landscape) layout. */
private val WideThreshold = 600.dp
private val LEASE_YEARS = listOf(1, 2, 3)
private val moveInFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    propertyId: String,
    onBack: () -> Unit,
    onBookingConfirmed: () -> Unit,
    onMessageOwner: () -> Unit,
    viewModel: CheckoutViewModel = koinViewModel { parametersOf(propertyId) },
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val selectedYears by viewModel.selectedYears.collectAsStateWithLifecycle()
    val moveInMillis by viewModel.moveInDateMillis.collectAsStateWithLifecycle()
    val colors = MaterialTheme.appColors
    val context = LocalContext.current

    when (val state = uiState) {
        is CheckoutUiState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Accent)
        }
        is CheckoutUiState.Error -> Text(state.message, modifier = Modifier.padding(18.dp), color = colors.danger)
        is CheckoutUiState.Success -> {
            val property = state.property
            var showDatePicker by remember { mutableStateOf(false) }

            val moveInDate = moveInMillis?.let {
                Instant.ofEpochMilli(it).atZone(ZoneOffset.UTC).toLocalDate()
            }
            val moveInLabel = moveInDate?.format(moveInFormatter) ?: "Select date"
            val durationLabel = moveInDate?.let { start ->
                val end = start.plusYears(selectedYears.toLong())
                "${start.format(moveInFormatter)} → ${end.format(moveInFormatter)}"
            }

            val onCall: () -> Unit = {
                // Owner phone is not yet part of the data model — opens the dialer ready for input.
                context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:")))
            }

            Scaffold(
                containerColor = colors.bg,
                topBar = { StackHeader(title = "Checkout", onBack = onBack) },
            ) { innerPadding ->
                BoxWithConstraints(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                ) {
                    val wide = maxWidth >= WideThreshold

                    if (wide) {
                        // Two-pane: details on the left, sticky cost + actions on the right.
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 24.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(24.dp),
                        ) {
                            Column(
                                modifier = Modifier
                                    .weight(1.4f)
                                    .verticalScroll(rememberScrollState()),
                                verticalArrangement = Arrangement.spacedBy(22.dp),
                            ) {
                                PropertySummaryCard(property)
                                BookingDetailsSection(
                                    selectedYears = selectedYears,
                                    moveInLabel = moveInLabel,
                                    durationLabel = durationLabel,
                                    onMoveInClick = { showDatePicker = true },
                                    onSelectYears = viewModel::selectYears,
                                )
                            }
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .widthIn(max = 360.dp)
                                    .verticalScroll(rememberScrollState()),
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                            ) {
                                DueTodayCard(property, selectedYears)
                                CallMeetActions(onCall = onCall, onMeet = onMessageOwner)
                            }
                        }
                    } else {
                        // Compact: single scroll column with the actions pinned to the bottom.
                        Column(Modifier.fillMaxSize()) {
                            LazyColumn(
                                modifier = Modifier.weight(1f).fillMaxWidth(),
                                contentPadding = PaddingValues(horizontal = 18.dp, vertical = 4.dp),
                            ) {
                                item { Spacer(Modifier.height(8.dp)) }
                                item { PropertySummaryCard(property) }
                                item { Spacer(Modifier.height(22.dp)) }
                                item {
                                    BookingDetailsSection(
                                        selectedYears = selectedYears,
                                        moveInLabel = moveInLabel,
                                        durationLabel = durationLabel,
                                        onMoveInClick = { showDatePicker = true },
                                        onSelectYears = viewModel::selectYears,
                                    )
                                    Spacer(Modifier.height(22.dp))
                                }
                                item {
                                    DueTodayCard(property, selectedYears)
                                    Spacer(Modifier.height(16.dp))
                                }
                            }
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .background(colors.card)
                                    .padding(horizontal = 18.dp, vertical = 14.dp)
                                    .padding(bottom = 16.dp),
                            ) {
                                CallMeetActions(onCall = onCall, onMeet = onMessageOwner)
                            }
                        }
                    }
                }
            }

            if (showDatePicker) {
                // Earliest selectable = tomorrow (UTC midnight, matching DatePicker's clock).
                val minSelectableMillis = remember {
                    LocalDate.now().plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
                }
                val minSelectableYear = remember { LocalDate.now().year }
                val datePickerState = rememberDatePickerState(
                    initialSelectedDateMillis = moveInMillis,
                    yearRange = minSelectableYear..(minSelectableYear + 5),
                    selectableDates = object : SelectableDates {
                        override fun isSelectableDate(utcTimeMillis: Long) = utcTimeMillis >= minSelectableMillis
                        override fun isSelectableYear(year: Int) = year >= minSelectableYear
                    },
                )
                DatePickerDialog(
                    onDismissRequest = { showDatePicker = false },
                    confirmButton = {
                        TextButton(onClick = {
                            viewModel.setMoveInDate(datePickerState.selectedDateMillis)
                            showDatePicker = false
                        }) { Text("Confirm", color = Accent, fontWeight = FontWeight.Bold) }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDatePicker = false }) {
                            Text("Cancel", color = colors.muted)
                        }
                    },
                ) {
                    DatePicker(state = datePickerState)
                }
            }
        }
    }
}

@Composable
private fun BookingDetailsSection(
    selectedYears: Int,
    moveInLabel: String,
    durationLabel: String?,
    onMoveInClick: () -> Unit,
    onSelectYears: (Int) -> Unit,
) {
    val colors = MaterialTheme.appColors
    Column {
        Text("Booking details", style = MaterialTheme.typography.titleMedium.copy(color = colors.ink, fontWeight = FontWeight.Bold, fontSize = 16.sp))
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            BookingDetailCard(
                label = "Move-in",
                value = moveInLabel,
                icon = Icons.Filled.CalendarMonth,
                modifier = Modifier.weight(1f).clickable(onClick = onMoveInClick),
            )
            val yearLabel = "$selectedYears ${if (selectedYears == 1) "year" else "years"}"
            BookingDetailCard("Lease term", yearLabel, Icons.Filled.Shield, Modifier.weight(1f))
        }
        Spacer(Modifier.height(14.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            LEASE_YEARS.forEach { years ->
                val selected = selectedYears == years
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (selected) AccentSoft else colors.card)
                        .border(1.5.dp, if (selected) Accent else colors.line2, RoundedCornerShape(12.dp))
                        .clickable { onSelectYears(years) },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        "$years ${if (years == 1) "yr" else "yrs"}",
                        style = MaterialTheme.typography.labelLarge.copy(color = if (selected) AccentDeep else colors.ink2, fontWeight = FontWeight.Bold),
                    )
                }
            }
        }
        if (durationLabel != null) {
            Spacer(Modifier.height(14.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(AccentSoft)
                    .padding(horizontal = 13.dp, vertical = 11.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(Icons.Filled.CalendarMonth, null, tint = AccentDeep, modifier = Modifier.size(16.dp))
                Column {
                    Text("Total duration", style = MaterialTheme.typography.labelSmall.copy(color = colors.muted, fontWeight = FontWeight.SemiBold))
                    Text(durationLabel, style = MaterialTheme.typography.titleSmall.copy(color = AccentDeep, fontWeight = FontWeight.Bold, fontSize = 14.sp))
                }
            }
        }
    }
}

@Composable
private fun DueTodayCard(property: Property, years: Int) {
    val colors = MaterialTheme.appColors
    val advance = property.advanceAmount?.toInt() ?: property.price
    val totalPay = property.price * 12 * years
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(18.dp))
            .clip(RoundedCornerShape(18.dp))
            .background(colors.card)
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(13.dp),
    ) {
        SummaryRow("Advance rent", formatPrice(advance))
        SummaryRow("Monthly rent", formatPrice(property.price))
        Box(Modifier.fillMaxWidth().height(1.dp).background(colors.line))
        SummaryRow("Total pay ($years ${if (years == 1) "yr" else "yrs"})", formatPrice(totalPay), strong = true)
    }
}

@Composable
private fun CallMeetActions(onCall: () -> Unit, onMeet: () -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        SecondaryButton(
            text = "Call",
            onClick = onCall,
            modifier = Modifier.weight(1f),
            icon = Icons.Filled.Phone,
        )
        PrimaryButton(
            text = "Meet",
            onClick = onMeet,
            modifier = Modifier.weight(1f),
            icon = Icons.AutoMirrored.Filled.Chat,
        )
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
