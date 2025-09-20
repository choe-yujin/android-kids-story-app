package com.timor.kidsstory.presentation.bookshelf.components

import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.timor.kidsstory.R
import com.timor.kidsstory.domain.model.Language
import com.timor.kidsstory.domain.util.LanguageConstants
import com.timor.kidsstory.ui.theme.KidsStoryTheme
import com.timor.kidsstory.ui.theme.ResponsiveTextUtils
import kotlin.math.PI

/**
 * 출석 및 읽기 진도 표시 섹션 - 디자인 명세에 맞춘 구현
 * - 연속 출석 일수 (🔥 아이콘 + "3day" 텍스트)
 * - 원형 프로그레스바 (총 책 수에 따라 영역 분할, 읽은 책 수만큼 채워짐)
 * - 읽은 책 수 / 총 책 수 텍스트
 * - 언어별 폰트 적용 (영어/테툼어: Gummy, 한국어: CookieRun)
 *
 * @param streakCount 연속 출석 일수
 * @param readingProgress 읽기 진도 (0.0 ~ 1.0) - 사용하지 않음
 * @param completedBooks 완독한 책 수
 * @param totalBooks 전체 책 수
 * @param currentLanguage 현재 언어 (폰트 결정용)
 * @param onMyPageClick MyPage 클릭 콜백
 */
@Composable
fun ProgressAndAttendanceSection(
    streakCount: Int,
    readingProgress: Float, // 호환성을 위해 유지하지만 사용하지 않음
    completedBooks: Int,
    totalBooks: Int,
    currentLanguage: Language = LanguageConstants.ENGLISH,
    onMyPageClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val scaleFactor = ResponsiveTextUtils.getScreenScaleFactor()
    
    // 태블릿에서는 더 크게
    val isTablet = screenWidth >= 800
    
    // 언어별 폰트
    val fontFamily = when (currentLanguage.code) {
        "ko" -> FontFamily(Font(R.font.cookierun_regular))
        "en", "tet" -> FontFamily(Font(R.font.gummy_italic_variable))
        else -> FontFamily(Font(R.font.gummy_italic_variable))
    }
    
    // 크기 조정 - 디자인 명세에 맞게 더 크게
    val iconSize = if (isTablet) (28 * scaleFactor).sp else 24.sp // 불꽃 아이콘 더 크게
    val dayTextSize = if (isTablet) (22 * scaleFactor).sp else 18.sp // day 텍스트 더 크게
    val progressTextSize = if (isTablet) (14 * scaleFactor).sp else 12.sp // 진행률 텍스트도 약간 크게
    val progressSize = if (isTablet) (48 * scaleFactor).dp else 40.dp
    val itemSpacing = if (isTablet) (16 * scaleFactor).dp else 12.dp
    val padding = if (isTablet) (12 * scaleFactor).dp else 8.dp
    
    Row(
        horizontalArrangement = Arrangement.spacedBy(itemSpacing),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.clickable { onMyPageClick() }
    ) {
        // 연속 출석 표시
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp) // 아이콘과 텍스트 사이 간격
        ) {
            Text(
                text = "🔥",
                fontSize = iconSize
            )
            Text(
                text = if (currentLanguage.code == "tet") {
                    // 테툨어: 단어 + 숫자 순서 (loron 1, loron 3)
                    "${stringResource(
                        if (streakCount == 1) R.string.attendance_day else R.string.attendance_days
                    )} $streakCount"
                } else {
                    // 영어/한국어: 숫자 + 단어 순서 (1day, 3일)
                    "$streakCount${stringResource(
                        if (streakCount == 1) R.string.attendance_day else R.string.attendance_days
                    )}"
                },
                fontSize = dayTextSize, // 더 큰 글씨
                fontFamily = fontFamily,
                fontWeight = FontWeight.Bold, // 발두어졌게
                color = Color.Black // 검정색으로 변경
            )
        }
        
        // 원형 프로그레스바와 진행률 텍스트 겹치게 표시
        Box(
            contentAlignment = Alignment.Center
        ) {
            CircularBookProgress(
                completedBooks = completedBooks,
                totalBooks = totalBooks,
                size = progressSize,
                strokeWidth = if (isTablet) (6 * scaleFactor).dp else 5.dp // 더 굵게 (4dp/3dp에서 6dp/5dp로)
            )
            
            // 진행률 텍스트를 프로그레스바 위에 겹치게 표시
            Text(
                text = "$completedBooks/$totalBooks",
                fontSize = progressTextSize,
                fontFamily = fontFamily,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}

/**
 * 원형 책 진행률 표시
 * 총 책 수에 따라 원을 등분하고, 읽은 책 수만큼 채워지는 프로그레스바
 */
@Composable
private fun CircularBookProgress(
    completedBooks: Int,
    totalBooks: Int,
    size: androidx.compose.ui.unit.Dp,
    strokeWidth: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier.size(size)
    ) {
        val center = androidx.compose.ui.geometry.Offset(
            x = this.size.width / 2f,
            y = this.size.height / 2f
        )
        val radius = (this.size.width - strokeWidth.toPx()) / 2f
        
        // 배경 원 (회색)
        drawCircle(
            color = Color.White.copy(alpha = 0.3f),
            radius = radius,
            center = center,
            style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
        )
        
        // 진행률 원 (파란색)
        if (totalBooks > 0 && completedBooks > 0) {
            val progressAngle = (completedBooks.toFloat() / totalBooks.toFloat()) * 360f
            drawArc(
                color = Color(0xFF4A90E2), // 파란색
                startAngle = -90f, // 12시 방향부터 시작
                sweepAngle = progressAngle,
                useCenter = false,
                topLeft = androidx.compose.ui.geometry.Offset(
                    x = center.x - radius,
                    y = center.y - radius
                ),
                size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
                style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            )
        }
    }
}

