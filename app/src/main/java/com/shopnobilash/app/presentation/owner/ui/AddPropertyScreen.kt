package com.shopnobilash.app.presentation.owner.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.shopnobilash.app.presentation.components.AppText
import com.shopnobilash.app.data.property.model.PropertyCategory
import com.shopnobilash.app.data.property.model.PropertyDraft
import com.shopnobilash.app.presentation.components.AppTextField
import com.shopnobilash.app.presentation.components.PrimaryButton
import com.shopnobilash.app.presentation.owner.viewmodel.OwnerDashboardState
import com.shopnobilash.app.presentation.owner.viewmodel.OwnerDashboardViewModel
import com.shopnobilash.app.presentation.theme.Accent
import com.shopnobilash.app.presentation.theme.appColors

private const val MAX_PHOTOS = 6

/** STEP 3 — the listing form. Mirrors the `properties` schema field-for-field. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddPropertyScreen(
    state: OwnerDashboardState,
    viewModel: OwnerDashboardViewModel,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.appColors
    val context = LocalContext.current

    var category by rememberSaveable { mutableStateOf<PropertyCategory?>(null) }
    var houseName by rememberSaveable { mutableStateOf("") }
    var floor by rememberSaveable { mutableStateOf("") }
    var bedNo by rememberSaveable { mutableStateOf("") }
    var bathNo by rememberSaveable { mutableStateOf("") }
    var areaSqft by rememberSaveable { mutableStateOf("") }
    var rent by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var contractTerms by rememberSaveable { mutableStateOf("") }
    val images = remember { mutableStateListOf<Uri>() }
    var photoLimitHit by remember { mutableStateOf(false) }

    val picker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        val remaining = MAX_PHOTOS - images.size
        photoLimitHit = uris.size > remaining
        images.addAll(uris.take(remaining))
    }

    val bedVal = bedNo.toIntOrNull()
    val bathVal = bathNo.toIntOrNull()
    val areaVal = areaSqft.toIntOrNull()
    val rentVal = rent.toDoubleOrNull()

    val valid = category != null &&
        houseName.trim().isNotEmpty() &&
        bedVal != null && bedVal >= 0 &&
        bathVal != null && bathVal >= 0 &&
        areaVal != null && areaVal > 0 &&
        rentVal != null && rentVal > 0 &&
        images.isNotEmpty()

    val canSubmit = valid && !state.isSubmittingProperty

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        AppText(
            "Add Property",
            style = MaterialTheme.typography.titleLarge.copy(
                color = colors.ink,
                fontWeight = FontWeight.ExtraBold,
            ),
        )

        // Category
        SectionCard("Category") {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PropertyCategory.entries.forEach { cat ->
                    CategoryChip(
                        label = cat.rawValue,
                        selected = category == cat,
                        onClick = { category = cat },
                    )
                }
            }
        }

        // Basic details
        SectionCard("Basic Details") {
            FieldLabel("House Name *")
            AppTextField(value = houseName, onValueChange = { houseName = it }, placeholder = "e.g. Sherman Oaks", cornerRadius = 20.dp)
            FieldLabel("Floor")
            AppTextField(value = floor, onValueChange = { floor = it }, placeholder = "e.g. 7A (optional)", cornerRadius = 20.dp)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    FieldLabel("Bedrooms *")
                    AppTextField(
                        value = bedNo,
                        onValueChange = { bedNo = it.filter(Char::isDigit) },
                        placeholder = "0",
                        keyboardType = KeyboardType.Number,
                        cornerRadius = 20.dp,
                    )
                }
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    FieldLabel("Bathrooms *")
                    AppTextField(
                        value = bathNo,
                        onValueChange = { bathNo = it.filter(Char::isDigit) },
                        placeholder = "0",
                        keyboardType = KeyboardType.Number,
                        cornerRadius = 20.dp,
                    )
                }
            }
            AppText(
                "Enter 0 for a studio / open-plan unit.",
                style = MaterialTheme.typography.labelSmall.copy(color = colors.faint),
            )
            FieldLabel("Area (sqft) *")
            AppTextField(
                value = areaSqft,
                onValueChange = { areaSqft = it.filter(Char::isDigit) },
                placeholder = "e.g. 1200",
                keyboardType = KeyboardType.Number,
                cornerRadius = 20.dp,
            )
        }

        // Pricing
        SectionCard("Pricing") {
            FieldLabel("Monthly Rent *")
            AppTextField(
                value = rent,
                onValueChange = { input -> rent = input.filter { it.isDigit() || it == '.' } },
                placeholder = "e.g. 2000",
                keyboardType = KeyboardType.Decimal,
                cornerRadius = 20.dp,
            )
        }

        // Description
        SectionCard("Description") {
            FieldLabel("Description")
            AppTextField(
                value = description,
                onValueChange = { description = it },
                placeholder = "Describe the property",
                singleLine = false,
                cornerRadius = 20.dp,
            )
            FieldLabel("Contract Terms")
            AppTextField(
                value = contractTerms,
                onValueChange = { contractTerms = it },
                placeholder = "Rental contract terms",
                singleLine = false,
                cornerRadius = 20.dp,
            )
        }

        // Photos
        SectionCard("Photos (Max $MAX_PHOTOS)") {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                items(images, key = { it.toString() }) { uri ->
                    Box(modifier = Modifier.size(84.dp)) {
                        AsyncImage(
                            model = uri,
                            contentDescription = "Property photo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(12.dp))
                                .background(colors.field),
                        )
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(4.dp)
                                .size(20.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.Black.copy(alpha = 0.55f))
                                .clickable { images.remove(uri) },
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(Icons.Filled.Close, "Remove", tint = Color.White, modifier = Modifier.size(13.dp))
                        }
                    }
                }
                if (images.size < MAX_PHOTOS) {
                    item {
                        Box(
                            modifier = Modifier
                                .size(84.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(colors.field)
                                .border(1.dp, colors.line, RoundedCornerShape(12.dp))
                                .clickable(enabled = !state.isSubmittingProperty) { picker.launch("image/*") },
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(Icons.Filled.Add, "Add photo", tint = colors.muted, modifier = Modifier.size(28.dp))
                        }
                    }
                }
            }
            if (photoLimitHit) {
                AppText(
                    "Maximum $MAX_PHOTOS photos.",
                    style = MaterialTheme.typography.labelSmall.copy(color = colors.danger),
                )
            }
        }

        Spacer(Modifier.height(4.dp))

        if (state.isSubmittingProperty && state.uploadTotal > 0) {
            AppText(
                "Uploading photos ${state.uploadDone}/${state.uploadTotal}…",
                style = MaterialTheme.typography.labelMedium.copy(color = colors.muted),
            )
        }

        PrimaryButton(
            text = "Complete Listing",
            onClick = {
                val draft = PropertyDraft(
                    category = category!!,
                    houseName = houseName.trim(),
                    floor = floor.trim().ifEmpty { null },
                    bedNo = bedVal ?: 0,
                    bathNo = bathVal ?: 0,
                    areaSqft = areaVal ?: 0,
                    rent = rentVal ?: 0.0,
                    description = description.trim().ifEmpty { null },
                    contractTerms = contractTerms.trim().ifEmpty { null },
                )
                viewModel.submitProperty(draft, images.toList(), context)
            },
            enabled = canSubmit,
            isLoading = state.isSubmittingProperty,
            cornerRadius = 28.dp,
        )

        if (images.isEmpty()) {
            AppText(
                "Add at least one photo to publish.",
                style = MaterialTheme.typography.labelSmall.copy(color = colors.faint),
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun SectionCard(title: String, content: @Composable () -> Unit) {
    val colors = MaterialTheme.appColors
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        AppText(
            title.uppercase(),
            style = MaterialTheme.typography.labelMedium.copy(
                color = colors.muted,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp,
            ),
        )
        content()
    }
}

@Composable
private fun CategoryChip(label: String, selected: Boolean, onClick: () -> Unit) {
    val colors = MaterialTheme.appColors
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) Accent else colors.card)
            .then(if (selected) Modifier else Modifier.border(1.dp, colors.line, RoundedCornerShape(12.dp)))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 9.dp),
    ) {
        AppText(
            label,
            style = MaterialTheme.typography.labelLarge.copy(
                color = if (selected) Color.White else colors.ink,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
            ),
        )
    }
}

@Composable
private fun FieldLabel(text: String) {
    AppText(
        text,
        style = MaterialTheme.typography.labelLarge.copy(
            color = MaterialTheme.appColors.ink,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp,
        ),
    )
}
