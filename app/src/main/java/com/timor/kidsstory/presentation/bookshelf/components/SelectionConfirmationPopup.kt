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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.timor.kidsstory.R
import com.timor.kidsstory.domain.model.Book
import com.timor.kidsstory.ui.theme.AppColors
import java.text.DecimalFormat

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
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = AppColors.neutralWhite
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                // 헤더
                PopupHeader(
                    actionType = actionType,
                    selectedCount = selectedBooks.size,
                    totalSize = totalSize
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 선택된 항목 목록
                SelectedItemsList(
                    books = selectedBooks,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 200.dp)
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // 용량 경고 (1GB 이상일 때)
                if (isLargeDownload) {
                    WarningMessage(totalSizeGB = totalSizeGB)
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                // 버튼들
                ActionButtons(
                    actionType = actionType,
                    onConfirm = onConfirm,
                    onCancel = onCancel,
                    modifier = Modifier.fillMaxWidth()
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
        Icon(
            painter = painterResource(
                when (actionType) {
                    ActionType.DOWNLOAD -> R.drawable.ic_download
                    ActionType.UPDATE -> R.drawable.ic_update
                    ActionType.DELETE -> R.drawable.ic_delete
                }
            ),
            contentDescription = null,
            tint = when (actionType) {
                ActionType.DOWNLOAD -> AppColors.blue500
                ActionType.UPDATE -> AppColors.yellowRed500
                ActionType.DELETE -> AppColors.red600
            },
            modifier = Modifier.size(32.dp)
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Text(
            text = when (actionType) {
                ActionType.DOWNLOAD -> "${selectedCount}개 항목 다운로드"
                ActionType.UPDATE -> "${selectedCount}개 항목 업데이트"
                ActionType.DELETE -> "${selectedCount}개 항목 삭제"
            },
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.neutral800,
            textAlign = TextAlign.Center
        )
        
        Text(
            text = "총 용량: ${formatFileSize(totalSize)}",
            fontSize = 14.sp,
            color = AppColors.neutral600,
            textAlign = TextAlign.Center
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
            Text(
                text = "Level ${book.level}",
                fontSize = 12.sp,
                color = AppColors.neutral500
            )
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
        
        Text(
            text = "용량이 큽니다 (${String.format("%.1f", totalSizeGB)}GB). " +
                    "Wi-Fi 연결을 권장합니다.",
            fontSize = 12.sp,
            color = AppColors.yellowRed700,
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
            Text(
                text = "취소",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
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
            Text(
                text = when (actionType) {
                    ActionType.DOWNLOAD -> "다운로드"
                    ActionType.UPDATE -> "업데이트"
                    ActionType.DELETE -> "삭제"
                },
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = AppColors.neutralWhite
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

/**
 * 파일 크기를 사람이 읽기 쉬운 형태로 변환
 */
private fun formatFileSize(bytes: Long): String {
    if (bytes == 0L) return "0 B"
    
    val units = arrayOf("B", "KB", "MB", "GB")
    var size = bytes.toDouble()
    var unitIndex = 0
    
    while (size >= 1024 && unitIndex < units.size - 1) {
        size /= 1024
        unitIndex++
    }
    
    val format = if (size >= 100) {
        DecimalFormat("#")
    } else {
        DecimalFormat("#.#")
    }
    
    return "${format.format(size)} ${units[unitIndex]}"
}

// 추가 확장 속성들 (AppColors에 없는 색상들을 위한 임시 해결책)
private val AppColors.yellowRed50: Color
    get() = Color(0xFFFFF7ED)
