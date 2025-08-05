package com.timor.kidsstory.presentation.setting.components

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.timor.kidsstory.ui.theme.ResponsiveTextUtils

@Composable
fun SettingCard(
    modifier: Modifier = Modifier,
    scaleFactor: Float,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier.fillMaxHeight(),
        shape = RoundedCornerShape((16 * scaleFactor).dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = (4 * scaleFactor).dp)
    ) {
        content()
    }
} 