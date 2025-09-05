package com.timor.kidsstory.presentation.bookshelf.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.timor.kidsstory.R
import com.timor.kidsstory.ui.theme.AppTextStyles

/**
 * 책장이 비어있을 때 표시되는 화면
 */
@Composable
fun EmptyBookshelf(
    modifier: Modifier = Modifier,
    isFiltered: Boolean = false,
    isLoading: Boolean = false
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
            // 아이콘 또는 일러스트 (추후 추가)
            Spacer(modifier = Modifier.height(24.dp))
            
            // 제목
            Text(
                text = when {
                    isLoading -> stringResource(R.string.empty_downloading)
                    isFiltered -> stringResource(R.string.empty_filter_title)
                    else -> stringResource(R.string.empty_bookshelf_title)
                },
                style = AppTextStyles.gummyLgSemiboldItalic.copy(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = Color(0xFF6B442B),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // 설명 메시지
            if (!isLoading) {
                Text(
                    text = if (isFiltered) {
                        stringResource(R.string.empty_filter_message)
                    } else {
                        stringResource(R.string.empty_bookshelf_message)
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