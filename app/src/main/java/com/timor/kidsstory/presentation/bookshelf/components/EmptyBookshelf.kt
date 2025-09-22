package com.timor.kidsstory.presentation.bookshelf.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.timor.kidsstory.R
import com.timor.kidsstory.ui.components.LocalizedText
import com.timor.kidsstory.ui.components.FontPolicy

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
            
            // 제목
            LocalizedText(
                resId = when {
                    isLoading -> R.string.empty_downloading
                    isFiltered -> R.string.empty_filter_title
                    else -> R.string.empty_bookshelf_title
                },
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFF6B442B),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // 설명 메시지
            if (!isLoading) {
                LocalizedText(
                    resId = if (isFiltered) {
                        R.string.empty_filter_message
                    } else {
                        R.string.empty_bookshelf_message
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF9E9E9E),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.widthIn(max = 280.dp)
                )
            }
        }
    }
}
