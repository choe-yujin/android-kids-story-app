package com.timor.kidsstory.presentation.book.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.timor.kidsstory.R
import com.timor.kidsstory.domain.model.Mission
import com.timor.kidsstory.ui.components.LocalizedText
import com.timor.kidsstory.ui.theme.AppTextStyles
import com.timor.kidsstory.ui.theme.KidsStoryTheme

/**
 * 책 완독 축하 다이얼로그 컴포넌트
 *
 * @param isVisible 다이얼로그 표시 여부
 * @param mission 미션 정보 (null일 경우 기본 축하 메시지 사용)
 * @param onConfirm 확인 버튼 클릭 콜백
 */
@Composable
fun CompletionDialog(
    isVisible: Boolean,
    mission: Mission? = null,
    onConfirm: () -> Unit
) {
    if (isVisible) {
        Dialog(
            onDismissRequest = onConfirm,
            properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = false)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.95f) // 🆕 가로 폭 확장
                    .padding(16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp), // 🆕 패딩 축소
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // 미션 제목 또는 기본 축하 제목
                    if (mission != null) {
                        // 🆕 미션 데이터가 있을 때 - 1줄로 제한
                        Text(
                            text = mission.title,
                            style = AppTextStyles.gummyLgSemiboldItalic.copy(
                                fontSize = 24.sp, // 🆕 크기 약간 축소
                                fontWeight = FontWeight.Bold
                            ),
                            color = Color(0xFF4CAF50),
                            textAlign = TextAlign.Center,
                            maxLines = 1, // 🆕 1줄로 제한
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        // 기본 축하 제목
                        LocalizedText(
                            resId = R.string.completion_title,
                            style = AppTextStyles.gummyLgSemiboldItalic.copy(
                                fontSize = 24.sp, // 🆕 크기 축소
                                fontWeight = FontWeight.Bold
                            ),
                            color = Color(0xFF4CAF50),
                            textAlign = TextAlign.Center,
                            maxLines = 1, // 🆕 1줄로 제한
                            modifier = Modifier.fillMaxWidth()
                        )
                    }                    
                    Spacer(modifier = Modifier.height(20.dp))

                    // 미션 설명 또는 기본 축하 메시지
                    if (mission != null) {
                        // 미션 데이터가 있을 때 - 일반 크기로 왼쪽 정렬
                        Text(
                            text = mission.description,
                            style = AppTextStyles.gummyMedium.copy(fontSize = 16.sp),
                            color = Color(0xFF424242),
                            textAlign = TextAlign.Start,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp) // 🆕 패딩 축소
                        )
                    } else {
                        // 기본 축하 메시지
                        LocalizedText(
                            resId = R.string.completion_message,
                            style = AppTextStyles.gummyMedium.copy(fontSize = 16.sp), // 🆕 크기 축소
                            color = Color(0xFF424242),
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp) // 🆕 패딩 축소
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = onConfirm,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        LocalizedText(
                            resId = R.string.completion_button,
                            style = AppTextStyles.gummyMedium.copy(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Default Completion Dialog")
@Composable
private fun CompletionDialogPreview() {
    KidsStoryTheme {
        CompletionDialog(
            isVisible = true,
            onConfirm = {}
        )
    }
}

@Preview(showBackground = true, name = "Mission Completion Dialog")
@Composable
private fun CompletionDialogWithMissionPreview() {
    KidsStoryTheme {
        CompletionDialog(
            isVisible = true,
            mission = Mission(
                title = "축하합니다!",
                description = "미래의 동티모르 지도자가 되기 위해 학습을 계속하고 책을 많이 읽어보세요. 여러분의 노력이 반드시 도움이 될 것입니다."
            ),
            onConfirm = {}
        )
    }
}
