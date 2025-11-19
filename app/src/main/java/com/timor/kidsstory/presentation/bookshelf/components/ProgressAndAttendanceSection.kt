package com.timor.kidsstory.presentation.bookshelf.components

import android.content.res.Configuration
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.timor.kidsstory.R
import com.timor.kidsstory.domain.model.Language
import com.timor.kidsstory.domain.util.LanguageConstants
import com.timor.kidsstory.domain.util.LanguageManager
import com.timor.kidsstory.ui.theme.KidsStoryTheme
import java.util.Locale

/**
 * 출석 및 읽기 진도 표시 섹션 - 디자인 명세에 맞춘 구현
 * - 연속 출석 일수 (🔥 아이콘 + "3day" 텍스트)
 * - 원형 프로그레스바 (총 책 수에 따라 영역 분할, 읽은 책 수만큼 채워짐)
 * - 읽은 책 수 / 총 책 수 텍스트
 * - 언어별 폰트 적용 (영어/테툼어: Gummy, 한국어: CookieRun)
 * - 태블릿 다국어 캐싱 이슈 해결
 *
 * @param streakCount 연속 출석 일수
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
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp

    val isTablet = screenWidth >= 800

    val fontFamily = when (currentLanguage.code) {
        "ko" -> FontFamily(Font(R.font.cookierun_regular))
        "en", "tet" -> FontFamily(Font(R.font.gummy_italic_variable))
        else -> FontFamily(Font(R.font.gummy_italic_variable))
    }

    val iconSize = if (isTablet) 24.sp else 22.sp  // 🔥 아이콘 크기 증가
    val dayTextSize = if (isTablet) 18.sp else 16.sp  // 출석일 텍스트 크기 증가
    val progressTextSize = if (isTablet) 13.sp else 12.sp  // 진행률 텍스트 크기 증가
    val progressSize = if (isTablet) 36.dp else 32.dp
    val itemSpacing = if (isTablet) 12.dp else 10.dp

    // 출석일 텍스트 (다국어 및 형식 처리)
    val attendanceText = remember(LanguageManager.getCurrentLanguageCode(), streakCount) {
        val langCode = LanguageManager.getCurrentLanguageCode()
        val locale = Locale(langCode)
        val config = Configuration(context.resources.configuration).apply { setLocale(locale) }
        val localizedContext = context.createConfigurationContext(config)
        val resId = if (streakCount == 1) R.string.attendance_day else R.string.attendance_days
        val dayText = localizedContext.getString(resId)
        when (langCode) {
            "tet" -> "$dayText $streakCount"
            else -> "$streakCount$dayText"
        }
    }

    // 진행률 텍스트 (다국어 처리)
    val progressText = remember(LanguageManager.getCurrentLanguageCode(), completedBooks, totalBooks) {
        val langCode = LanguageManager.getCurrentLanguageCode()
        val locale = Locale(langCode)
        val config = Configuration(context.resources.configuration).apply { setLocale(locale) }
        val localizedContext = context.createConfigurationContext(config)
        localizedContext.getString(R.string.reading_progress_fraction, completedBooks, totalBooks)
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(itemSpacing),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.clickable { onMyPageClick() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "🔥",
                fontSize = iconSize
            )
            Text(
                text = attendanceText,
                fontSize = dayTextSize,
                fontFamily = fontFamily,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
        
        // 🆕 progressbar와 1일 사이에 4dp 간격 추가
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(4.dp))

        Box(
            contentAlignment = Alignment.Center
        ) {
            CircularBookProgress(
                completedBooks = completedBooks,
                totalBooks = totalBooks,
                size = progressSize,
                strokeWidth = if (isTablet) 5.dp else 4.dp  // 🆕 원형 progressbar 굵기 증가
            )
            Text(
                text = progressText,
                fontSize = progressTextSize,
                fontFamily = fontFamily,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}

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

        drawCircle(
            color = Color.White.copy(alpha = 0.3f),
            radius = radius,
            center = center,
            style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
        )

        if (totalBooks > 0 && completedBooks > 0) {
            val progressAngle = (completedBooks.toFloat() / totalBooks.toFloat()) * 360f
            drawArc(
                color = Color(0xFF4A90E2),
                startAngle = -90f,
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

// 호환성을 위한 오버로드
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
        currentLanguage = LanguageConstants.ENGLISH,
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
