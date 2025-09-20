package com.timor.kidsstory.presentation.bookshelf.components.filter

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.timor.kidsstory.R
import com.timor.kidsstory.presentation.bookshelf.model.FilterBookCategory
import com.timor.kidsstory.ui.theme.ResponsiveTextUtils

@Composable
fun CategoryFilterBar(
    isExpanded: Boolean,
    selectedCategory: FilterBookCategory? = null,
    currentLanguageCode: String = "en",
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
            val configuration = LocalConfiguration.current
            val screenWidth = configuration.screenWidthDp
            val scaleFactor = ResponsiveTextUtils.getScreenScaleFactor()
            
            // 사용 가능한 영역 계산 (전체 - All/Stage/Category 버튼들 - 세로바 공간)
            val filterButtonsWidth = 300 // All, Stage, Category 버튼들 예상 너비
            val availableWidth = screenWidth - filterButtonsWidth - 73 // 세로바 공간
            
            // 태블릿에서는 크고 여유롭게, 휴대폰에서는 컴팩트하게
            val iconSize = when {
                screenWidth >= 800 -> (64 * scaleFactor).dp // 태블릿: 큰 아이콘
                else -> 48.dp // 휴대폰: 기존 크기
            }
            
            val spacing = when {
                screenWidth >= 800 -> (8 * scaleFactor).dp // 태블릿: 여유로운 간격
                else -> 0.dp // 휴대폰: 간격 없음
            }
            
            val marginSpace = when {
                screenWidth >= 800 -> (32 * scaleFactor).dp // 태블릿: 여유 공간
                else -> 16.dp // 휴대폰: 최소 여유 공간
            }
            
            // 7개 아이콘의 최소 필요 공간 계산
            val totalNeededWidth = (iconSize * 7) + (spacing * 6) + marginSpace
            
            Row(
                verticalAlignment = Alignment.Top
            ) {
                // 가로 스크롤 가능한 아이콘 영역
                Row(
                    modifier = Modifier
                        .width(availableWidth.dp.coerceAtMost(totalNeededWidth))
                        .horizontalScroll(rememberScrollState())
                        .padding(start = 8.dp),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(spacing)
                ) {
                    FilterBookCategory.values().forEach { category ->
                        CategoryIcon(
                            category = category,
                            isSelected = selectedCategory == category,
                            onCategorySelected = onCategorySelected,
                            showTextLabel = true,
                            currentLanguageCode = currentLanguageCode,
                            iconSize = iconSize,
                            isTablet = screenWidth >= 800
                        )
                    }
                }
                
                // 스크롤 가능 표시 (공간이 부족한 경우에만)
                if (availableWidth < totalNeededWidth.value) {
                    Icon(
                        painter = painterResource(R.drawable.arrow_right),
                        contentDescription = "스크롤 가능",
                        tint = Color(0xFF666666),
                        modifier = Modifier
                            .size(16.dp)
                            .padding(start = 4.dp, top = 8.dp)
                    )
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
    onCategorySelected: (FilterBookCategory) -> Unit,
    showTextLabel: Boolean = false,
    currentLanguageCode: String = "ko",
    iconSize: androidx.compose.ui.unit.Dp = 48.dp,
    isTablet: Boolean = false
) {
    val scaleFactor = ResponsiveTextUtils.getScreenScaleFactor()
    
    // 선택된 아이콘은 2dp 더 크게 (태블릿에서는 4dp)
    val selectionBoost = if (isTablet) 4.dp else 2.dp
    val actualIconSize = if (isSelected) iconSize + selectionBoost else iconSize
    val iconInnerSize = actualIconSize * 0.75f

    // 태블릿에서는 더 큰 텍스트 공간
    val textAreaWidth = if (isTablet) iconSize + (16 * scaleFactor).dp else iconSize + 8.dp

    Column(
        modifier = modifier
            .width(textAreaWidth)
            .clickable { onCategorySelected(category) },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // 경계 없이 아이콘만 표시
        Icon(
            imageVector = ImageVector.vectorResource(category.iconRes),
            contentDescription = stringResource(category.displayNameRes),
            tint = Color.Unspecified,
            modifier = Modifier.size(iconInnerSize)
        )

        if (showTextLabel) {
            // 언어별 폰트 적용
            val fontFamily = when (currentLanguageCode) {
                "ko" -> FontFamily(Font(R.font.cookierun_regular))
                "en", "tet" -> FontFamily(Font(R.font.gummy_italic_variable))
                else -> FontFamily(Font(R.font.cookierun_regular))
            }

            // 태블릿에서는 더 큰 텍스트
            val textSize = when {
                isTablet -> (14 * scaleFactor).sp // 태블릿: 큰 텍스트
                else -> 10.sp // 휴대폰: 기존 크기
            }

            val topPadding = if (isTablet) (6 * scaleFactor).dp else 3.dp

            Text(
                text = stringResource(category.displayNameRes),
                style = TextStyle(
                    fontSize = textSize,
                    lineHeight = (textSize.value + 2).sp,
                    fontFamily = fontFamily,
                    fontWeight = FontWeight(400),
                    fontStyle = FontStyle.Italic,
                    color = if (isSelected) Color(0xFF000000) else Color(0xFF919191),
                ),
                maxLines = 1,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = topPadding)
            )
        }
    }
}
