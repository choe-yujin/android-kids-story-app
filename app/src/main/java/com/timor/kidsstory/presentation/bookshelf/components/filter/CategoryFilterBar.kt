package com.timor.kidsstory.presentation.bookshelf.components.filter

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.timor.kidsstory.R
import com.timor.kidsstory.presentation.bookshelf.model.FilterBookCategory
import com.timor.kidsstory.presentation.util.getCategoryName

@Composable
fun CategoryFilterBar(
    isExpanded: Boolean,
    selectedCategory: FilterBookCategory? = null,
    onCategorySelected: (FilterBookCategory) -> Unit = {},
) {
    Row(
        verticalAlignment = Alignment.Top
    ) {
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandHorizontally(),
            exit = shrinkHorizontally()
        ) {
            BoxWithConstraints {
                val itemWidth = maxWidth / FilterBookCategory.values().size

                // Layer 1: Icons
                Row(
                    modifier = Modifier.padding(start = 8.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    FilterBookCategory.values().forEach { category ->
                        CategoryIcon(
                            modifier = Modifier.width(itemWidth),
                            category = category,
                            isSelected = selectedCategory == category,
                            onCategorySelected = onCategorySelected
                        )
                    }
                }

                // Layer 2: Text
                if (selectedCategory != null) {
                    val selectedIndex = selectedCategory.ordinal
                    val textXOffset = (itemWidth * selectedIndex) + (itemWidth / 2) - (maxWidth / 2) + 8.dp

                    Box(
                        contentAlignment = Alignment.TopCenter,
                        modifier = Modifier
                            .fillMaxSize()
                            .offset(x = textXOffset)
                    ) {
                        Text(
                            text = getCategoryName(LocalContext.current, selectedCategory),
                            fontSize = 12.sp,
                            color = Color.Black,
                            maxLines = 1,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 38.dp) // Position text below icon
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun CategoryIcon(
    modifier: Modifier = Modifier,
    category: FilterBookCategory,
    isSelected: Boolean = false,
    onCategorySelected: (FilterBookCategory) -> Unit
) {
    val categoryBackgroundColor = when (category) {
        FilterBookCategory.FOLKTALES_HISTORY -> Color(0xFFE4E9D6)
        FilterBookCategory.CULTURE_WORLD -> Color(0xFFDFE9F2)
        FilterBookCategory.DAILY_LIFE -> Color(0xFFD9EEE7)
        FilterBookCategory.ENVIRONMENT -> Color(0xFFE8F5E9)
        FilterBookCategory.SCIENCE_NATURE -> Color(0xFFE1F5FE)
        FilterBookCategory.ADVENTURE_FANTASY -> Color(0xFFFFF3E0)
        FilterBookCategory.SOCIAL_EMOTIONAL -> Color(0xFFF3E5F5)
    }

    val categoryBorderColor = when (category) {
        FilterBookCategory.FOLKTALES_HISTORY -> Color(0xFF60A917)
        FilterBookCategory.CULTURE_WORLD -> Color(0xFF7DBDF9)
        FilterBookCategory.DAILY_LIFE -> Color(0xFF31C292)
        FilterBookCategory.ENVIRONMENT -> Color(0xFF4CAF50)
        FilterBookCategory.SCIENCE_NATURE -> Color(0xFF2196F3)
        FilterBookCategory.ADVENTURE_FANTASY -> Color(0xFFFF9800)
        FilterBookCategory.SOCIAL_EMOTIONAL -> Color(0xFF9C27B0)
    }

    val icon = when (category) {
        FilterBookCategory.CULTURE_WORLD -> ImageVector.vectorResource(R.drawable.culture)
        FilterBookCategory.FOLKTALES_HISTORY -> ImageVector.vectorResource(R.drawable.folktale)
        else -> ImageVector.vectorResource(R.drawable.life) // Placeholder
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        val iconBoxModifier = if (isSelected) {
            Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(categoryBackgroundColor)
                .border(width = 2.dp, color = categoryBorderColor, shape = CircleShape)
        } else {
            Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(categoryBackgroundColor)
        }

        Box(
            modifier = iconBoxModifier.clickable { onCategorySelected(category) },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = category.displayName,
                tint = Color.Unspecified,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}