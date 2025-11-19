package com.timor.kidsstory.presentation.bookshelf.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.timor.kidsstory.R
import com.timor.kidsstory.domain.model.Book
import com.timor.kidsstory.ui.theme.AppColors
import com.timor.kidsstory.ui.components.LocalizedText
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
 * 선택된 항목들에 대한 최종 확인 팝업
 */
@Composable
fun SelectionConfirmationPopup(
    selectedBooks: List<Book>,
    actionType: ActionType,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (selectedBooks.isEmpty()) return
    
    val totalSize = selectedBooks.sumOf { it.totalSize }
    val totalSizeGB = totalSize.toDouble() / (1024 * 1024 * 1024)
    
    val isLargeDownload = totalSizeGB >= 1.0
    
    Dialog(onDismissRequest = onCancel) {
        Card(
            modifier = modifier
                .fillMaxWidth(0.95f) // 🆕 가로 폭을 더 넓게 확장 (85% -> 95%)
                .fillMaxHeight(0.85f) // 🆕 세로 높이를 더 높게 확장 (70% -> 85%)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = AppColors.neutralWhite
            )
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // 헤더 (고정)
                PopupHeader(
                    actionType = actionType,
                    selectedCount = selectedBooks.size,
                    totalSize = totalSize,
                    modifier = Modifier.padding(20.dp)
                )
                
                // 스크롤 가능한 콘텐츠 영역
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 20.dp)
                ) {
                    // 선택된 항목 목록 (스크롤 가능)
                    SelectedItemsList(
                        books = selectedBooks,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f) // 🆕 남은 공간을 모두 차지하되 스크롤 가능
                    )
                    
                    // 용량 경고 (1GB 이상일 때)
                    if (isLargeDownload) {
                        Spacer(modifier = Modifier.height(16.dp))
                        WarningMessage(totalSizeGB = totalSizeGB)
                    }
                }
                
                // 버튼들 (하단 고정)
                ActionButtons(
                    actionType = actionType,
                    onConfirm = onConfirm,
                    onCancel = onCancel,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                )
            }
        }
    }
}

@Composable
private fun PopupHeader(
    actionType: ActionType,
    selectedCount: Int,
    totalSize: Long,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 🆕 아이콘 제거
        
        // 🆕 다국어 처리
        LocalizedText(
            resId = when (actionType) {
                ActionType.DOWNLOAD -> R.string.management_popup_download_title
                ActionType.UPDATE -> R.string.management_popup_update_title
                ActionType.DELETE -> R.string.management_popup_delete_title
            },
            formatArgs = arrayOf(selectedCount),
            style = TextStyle(
                fontSize = 20.sp, // 🆕 아이콘 제거로 인해 제목 크기 증가
                fontWeight = FontWeight.Bold,
                color = AppColors.neutral800,
                textAlign = TextAlign.Center
            )
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // 🆕 다국어 처리
        LocalizedText(
            resId = R.string.management_popup_total_size,
            formatArgs = arrayOf(formatFileSize(totalSize)),
            style = TextStyle(
                fontSize = 14.sp,
                color = AppColors.neutral600,
                textAlign = TextAlign.Center
            )
        )
    }
}

@Composable
private fun SelectedItemsList(
    books: List<Book>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .background(
                color = AppColors.neutral50,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(books) { book ->
            SelectedBookItem(book = book)
        }
    }
}

@Composable
private fun SelectedBookItem(
    book: Book,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = book.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = AppColors.neutral800,
                maxLines = 1
            )
            
            // 🆕 레벨과 카테고리 정보 추가
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Level ${book.level}",
                    fontSize = 12.sp,
                    color = AppColors.neutral500
                )
                
                Text(
                    text = "•", // 분리 도트
                    fontSize = 12.sp,
                    color = AppColors.neutral400
                )
                
                Text(
                    text = getCategoryDisplayName(book.category),
                    fontSize = 12.sp,
                    color = AppColors.neutral500,
                    maxLines = 1
                )
            }
        }
        
        Text(
            text = formatFileSize(book.totalSize),
            fontSize = 12.sp,
            color = AppColors.neutral600,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun WarningMessage(
    totalSizeGB: Double,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = AppColors.yellowRed50,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_warning),
            contentDescription = "경고",
            tint = AppColors.yellowRed600,
            modifier = Modifier.size(20.dp)
        )
        
        // 🆕 다국어 처리
        LocalizedText(
            resId = R.string.management_popup_warning_large_download,
            formatArgs = arrayOf(String.format("%.1f", totalSizeGB) + "GB"),
            style = TextStyle(
                fontSize = 12.sp,
                color = AppColors.yellowRed700
            ),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun ActionButtons(
    actionType: ActionType,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 취소 버튼
        OutlinedButton(
            onClick = onCancel,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = AppColors.neutral600
            ),
            border = ButtonDefaults.outlinedButtonBorder.copy(
                brush = androidx.compose.ui.graphics.SolidColor(AppColors.neutral300)
            )
        ) {
            // 🆕 다국어 처리
            LocalizedText(
                resId = R.string.management_popup_cancel,
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            )
        }
        
        // 확인 버튼
        Button(
            onClick = onConfirm,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
                containerColor = when (actionType) {
                    ActionType.DOWNLOAD -> AppColors.blue500
                    ActionType.UPDATE -> AppColors.yellowRed500
                    ActionType.DELETE -> AppColors.red600
                }
            )
        ) {
            // 🆕 다국어 처리
            LocalizedText(
                resId = when (actionType) {
                    ActionType.DOWNLOAD -> R.string.management_popup_download
                    ActionType.UPDATE -> R.string.management_popup_update
                    ActionType.DELETE -> R.string.management_popup_delete
                },
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = AppColors.neutralWhite
                )
            )
        }
    }
}

/**
 * 액션 타입
 */
enum class ActionType {
    DOWNLOAD,
    UPDATE,
    DELETE
}

// 추가 확장 속성들 (AppColors에 없는 색상들을 위한 임시 해결책)
private val AppColors.yellowRed50: Color
    get() = Color(0xFFFFF7ED)