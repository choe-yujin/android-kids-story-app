package com.timor.kidsstory.presentation.bookshelf.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.timor.kidsstory.domain.model.Category
import com.timor.kidsstory.domain.model.ReadingLevel

/**
 * 카테고리 필터 컴포넌트
 * 가로 스크롤 가능한 칩 형태로 카테고리 표시
 */
@Composable
fun CategoryFilter(
    categories: List<Category>,
    selectedCategory: Category?,
    onCategorySelected: (Category?) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        // "전체" 칩
        item {
            FilterChip(
                selected = selectedCategory == null,
                onClick = { onCategorySelected(null) },
                label = {
                    Text(
                        text = "전체",
                        fontSize = 14.sp,
                        fontWeight = if (selectedCategory == null) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
        
        // 카테고리 칩들
        items(categories) { category ->
            FilterChip(
                selected = selectedCategory == category,
                onClick = { 
                    onCategorySelected(
                        if (selectedCategory == category) null else category
                    )
                },
                label = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = category.description,
                            fontSize = 14.sp,
                            fontWeight = if (selectedCategory == category) FontWeight.Bold else FontWeight.Normal
                        )
                        if (selectedCategory == category) {
                            Text(
                                text = category.displayName,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                            )
                        }
                    }
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    }
}

/**
 * 레벨 필터 컴포넌트
 * 가로 스크롤 가능한 레벨 선택기
 */
@Composable
fun LevelFilter(
    levels: List<ReadingLevel>,
    selectedLevel: ReadingLevel?,
    onLevelSelected: (ReadingLevel?) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        // "전체 레벨" 칩
        item {
            FilterChip(
                selected = selectedLevel == null,
                onClick = { onLevelSelected(null) },
                label = {
                    Text(
                        text = "전체 레벨",
                        fontSize = 14.sp,
                        fontWeight = if (selectedLevel == null) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.secondary,
                    selectedLabelColor = MaterialTheme.colorScheme.onSecondary
                )
            )
        }
        
        // 레벨 칩들
        items(levels) { level ->
            FilterChip(
                selected = selectedLevel == level,
                onClick = { 
                    onLevelSelected(
                        if (selectedLevel == level) null else level
                    )
                },
                label = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Level ${level.level}",
                            fontSize = 14.sp,
                            fontWeight = if (selectedLevel == level) FontWeight.Bold else FontWeight.Normal
                        )
                        Text(
                            text = level.ageRange,
                            fontSize = 11.sp,
                            color = if (selectedLevel == level) 
                                MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.8f)
                            else 
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = when(level.level) {
                        1 -> Color(0xFF4CAF50) // 초록
                        2 -> Color(0xFF2196F3) // 파랑
                        3 -> Color(0xFFFFC107) // 노랑
                        4 -> Color(0xFFFF9800) // 오렌지
                        5 -> Color(0xFFF44336) // 빨강
                        else -> MaterialTheme.colorScheme.secondary
                    },
                    selectedLabelColor = Color.White
                )
            )
        }
    }
}

/**
 * 태그 필터 컴포넌트
 * 선택된 카테고리의 태그를 표시하고 선택 가능
 */
@Composable
fun TagFilter(
    tags: List<String>,
    selectedTags: List<String>,
    onTagSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (tags.isEmpty()) return
    
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(tags) { tag ->
            FilterChip(
                selected = tag in selectedTags,
                onClick = { onTagSelected(tag) },
                label = {
                    Text(
                        text = "#$tag",
                        fontSize = 12.sp
                    )
                },
                modifier = Modifier.height(32.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.tertiary,
                    selectedLabelColor = MaterialTheme.colorScheme.onTertiary,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }
    }
}