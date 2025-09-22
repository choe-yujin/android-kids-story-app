package com.timor.kidsstory.presentation.attendance

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.timor.kidsstory.R
import com.timor.kidsstory.ui.components.LocalizedText
import com.timor.kidsstory.ui.theme.AppTextStyles
import com.timor.kidsstory.ui.theme.KidsStoryTheme

/**
 * 출석 축하 팝업 컴포넌트 (수정됨)
 * - 반응형 레이아웃 적용 (태블릿/휴대폰)
 * - 다국어 처리 완료
 * - 텍스트 중앙 정렬
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
            properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
        ) {
            val configuration = LocalConfiguration.current
            val screenWidth = configuration.screenWidthDp
            val isTablet = screenWidth >= 600

            Card(
                modifier = if (isTablet) {
                    Modifier
                        .fillMaxWidth(0.6f)
                        .padding(16.dp)
                } else {
                    Modifier
                        .fillMaxWidth(0.9f) // 휴대폰에서 90% 너비 사용
                        .padding(16.dp)
                },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🎉",
                        fontSize = 48.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    LocalizedText(
                        resId = if (streakCount == 1) R.string.attendance_popup_title_first
                                else R.string.attendance_popup_title_consecutive,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center,
                        formatArgs = arrayOf(streakCount)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    LocalizedText(
                        resId = if (streakCount == 1) R.string.attendance_popup_message_first
                                else R.string.attendance_popup_message_consecutive,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🔥",
                            fontSize = 24.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        LocalizedText(
                            resId = R.string.attendance_popup_streak_info,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.primary,
                            formatArgs = arrayOf(streakCount)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(26.dp)
                    ) {
                        LocalizedText(
                            resId = R.string.attendance_popup_button_ok,
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                            fontSize = 18.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Preview(name = "Phone - First Day")
@Composable
private fun FirstAttendancePopupPreview() {
    KidsStoryTheme {
        AttendancePopup(
            isVisible = true,
            streakCount = 1,
            onDismiss = {}
        )
    }
}

@Preview(name = "Phone - Nth Day")
@Composable
private fun AttendancePopupPreview() {
    KidsStoryTheme {
        AttendancePopup(
            isVisible = true,
            streakCount = 7,
            onDismiss = {}
        )
    }
}

@Preview(name = "Tablet - Nth Day", widthDp = 1024, heightDp = 600)
@Composable
private fun TabletAttendancePopupPreview() {
    KidsStoryTheme {
        AttendancePopup(
            isVisible = true,
            streakCount = 15,
            onDismiss = {}
        )
    }
}