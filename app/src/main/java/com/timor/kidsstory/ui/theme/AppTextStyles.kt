package com.timor.kidsstory.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.timor.kidsstory.R

/**
 * 앱 전체에서 사용하는 텍스트 스타일 정의 객체
 *
 * 앱의 일관된 텍스트 스타일을 위해 모든 텍스트 스타일을 중앙에서 관리합니다.
 * 앱에서는 주로 Pretendard와 Sour Gummy 두 가지 폰트 패밀리를 사용합니다.
 * - Pretendard: 본문, 정보성 텍스트에 적합한 고가독성 폰트
 * - Sour Gummy: 제목, 강조, 친근한 느낌을 주는 특징적인 폰트
 */
object AppTextStyles {
    /**
     * 각 폰트 패밀리 정의
     * 언어별로 다른 폰트를 적용하기 위한 설정
     */
    private val pretendardFamily = FontFamily(
        Font(R.font.pretendard_variable) // 기본 폰트
    )

    // 한국어용 폰트 추가
    private val koreanFontFamily = FontFamily(
        Font(R.font.cookierun_regular) // 한국어용 폰트
    )

    private val sourGummyFamily = FontFamily(
        Font(R.font.gummy_variable) // 기본 폰트
    )

    // 한국어용 Gummy 폰트
    private val koreanGummyFamily = FontFamily(
        Font(R.font.cookierun_bold) // 한국어용 폰트
    )

    private val sourGummyItalicFamily = FontFamily(
        Font(R.font.gummy_italic_variable) // 기본 폰트
    )

    // 한국어용 이탤릭 폰트
    private val koreanGummyItalicFamily = FontFamily(
        Font(R.font.cookierun_black) // 한국어용 이탤릭 폰트
    )

    // CookieRun 폰트 패밀리 추가
    private val cookieRunRegularFamily = FontFamily(
        Font(R.font.cookierun_regular)
    )

    private val cookieRunBoldFamily = FontFamily(
        Font(R.font.cookierun_bold)
    )

    private val cookieRunBlackFamily = FontFamily(
        Font(R.font.cookierun_black)
    )

    /**
     * Pretendard 폰트 스타일 정의
     * 주로 본문, 정보 텍스트, 상세 설명 등에 사용됩니다.
     */

    /**
     * 매우 작은 정보성 텍스트용 스타일
     * 저작권 정보, 부가 설명 등에 적합합니다.
     */
    val pretendardVSmall = TextStyle(
        fontSize = 10.sp,
        lineHeight = 12.sp,
        fontWeight = FontWeight.W400,
        fontFamily = pretendardFamily
    )

    /**
     * 작은 텍스트, 중간 강조용 스타일
     * 페이지 번호, 부가 정보 등에 적합합니다.
     */
    val pretendardSmallMedium = TextStyle(
        fontSize = 12.sp,
        lineHeight = 16.sp,
        fontWeight = FontWeight.W400,
        fontFamily = pretendardFamily
    )

    /**
     * 작은 텍스트, 굵은 강조용 스타일
     * 현재 페이지 표시, 책 제목 등에 적합합니다.
     */
    val pretendardSemibold = TextStyle(
        fontSize = 12.sp,
        lineHeight = 16.sp,
        fontWeight = FontWeight.W600,
        fontFamily = pretendardFamily
    )

    /**
     * 중간 크기의 본문 텍스트
     * UI 요소, 버튼 텍스트 등에 적합합니다.
     */
    val pretendardMedium = TextStyle(
        fontSize = 14.sp,
        lineHeight = 20.sp,
        fontWeight = FontWeight.W400,
        fontFamily = pretendardFamily
    )

    /**
     * 큰 본문 텍스트 스타일
     * 일반 동화 본문 텍스트에 적합합니다.
     */
    val pretendardLargeMedium = TextStyle(
        fontSize = 16.sp,
        lineHeight = 28.sp,
        fontWeight = FontWeight.W500,
        fontFamily = pretendardFamily
    )

    /**
     * 더 큰 본문 텍스트, 굵은 강조용 스타일
     * 도입부, 중요 문장 등에 적합합니다.
     */
    val pretendardXLargeSemiBold = TextStyle(
        fontSize = 18.sp,
        lineHeight = 28.sp,
        fontWeight = FontWeight.W700,
        fontFamily = pretendardFamily
    )

