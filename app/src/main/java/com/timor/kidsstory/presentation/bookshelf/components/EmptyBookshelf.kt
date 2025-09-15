package com.timor.kidsstory.presentation.bookshelf.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.timor.kidsstory.R
import com.timor.kidsstory.presentation.util.getEmptyBookshelfMessage
import com.timor.kidsstory.presentation.util.getEmptyBookshelfTitle
import com.timor.kidsstory.presentation.util.getEmptyStateMessage
import com.timor.kidsstory.presentation.util.getEmptyStateTitle
import com.timor.kidsstory.presentation.util.ContextLanguageHelper
import com.timor.kidsstory.ui.theme.AppTextStyles

/**
 * 책장이 비어있을 때 표시되는 화면
 */
@Composable
fun EmptyBookshelf(
    modifier: Modifier = Modifier,
    isFiltered: Boolean = false,
    isLoading: Boolean = false,
    selectedLanguageCode: String = "en" // 추가: 선택된 언어 코드
) {
    val context = LocalContext.current
    
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            // 아이콘 또는 일러스트 (추후 추가)
            Spacer(modifier = Modifier.height(24.dp))
            
            // 제목 (선택된 언어로 표시)
            Text(
                text = when {
                    isLoading -> ContextLanguageHelper.getStringInLanguage(
                        context, selectedLanguageCode, R.string.empty_downloading
                    )
                    isFiltered -> getEmptyStateTitle(context, selectedLanguageCode)
                    else -> getEmptyBookshelfTitle(context, selectedLanguageCode)
                },
                style = AppTextStyles.gummyLgSemiboldItalic.copy(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = Color(0xFF6B442B),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // 설명 메시지 (선택된 언어로 표시)
            if (!isLoading) {
                Text(
                    text = if (isFiltered) {
                        getEmptyStateMessage(context, selectedLanguageCode)
                    } else {
                        getEmptyBookshelfMessage(context, selectedLanguageCode)
                    },
                    style = AppTextStyles.gummyMedium.copy(
                        fontSize = 16.sp
                    ),
                    color = Color(0xFF9E9E9E),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.widthIn(max = 280.dp)
                )
            }
        }
    }
}
