package com.timor.kidsstory.presentation.bookshelf.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.timor.kidsstory.R
import com.timor.kidsstory.domain.model.Book
import com.timor.kidsstory.domain.model.DownloadStatus
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
                val imageModifier = when {
                    book.downloadProgress.status == DownloadStatus.DOWNLOADING -> {
                        Modifier
                            .fillMaxSize()
                            .blur(4.dp)  // 다운로드 중일 때 더 강한 블러
                    }
                    !book.isDownloaded && onDownloadClick != null -> {
                        Modifier
                            .fillMaxSize()
                            .blur(2.dp)  // 블러 효과 정도
                    }
                    else -> {
                        Modifier.fillMaxSize()
                    }
                }

                AsyncImage(
                    model = book.coverImage,
                    contentDescription = book.title,
                    modifier = imageModifier,
                    contentScale = ContentScale.Fit
                )
                
                // 다운로드 중일 때 추가 오버레이
                if (book.downloadProgress.status == DownloadStatus.DOWNLOADING) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.6f))
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .width(180.dp)
                        .height(140.dp)
                        .background(Color.Gray)
                )
            }

            // 다운로드 상태 표시
            when (book.downloadProgress.status) {
                DownloadStatus.AVAILABLE -> {
                    // 다운로드 가능한 경우 다운로드 버튼 표시
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
                DownloadStatus.DOWNLOADING -> {
                    // 다운로드 중인 경우 진행률 표시
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                            .drawBehind {
                                // 텍스트 배경에 반투명 원형 배경 추가
                                drawCircle(
                                    color = Color.Black.copy(alpha = 0.3f),
                                    radius = size.maxDimension * 0.8f
                                )
                            },
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // 진행률 텍스트
                        Text(
                            text = "${(book.downloadProgress.progress * 100).toInt()}%",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        
                        // 진행률 바
                        LinearProgressIndicator(
                            progress = book.downloadProgress.progress,
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .height(6.dp),
                            color = AppColors.unknown500,
                            trackColor = Color.White.copy(alpha = 0.3f)
                        )
                        
                        // 다운로드 중 텍스트
                        LocalizedText(
                            resId = R.string.bookcover_downloading,
                            style = AppTextStyles.gummyVSmallMediumItalic,
                            color = Color.White,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
                DownloadStatus.FAILED -> {
                    // 다운로드 실패한 경우 재시도 버튼
                    if (onDownloadClick != null) {
                        Button(
                            onClick = onDownloadClick,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Red.copy(alpha = 0.8f)
                            ),
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(8.dp)
                        ) {
                            LocalizedText(
                                resId = R.string.bookcover_retry,
                                style = AppTextStyles.gummyVSmallMediumItalic,
                                color = Color.White
                            )
                        }
                    }
                }
                DownloadStatus.DOWNLOADED -> {
                    // 다운로드 완료된 경우 아무것도 표시하지 않음
                }
            }
        }
    }
}