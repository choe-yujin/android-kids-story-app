package com.timor.kidsstory.presentation.bookshelf.components.filter

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.orhanobut.logger.Logger
import com.timor.kidsstory.presentation.bookshelf.model.FilterLevel
import com.timor.kidsstory.ui.theme.AppColors
import com.timor.kidsstory.ui.theme.AppTextStyles
import com.timor.kidsstory.ui.theme.KidsStoryTheme

@Composable
fun LevelButton(
    level: FilterLevel,
    isSelected: Boolean = false,
    onLevelSelected: (FilterLevel) -> Unit
) {
    val levelBackgroundColor = when (level) {
        FilterLevel.ONE -> Color(0xFF47A714) // 녹색
        FilterLevel.TWO -> Color(0xFFC9A93B) // 노란색
        FilterLevel.THREE -> Color(0xFFE38400) // 주황색
        FilterLevel.FOUR -> Color(0xFFC52820) // 빨간색
    }

    val levelBorderColor = when (level) {
        FilterLevel.ONE -> AppColors.level1
        FilterLevel.TWO -> AppColors.level2
        FilterLevel.THREE -> AppColors.level3
        FilterLevel.FOUR -> AppColors.level4
    }


    // 선택여부에 따라 디자인 다르게 적용
    if (isSelected) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(levelBackgroundColor)
                .border(width = 2.dp, color = levelBorderColor, shape = CircleShape)
                .clickable { onLevelSelected(level) },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = level.displayName.toString(),
                color = Color.White,
                style = AppTextStyles.gummyMediumSemibold,
                textAlign = TextAlign.Center
            )
        }
    } else {
        Logger.e("선택 X")
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(levelBackgroundColor)
                .clickable { onLevelSelected(level) },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = level.displayName.toString(),
                color = Color.White,
                style = AppTextStyles.gummyMediumSemibold,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun LevelFilterBar(
    isExpanded: Boolean,
    selectedLevel: FilterLevel? = null,
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
                LevelButton(
                    level = FilterLevel.ONE,
                    isSelected = selectedLevel == FilterLevel.ONE,
                    onLevelSelected = onLevelSelected
                )
                LevelButton(
                    level = FilterLevel.TWO,
                    isSelected = selectedLevel == FilterLevel.TWO,
                    onLevelSelected = onLevelSelected
                )
                LevelButton(
                    level = FilterLevel.THREE,
                    isSelected = selectedLevel == FilterLevel.THREE,
                    onLevelSelected = onLevelSelected
                )
                LevelButton(
                    level = FilterLevel.FOUR,
                    isSelected = selectedLevel == FilterLevel.FOUR,
                    onLevelSelected = onLevelSelected
                )
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