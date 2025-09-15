package com.timor.kidsstory.presentation.bookshelf.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.timor.kidsstory.ui.theme.KidsStoryTheme

/**
 * 출석 및 읽기 진도 표시 섹션
 * - 연속 출석 일수와 읽기 진도를 간단하게 표시
 * - 헤더 우측에 배치되어 MyPage로 이동할 수 있는 클릭 가능한 영역
 *
 * @param streakCount 연속 출석 일수
 * @param readingProgress 읽기 진도 (0.0 ~ 1.0)
 * @param completedBooks 완독한 책 수
 * @param totalBooks 전체 책 수
 * @param onMyPageClick MyPage 클릭 콜백
 */
@Composable
fun ProgressAndAttendanceSection(
    streakCount: Int,
    readingProgress: Float,
    completedBooks: Int,
    totalBooks: Int,
    onMyPageClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = 0.2f))
            .clickable { onMyPageClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 연속 출석 표시
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🔥",
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${streakCount}days",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            }
            
            // 읽기 진도 표시
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📚",
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "$completedBooks/$totalBooks",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            }
        }
    }
}

@Preview
@Composable
private fun ProgressAndAttendanceSectionPreview() {
    KidsStoryTheme {
        Box(
            modifier = Modifier
                .background(Color(0xFFFDD25A))
                .padding(16.dp)
        ) {
            ProgressAndAttendanceSection(
                streakCount = 7,
                readingProgress = 0.6f,
                completedBooks = 12,
                totalBooks = 20,
                onMyPageClick = {}
            )
        }
    }
}

@Preview
@Composable
private fun ProgressAndAttendanceSectionZeroPreview() {
    KidsStoryTheme {
        Box(
            modifier = Modifier
                .background(Color(0xFFFDD25A))
                .padding(16.dp)
        ) {
            ProgressAndAttendanceSection(
                streakCount = 0,
                readingProgress = 0f,
                completedBooks = 0,
                totalBooks = 15,
                onMyPageClick = {}
            )
        }
    }
}