    /**
     * Sour Gummy 폰트 스타일 정의
     * 주로 제목, 강조, 친근한 느낌을 주는 UI 요소에 사용됩니다.
     */

    /**
     * 작은 크기의 Gummy 폰트 스타일
     * 채팅창 텍스트, 친근한 UI 요소에 적합합니다.
     */
    val gummySmall = TextStyle(
        fontSize = 16.sp,
        lineHeight = 32.sp,
        fontWeight = FontWeight.W500,
        fontFamily = sourGummyFamily
    )

    /**
     * 작은 크기의 굵은 Gummy 폰트 스타일
     * 카드 타이틀, 강조 포인트 등에 적합합니다.
     */
    val gummySmallSemibold = TextStyle(
        fontSize = 16.sp,
        lineHeight = 32.sp,
        fontWeight = FontWeight.W700,
        fontFamily = sourGummyItalicFamily
    )

    /**
     * 매우 작은 Gummy 이탤릭체 스타일
     * 작은 강조, 특별 텍스트 등에 적합합니다.
     */
    val gummyVSmallMediumItalic = TextStyle(
        fontSize = 14.sp,
        lineHeight = 28.sp,
        fontWeight = FontWeight.W500,
        fontFamily = sourGummyItalicFamily
    )

    /**
     * 더 작은 Gummy 이탤릭체 스타일
     * 부가 정보, 작은 라벨 등에 적합합니다.
     */
    val gummyVvSmallMediumItalic = TextStyle(
        fontSize = 12.sp,
        lineHeight = 24.sp,
        fontWeight = FontWeight.W500,
        fontFamily = sourGummyItalicFamily
    )

    /**
     * 더 작은 Gummy 이탤릭체 일반 스타일
     * 부가 정보, 설명 텍스트 등에 적합합니다.
     */
    val gummyVvSmallRegularItalic = TextStyle(
        fontSize = 12.sp,
        lineHeight = 24.sp,
        fontWeight = FontWeight.W400,
        fontFamily = sourGummyItalicFamily
    )

    /**
     * 중간 크기의 굵은 Gummy 폰트 스타일
     * 입력 필드, 중요 UI 요소 등에 적합합니다.
     */
    val gummyMediumSemibold = TextStyle(
        fontSize = 20.sp,
        lineHeight = 32.sp,
        fontWeight = FontWeight.W700,
        fontFamily = sourGummyFamily
    )

    /**
     * 중간 크기의 Gummy 폰트 스타일
     * 메시지, 토글 버튼 텍스트 등에 적합합니다.
     */
    val gummyMedium = TextStyle(
        fontSize = 20.sp,
        lineHeight = 32.sp,
        fontWeight = FontWeight.W500,
        fontFamily = sourGummyFamily
    )

    /**
     * 큰 크기의 굵은 Gummy 이탤릭체 스타일
     * 헤더 타이틀, 주요 제목 등에 적합합니다.
     */
    val gummyLgSemiboldItalic = TextStyle(
        fontSize = 28.sp,
        lineHeight = 48.sp,
        fontWeight = FontWeight.W700,
        fontFamily = sourGummyItalicFamily
    )

    /**
     * CookieRun 폰트 스타일 정의
     * 친근하고 귀여운 느낌을 주는 폰트로, 어린이 앱에 적합합니다.
     */

    /**
     * CookieRun Regular 스타일
     * 일반 텍스트, 설명 메시지 등에 적합합니다.
     */
    val cookieRunRegular = TextStyle(
        fontSize = 16.sp,
        lineHeight = 24.sp,
        fontWeight = FontWeight.W400,
        fontFamily = cookieRunRegularFamily
    )

    /**
     * CookieRun Bold 스타일
     * 강조 텍스트, 버튼 등에 적합합니다.
     */
    val cookieRunBold = TextStyle(
        fontSize = 18.sp,
        lineHeight = 26.sp,
        fontWeight = FontWeight.W700,
        fontFamily = cookieRunBoldFamily
    )

    /**
     * CookieRun Black Regular 스타일
     * 제목, 메인 헤더 등에 적합합니다.
     */
    val cookieRunBlackRegular = TextStyle(
        fontSize = 24.sp,
        lineHeight = 32.sp,
        fontWeight = FontWeight.W900,
        fontFamily = cookieRunBlackFamily
    )
}
