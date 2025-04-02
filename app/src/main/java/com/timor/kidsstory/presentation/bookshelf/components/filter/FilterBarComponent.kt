package com.timor.kidsstory.presentation.bookshelf.components.filter

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.timor.kidsstory.ui.theme.KidsStoryTheme




enum class FilterOption {
    ALL,
    LEVEL,
    CATEGORY
}

// 책 레벨 (1, 2, 3, 4)
enum class BookLevel(val level: Int) {
    LEVEL_1(1),
    LEVEL_2(2),
    LEVEL_3(3),
    LEVEL_4(4)
}

// 책 카테고리 (LEGEND, FOLKTALE, CULTURE, LIFE)
enum class BookCategory {
    LEGEND,
    FOLKTALE,
    CULTURE,
    LIFE
}


// 필터 상태를 저장하는 데이터 클래스
data class FilterState(
    val selectedFilter: FilterOption = FilterOption.ALL,
    val expandedFilter: FilterOption? = null,
    val selectedLevel: BookLevel? = null,
    val selectedCategory: BookCategory? = null
)

//@Composable
//fun FilterBar(
//    filterState: FilterState,
//    onFilterSelected: (FilterOption) -> Unit,
//    onLevelSelected: (BookLevel) -> Unit,
//    onCategorySelected: (BookCategory) -> Unit,
//    modifier: Modifier = Modifier
//) {
//    Row(
//        modifier = modifier
//            .fillMaxWidth()
//            .background(Color(0xFFFFFCF2))
//            .padding(horizontal = 48.dp, vertical = 8.dp),
//        horizontalArrangement = Arrangement.spacedBy(12.dp),
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        // All 버튼
//        FilterButton(
//            text = "All",
//            isSelected = filterState.selectedFilter == FilterOption.ALL,
//            onClick = { onFilterSelected(FilterOption.ALL) }
//        )
//
//        // Level 버튼과 확장된 옵션들
//        LevelFilterSection(
//            filterState = filterState,
//            isExpanded = filterState.expandedFilter == FilterOption.LEVEL,
//            onLevelFilterClick = { onFilterSelected(FilterOption.LEVEL) },
//            onLevelSelected = onLevelSelected
//        )
//
//        // Category 버튼과 확장된 옵션들
//        CategoryFilterSection(
//            filterState = filterState,
//            isExpanded = filterState.expandedFilter == FilterOption.CATEGORY,
//            onCategoryFilterClick = { onFilterSelected(FilterOption.CATEGORY) },
//            onCategorySelected = onCategorySelected
//        )
//    }
//}

@Composable
fun FilterButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isSelected) {
        when (text) {
            "All" -> Color(0xFF4CAF50)
            "Stage" -> Color(0xFFFFEB3B)
            "Level" -> Color(0xFF2196F3)
            "Category" -> Color(0xFFF44336)
            else -> Color(0xFF9C27B0)
        }
    } else Color.White

    val textColor = if (isSelected) Color.White else Color.DarkGray

    Card(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = BorderStroke(1.dp, Color.LightGray)
    ) {
        Text(
            text = text,
            color = textColor,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Composable
fun LevelFilterSection(
    filterState: FilterState,
    isExpanded: Boolean,
    onLevelFilterClick: () -> Unit,
    onLevelSelected: (BookLevel) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Level 버튼 (선택된 레벨이 있으면 표시)
        LevelButton(
            selectedLevel = filterState.selectedLevel,
            isSelected = filterState.selectedFilter == FilterOption.LEVEL,
            onClick = onLevelFilterClick
        )

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
                LevelBadge(level = BookLevel.LEVEL_1, onLevelSelected = onLevelSelected)
                LevelBadge(level = BookLevel.LEVEL_2, onLevelSelected = onLevelSelected)
                LevelBadge(level = BookLevel.LEVEL_3, onLevelSelected = onLevelSelected)
                LevelBadge(level = BookLevel.LEVEL_4, onLevelSelected = onLevelSelected)
            }
        }
    }
}

