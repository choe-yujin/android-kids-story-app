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
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.timor.kidsstory.R
import com.timor.kidsstory.domain.model.Language
import com.timor.kidsstory.domain.util.LanguageConstants
import com.timor.kidsstory.ui.theme.KidsStoryTheme
import com.timor.kidsstory.ui.theme.ResponsiveTextUtils

/**
 * 책장 화면 상단의 헤더 컴포넌트
 * - 로고, 설정, 언어 선택 버튼
 * - 출석 및 읽기 진도 표시 (국기 옆에 위치)
 *
 * @param currentLanguage 현재 선택된 언어
 * @param onSettingClick setting 버튼 클릭 이벤트
 * @param onLanguageClick 언어 선택 버튼 클릭 이벤트
 * @param onChatbotClick 챗봇 버튼 클릭 이벤트
 * @param progressAndAttendanceContent 출석 및 진도 컴포넌트 (선택적)
 */
@Composable
fun BookshelfHeader(
    currentLanguage: Language,
    onSettingClick: () -> Unit,
    onLanguageClick: () -> Unit,
    onChatbotClick: () -> Unit,
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
        // 좌측 설정 아이콘
        Icon(
            painter = painterResource(id = R.drawable.ic_setting),
            contentDescription = "setting",
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = headerPadding)
                .size(iconSize)
                .clickable(onClick = onSettingClick),
            tint = Color.Unspecified
        )

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
    name = "BookshelfHeader - with Progress",
    group = "BookshelfHeader",
    showBackground = true,
    backgroundColor = 0xFFFDD25A,
    widthDp = 600,
    heightDp = 80
)
@Composable
fun BookshelfHeaderWithProgressPreview() {
    KidsStoryTheme {
        BookshelfHeader(
            currentLanguage = LanguageConstants.KOREAN,
            onSettingClick = {},
            onLanguageClick = {},
            onChatbotClick = {},
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
    name = "BookshelfHeader - Pixel 5",
    group = "BookshelfHeader",
    showBackground = true,
    backgroundColor = 0xFFFDD25A,
    widthDp = 393,
    heightDp = 20,
    device = "spec:width=393dp,height=20dp"
)
@Composable
fun BookshelfHeaderPreviewPixel5() {
    KidsStoryTheme {
        BookshelfHeader(
            currentLanguage = LanguageConstants.ENGLISH,
            onSettingClick = {},
            onLanguageClick = {},
            onChatbotClick = {}
        )
    }
}

@Preview(
    name = "BookshelfHeader - Phone",
    group = "BookshelfHeader",
    showBackground = true,
    backgroundColor = 0xFFFDD25A,
    widthDp = 400,
    heightDp = 20,
    device = "spec:width=400dp,height=20dp"
)
@Composable
fun BookshelfHeaderPreviewPhone() {
    KidsStoryTheme {
        BookshelfHeader(
            currentLanguage = LanguageConstants.KOREAN,
            onSettingClick = {},
            onLanguageClick = {},
            onChatbotClick = {}
        )
    }
}

@Preview(
    name = "BookshelfHeader - Small Phone",
    group = "BookshelfHeader",
    showBackground = true,
    backgroundColor = 0xFFFDD25A,
    widthDp = 360,
    heightDp = 16,
    device = "spec:width=360dp,height=16dp"
)
@Composable
fun BookshelfHeaderPreviewSmallPhone() {
    KidsStoryTheme {
        BookshelfHeader(
            currentLanguage = LanguageConstants.TETUM,
            onSettingClick = {},
            onLanguageClick = {},
            onChatbotClick = {},
        )
    }
}

@Preview(
    name = "BookshelfHeader - Tablet",
    group = "BookshelfHeader",
    showBackground = true,
    backgroundColor = 0xFFFDD25A,
    widthDp = 800,
    heightDp = 75,
    device = "spec:width=800dp,height=75dp"
)
@Composable
fun BookshelfHeaderPreviewTablet() {
    KidsStoryTheme {
        BookshelfHeader(
            currentLanguage = LanguageConstants.TETUM,
            onSettingClick = {},
            onLanguageClick = {},
            onChatbotClick = {}
        )
    }
}

@Preview(
    name = "BookshelfHeader - Large Tablet",
    group = "BookshelfHeader",
    showBackground = true,
    backgroundColor = 0xFFFDD25A,
    widthDp = 1024,
    heightDp = 100,
    device = "spec:width=1024dp,height=100dp"
)
@Composable
fun BookshelfHeaderPreviewLargeTablet() {
    KidsStoryTheme {
        BookshelfHeader(
            currentLanguage = LanguageConstants.TETUM,
            onSettingClick = {},
            onLanguageClick = {},
            onChatbotClick = {}
        )
    }
}
