package com.timor.kidsstory.presentation.book.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.timor.kidsstory.R
import com.timor.kidsstory.ui.theme.AppTextStyles
import com.timor.kidsstory.ui.theme.KidsStoryTheme

/**
 * 책 완독 축하 다이얼로그 컴포넌트
 * 
 * @param isVisible 다이얼로그 표시 여부
 * @param currentLanguageCode 현재 언어 코드 (폰트 선택용)
 * @param onConfirm 확인 버튼 클릭 콜백
 */
@Composable
fun CompletionDialog(
    isVisible: Boolean,
    currentLanguageCode: String = "ko",
    onConfirm: () -> Unit
) {
    if (isVisible) {
        Dialog(
            onDismissRequest = onConfirm,
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = false
            )
        ) {
            // 언어별 폰트 선택
            val (titleStyle, messageStyle, buttonStyle) = when (currentLanguageCode) {
                "ko" -> Triple(
                    AppTextStyles.cookieRunBlackRegular.copy(
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    AppTextStyles.cookieRunRegular.copy(
                        fontSize = 18.sp
                    ),
                    AppTextStyles.cookieRunBold.copy(
                        fontSize = 16.sp
                    )
                )
                else -> Triple( // 영어, 테툼어 등
                    AppTextStyles.gummyLgSemiboldItalic.copy(
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    AppTextStyles.gummyMedium.copy(
                        fontSize = 18.sp
                    ),
                    AppTextStyles.gummyMedium.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 8.dp
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // 축하 제목
                    Text(
                        text = stringResource(R.string.completion_title),
                        style = titleStyle,
                        color = Color(0xFF4CAF50),
                        textAlign = TextAlign.Center
                    )
                    
                    // 축하 메시지
                    Text(
                        text = stringResource(R.string.completion_message),
                        style = messageStyle,
                        color = Color(0xFF424242),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    
                    // 확인 버튼
                    Button(
                        onClick = onConfirm,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4CAF50)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.completion_button),
                            style = buttonStyle,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CompletionDialogPreview() {
    KidsStoryTheme {
        CompletionDialog(
            isVisible = true,
            currentLanguageCode = "ko",
            onConfirm = {}
        )
    }
}