// BookshelfScreen에서 사용할 때 현재 언어를 전달하지 않는 기존 버전 (호환성)
@Composable
fun ProgressAndAttendanceSection(
    streakCount: Int,
    readingProgress: Float,
    completedBooks: Int,
    totalBooks: Int,
    onMyPageClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ProgressAndAttendanceSection(
        streakCount = streakCount,
        readingProgress = readingProgress,
        completedBooks = completedBooks,
        totalBooks = totalBooks,
        currentLanguage = LanguageConstants.ENGLISH, // 기본값
        onMyPageClick = onMyPageClick,
        modifier = modifier
    )
}

@Preview(name = "English - 3/8 Books")
@Composable
private fun ProgressAndAttendanceSectionEnglishPreview() {
    KidsStoryTheme {
        Box(
            modifier = Modifier
                .background(Color(0xFFFDD25A))
                .padding(16.dp)
        ) {
            ProgressAndAttendanceSection(
                streakCount = 3,
                readingProgress = 0.375f,
                completedBooks = 3,
                totalBooks = 8,
                currentLanguage = LanguageConstants.ENGLISH,
                onMyPageClick = {}
            )
        }
    }
}

@Preview(name = "Korean - 5/20 Books")
@Composable
private fun ProgressAndAttendanceSectionKoreanPreview() {
    KidsStoryTheme {
        Box(
            modifier = Modifier
                .background(Color(0xFFFDD25A))
                .padding(16.dp)
        ) {
            ProgressAndAttendanceSection(
                streakCount = 7,
                readingProgress = 0.25f,
                completedBooks = 5,
                totalBooks = 20,
                currentLanguage = LanguageConstants.KOREAN,
                onMyPageClick = {}
            )
        }
    }
}

@Preview(name = "Tetum - 0/15 Books")
@Composable
private fun ProgressAndAttendanceSectionTetumPreview() {
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
                currentLanguage = LanguageConstants.TETUM,
                onMyPageClick = {}
            )
        }
    }
}

@Preview(name = "Tablet - 12/20 Books", widthDp = 800, heightDp = 100)
@Composable
private fun ProgressAndAttendanceSectionTabletPreview() {
    KidsStoryTheme {
        Box(
            modifier = Modifier
                .background(Color(0xFFFDD25A))
                .padding(16.dp)
        ) {
            ProgressAndAttendanceSection(
                streakCount = 14,
                readingProgress = 0.6f,
                completedBooks = 12,
                totalBooks = 20,
                currentLanguage = LanguageConstants.KOREAN,
                onMyPageClick = {}
            )
        }
    }
}
