package com.timor.kidsstory.presentation.bookshelf.components.filter

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.orhanobut.logger.Logger
import com.timor.kidsstory.R
import com.timor.kidsstory.presentation.bookshelf.model.FilterBarCategory
import com.timor.kidsstory.presentation.bookshelf.model.FilterBarState
import com.timor.kidsstory.presentation.bookshelf.model.FilterBookCategory
import com.timor.kidsstory.presentation.bookshelf.model.FilterLevel
import com.timor.kidsstory.ui.components.LocalizedText
import com.timor.kidsstory.ui.theme.AppColors
import com.timor.kidsstory.ui.theme.AppTextStyles
import com.timor.kidsstory.ui.theme.KidsStoryTheme


/*
* 메인 - 필터바
* Create by JaeYeon Kim
* @since 2025.04.02
* */
@Composable
fun FilterBar(
    filterBarState: FilterBarState = FilterBarState(),
    onAllClick: () -> Unit = {},
    onStageClick: () -> Unit = {},
    onCategoryClick: () -> Unit = {},
    onLevelClick: (FilterLevel) -> Unit = {},
    onBookCategoryClick: (FilterBookCategory) -> Unit = {},
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 48.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        FilterBarButton(textResId = R.string.filter_all, isSelected = filterBarState.selectedFilter == FilterBarCategory.All) {
            onAllClick()
        }

        Icon(
            painter = painterResource(R.drawable.separation_bar),
            tint = AppColors.unknown400,
            contentDescription = null
        )

        FilterBarButton(textResId = R.string.filter_stage, isSelected = filterBarState.selectedFilter == FilterBarCategory.STAGE) {
            onStageClick()
        }

        LevelFilterBar(
            isExpanded = filterBarState.isStageFilterExpanded,
            selectedLevel = filterBarState.selectedStage,
        ) { level ->
            onLevelClick(level)
        }

        Icon(
            painter = painterResource(R.drawable.separation_bar),
            tint = AppColors.unknown400,
            contentDescription = null
        )

        FilterBarButton(textResId = R.string.filter_category, isSelected = filterBarState.selectedFilter == FilterBarCategory.CATEGORY) {
            onCategoryClick()
        }

        CategoryFilterBar(
            isExpanded = filterBarState.isCategoryFilterExpanded,
            selectedCategory = filterBarState.selectedCategory,
        ) { bookCategory ->
            onBookCategoryClick(bookCategory)
        }
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
                .noRippleClickable {
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
                .noRippleClickable {
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