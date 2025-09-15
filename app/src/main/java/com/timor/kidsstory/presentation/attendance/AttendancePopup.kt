package com.timor.kidsstory.presentation.attendance

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.timor.kidsstory.ui.theme.KidsStoryTheme

/**
 * 출석 축하 팝업 컴포넌트
 * - 첫 출석 시 축하 메시지와 연속 출석 일수 표시
 * - 사용자가 닫을 때까지 표시
 *
 * @param isVisible 팝업 표시 여부
 * @param streakCount 연속 출석 일수
 * @param onDismiss 팝업 닫기 콜백
 */
@Composable
fun AttendancePopup(
    isVisible: Boolean,
    streakCount: Int,
    onDismiss: () -> Unit
) {
    if (isVisible) {
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            )
        ) {
            Card(
                modifier = Modifier
                    .width(320.dp)
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // 축하 이모지
                    Text(
                        text = "🎉",
                        fontSize = 48.sp,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // 축하 메시지
                    Text(
                        text = if (streakCount == 1) {
                            "첫 출석을 축하해요!"
                        } else {
                            "연속 ${streakCount}일째 출석!"
                        },
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = Color(0xFF2E7D32)
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // 격려 메시지
                    Text(
                        text = if (streakCount == 1) {
                            "매일 책을 읽으며 성장해보세요!"
                        } else {
                            "꾸준한 독서 습관이 멋져요!"
                        },
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        color = Color.Gray
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // 연속 출석 표시
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🔥",
                            fontSize = 24.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${streakCount}일째 연속 출석",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFFE65100)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // 확인 버튼
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .width(120.dp)
                            .height(40.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4CAF50)
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            text = "확인",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun AttendancePopupPreview() {
    KidsStoryTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f)),
            contentAlignment = Alignment.Center
        ) {
            AttendancePopup(
                isVisible = true,
                streakCount = 7,
                onDismiss = {}
            )
        }
    }
}

@Preview
@Composable
private fun FirstAttendancePopupPreview() {
    KidsStoryTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f)),
            contentAlignment = Alignment.Center
        ) {
            AttendancePopup(
                isVisible = true,
                streakCount = 1,
                onDismiss = {}
            )
        }
    }
}
