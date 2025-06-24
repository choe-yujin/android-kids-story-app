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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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
    // 화면 크기에 따른 반응형 계산
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val screenWidth = configuration.screenWidthDp
    val isSmallScreen = screenWidth <= 400
    val isTablet = screenWidth >= 600 // 태블릿 판단 기준
    
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
                            .blur(if (isTablet) 6.dp else 4.dp)  // 태블릿에서 더 강한 블러
                    }
                    !book.isDownloaded && onDownloadClick != null -> {
                        Modifier
                            .fillMaxSize()
                            .blur(if (isTablet) 4.dp else 2.dp)  // 태블릿에서 더 강한 블러
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
                
                // 다운로드되지 않았거나 다운로드 중일 때 오버레이 추가
                if (!book.isDownloaded || book.downloadProgress.status == DownloadStatus.DOWNLOADING) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                when {
                                    book.downloadProgress.status == DownloadStatus.DOWNLOADING -> {
                                        Color.Black.copy(alpha = if (isTablet) 0.7f else 0.6f)
                                    }
                                    !book.isDownloaded -> {
                                        Color.Black.copy(alpha = if (isTablet) 0.5f else 0.3f)
                                    }
                                    else -> Color.Transparent
                                }
                            )
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
                                containerColor = AppColors.unknown500.copy(alpha = 0.9f) // 더 진한 배경
                            ),
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(8.dp)
                                .height(if (isSmallScreen) 28.dp else if (isTablet) 36.dp else 32.dp),
                            contentPadding = PaddingValues(
                                horizontal = if (isSmallScreen) 8.dp else if (isTablet) 16.dp else 12.dp,
                                vertical = if (isSmallScreen) 4.dp else if (isTablet) 8.dp else 6.dp
                            ),
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = 4.dp // 버튼에 그림자 추가
                            )
                        ) {
                            LocalizedText(
                                resId = R.string.bookcover_download,
                                style = when {
                                    isSmallScreen -> AppTextStyles.gummyVSmallMediumItalic.copy(
                                        fontSize = 10.sp,
                                        lineHeight = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    isTablet -> AppTextStyles.gummyVSmallMediumItalic.copy(
                                        fontSize = 14.sp,
                                        lineHeight = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    else -> AppTextStyles.gummyVSmallMediumItalic.copy(
                                        fontSize = 12.sp,
                                        lineHeight = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                },
                                color = Color.White, // 흰색으로 변경해서 더 잘 보이게
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
                DownloadStatus.DOWNLOADING -> {
                    // 다운로드 중인 경우 동그라미 버퍼링만 표시
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(if (isTablet) 60.dp else 48.dp) // 태블릿에서 더 크게
                            .drawBehind {
                                // 배경에 반투명 원형 배경 추가
                                drawCircle(
                                    color = Color.Black.copy(alpha = 0.7f),
                                    radius = size.maxDimension * 0.5f
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(if (isTablet) 36.dp else 28.dp),
                            color = Color.White,
                            strokeWidth = if (isTablet) 4.dp else 3.dp
                        )
                    }
                }
                DownloadStatus.FAILED -> {
                    // 다운로드 실패한 경우 재시도 버튼
                    if (onDownloadClick != null) {
                        Button(
                            onClick = onDownloadClick,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Red.copy(alpha = 0.9f) // 더 진한 빨간색
                            ),
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(8.dp)
                                .height(if (isSmallScreen) 28.dp else if (isTablet) 36.dp else 32.dp),
                            contentPadding = PaddingValues(
                                horizontal = if (isSmallScreen) 8.dp else if (isTablet) 16.dp else 12.dp,
                                vertical = if (isSmallScreen) 4.dp else if (isTablet) 8.dp else 6.dp
                            ),
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = 4.dp
                            )
                        ) {
                            LocalizedText(
                                resId = R.string.bookcover_retry,
                                style = when {
                                    isSmallScreen -> AppTextStyles.gummyVSmallMediumItalic.copy(
                                        fontSize = 10.sp,
                                        lineHeight = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    isTablet -> AppTextStyles.gummyVSmallMediumItalic.copy(
                                        fontSize = 14.sp,
                                        lineHeight = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    else -> AppTextStyles.gummyVSmallMediumItalic.copy(
                                        fontSize = 12.sp,
                                        lineHeight = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                },
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
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
