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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.timor.kidsstory.presentation.bookshelf.model.FilterBookCategory

@Composable
fun CategoryFilterBar(
    isExpanded: Boolean,
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
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CategoryIcon(
                    category = FilterBookCategory.LEGEND,
                    icon = Icons.Default.Close,
                    contentDescription = "Legend",
                    onCategorySelected = onCategorySelected
                )
                CategoryIcon(
                    category = FilterBookCategory.FOLKTALE,
                    icon = Icons.Default.DateRange,
                    contentDescription = "Folktale",
                    onCategorySelected = onCategorySelected
                )
                CategoryIcon(
                    category = FilterBookCategory.CULTURE,
                    icon = Icons.Default.Favorite,
                    contentDescription = "Culture",
                    onCategorySelected = onCategorySelected
                )
                CategoryIcon(
                    category = FilterBookCategory.LIFE,
                    icon = Icons.Default.Place,
                    contentDescription = "Life",
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
    onCategorySelected: (FilterBookCategory) -> Unit
) {
    val categoryColor = when (category) {
        FilterBookCategory.LEGEND -> Color(0xFF3F51B5)
        FilterBookCategory.FOLKTALE -> Color(0xFF009688)
        FilterBookCategory.CULTURE -> Color(0xFFE91E63)
        FilterBookCategory.LIFE -> Color(0xFFFF5722)
    }

    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(categoryColor)
            .clickable { onCategorySelected(category) },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = Color.White,
            modifier = Modifier.size(20.dp)
        )
    }
}