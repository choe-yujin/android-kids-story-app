package com.timor.kidsstory.presentation.bookshelf.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.timor.kidsstory.R
import com.timor.kidsstory.ui.theme.AppTextStyles

/**
 * 책장이 비어있을 때 표시되는 화면 (완전한 stateless)
 */
@Composable
fun EmptyBookshelf(
    modifier: Modifier = Modifier,
    isFiltered: Boolean = false,
    isLoading: Boolean = false,
    currentLanguageCode: String = "ko" // 언어 코드 추가
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            
            // 언어별 폰트 선택
            val (titleStyle, descriptionStyle) = when (currentLanguageCode) {
                "ko" -> Pair(
                    AppTextStyles.cookieRunBlackRegular.copy(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    AppTextStyles.cookieRunRegular.copy(
                        fontSize = 16.sp
                    )
                )
                else -> Pair( // 영어, 테틀어 등
                    AppTextStyles.gummyLgSemiboldItalic.copy(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    AppTextStyles.gummyMedium.copy(
                        fontSize = 16.sp
                    )
                )
            }
            
            // 제목 - 언어별 폰트 적용
            Text(
                text = when {
                    isLoading -> stringResource(R.string.empty_downloading)
                    isFiltered -> stringResource(R.string.empty_filter_title)
                    else -> stringResource(R.string.empty_bookshelf_title)
                },
                style = titleStyle,
                color = Color(0xFF6B442B),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // 설명 메시지 - 언어별 폰트 적용
            if (!isLoading) {
                Text(
                    text = if (isFiltered) {
                        stringResource(R.string.empty_filter_message)
                    } else {
                        stringResource(R.string.empty_bookshelf_message)
                    },
                    style = descriptionStyle,
                    color = Color(0xFF9E9E9E),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.widthIn(max = 280.dp)
                )
            }
        }
    }
}
