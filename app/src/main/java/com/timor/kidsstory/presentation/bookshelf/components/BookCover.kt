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

@Composable
fun BookCover(
    book: Book,
    onClick: () -> Unit,
    onDownloadClick: (() -> Unit)? = null
) {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val screenWidth = configuration.screenWidthDp
    val isSmallScreen = screenWidth <= 400
    val isTablet = screenWidth >= 600

    Card(
        modifier = Modifier
            .width(180.dp)
            .clickable(enabled = book.isDownloaded, onClick = onClick),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (book.coverImage.isNotEmpty()) {
                val imageModifier = when {
                    book.downloadProgress.status == DownloadStatus.DOWNLOADING -> {
                        Modifier.fillMaxSize().blur(if (isTablet) 6.dp else 4.dp)
                    }
                    !book.isDownloaded && onDownloadClick != null -> {
                        Modifier.fillMaxSize().blur(if (isTablet) 4.dp else 2.dp)
                    }
                    else -> Modifier.fillMaxSize()
                }

                AsyncImage(
                    model = book.coverImage,
                    contentDescription = book.title,
                    modifier = imageModifier,
                    contentScale = ContentScale.Fit
                )

                if (!book.isDownloaded || book.downloadProgress.status == DownloadStatus.DOWNLOADING) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                when {
                                    book.downloadProgress.status == DownloadStatus.DOWNLOADING -> Color.Black.copy(alpha = if (isTablet) 0.7f else 0.6f)
                                    !book.isDownloaded -> Color.Black.copy(alpha = if (isTablet) 0.5f else 0.3f)
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

            when (book.downloadProgress.status) {
                DownloadStatus.AVAILABLE -> {
                    if (!book.isDownloaded && onDownloadClick != null) {
                        Button(
                            onClick = onDownloadClick,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AppColors.unknown500.copy(alpha = 0.9f)
                            ),
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(8.dp)
                                .height(if (isSmallScreen) 28.dp else if (isTablet) 36.dp else 32.dp),
                            contentPadding = PaddingValues(
                                horizontal = if (isSmallScreen) 8.dp else if (isTablet) 16.dp else 12.dp,
                                vertical = if (isSmallScreen) 4.dp else if (isTablet) 8.dp else 6.dp
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                        ) {
                            LocalizedText(
                                resId = R.string.bookcover_download,
                                style = when {
                                    isSmallScreen -> AppTextStyles.gummyVSmallMediumItalic.copy(fontSize = 10.sp, lineHeight = 12.sp, fontWeight = FontWeight.Bold)
                                    isTablet -> AppTextStyles.gummyVSmallMediumItalic.copy(fontSize = 14.sp, lineHeight = 16.sp, fontWeight = FontWeight.Bold)
                                    else -> AppTextStyles.gummyVSmallMediumItalic.copy(fontSize = 12.sp, lineHeight = 14.sp, fontWeight = FontWeight.Bold)
                                },
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
                DownloadStatus.DOWNLOADING -> {
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(if (isTablet) 60.dp else 48.dp)
                            .drawBehind {
                                drawCircle(color = Color.Black.copy(alpha = 0.7f), radius = size.maxDimension * 0.5f)
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
                DownloadStatus.UPDATE_AVAILABLE -> {
                    if (onDownloadClick != null) {
                        Button(
                            onClick = onDownloadClick,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AppColors.unknown500.copy(alpha = 0.9f)
                            ),
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(8.dp)
                                .height(if (isSmallScreen) 28.dp else if (isTablet) 36.dp else 32.dp),
                            contentPadding = PaddingValues(
                                horizontal = if (isSmallScreen) 8.dp else if (isTablet) 16.dp else 12.dp,
                                vertical = if (isSmallScreen) 4.dp else if (isTablet) 8.dp else 6.dp
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                        ) {
                            LocalizedText(
                                resId = R.string.bookcover_update,
                                style = when {
                                    isSmallScreen -> AppTextStyles.gummyVSmallMediumItalic.copy(fontSize = 10.sp, lineHeight = 12.sp, fontWeight = FontWeight.Bold)
                                    isTablet -> AppTextStyles.gummyVSmallMediumItalic.copy(fontSize = 14.sp, lineHeight = 16.sp, fontWeight = FontWeight.Bold)
                                    else -> AppTextStyles.gummyVSmallMediumItalic.copy(fontSize = 12.sp, lineHeight = 14.sp, fontWeight = FontWeight.Bold)
                                },
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
                DownloadStatus.FAILED -> {
                    if (onDownloadClick != null) {
                        Button(
                            onClick = onDownloadClick,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Red.copy(alpha = 0.9f)
                            ),
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(8.dp)
                                .height(if (isSmallScreen) 28.dp else if (isTablet) 36.dp else 32.dp),
                            contentPadding = PaddingValues(
                                horizontal = if (isSmallScreen) 8.dp else if (isTablet) 16.dp else 12.dp,
                                vertical = if (isSmallScreen) 4.dp else if (isTablet) 8.dp else 6.dp
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                        ) {
                            LocalizedText(
                                resId = R.string.bookcover_retry,
                                style = when {
                                    isSmallScreen -> AppTextStyles.gummyVSmallMediumItalic.copy(fontSize = 10.sp, lineHeight = 12.sp, fontWeight = FontWeight.Bold)
                                    isTablet -> AppTextStyles.gummyVSmallMediumItalic.copy(fontSize = 14.sp, lineHeight = 16.sp, fontWeight = FontWeight.Bold)
                                    else -> AppTextStyles.gummyVSmallMediumItalic.copy(fontSize = 12.sp, lineHeight = 14.sp, fontWeight = FontWeight.Bold)
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
