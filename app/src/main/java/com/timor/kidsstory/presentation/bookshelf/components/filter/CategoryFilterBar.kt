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
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.timor.kidsstory.R
import com.timor.kidsstory.presentation.bookshelf.model.FilterBookCategory

@Composable
fun CategoryFilterBar(
    isExpanded: Boolean,
    selectedCategory: FilterBookCategory? = null,
    onCategorySelected: (FilterBookCategory) -> Unit = {},
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 확장된 카테고리 옵션들
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandHorizontally(),
            exit = shrinkHorizontally()
        ) {
            Row(
                modifier = Modifier.padding(start = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically // 아이콘들을 카테고리 버튼과 완벽하게 같은 높이에 정렬
            ) {
                CategoryIcon(
                    category = FilterBookCategory.LEGEND,
                    icon = ImageVector.vectorResource(R.drawable.legend),
                    contentDescription = "Legend",
                    isSelected = selectedCategory == FilterBookCategory.LEGEND,
                    onCategorySelected = onCategorySelected
                )
                CategoryIcon(
                    category = FilterBookCategory.FOLKTALE,
                    icon = ImageVector.vectorResource(R.drawable.folktale),
                    contentDescription = "Folktale",
                    isSelected = selectedCategory == FilterBookCategory.FOLKTALE,
                    onCategorySelected = onCategorySelected
                )
                CategoryIcon(
                    category = FilterBookCategory.CULTURE,
                    icon = ImageVector.vectorResource(R.drawable.culture),
                    contentDescription = "Culture",
                    isSelected = selectedCategory == FilterBookCategory.CULTURE,
                    onCategorySelected = onCategorySelected
                )
                CategoryIcon(
                    category = FilterBookCategory.LIFE,
                    icon = ImageVector.vectorResource(R.drawable.life),
                    contentDescription = "Life",
                    isSelected = selectedCategory == FilterBookCategory.LIFE,
                    onCategorySelected = onCategorySelected
                )
            }
        }
    }
}


@Composable
fun CategoryIcon(
    category: FilterBookCategory,
    icon: ImageVector,
    contentDescription: String,
    isSelected: Boolean = false,
    onCategorySelected: (FilterBookCategory) -> Unit
) {
    val categoryBackgroundColor = when (category) {
        FilterBookCategory.LEGEND -> Color(0xFFEBDCD2)
        FilterBookCategory.FOLKTALE -> Color(0xFFE4E9D6)
        FilterBookCategory.CULTURE -> Color(0xFFDFE9F2)
        FilterBookCategory.LIFE -> Color(0xFFD9EEE7)
    }

    val categoryBorderColor = when (category) {
        FilterBookCategory.LEGEND -> Color(0xFF6B442B)
        FilterBookCategory.FOLKTALE -> Color(0xFF60A917)
        FilterBookCategory.CULTURE -> Color(0xFF7DBDF9)
        FilterBookCategory.LIFE -> Color(0xFF31C292)
    }

    // 아이콘만 표시
    Box(
        modifier = Modifier.size(34.dp),
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(categoryBackgroundColor)
                    .border(width = 2.dp, color = categoryBorderColor, shape = CircleShape)
                    .clickable { onCategorySelected(category) },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = contentDescription,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(20.dp)
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(categoryBackgroundColor)
                    .clickable { onCategorySelected(category) },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = contentDescription,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
