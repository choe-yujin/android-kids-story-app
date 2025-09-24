package com.timor.kidsstory.presentation.bookshelf.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.timor.kidsstory.R
import com.timor.kidsstory.presentation.bookshelf.model.ManagementActionType
import com.timor.kidsstory.ui.components.FontPolicy
import com.timor.kidsstory.ui.components.LocalizedText
import com.timor.kidsstory.ui.theme.AppColors
import com.timor.kidsstory.ui.theme.AppTextStyles

/**
 * 관리 모드 작업 확인 다이얼로그
 */
@Composable
fun ManagementConfirmationDialog(
    actionType: ManagementActionType,
    count: Int,
    totalSize: Long,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = AppColors.neutralWhite,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 제목
                LocalizedText(
                    resId = when (actionType) {
                        ManagementActionType.DOWNLOAD -> R.string.management_confirm_title_download
                        ManagementActionType.UPDATE -> R.string.management_confirm_title_update
                        ManagementActionType.DELETE -> R.string.management_confirm_title_delete
                    },
                    formatArgs = arrayOf(count),
                    style = AppTextStyles.pretendardMedium.copy(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = AppColors.neutral900,
                    fontPolicy = FontPolicy.PRETENDA_ALL
                )
                
                // 메시지
                LocalizedText(
                    resId = R.string.management_confirm_size,
                    formatArgs = arrayOf(formatFileSize(totalSize)),
                    style = AppTextStyles.pretendardMedium.copy(fontSize = 16.sp),
                    color = AppColors.neutral700,
                    textAlign = TextAlign.Center,
                    fontPolicy = FontPolicy.PRETENDA_ALL
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // 버튼들
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // 취소 버튼
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = AppColors.neutral700
                        ),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            width = 1.dp
                        )
                    ) {
                        LocalizedText(
                            resId = R.string.management_confirm_cancel,
                            style = AppTextStyles.pretendardMedium.copy(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            color = AppColors.neutral700,
                            fontPolicy = FontPolicy.PRETENDA_ALL
                        )
                    }
                    
                    // 확인 버튼
                    Button(
                        onClick = {
                            onConfirm()
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = when (actionType) {
                                ManagementActionType.DOWNLOAD -> AppColors.blue500
                                ManagementActionType.UPDATE -> AppColors.green500
                                ManagementActionType.DELETE -> AppColors.yellowRed500
                            }
                        )
                    ) {
                        LocalizedText(
                            resId = when (actionType) {
                                ManagementActionType.DOWNLOAD -> R.string.management_action_download
                                ManagementActionType.UPDATE -> R.string.management_action_update
                                ManagementActionType.DELETE -> R.string.management_action_delete
                            },
                            style = AppTextStyles.pretendardMedium.copy(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = AppColors.neutralWhite,
                            fontPolicy = FontPolicy.PRETENDA_ALL
                        )
                    }
                }
            }
        }
    }
}

private fun formatFileSize(bytes: Long): String {
    return when {
        bytes < 1024 -> "$bytes B"
        bytes < 1024 * 1024 -> "${bytes / 1024} KB"
        bytes < 1024 * 1024 * 1024 -> "${bytes / (1024 * 1024)} MB"
        else -> String.format("%.1f GB", bytes / (1024.0 * 1024.0 * 1024.0))
    }
}
