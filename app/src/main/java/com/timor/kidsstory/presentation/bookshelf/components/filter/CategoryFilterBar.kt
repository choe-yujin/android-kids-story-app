package com.timor.kidsstory.presentation.bookshelf.components.filter

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Place
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.timor.kidsstory.presentation.bookshelf.model.BookCategory
import com.timor.kidsstory.presentation.bookshelf.model.FilterOption

@Composable
fun CategoryFilterBar(
    filterState: FilterState,
    isExpanded: Boolean,
    onCategoryFilterClick: () -> Unit,
    onCategorySelected: (BookCategory) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Category 버튼 (선택된 카테고리가 있으면 표시)
        CategoryButton(
            selectedCategory = filterState.selectedCategory,
            isSelected = filterState.selectedFilter == FilterOption.CATEGORY,
            onClick = onCategoryFilterClick
        )

        // 확장된 카테고리 옵션들
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandHorizontally(),
            exit = shrinkHorizontally()
        ) {
            Row(
                modifier = Modifier.padding(start = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CategoryIcon(
                    category = BookCategory.LEGEND,
                    icon = Icons.Default.Close,
                    contentDescription = "Legend",
                    onCategorySelected = onCategorySelected
                )
                CategoryIcon(
                    category = BookCategory.FOLKTALE,
                    icon = Icons.Default.DateRange,
                    contentDescription = "Folktale",
                    onCategorySelected = onCategorySelected
                )
                CategoryIcon(
                    category = BookCategory.CULTURE,
                    icon = Icons.Default.Favorite,
                    contentDescription = "Culture",
                    onCategorySelected = onCategorySelected
                )
                CategoryIcon(
                    category = BookCategory.LIFE,
                    icon = Icons.Default.Place,
                    contentDescription = "Life",
                    onCategorySelected = onCategorySelected
                )
            }
        }
    }
}