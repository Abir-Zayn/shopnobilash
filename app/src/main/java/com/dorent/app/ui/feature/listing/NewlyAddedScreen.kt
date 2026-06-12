package com.dorent.app.ui.feature.listing

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dorent.app.ui.components.PropertyCardHorizontal
import com.dorent.app.ui.components.RoundIconButton
import com.dorent.app.ui.components.StackHeader
import com.dorent.app.ui.theme.Accent
import com.dorent.app.ui.theme.appColors
import org.koin.androidx.compose.koinViewModel

@Composable
fun NewlyAddedScreen(
    onBack: () -> Unit,
    onNavigateToDetail: (String) -> Unit,
    viewModel: ListingViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val savedIds by viewModel.savedIds.collectAsStateWithLifecycle()
    val colors = MaterialTheme.appColors

    Scaffold(
        containerColor = colors.bg,
        topBar = {
            StackHeader(
                title = "Newly Added",
                onBack = onBack,
                actions = {
                    RoundIconButton(icon = Icons.Filled.Tune, onClick = {}, contentDescription = "Filter")
                },
            )
        },
    ) { innerPadding ->
        when (val state = uiState) {
            is ListingUiState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Accent)
            }
            is ListingUiState.Error -> Text(state.message, modifier = Modifier.padding(18.dp), color = colors.danger)
            is ListingUiState.Success -> LazyColumn(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentPadding = PaddingValues(horizontal = 18.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                val ordered = state.properties.filter { it.id in listOf("minimal", "earth", "aspen", "lara") }
                    .sortedBy { listOf("minimal", "earth", "aspen", "lara").indexOf(it.id) }
                items(ordered, key = { it.id }) { prop ->
                    PropertyCardHorizontal(
                        property = prop,
                        isSaved = prop.id in savedIds,
                        onOpen = { onNavigateToDetail(prop.id) },
                        onSaveToggle = { viewModel.toggleSave(prop.id) },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}
