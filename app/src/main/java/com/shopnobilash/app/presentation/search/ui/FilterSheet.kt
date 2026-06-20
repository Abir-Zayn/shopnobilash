package com.shopnobilash.app.presentation.search.ui

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shopnobilash.app.data.property.model.PropertyFilter
import com.shopnobilash.app.data.property.model.PropertyFilterRanges
import com.shopnobilash.app.presentation.components.PrimaryButton
import com.shopnobilash.app.presentation.theme.Accent
import com.shopnobilash.app.presentation.theme.appColors

/**
 * Content of the filter bottom sheet. Holds its own draft state so edits only commit
 * when the user taps Apply; [onApply] passes the resolved bounds back to the ViewModel.
 */
@Composable
fun FilterSheetContent(
    current: PropertyFilter,
    onApply: (
        minRent: Int?,
        maxRent: Int?,
        minArea: Int?,
        maxArea: Int?,
        bathrooms: Set<Int>,
        bedrooms: Set<Int>,
        newlyAdded: Boolean,
    ) -> Unit,
    onReset: () -> Unit,
) {
    val colors = MaterialTheme.appColors

    val rentBounds = PropertyFilterRanges.RENT_MIN.toFloat()..PropertyFilterRanges.RENT_MAX.toFloat()
    val areaBounds = PropertyFilterRanges.AREA_MIN.toFloat()..PropertyFilterRanges.AREA_MAX.toFloat()

    var rentRange by remember {
        mutableStateOf(
            (current.minRent ?: PropertyFilterRanges.RENT_MIN).toFloat()..
                (current.maxRent ?: PropertyFilterRanges.RENT_MAX).toFloat(),
        )
    }
    var areaRange by remember {
        mutableStateOf(
            (current.minArea ?: PropertyFilterRanges.AREA_MIN).toFloat()..
                (current.maxArea ?: PropertyFilterRanges.AREA_MAX).toFloat(),
        )
    }
    var bathrooms by remember { mutableStateOf(current.bathrooms) }
    var bedrooms by remember { mutableStateOf(current.bedrooms) }
    var newlyAdded by remember { mutableStateOf(current.newlyAdded) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 22.dp)
            .padding(bottom = 28.dp, top = 4.dp),
    ) {
        Text(
            "Filters",
            style = MaterialTheme.typography.headlineSmall.copy(
                color = colors.ink,
                fontWeight = FontWeight.Bold,
            ),
        )
        Spacer(Modifier.height(20.dp))

        // ── Rent range ────────────────────────────────────────────────────────
        SectionLabel(
            title = "Monthly rent",
            value = "৳${rentRange.start.toInt()} – ৳${rentRange.endInclusive.toInt()}",
        )
        RangeSlider(
            value = rentRange,
            onValueChange = { rentRange = it },
            valueRange = rentBounds,
            colors = androidx.compose.material3.SliderDefaults.colors(
                thumbColor = Accent,
                activeTrackColor = Accent,
            ),
        )
        Spacer(Modifier.height(16.dp))

        // ── Area range ────────────────────────────────────────────────────────
        SectionLabel(
            title = "Area (sqft)",
            value = "${areaRange.start.toInt()} – ${areaRange.endInclusive.toInt()}",
        )
        RangeSlider(
            value = areaRange,
            onValueChange = { areaRange = it },
            valueRange = areaBounds,
            colors = androidx.compose.material3.SliderDefaults.colors(
                thumbColor = Accent,
                activeTrackColor = Accent,
            ),
        )
        Spacer(Modifier.height(20.dp))

        // ── Bathrooms ─────────────────────────────────────────────────────────
        SectionLabel(title = "Bathrooms")
        Spacer(Modifier.height(10.dp))
        RoomChipRow(
            options = PropertyFilterRanges.ROOM_OPTIONS,
            selected = bathrooms,
            onToggle = { n -> bathrooms = bathrooms.toggle(n) },
        )
        Spacer(Modifier.height(20.dp))

        // ── Bedrooms ──────────────────────────────────────────────────────────
        SectionLabel(title = "Bedrooms / Rooms")
        Spacer(Modifier.height(10.dp))
        RoomChipRow(
            options = PropertyFilterRanges.ROOM_OPTIONS,
            selected = bedrooms,
            onToggle = { n -> bedrooms = bedrooms.toggle(n) },
        )
        Spacer(Modifier.height(20.dp))

        // ── Newly added ───────────────────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(
                    "Newly added",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = colors.ink,
                        fontWeight = FontWeight.SemiBold,
                    ),
                )
                Text(
                    "Listed in the last 30 days",
                    style = MaterialTheme.typography.bodySmall.copy(color = colors.muted),
                )
            }
            Switch(
                checked = newlyAdded,
                onCheckedChange = { newlyAdded = it },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Accent,
                ),
            )
        }
        Spacer(Modifier.height(26.dp))

        // ── Actions ───────────────────────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .height(54.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(colors.field)
                    .clickable {
                        rentRange = rentBounds
                        areaRange = areaBounds
                        bathrooms = emptySet()
                        bedrooms = emptySet()
                        newlyAdded = false
                        onReset()
                    }
                    .padding(horizontal = 24.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    "Reset",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = colors.ink,
                        fontWeight = FontWeight.Bold,
                    ),
                )
            }
            PrimaryButton(
                text = "Apply filters",
                onClick = {
                    onApply(
                        rentRange.start.toInt().takeIf { it > PropertyFilterRanges.RENT_MIN },
                        rentRange.endInclusive.toInt().takeIf { it < PropertyFilterRanges.RENT_MAX },
                        areaRange.start.toInt().takeIf { it > PropertyFilterRanges.AREA_MIN },
                        areaRange.endInclusive.toInt().takeIf { it < PropertyFilterRanges.AREA_MAX },
                        bathrooms,
                        bedrooms,
                        newlyAdded,
                    )
                },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun SectionLabel(title: String, value: String? = null) {
    val colors = MaterialTheme.appColors
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            title,
            style = MaterialTheme.typography.titleMedium.copy(
                color = colors.ink,
                fontWeight = FontWeight.SemiBold,
            ),
        )
        if (value != null) {
            Text(
                value,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = colors.accent,
                    fontWeight = FontWeight.Bold,
                ),
            )
        }
    }
}

@Composable
private fun RoomChipRow(
    options: List<Int>,
    selected: Set<Int>,
    onToggle: (Int) -> Unit,
) {
    val colors = MaterialTheme.appColors
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        options.forEach { n ->
            val on = n in selected
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (on) Accent else colors.field)
                    .clickable { onToggle(n) },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    if (n == options.last()) "$n+" else "$n",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = if (on) Color.White else colors.ink,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                    ),
                )
            }
        }
    }
}

private fun Set<Int>.toggle(value: Int): Set<Int> =
    if (value in this) this - value else this + value
