package com.timor.kidsstory.presentation.bookshelf.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.timor.kidsstory.R
import com.timor.kidsstory.ui.theme.AppColors
import com.timor.kidsstory.ui.components.LocalizedText

/**
 * 다운로드/업데이트/삭제 완료 시 표시되는 다이얼로그
 */
@Composable
fun ActionCompletionDialog(
    actionType: ActionType,
    completedCount: Int,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = modifier
                .fillMaxWidth(0.9f) // 🆕 가로 폭 제한 추가
                .padding(24.dp), // 🆕 패딩 축소
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = AppColors.neutralWhite
            )
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 성공 아이콘
                Icon(
                    painter = painterResource(R.drawable.ic_check_circle),
                    contentDescription = null,
                    tint = AppColors.green500,
                    modifier = Modifier.size(48.dp)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 완료 메시지
                LocalizedText(
                    resId = when (actionType) {
                        ActionType.DOWNLOAD -> R.string.action_completion_download_title
                        ActionType.UPDATE -> R.string.action_completion_update_title
                        ActionType.DELETE -> R.string.action_completion_delete_title
                    },
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.neutral800,
                        textAlign = TextAlign.Center
                    )
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // 상세 메시지
                LocalizedText(
                    resId = when (actionType) {
                        ActionType.DOWNLOAD -> R.string.action_completion_download_message
                        ActionType.UPDATE -> R.string.action_completion_update_message  
                        ActionType.DELETE -> R.string.action_completion_delete_message
                    },
                    formatArgs = arrayOf(completedCount),
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = AppColors.neutral600,
                        textAlign = TextAlign.Center
                    )
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // 확인 버튼
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppColors.blue500
                    )
                ) {
                    LocalizedText(
                        resId = R.string.action_completion_button_ok,
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = AppColors.neutralWhite
                        )
                    )
                }
            }
        }
    }
}
