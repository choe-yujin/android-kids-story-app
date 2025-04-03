package com.timor.kidsstory.presentation.bookshelf.components.filter

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.timor.kidsstory.presentation.bookshelf.model.FilterLevel
import com.timor.kidsstory.ui.theme.KidsStoryTheme

@Composable
fun LevelButton(
    level: FilterLevel,
    onLevelSelected: (FilterLevel) -> Unit
) {
    val levelColor = when (level) {
        FilterLevel.ONE   -> Color(0xFF4CAF50) // 녹색
        FilterLevel.TWO -> Color(0xFFFFEB3B) // 노란색
        FilterLevel.THREE -> Color(0xFFFF9800) // 주황색
        FilterLevel.FOUR -> Color(0xFFF44336) // 빨간색
    }

    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(levelColor)
            .clickable { onLevelSelected(level) },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = level.displayName.toString(),
            color = Color.White,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun LevelFilterBar(
    isExpanded: Boolean,
    onLevelSelected: (FilterLevel) -> Unit = {}
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 확장된 레벨 옵션들
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandHorizontally(),
            exit = shrinkHorizontally()
        ) {
            Row(
                modifier = Modifier.padding(start = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LevelButton(level = FilterLevel.ONE, onLevelSelected = onLevelSelected)
                LevelButton(level = FilterLevel.TWO, onLevelSelected = onLevelSelected)
                LevelButton(level = FilterLevel.THREE, onLevelSelected = onLevelSelected)
                LevelButton(level = FilterLevel.FOUR, onLevelSelected = onLevelSelected)
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun LevelButtonPreview() {
    KidsStoryTheme {
        LevelButton(
            level = FilterLevel.ONE,
            onLevelSelected = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LevelFilterBarPreview() {
    KidsStoryTheme {
        LevelFilterBar(
            isExpanded = true,
            onLevelSelected = {}
        )
    }
}