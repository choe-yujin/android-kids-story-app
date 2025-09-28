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
import com.timor.kidsstory.presentation.util.formatFileSize

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
            modifier = Modifier
                .fillMaxWidth(1f) // 🔧 가로 폭 확장: 85% → 92%
                .fillMaxHeight(1f) // 🆕 세로 높이 고정: 화면의 40%
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            color = AppColors.neutralWhite,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp) // 🔧 패딩 증가: 32dp → 40dp
                    .fillMaxSize(), // 🆕 전체 공간 활용
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp) // 🔧 간격 증가: 20dp → 24dp
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
                        fontSize = 24.sp, // 🔧 폰트 크기 증가: 24sp → 26sp
                        fontWeight = FontWeight.Bold
                    ),
                    color = AppColors.neutral900,
                    fontPolicy = FontPolicy.PRETENDA_ALL
                )
                
                // 메시지
                LocalizedText(
                    resId = R.string.management_confirm_size,
                    formatArgs = arrayOf(formatFileSize(totalSize)),
                    style = AppTextStyles.pretendardMedium.copy(
                        fontSize = 18.sp, // 🔧 폰트 크기 증가: 18sp → 20sp
                        lineHeight = 24.sp // 🆕 줄 간격 추가
                    ),
                    color = AppColors.neutral700,
                    textAlign = TextAlign.Center,
                    fontPolicy = FontPolicy.PRETENDA_ALL,
                    modifier = Modifier.fillMaxWidth() // 🆕 전체 폭 활용
                )
                
                Spacer(modifier = Modifier.height(16.dp)) // 🔧 간격 증가: 16dp → 20dp
                
                // 버튼들
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp) // 🔧 버튼 간격 증가: 16dp → 20dp
                ) {
                    // 취소 버튼
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp), // 🔧 버튼 높이 증가: 52dp → 56dp
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
                                fontSize = 18.sp, // 🔧 버튼 폰트 크기 증가: 18sp → 20sp
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
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp), // 🔧 버튼 높이 증가: 52dp → 56dp
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
                                fontSize = 18.sp, // 🔧 버튼 폰트 크기 증가: 18sp → 20sp
                                fontWeight = FontWeight.Bold
                            ),
                            color = AppColors.neutralWhite,
                            fontPolicy = FontPolicy.PRETENDA_ALL
                        )
                    }
                }
                
                // 🆕 여백 추가 (세로 공간 충분히 활용)
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}