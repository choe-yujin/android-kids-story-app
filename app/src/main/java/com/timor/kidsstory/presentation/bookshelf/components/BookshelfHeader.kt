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

/**
 * 책장 화면 상단의 헤더 컴포넌트
 *
 * @param onSettingClick setting 버튼 클릭 이벤트
 * @param currentLanguage 현재 선택된 언어
 * @param onLanguageClick 언어 선택 버튼 클릭 이벤트
 */
@Composable
fun BookshelfHeader(
    currentLanguage: Language,
    onSettingClick: () -> Unit,
    onLanguageClick: () -> Unit,
    onChatbotClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(Color(0xFFFDD25A))
    ) {
        // 좌측 설정 아이콘
        Icon(
            painter = painterResource(id = R.drawable.ic_setting),
            contentDescription = "setting",
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 48.dp)
                .size(32.dp)
                .clickable(onClick = onSettingClick),
            tint = Color.Unspecified
        )

        // 앱 제목 (중앙 배치)
        Icon(
            painter = painterResource(id = R.drawable.ic_logo),
            contentDescription = "App title",
            modifier = Modifier
                .align(Alignment.Center)
                .size(112.dp),
            tint = Color.Unspecified
        )

        // 우측 기능 버튼들
        Row(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 48.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
//            // 챗봇 아이콘
//            Icon(
//                painter = painterResource(id = R.drawable.ic_chatbot_round),
//                contentDescription = "Chatbot",
//                modifier = Modifier
//                    .size(40.dp)
//                    .clickable(onClick = onChatbotClick),
//                tint = Color.Unspecified
//            )

            // 간격
            Spacer(modifier = Modifier.width(20.dp))

            // 언어 선택 버튼
            LanguageSelector(
                currentLanguage = currentLanguage,
                onClick = onLanguageClick
            )
        }
    }
}

@Preview(
    name = "BookshelfHeader - English",
    group = "BookshelfHeader",
    showBackground = true,
    backgroundColor = 0xFFFDD25A,
    widthDp = 800,
    device = "spec:width=800dp,height=360dp,orientation=landscape"
)
@Composable
fun BookshelfHeaderPreviewEnglish() {
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
    name = "BookshelfHeader - Korean",
    group = "BookshelfHeader",
    showBackground = true,
    backgroundColor = 0xFFFDD25A,
    widthDp = 800,
    device = "spec:width=800dp,height=360dp,orientation=landscape"
)
@Composable
fun BookshelfHeaderPreviewKorean() {
    KidsStoryTheme {
        BookshelfHeader(
            currentLanguage = LanguageConstants.KOREAN,
            onSettingClick = {},
            onLanguageClick = {},
            onChatbotClick = {},
        )
    }
}

@Preview(
    name = "BookshelfHeader - Tetum",
    group = "BookshelfHeader",
    showBackground = true,
    backgroundColor = 0xFFFDD25A,
    widthDp = 800,
    device = "spec:width=800dp,height=360dp,orientation=landscape"
)
@Composable
fun BookshelfHeaderPreviewTetum() {
    KidsStoryTheme {
        BookshelfHeader(
            currentLanguage = LanguageConstants.TETUM,
            onSettingClick = {},
            onLanguageClick = {},
            onChatbotClick = {}
        )
    }
}