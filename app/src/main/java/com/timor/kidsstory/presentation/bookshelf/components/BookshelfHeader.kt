package com.timor.kidsstory.presentation.bookshelf.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.timor.kidsstory.R
import com.timor.kidsstory.domain.model.Language
import com.timor.kidsstory.domain.util.LanguageConstants
import com.timor.kidsstory.ui.components.FontPolicy
import com.timor.kidsstory.ui.components.LocalizedText
import com.timor.kidsstory.ui.theme.AppColors
import com.timor.kidsstory.ui.theme.AppTextStyles
import com.timor.kidsstory.ui.theme.KidsStoryTheme
import com.timor.kidsstory.ui.theme.ResponsiveTextUtils

/**
 * 책장 화면 상단의 헤더 컴포넌트
 * - 로고, 설정, 언어 선택 버튼
 * - 출석 및 읽기 진도 표시 (국기 옆에 위치)
 * - 관리 모드 버튼 (🆕 추가)
 *
 * @param currentLanguage 현재 선택된 언어
 * @param isManagementMode 현재 관리 모드 여부
 * @param downloadableItemsCount 다운로드 가능한 항목 수 (새 책 + 업데이트)
 * @param onSettingClick setting 버튼 클릭 이벤트
 * @param onLanguageClick 언어 선택 버튼 클릭 이벤트
 * @param onChatbotClick 챗봇 버튼 클릭 이벤트
 * @param onManagementModeToggle 관리 모드 전환 버튼 클릭 이벤트 (🆕 추가)
 * @param progressAndAttendanceContent 출석 및 진도 컴포넌트 (선택적)
 */
@Composable
fun BookshelfHeader(
    currentLanguage: Language,
    isManagementMode: Boolean = false, // 🆕 추가
    downloadableItemsCount: Int = 0, // 🆕 추가
    hasCheckedUpdates: Boolean = false, // 🆕 추가 (한번 확인하면 뱃지 숨김)
    onSettingClick: () -> Unit,
    onLanguageClick: () -> Unit,
    onChatbotClick: () -> Unit,
    onManagementModeToggle: () -> Unit = {}, // 🆕 추가
    progressAndAttendanceContent: (@Composable () -> Unit)? = null
) {
    // 반응형 크기 계산
    val headerHeight = ResponsiveTextUtils.getHeaderHeight().dp
    val headerPadding = 48.dp  // 책 그리드 패딩과 일치시킴
    val iconSize = ResponsiveTextUtils.getHeaderIconSize().dp  // 설정 아이콘과 언어 선택 아이콘 크기 통일
    val logoSize = ResponsiveTextUtils.getHeaderLogoSize().dp
    val spacerWidth = (20 * ResponsiveTextUtils.getScreenScaleFactor()).dp
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(headerHeight)
            .background(Color(0xFFFDD25A))
    ) {
        // 좌측 설정 아이콘 + 관리 모드 버튼
        Row(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = headerPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 설정 아이콘
            Icon(
                painter = painterResource(id = R.drawable.ic_setting),
                contentDescription = "setting",
                modifier = Modifier
                    .size(iconSize)
                    .clickable(onClick = onSettingClick),
                tint = Color.Unspecified
            )

            Spacer(modifier = Modifier.width(24.dp))
            
            // 🆕 관리 모드 버튼
            Box {
                // 배경 박스
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            if (isManagementMode) AppColors.green600 else AppColors.blue500
                        )
                        .clickable(onClick = onManagementModeToggle)
                        .height(iconSize) // 설정 아이콘 높이와 동일하게 설정
                        .padding(horizontal = 12.dp, vertical = 0.dp), // 높이가 iconSize로 고정되므로 vertical padding 제거
                    contentAlignment = Alignment.Center
                ) {
                    LocalizedText(
                        resId = if (isManagementMode) R.string.management_mode_done 
                               else R.string.management_mode_button,
                        style = AppTextStyles.gummyMedium.copy(fontSize = 14.sp), // 글씨 크기 키움
                        color = Color.White,
                        fontPolicy = FontPolicy.DEFAULT
                    )
                }
                
                // 뱃지 (다운로드 가능한 항목이 있고, 관리 모드가 아니고, 아직 확인하지 않은 경우만 표시)
                if (downloadableItemsCount > 0 && !isManagementMode && !hasCheckedUpdates) {
                    Badge(
                        modifier = Modifier.align(Alignment.TopEnd),
                        containerColor = AppColors.red600
                    ) {
                        Text(
                            text = if (downloadableItemsCount > 9) "9+" else downloadableItemsCount.toString(),
                            fontSize = 8.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // 앱 제목 (중앙 배치)
        Icon(
            painter = painterResource(id = R.drawable.ic_logo),
            contentDescription = "App title",
            modifier = Modifier
                .align(Alignment.Center)
                .size(logoSize),
            tint = Color.Unspecified
        )

        // 우측 기능 버튼들
        Row(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = headerPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 출석 및 진도 컴포넌트 (국기 옆에 배치)
            progressAndAttendanceContent?.invoke()

            // 간격
            Spacer(modifier = Modifier.width(spacerWidth))

            // 언어 선택 버튼
            LanguageSelector(
                currentLanguage = currentLanguage,
                iconSize = iconSize,  // 설정 아이콘과 동일한 크기 전달
                onClick = onLanguageClick
            )
        }
    }
}

@Preview(
    name = "BookshelfHeader - Normal Mode",
    group = "BookshelfHeader",
    showBackground = true,
    backgroundColor = 0xFFFDD25A,
    widthDp = 600,
    heightDp = 80
)
@Composable
fun BookshelfHeaderNormalModePreview() {
    KidsStoryTheme {
        BookshelfHeader(
            currentLanguage = LanguageConstants.KOREAN,
            isManagementMode = false,
            downloadableItemsCount = 8, // 🆕 테스트
            onSettingClick = {},
            onLanguageClick = {},
            onChatbotClick = {},
            onManagementModeToggle = {}, // 🆕 테스트
            progressAndAttendanceContent = {
                ProgressAndAttendanceSection(
                    streakCount = 7,
                    readingProgress = 0.6f,
                    completedBooks = 12,
                    totalBooks = 20,
                    onMyPageClick = {}
                )
            }
        )
    }
}

@Preview(
    name = "BookshelfHeader - Management Mode",
    group = "BookshelfHeader",
    showBackground = true,
    backgroundColor = 0xFFFDD25A,
    widthDp = 600,
    heightDp = 80
)
@Composable
fun BookshelfHeaderManagementModePreview() {
    KidsStoryTheme {
        BookshelfHeader(
            currentLanguage = LanguageConstants.KOREAN,
            isManagementMode = true, // 관리 모드 활성화
            downloadableItemsCount = 8, // 뱃지 숨김
            onSettingClick = {},
            onLanguageClick = {},
            onChatbotClick = {},
            onManagementModeToggle = {},
            progressAndAttendanceContent = {
                ProgressAndAttendanceSection(
                    streakCount = 7,
                    readingProgress = 0.6f,
                    completedBooks = 12,
                    totalBooks = 20,
                    onMyPageClick = {}
                )
            }
        )
    }
}