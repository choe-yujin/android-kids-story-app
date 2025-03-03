package com.timor.kidsstory.presentation.bookshelf.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.timor.kidsstory.R
import com.timor.kidsstory.domain.model.Language
import com.timor.kidsstory.domain.util.LanguageConstants
import com.timor.kidsstory.ui.theme.KidsStoryTheme

/**
 * 책장 화면 상단의 헤더 컴포넌트
 *
 * @param onMakerClick Maker 버튼 클릭 이벤트
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
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(Color(0xFFFDD25A))
            .padding(horizontal = 48.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Maker 버튼
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .clickable(onClick = onSettingClick)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = "Maker",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }

        // 앱 제목
        Text(
            text = "TetumDream",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF121212)
        )

        // 챗봇 아이콘과 언어 선택 버튼을 묶는 Row
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 챗봇 아이콘
            Icon(
                painter = painterResource(id = R.drawable.ic_chatbot),
                contentDescription = "Chatbot",
                modifier = Modifier
                    .size(48.dp)
                    .clickable(onClick = onChatbotClick),
                tint = Color.Unspecified
            )

            // 간격
            Spacer(modifier = Modifier.width(10.dp))

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