package com.timor.kidsstory.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.timor.kidsstory.R

object AppTextStyles {
    private val pretendardFamily = FontFamily(
    Font(R.font.pretendard_variable) // Variable Font 사용
    )
    private val sourGummyFamily = FontFamily(
        Font(R.font.gummy_variable) // Variable Font 사용
    )
    private val sourGummyItalicFamily = FontFamily(
        Font(R.font.gummy_italic_variable) // Variable Font 사용
    )

    // Pretendard 스타일
    // sm/medium
    // 총 페이지 쪽수
    val pretendardSmallMedium = TextStyle(
        fontSize = 12.sp,
        lineHeight = 16.sp,
        fontWeight = FontWeight.W400,
        fontFamily = pretendardFamily
    )

    // sm/semibold
    // 현재 페이지 쪽수 / 책 제목
    val pretendardSemibold = TextStyle(
        fontSize = 12.sp,
        lineHeight = 16.sp,
        fontWeight = FontWeight.W600,
        fontFamily = pretendardFamily
    )

    // md/medium
    // 현재 쪽 페이지 / 토글버튼 텍스트
    val pretendardMedium = TextStyle(
        fontSize = 14.sp,
        lineHeight = 20.sp,
        fontWeight = FontWeight.W400,
        fontFamily = pretendardFamily
    )

    // lg/medium
    // 일반 동화 본문
    val pretendardLargeMedium = TextStyle(
        fontSize = 16.sp,
        lineHeight = 28.sp,
        fontWeight = FontWeight.W500,
        fontFamily = pretendardFamily
    )

    // lg/semiBold
    // 일반 동화 본문
    val pretendardXLargeSemiBold = TextStyle(
        fontSize = 18.sp,
        lineHeight = 28.sp,
        fontWeight = FontWeight.W700,
        fontFamily = pretendardFamily
    )

    // Sour Gummy 스타일

    // sm
    // 채팅창 텍스트
    val gummySmall = TextStyle(
        fontSize = 16.sp,
        lineHeight = 32.sp,
        fontWeight = FontWeight.W500,
        fontFamily = sourGummyFamily
    )

    // sm/semibold
    // 카드 타이틀 / 포인트
    val gummySmallSemibold = TextStyle(
        fontSize = 16.sp,
        lineHeight = 32.sp,
        fontWeight = FontWeight.W700,
        fontFamily = sourGummyFamily
    )

    // vsm/medium
    val gummyVSmallMediumItalic = TextStyle(
        fontSize = 14.sp,
        lineHeight = 28.sp,
        fontWeight = FontWeight.W500,
        fontFamily = sourGummyItalicFamily
    )

    // vvsm/medium
    val gummyVvSmallMediumItalic = TextStyle(
        fontSize = 12.sp,
        lineHeight = 24.sp,
        fontWeight = FontWeight.W500,
        fontFamily = sourGummyItalicFamily
    )

    // vvsm/regular
    val gummyVvSmallRegularItalic = TextStyle(
        fontSize = 12.sp,
        lineHeight = 24.sp,
        fontWeight = FontWeight.W400,
        fontFamily = sourGummyItalicFamily
    )

    // md/semibold
    // 인풋
    val gummyMediumSemibold = TextStyle(
        fontSize = 20.sp,
        lineHeight = 32.sp,
        fontWeight = FontWeight.W700,
        fontFamily = sourGummyFamily
    )

    // md/medium
    // 메시지 / 토글
    val gummyMedium = TextStyle(
        fontSize = 20.sp,
        lineHeight = 32.sp,
        fontWeight = FontWeight.W500,
        fontFamily = sourGummyFamily
    )

    // lg/semibold
    // 헤더타이틀
    val gummyLgSemiboldItalic = TextStyle(
        fontSize = 28.sp,
        lineHeight = 48.sp,
        fontWeight = FontWeight.W700,
        fontFamily = sourGummyItalicFamily
    )
}
