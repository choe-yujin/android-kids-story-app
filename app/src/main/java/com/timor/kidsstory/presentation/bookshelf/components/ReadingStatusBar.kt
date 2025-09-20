package com.timor.kidsstory.presentation.bookshelf.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.timor.kidsstory.R
import com.timor.kidsstory.ui.theme.ResponsiveTextUtils

/**
 * 읽음 상태 필터
 */
enum class ReadingStatusFilter {
    ALL, READING, READ, UNREAD
}

/**
 * 오른쪽 세로 읽음 상태 바 - 내용에 맞춰 동적 크기 조정
 */
@Composable
fun ReadingStatusBar(
    modifier: Modifier = Modifier,
    currentLanguageCode: String = "en",
    hasBooks: Boolean = true, // 현재 필터에 책이 있는지
    selectedStatus: ReadingStatusFilter = ReadingStatusFilter.ALL,
    onStatusSelected: (ReadingStatusFilter) -> Unit = {}
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val scaleFactor = ResponsiveTextUtils.getScreenScaleFactor()
    
    // 태블릿에서는 더 크고 여유롭게
    val isTablet = screenWidth >= 800
    
    // 가로 길이를 더 작게 조정
    val barWidth = if (isTablet) {
        (60 * scaleFactor).dp // 태블릿: 80dp에서 60dp로 축소
    } else {
        (45 * scaleFactor).dp // 휴대폰: 57dp에서 45dp로 축소
    }
    
    // 아이콘과 텍스트 크기 - 아이콘들이 온전히 보이는 것이 우선
    val iconSize = if (isTablet) {
        (24 * scaleFactor).dp // 태블릿: 충분한 크기 확보
    } else {
        (16 * scaleFactor).dp // 휴대폰: 적절한 크기
    }
    
    val textSize = if (isTablet) {
        (10 * scaleFactor).sp
    } else {
        (8 * scaleFactor).sp
    }
    
    // 간격을 충분히 확보 - 아이콘들이 잘리지 않도록
    val itemSpacing = if (isTablet) {
        (0 * scaleFactor).dp // 아이콘과 텍스트 사이 적절한 간격
    } else {
        (4 * scaleFactor).dp // 최소 간격 확보
    }
    
    val betweenItemSpacing = if (isTablet) {
        (12 * scaleFactor).dp // 아이템 간 충분한 간격
    } else {
        (8 * scaleFactor).dp // 아이템 간 여유 간격
    }
    
    val verticalPadding = if (isTablet) {
        (16 * scaleFactor).dp // 충분한 상하 패딩
    } else {
        (12 * scaleFactor).dp // 적절한 상하 패딩
    }
    
    // 정확한 크기 계산 - 아이콘들이 모두 온전히 보이도록
    val textHeight = (textSize.value * 1.2f).dp // 텍스트 높이 여유 확보
    val itemHeight = iconSize + itemSpacing + textHeight
    
    // 전체 높이를 충분히 확보 - 아이콘들이 절대 잘리지 않도록 + Unread 텍스트까지 완전히 보이도록
    val totalHeight = verticalPadding * 2 + (itemHeight * 4) + (betweenItemSpacing * 3) + (24 * scaleFactor).dp // 추가 여유 공간을 더 크게
    
    Box(
        modifier = modifier
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(12.dp),
                spotColor = Color(0x40000000),
                ambientColor = Color(0x40000000)
            )
            .width(barWidth)
            .height(totalHeight) // 내용에 정확히 맞는 높이
            .background(
                color = Color.White,
                shape = RoundedCornerShape(size = 12.dp)
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = verticalPadding),
            verticalArrangement = Arrangement.spacedBy(betweenItemSpacing),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // All 상태
            ReadingStatusItem(
                activeIconRes = R.drawable.ic_all,
                inactiveIconRes = R.drawable.ic_all_none,
                text = stringResource(R.string.reading_status_all),
                isSelected = selectedStatus == ReadingStatusFilter.ALL,
                isEnabled = hasBooks,
                iconSize = iconSize,
                textSize = textSize,
                itemSpacing = itemSpacing,
                currentLanguageCode = currentLanguageCode,
                onClick = { if (hasBooks) onStatusSelected(ReadingStatusFilter.ALL) }
            )
            
            // Reading 상태
            ReadingStatusItem(
                activeIconRes = R.drawable.ic_reading,
                inactiveIconRes = R.drawable.ic_reading_none,
                text = stringResource(R.string.reading_status_reading),
                isSelected = selectedStatus == ReadingStatusFilter.READING,
                isEnabled = hasBooks,
                iconSize = iconSize,
                textSize = textSize,
                itemSpacing = itemSpacing,
                currentLanguageCode = currentLanguageCode,
                onClick = { if (hasBooks) onStatusSelected(ReadingStatusFilter.READING) }
            )
            
            // Read 상태
            ReadingStatusItem(
                activeIconRes = R.drawable.ic_read,
                inactiveIconRes = R.drawable.ic_read_none,
                text = stringResource(R.string.reading_status_read),
                isSelected = selectedStatus == ReadingStatusFilter.READ,
                isEnabled = hasBooks,
                iconSize = iconSize,
                textSize = textSize,
                itemSpacing = itemSpacing,
                currentLanguageCode = currentLanguageCode,
                onClick = { if (hasBooks) onStatusSelected(ReadingStatusFilter.READ) }
            )
            
            // Unread 상태
            ReadingStatusItem(
                activeIconRes = R.drawable.ic_unread,
                inactiveIconRes = R.drawable.ic_unread_none,
                text = stringResource(R.string.reading_status_unread),
                isSelected = selectedStatus == ReadingStatusFilter.UNREAD,
                isEnabled = hasBooks,
                iconSize = iconSize,
                textSize = textSize,
                itemSpacing = itemSpacing,
                currentLanguageCode = currentLanguageCode,
                onClick = { if (hasBooks) onStatusSelected(ReadingStatusFilter.UNREAD) }
            )
        }
    }
}

