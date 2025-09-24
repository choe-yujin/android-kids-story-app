package com.timor.kidsstory.presentation.bookshelf.components

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.timor.kidsstory.R
import com.timor.kidsstory.domain.model.Book
import com.timor.kidsstory.domain.model.DownloadStatus
import com.timor.kidsstory.presentation.bookshelf.model.BookManagementStatus
import com.timor.kidsstory.ui.components.LocalizedText
import com.timor.kidsstory.ui.theme.AppColors
import com.timor.kidsstory.ui.theme.AppTextStyles
import kotlinx.coroutines.launch

@Composable
fun BookCover(
    book: Book,
    onClick: () -> Unit,
    onDoubleClick: (() -> Unit)? = null,  // 더블클릭 콜백 추가
    isLocked: Boolean = false, // 🆕 unlock 안 된 책인지 여부
    onLockedClick: (() -> Unit)? = null // 🆕 잠긴 책 클릭 시 콜백
) {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val screenWidth = configuration.screenWidthDp
    val isSmallScreen = screenWidth <= 400
    val isTablet = screenWidth >= 600
    
    // 더블클릭 감지를 위한 상태
    var clickCount by remember { mutableIntStateOf(0) }

    val scope = rememberCoroutineScope()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                if (onDoubleClick != null) {
                    // 더블클릭 처리
                    detectTapGestures(
                        onTap = {
                            clickCount++
                            
                            // 처음 클릭이면 300ms 대기
                            if (clickCount == 1) {
                                scope.launch {
                                    kotlinx.coroutines.delay(300)
                                    if (clickCount == 1) {
                                        // 단일클릭 처리
                                        if (isLocked && onLockedClick != null) {
                                            onLockedClick()
                                        } else {
                                            onClick()
                                        }
                                    }
                                    clickCount = 0
                                }
                            } else if (clickCount == 2) {
                                // 더블클릭 처리
                                onDoubleClick()
                                clickCount = 0
                            }
                        }
                    )
                } else {
                    // 기존 단일클릭만 처리
                    detectTapGestures(
                        onTap = {
                            if (isLocked && onLockedClick != null) {
                                onLockedClick()
                            } else {
                                onClick()
                            }
                        }
                    )
                }
            },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // 책 커버 이미지
            if (book.coverImage.isNotEmpty()) {
                val imageModifier = when {
                    isLocked -> Modifier.fillMaxSize().blur(if (isTablet) 8.dp else 6.dp)
                    else -> Modifier.fillMaxSize()
                }

                AsyncImage(
                    model = book.coverImage,
                    contentDescription = book.title,
                    modifier = imageModifier,
                    contentScale = ContentScale.Fit
                )

                // 오버레이 배경
                if (isLocked) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.6f))
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Gray)
                )
            }

            // 🆕 잠금 아이콘 (일반 모드에서 unlock 안 된 책)
            if (isLocked) {
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(if (isTablet) 64.dp else 48.dp)
                        .background(
                            color = AppColors.neutral800.copy(alpha = 0.9f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_lock),
                        contentDescription = "Locked",
                        tint = AppColors.neutralWhite,
                        modifier = Modifier.size(if (isTablet) 36.dp else 28.dp)
                    )
                }
            }
        }
    }
}

/**
 * 관리 모드 오버레이
 */
@Composable
private fun ManagementModeOverlay(
    status: BookManagementStatus,
    isSelected: Boolean,
    isTablet: Boolean,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.padding(8.dp)) {
        when (status) {
            is BookManagementStatus.Available -> {
                StatusIcon(
                    iconRes = R.drawable.ic_download,
                    backgroundColor = AppColors.blue500,
                    isTablet = isTablet
                )
            }
            is BookManagementStatus.Downloaded -> {
                if (isSelected) {
                    CheckboxIcon(isTablet = isTablet)
                } else {
                    StatusIcon(
                        iconRes = R.drawable.ic_delete,
                        backgroundColor = AppColors.yellowRed500,
                        isTablet = isTablet
                    )
                }
            }
            is BookManagementStatus.UpdateAvailable -> {
                StatusIcon(
                    iconRes = R.drawable.ic_update,
                    backgroundColor = AppColors.green500,
                    isTablet = isTablet
                )
            }
            is BookManagementStatus.Downloading -> {
                ProgressIndicator(
                    progress = status.progress,
                    isTablet = isTablet
                )
            }
            is BookManagementStatus.Updating -> {
                ProgressIndicator(
                    progress = status.progress,
                    isTablet = isTablet
                )
            }
            is BookManagementStatus.Locked -> {
                StatusIcon(
                    iconRes = R.drawable.ic_lock,
                    backgroundColor = AppColors.neutral600,
                    isTablet = isTablet
                )
            }
        }
    }
}

/**
 * 체크박스 아이콘
 */
@Composable
private fun CheckboxIcon(
    isTablet: Boolean
) {
    Box(
        modifier = Modifier
            .size(if (isTablet) 32.dp else 24.dp)
            .background(
                color = AppColors.blue500,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_check),
            contentDescription = "Selected",
            tint = AppColors.neutralWhite,
            modifier = Modifier.size(if (isTablet) 20.dp else 16.dp)
        )
    }
}

/**
 * 상태 아이콘
 */
@Composable
private fun StatusIcon(
    iconRes: Int,
    backgroundColor: Color,
    isTablet: Boolean
) {
    Box(
        modifier = Modifier
            .size(if (isTablet) 32.dp else 24.dp)
            .background(
                color = backgroundColor,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            tint = AppColors.neutralWhite,
            modifier = Modifier.size(if (isTablet) 18.dp else 14.dp)
        )
    }
}

/**
 * 진행률 표시
 */
@Composable
private fun ProgressIndicator(
    progress: Float,
    isTablet: Boolean
) {
    Box(
        modifier = Modifier
            .size(if (isTablet) 32.dp else 24.dp)
            .background(
                color = AppColors.neutral800.copy(alpha = 0.8f),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            progress = progress,
            modifier = Modifier.size(if (isTablet) 24.dp else 18.dp),
            color = AppColors.neutralWhite,
            strokeWidth = 2.dp
        )
    }
}

// 기존 버튼 컴포넌트들
@Composable
private fun DownloadButton(
    onClick: () -> Unit,
    isSmallScreen: Boolean,
    isTablet: Boolean,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = AppColors.unknown500.copy(alpha = 0.9f)
        ),
        modifier = modifier
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

@Composable
private fun UpdateButton(
    onClick: () -> Unit,
    isSmallScreen: Boolean,
    isTablet: Boolean,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = AppColors.unknown500.copy(alpha = 0.9f)
        ),
        modifier = modifier
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

@Composable
private fun RetryButton(
    onClick: () -> Unit,
    isSmallScreen: Boolean,
    isTablet: Boolean,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Red.copy(alpha = 0.9f)
        ),
        modifier = modifier
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

@Composable
private fun DownloadingIndicator(
    isTablet: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
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
