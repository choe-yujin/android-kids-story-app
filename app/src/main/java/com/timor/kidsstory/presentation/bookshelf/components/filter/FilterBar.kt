package com.timor.kidsstory.presentation.bookshelf.components.filter

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.timor.kidsstory.R
import com.timor.kidsstory.presentation.bookshelf.model.FilterBarCategory
import com.timor.kidsstory.presentation.bookshelf.model.FilterBarState
import com.timor.kidsstory.presentation.bookshelf.model.FilterBookCategory
import com.timor.kidsstory.presentation.bookshelf.model.FilterLevel
import com.timor.kidsstory.ui.components.LocalizedText
import com.timor.kidsstory.ui.theme.AppColors
import com.timor.kidsstory.ui.theme.AppTextStyles
import com.timor.kidsstory.ui.theme.KidsStoryTheme


/**
 * 메인 - 필터바
 * Create by JaeYeon Kim
 * @since 2025.04.02
 */
@Composable
fun FilterBar(
    filterBarState: FilterBarState = FilterBarState(),
    currentLanguageCode: String = "en", // 현재 언어 코드
    onAllClick: () -> Unit = {},
    onStageClick: () -> Unit = {},
    onCategoryClick: () -> Unit = {},
    onLevelClick: (FilterLevel) -> Unit = {},
    onBookCategoryClick: (FilterBookCategory) -> Unit = {},
) {
    // 화면 너비에 따른 동적 패딩 계산
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    
    // 오른쪽 세로 바 너비 + 간격을 고려한 패딩
    val horizontalPadding = when {
        screenWidth >= 800 -> 48.dp // 태블릿
        else -> 24.dp // 휴대폰
    }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 48.dp, end = 16.dp), // 설정 아이콘 시작점에 맞춰서 고정
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Top,
    ) {
        FilterBarButton(textResId = R.string.filter_all, isSelected = filterBarState.selectedFilter == FilterBarCategory.All) {
            onAllClick()
        }

        Box(
            modifier = Modifier.height(42.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.separation_bar),
                tint = AppColors.unknown400,
                contentDescription = null
            )
        }

        FilterBarButton(textResId = R.string.filter_stage, isSelected = filterBarState.selectedFilter == FilterBarCategory.STAGE) {
            onStageClick()
        }

        LevelFilterBar(
            isExpanded = filterBarState.isStageFilterExpanded,
            selectedLevel = filterBarState.selectedStage,
            currentLanguageCode = currentLanguageCode,
            onLevelSelected = { level ->
                onLevelClick(level)
            }
        )

        Box(
            modifier = Modifier.height(42.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.separation_bar),
                tint = AppColors.unknown400,
                contentDescription = null
            )
        }

        FilterBarButton(textResId = R.string.filter_category, isSelected = filterBarState.selectedFilter == FilterBarCategory.CATEGORY) {
            onCategoryClick()
        }

        CategoryFilterBar(
            isExpanded = filterBarState.isCategoryFilterExpanded,
            selectedCategory = filterBarState.selectedCategory,
            currentLanguageCode = currentLanguageCode,
            onCategorySelected = { bookCategory ->
                onBookCategoryClick(bookCategory)
            }
        )
    }
}

@Composable
fun FilterBarButton(
    textResId: Int,
    isSelected: Boolean,
    onClick: () -> Unit = {},
) {
    // 선택 여부에 따라 다르게
    if (!isSelected) {
        Box(
            modifier = Modifier
                .background(AppColors.neutralWhite, shape = RoundedCornerShape(50.dp))
                .border(width = 2.dp, color = AppColors.unknown200, shape = RoundedCornerShape(50.dp))
                .clickable {
                    onClick()
                }
        ) {
            LocalizedText(
                resId = textResId,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                style = AppTextStyles.gummyMedium,
                fontSize = 21.sp,
                color = AppColors.unknown200
            )
        }
    } else {
        Box(
            modifier = Modifier
                .background(AppColors.unknown300, shape = RoundedCornerShape(size = 50.dp))
                .clickable {
                    onClick()
                }
        ) {
            LocalizedText(
                resId = textResId,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                style = AppTextStyles.gummyMedium,
                fontSize = 21.sp,
                color = AppColors.secondary200
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FilterBarPreview() {
    KidsStoryTheme {
        FilterBar()
    }
}

@Preview(showBackground = true)
@Composable
fun FilterBarButtonPreview() {
    KidsStoryTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            FilterBarButton(textResId = R.string.filter_all, isSelected = false)
            FilterBarButton(textResId = R.string.filter_all, isSelected = true)
        }
    }
}