@Composable
private fun ReadingStatusItem(
    activeIconRes: Int,
    inactiveIconRes: Int,
    text: String,
    isSelected: Boolean,
    isEnabled: Boolean,
    iconSize: androidx.compose.ui.unit.Dp,
    textSize: androidx.compose.ui.unit.TextUnit,
    itemSpacing: androidx.compose.ui.unit.Dp,
    currentLanguageCode: String,
    onClick: () -> Unit
) {
    // 언어별 폰트
    val fontFamily = when (currentLanguageCode) {
        "ko" -> FontFamily(Font(R.font.cookierun_regular))
        "en", "tet" -> FontFamily(Font(R.font.gummy_italic_variable))
        else -> FontFamily(Font(R.font.cookierun_regular))
    }
    
    // 상태에 따른 아이콘과 색상 결정
    val iconRes = when {
        !isEnabled -> inactiveIconRes // 비활성화: _none 아이콘
        isSelected -> activeIconRes // 선택됨: 일반 아이콘
        else -> inactiveIconRes // 선택 안됨: _none 아이콘
    }
    
    val textColor = when {
        !isEnabled -> Color(0xFF919191) // 비활성화: #919191
        isSelected -> Color(0xFF000000) // 선택됨: 검정색
        else -> Color(0xFF919191) // 선택 안됨: #919191
    }
    
    Column(
        modifier = Modifier
            .clickable { onClick() }
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top // spacedBy 대신 Top으로 아이콘과 텍스트를 바로 붙임
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = text,
            modifier = Modifier.size(iconSize),
            tint = Color.Unspecified // 원본 색상 유지
        )
        
        // 아이콘 바로 밑에 간격 없이 텍스트 배치
        Spacer(modifier = Modifier.height(itemSpacing))
        
        Text(
            text = text,
            fontSize = textSize,
            fontFamily = fontFamily,
            fontWeight = FontWeight.W400,
            color = textColor,
            maxLines = 1
        )
    }
}
