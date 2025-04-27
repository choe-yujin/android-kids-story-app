package com.timor.kidsstory.presentation.bookshelf.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.timor.kidsstory.R
import com.timor.kidsstory.domain.model.Book
import com.timor.kidsstory.ui.components.LocalizedText
import com.timor.kidsstory.ui.theme.AppColors
import com.timor.kidsstory.ui.theme.AppTextStyles

/**
 * 책 표지 컴포넌트
 *
 * @param book 책 객체
 * @param onClick 책 클릭 이벤트 처리 함수
 * @param onDownloadClick 다운로드 버튼 클릭 이벤트 처리 함수 (null이면 다운로드 버튼 미표시)
 */
@Composable
fun BookCover(
    book: Book,
    onClick: () -> Unit,
    onDownloadClick: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .width(180.dp)
            .clickable(enabled = book.isDownloaded, onClick = onClick),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // 책 표지 이미지
            if (book.coverImage.isNotEmpty()) {
                // 다운로드 상태에 따라 블러 효과 적용
                val imageModifier = if (!book.isDownloaded && onDownloadClick != null) {
                    Modifier
                        .fillMaxSize()
                        .blur(2.dp)  // 블러 효과 정도
                } else {
                    Modifier.fillMaxSize()
                }

                AsyncImage(
                    model = book.coverImage,
                    contentDescription = book.title,
                    modifier = imageModifier,
                    contentScale = ContentScale.Fit
                )
            } else {
                Box(
                    modifier = Modifier
                        .width(180.dp)
                        .height(140.dp)
                        .background(Color.Gray)
                )
            }

            // 다운로드 상태 표시 (다운로드 가능한 경우에만)
            if (!book.isDownloaded && onDownloadClick != null) {
                Button(
                    onClick = onDownloadClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppColors.unknown500
                    ),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(8.dp)
                ) {
                    LocalizedText(
                        resId = R.string.bookcover_download,
                        style = AppTextStyles.gummyVSmallMediumItalic,
                        color = AppColors.unknown300,
                    )
                }
            }
        }
    }
}