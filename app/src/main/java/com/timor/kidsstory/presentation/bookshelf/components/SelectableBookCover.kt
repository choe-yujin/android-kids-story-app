package com.timor.kidsstory.presentation.bookshelf.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.timor.kidsstory.R
import com.timor.kidsstory.domain.model.Book
import com.timor.kidsstory.ui.theme.AppColors
import com.timor.kidsstory.presentation.util.formatFileSize
import java.text.DecimalFormat

/**
 * 카테고리 이름을 사람이 읽기 쉬운 형태로 변환
 */
private fun getCategoryDisplayName(category: String): String {
    return when (category.lowercase()) {
        "environment", "nature" -> "Nature"
        "science", "math" -> "Science"
        "culture", "world" -> "Culture"
        "social", "emotional", "emotion" -> "Emotion"
        "folktales", "history", "stories" -> "Stories"
        "daily", "life", "daily_life" -> "Daily Life"
        "adventure", "fantasy" -> "Adventure"
        else -> category.replaceFirstChar { it.uppercase() }
    }
}

/**
 * 선택 가능한 책 커버 컴포넌트
 * 체크박스와 함께 표시되며, 다중 선택을 지원합니다.
 */
@Composable
fun SelectableBookCover(
    book: Book,
    isSelected: Boolean,
    onSelectionChanged: (Boolean) -> Unit,
    onBookClick: () -> Unit,
    showUpdateBadge: Boolean = false,
    showDownloadBadge: Boolean = false,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth() // Grid 칸 전체 크기 채우고, 이미진 원본 비율로 세로 크기 자동 조정
            .clickable { 
                // 🆕 책 전체 영역 클릭 시 체크박스 상태 변경
                onSelectionChanged(!isSelected)
            }
    ) {
        // 메인 책 커버
        BookCoverContent(
            book = book,
            isSelected = isSelected,
            onClick = { /* 🆕 내부 클릭 비활성화 (상위 Box에서 처리) */ },
            modifier = Modifier.fillMaxSize()
        )
        
        // 선택 체크박스 (왼쪽 상단)
        SelectionCheckbox(
            isSelected = isSelected,
            onSelectionChanged = onSelectionChanged,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(8.dp)
        )
        
        // 업데이트/다운로드 뱃지 (오른쪽 상단)
        if (showUpdateBadge || showDownloadBadge) {
            ActionBadge(
                isUpdate = showUpdateBadge,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            )
        }
        
        // 파일 크기 정보 (하단 오른쪽)
        if (book.totalSize > 0) {
            FileSizeInfo(
                size = book.totalSize,
                modifier = Modifier
                    .align(Alignment.BottomEnd) // 🆕 오른쪽으로 이동
                    .padding(8.dp)
            )
        }
        
        // 🆕 레벨과 카테고리 정보 (하단 왼쪽)
        BookInfoBadge(
            book = book,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(8.dp)
        )
    }
}

@Composable
private fun BookCoverContent(
    book: Book,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val isTablet = screenWidth >= 600
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = if (isSelected) 3.dp else 0.dp,
                color = if (isSelected) AppColors.primary500 else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            ),
        elevation = CardDefaults.cardElevation(if (isSelected) 8.dp else 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // 책 커버 이미지 (일반 모드와 동일한 방식)
            if (book.coverImage.isNotEmpty()) {
                AsyncImage(
                    model = book.coverImage,
                    contentDescription = book.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit,
                    placeholder = painterResource(R.drawable.ic_book_placeholder),
                    error = painterResource(R.drawable.ic_book_placeholder)
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Gray)
                )
            }
        }
    }
}

@Composable
private fun SelectionCheckbox(
    isSelected: Boolean,
    onSelectionChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(24.dp)
            .background(
                color = if (isSelected) {
                    AppColors.primary500
                } else {
                    AppColors.neutralWhite
                },
                shape = CircleShape
            )
            .border(
                width = 2.dp,
                color = if (isSelected) {
                    AppColors.primary500
                } else {
                    AppColors.neutral300
                },
                shape = CircleShape
            )
            .clickable { onSelectionChanged(!isSelected) },
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Icon(
                painter = painterResource(R.drawable.ic_check),
                contentDescription = "선택됨",
                tint = AppColors.neutralWhite,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

@Composable
private fun ActionBadge(
    isUpdate: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                color = if (isUpdate) {
                    AppColors.yellowRed500
                } else {
                    AppColors.blue500
                },
                shape = CircleShape
            )
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(
                if (isUpdate) R.drawable.ic_update else R.drawable.ic_down // 🆕 ic_download 대신 ic_down 사용
            ),
            contentDescription = if (isUpdate) "업데이트" else "다운로드",
            tint = AppColors.neutralWhite,
            modifier = Modifier.size(12.dp)
        )
    }
}

@Composable
private fun FileSizeInfo(
    size: Long,
    modifier: Modifier = Modifier
) {
    val formattedSize = formatFileSize(size)
    
    Box(
        modifier = modifier
            .background(
                color = AppColors.neutral800.copy(alpha = 0.8f),
                shape = RoundedCornerShape(4.dp)
            )
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = formattedSize,
            fontSize = 9.sp,
            color = AppColors.neutralWhite,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * 🆕 레벨과 카테고리 정보를 보여주는 배지
 */
@Composable
private fun BookInfoBadge(
    book: Book,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(2.dp),
        horizontalAlignment = Alignment.Start
    ) {
        // 레벨 배지
        Box(
            modifier = Modifier
                .background(
                    color = AppColors.primary500.copy(alpha = 0.9f),
                    shape = RoundedCornerShape(4.dp)
                )
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(
                text = "Level ${book.level}",
                fontSize = 9.sp,
                color = AppColors.neutralWhite,
                fontWeight = FontWeight.Medium
            )
        }
        
        // 카테고리 배지
        Box(
            modifier = Modifier
                .background(
                    color = AppColors.green500.copy(alpha = 0.9f),
                    shape = RoundedCornerShape(4.dp)
                )
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(
                text = getCategoryDisplayName(book.category),
                fontSize = 9.sp,
                color = AppColors.neutralWhite,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}