@Composable
fun LevelButton(
    selectedLevel: BookLevel?,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) Color(0xFF2196F3) else Color.White
    val textColor = if (isSelected) Color.White else Color.DarkGray

    Card(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = BorderStroke(1.dp, Color.LightGray)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (selectedLevel != null) "Level ${selectedLevel.level}" else "Level",
                color = textColor,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )

            if (selectedLevel == null) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "Expand level options",
                    tint = textColor,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun LevelBadge(
    level: BookLevel,
    onLevelSelected: (BookLevel) -> Unit
) {
    val levelColor = when (level) {
        BookLevel.LEVEL_1 -> Color(0xFF4CAF50) // 녹색
        BookLevel.LEVEL_2 -> Color(0xFFFFEB3B) // 노란색
        BookLevel.LEVEL_3 -> Color(0xFFFF9800) // 주황색
        BookLevel.LEVEL_4 -> Color(0xFFF44336) // 빨간색
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
            text = level.level.toString(),
            color = Color.White,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

//@Composable
//fun CategoryFilterSection(
//    filterState: FilterState,
//    isExpanded: Boolean,
//    onCategoryFilterClick: () -> Unit,
//    onCategorySelected: (BookCategory) -> Unit
//) {
//    Row(
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        // Category 버튼 (선택된 카테고리가 있으면 표시)
//        CategoryButton(
//            selectedCategory = filterState.selectedCategory,
//            isSelected = filterState.selectedFilter == FilterOption.CATEGORY,
//            onClick = onCategoryFilterClick
//        )
//
//        // 확장된 카테고리 옵션들
//        AnimatedVisibility(
//            visible = isExpanded,
//            enter = expandHorizontally(),
//            exit = shrinkHorizontally()
//        ) {
//            Row(
//                modifier = Modifier.padding(start = 8.dp),
//                horizontalArrangement = Arrangement.spacedBy(8.dp)
//            ) {
//                CategoryIcon(
//                    category = BookCategory.LEGEND,
//                    icon = Icons.Default.Close,
//                    contentDescription = "Legend",
//                    onCategorySelected = onCategorySelected
//                )
//                CategoryIcon(
//                    category = BookCategory.FOLKTALE,
//                    icon = Icons.Default.DateRange,
//                    contentDescription = "Folktale",
//                    onCategorySelected = onCategorySelected
//                )
//                CategoryIcon(
//                    category = BookCategory.CULTURE,
//                    icon = Icons.Default.Favorite,
//                    contentDescription = "Culture",
//                    onCategorySelected = onCategorySelected
//                )
//                CategoryIcon(
//                    category = BookCategory.LIFE,
//                    icon = Icons.Default.Place,
//                    contentDescription = "Life",
//                    onCategorySelected = onCategorySelected
//                )
//            }
//        }
//    }
//}

@Composable
fun CategoryButton(
    selectedCategory: BookCategory?,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) Color(0xFFF44336) else Color.White
    val textColor = if (isSelected) Color.White else Color.DarkGray

    Card(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = BorderStroke(1.dp, Color.LightGray)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 선택된 카테고리가 있으면 해당 아이콘을 표시
            if (selectedCategory != null) {
                val icon = when (selectedCategory) {
                    BookCategory.LEGEND -> Icons.Default.Close
                    BookCategory.FOLKTALE -> Icons.Default.Favorite
                    BookCategory.CULTURE -> Icons.Default.Edit
                    BookCategory.LIFE -> Icons.Default.Place
                }

                Icon(
                    imageVector = icon,
                    contentDescription = selectedCategory.name,
                    tint = textColor,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
            }

            Text(
                text = selectedCategory?.name?.lowercase()?.capitalize() ?: "Category",
                color = textColor,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )

            if (selectedCategory == null) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "Expand category options",
                    tint = textColor,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}



// String의 첫 글자를 대문자로 만드는 확장 함수
fun String.capitalize(): String {
    return if (this.isEmpty()) this else this.substring(0, 1).uppercase() + this.substring(1)
}

//@Preview(showBackground = true, widthDp = 800, heightDp = 120)
//@Composable
//fun FilterBarPreview() {
//    KidsStoryTheme {
//        FilterBar(
//            filterState = FilterState(),
//            onFilterSelected = {},
//            onLevelSelected = {},
//            onCategorySelected = {}
//        )
//    }
//}

//@Preview(showBackground = true, widthDp = 800, heightDp = 120)
//@Composable
//fun FilterBarWithLevelExpandedPreview() {
//    KidsStoryTheme {
//        FilterBar(
//            filterState = FilterState(
//                expandedFilter = FilterOption.LEVEL,
//                selectedFilter = FilterOption.LEVEL
//            ),
//            onFilterSelected = {},
//            onLevelSelected = {},
//            onCategorySelected = {}
//        )
//    }
//}

//@Preview(showBackground = true, widthDp = 800, heightDp = 120)
//@Composable
//fun FilterBarWithLevelSelectedPreview() {
//    KidsStoryTheme {
//        FilterBar(
//            filterState = FilterState(
//                selectedFilter = FilterOption.LEVEL,
//                selectedLevel = BookLevel.LEVEL_3
//            ),
//            onFilterSelected = {},
//            onLevelSelected = {},
//            onCategorySelected = {}
//        )
//    }
//}

//@Preview(showBackground = true, widthDp = 800, heightDp = 120)
//@Composable
//fun FilterBarWithCategoryExpandedPreview() {
//    KidsStoryTheme {
//        FilterBar(
//            filterState = FilterState(
//                expandedFilter = FilterOption.CATEGORY,
//                selectedFilter = FilterOption.CATEGORY
//            ),
//            onFilterSelected = {},
//            onLevelSelected = {},
//            onCategorySelected = {}
//        )
//    }
//}

//@Preview(showBackground = true, widthDp = 800, heightDp = 120)
//@Composable
//fun FilterBarWithCategorySelectedPreview() {
//    KidsStoryTheme {
//        FilterBar(
//            filterState = FilterState(
//                selectedFilter = FilterOption.CATEGORY,
//                selectedCategory = BookCategory.FOLKTALE
//            ),
//            onFilterSelected = {},
//            onLevelSelected = {},
//            onCategorySelected = {}
//        )
//    }
//}
//
