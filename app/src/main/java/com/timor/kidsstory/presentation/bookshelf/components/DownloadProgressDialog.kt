package com.timor.kidsstory.presentation.bookshelf.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.timor.kidsstory.R
import com.timor.kidsstory.ui.theme.AppColors
import com.timor.kidsstory.ui.components.LocalizedText
import com.timor.kidsstory.presentation.bookshelf.model.ManagementActionType

/**
 * 다운로드/업데이트/삭제 진행 중 표시되는 로딩 다이얼로그
 */
@Composable
fun DownloadProgressDialog(
    isVisible: Boolean,
    actionType: ManagementActionType?,
    downloadingCount: Int,
    modifier: Modifier = Modifier
) {
    if (!isVisible || actionType == null) return
    
    Dialog(
        onDismissRequest = { /* 다운로드 중에는 취소 불가 */ },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth(0.8f)
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = AppColors.neutralWhite
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // 로딩 인디케이터
                CircularProgressIndicator(
                    modifier = Modifier.size(56.dp),
                    color = when (actionType) {
                        ManagementActionType.DOWNLOAD -> AppColors.blue500
                        ManagementActionType.UPDATE -> AppColors.green500
                        ManagementActionType.DELETE -> AppColors.red600
                    },
                    strokeWidth = 5.dp
                )
                
                // 진행 중 메시지
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // 제목
                    LocalizedText(
                        resId = when (actionType) {
                            ManagementActionType.DOWNLOAD -> R.string.download_progress_title
                            ManagementActionType.UPDATE -> R.string.update_progress_title
                            ManagementActionType.DELETE -> R.string.delete_progress_title
                        },
                        style = TextStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.neutral800,
                            textAlign = TextAlign.Center
                        )
                    )
                    
                    // 상세 메시지
                    LocalizedText(
                        resId = when (actionType) {
                            ManagementActionType.DOWNLOAD -> R.string.download_progress_message
                            ManagementActionType.UPDATE -> R.string.update_progress_message
                            ManagementActionType.DELETE -> R.string.delete_progress_message
                        },
                        formatArgs = arrayOf(downloadingCount),
                        style = TextStyle(
                            fontSize = 16.sp,
                            color = AppColors.neutral600,
                            textAlign = TextAlign.Center
                        )
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // 안내 메시지
                    LocalizedText(
                        resId = R.string.download_progress_wait_message,
                        style = TextStyle(
                            fontSize = 14.sp,
                            color = AppColors.neutral500,
                            textAlign = TextAlign.Center
                        )
                    )
                }
            }
        }
    }
}
