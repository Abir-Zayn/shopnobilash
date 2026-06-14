package com.dorent.app.ui.feature.auth.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dorent.app.ui.components.AppText
import com.dorent.app.ui.theme.appColors

@Composable
fun LoginField(label: String, content: @Composable () -> Unit) {
    val colors = MaterialTheme.appColors
    Column {
        AppText(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = colors.ink,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
            ),
            modifier = Modifier.padding(bottom = 12.dp),
        )
        content()
    }
}
