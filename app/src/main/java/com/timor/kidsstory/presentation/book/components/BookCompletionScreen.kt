package com.timor.kidsstory.presentation.book.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.timor.kidsstory.R
import com.timor.kidsstory.ui.theme.AppColors
import com.timor.kidsstory.ui.theme.KidsStoryTheme
import com.timor.kidsstory.ui.theme.ResponsiveTextUtils
import kotlinx.coroutines.delay

/**
 * 책 완독 축하 화면 컴포넌트
 *
 * 사용자가 책을 모두 읽었을 때 나타나는 축하 화면입니다.
 * 아이들에게 성취감을 주고 다음 행동을 유도하는 역할을 합니다.
 *
 * @param onConfirm 확인 버튼 클릭 시 호출되는 콜백 (책장으로 돌아가기)
 * @param modifier 레이아웃 수정자
 */
@Composable
fun BookCompletionScreen(
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 자동으로 효과음 재생 (선택사항)
    LaunchedEffect(Unit) {
        // 완독 축하 효과음이 있다면 여기서 재생
        delay(500) // 화면이 완전히 나타난 후 재생
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f)) // 반투명 배경
            .zIndex(10f), // 다른 모든 요소 위에 표시
        contentAlignment = Alignment.Center
    ) {
        // 🆕 축하 카드 - 반응형으로 수정
        Column(
            modifier = Modifier
                .fillMaxWidth(0.95f) // 🆕 가로 폭 95% 사용
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(24.dp) // 🆕 둥근 모서리 증가
                )
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 축하 아이콘 (트로피나 별 등)
            Icon(
                painter = painterResource(id = android.R.drawable.btn_star_big_on),
                contentDescription = "축하",
                tint = Color(0xFFFFD700), // 금색
                modifier = Modifier.size(80.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 🆕 축하 메시지 - 1줄로 유지
            Text(
                text = "참 잘했어요!",
                fontSize = 28.sp, // 🆕 고정 크기로 변경
                fontWeight = FontWeight.Bold,
                color = AppColors.primary600,
                textAlign = TextAlign.Center,
                maxLines = 1, // 🆕 1줄로 제한
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 🆕 부제목 - 1줄로 유지
            Text(
                text = "책을 끝까지 읽었어요",
                fontSize = 16.sp, // 🆕 고정 크기로 변경
                color = AppColors.neutral600,
                textAlign = TextAlign.Center,
                maxLines = 1, // 🆕 1줄로 제한
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(32.dp)) // 🆕 간격 증가
            
            // 🆕 확인 버튼 - 가로 폭 전체 사용
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppColors.primary500
                ),
                shape = RoundedCornerShape(16.dp), // 🆕 둥근 모서리 증가
                modifier = Modifier
                    .fillMaxWidth() // 🆕 가로 폭 전체 사용
                    .height(56.dp) // 🆕 높이 증가
            ) {
                Text(
                    text = "책장으로 돌아가기", // 🆕 더 명확한 텍스트
                    fontSize = 18.sp, // 🆕 폰트 크기 증가
                    fontWeight = FontWeight.Bold, // 🆕 굵게
                    color = Color.White
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BookCompletionScreenPreview() {
    KidsStoryTheme {
        BookCompletionScreen(
            onConfirm = {}
        )
    }
}
