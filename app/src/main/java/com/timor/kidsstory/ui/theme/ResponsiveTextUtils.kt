package com.timor.kidsstory.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 화면 크기에 따른 반응형 텍스트 스타일을 제공하는 유틸리티 객체
 * 태블릿과 휴대폰에서 일관된 글씨 크기 비율을 유지합니다.
 */
object ResponsiveTextUtils {
    
    /**
     * 화면 크기 기준 정의
     */
    private const val PHONE_WIDTH_DP = 400f  // 휴대폰 기준 너비
    private const val TABLET_WIDTH_DP = 800f // 태블릿 기준 너비
    
    /**
     * 화면 크기에 따른 스케일링 팩터를 계산합니다.
     * @return 1.0 (휴대폰) ~ 1.5 (태블릿) 범위의 스케일링 팩터
     */
    @Composable
    fun getScreenScaleFactor(): Float {
        val configuration = LocalConfiguration.current
        val screenWidth = configuration.screenWidthDp.toFloat()
        
        return when {
            screenWidth <= PHONE_WIDTH_DP -> 1.0f
            screenWidth >= TABLET_WIDTH_DP -> 1.5f
            else -> {
                // 중간 크기는 선형 보간
                1.0f + (screenWidth - PHONE_WIDTH_DP) / (TABLET_WIDTH_DP - PHONE_WIDTH_DP) * 0.5f
            }
        }
    }
    
    /**
     * 페이지 번호에 따른 텍스트 스타일을 반환합니다.
     * 첫 번째 페이지(표지)는 특별히 큰 글씨를 사용합니다.
     */
    @Composable
    fun getPageTextStyle(pageNumber: Int, isTitle: Boolean = false): TextStyle {
        val scaleFactor = getScreenScaleFactor()
        
        return when {
            // 첫 번째 페이지 제목 (표지)
            pageNumber == 0 || isTitle -> {
                AppTextStyles.gummyLgSemiboldItalic.copy(
                    fontSize = (32.sp * scaleFactor),
                    lineHeight = (40.sp * scaleFactor),
                    fontWeight = FontWeight.W800
                )
            }
            // 일반 페이지 텍스트
            else -> {
                AppTextStyles.pretendardLargeMedium.copy(
                    fontSize = (18.sp * scaleFactor),
                    lineHeight = (28.sp * scaleFactor),
                    fontWeight = FontWeight.W500
                )
            }
        }
    }
    
    /**
     * 페이지 번호 표시용 텍스트 스타일
     */
    @Composable
    fun getPageNumberStyle(): TextStyle {
        val scaleFactor = getScreenScaleFactor()
        
        return AppTextStyles.pretendardSmallMedium.copy(
            fontSize = (14.sp * scaleFactor),
            lineHeight = (18.sp * scaleFactor)
        )
    }
    
    /**
     * 제목인지 판단하는 유틸리티 함수
     * 첫 페이지이거나 텍스트가 짧고 대문자가 많으면 제목으로 간주
     */
    fun isLikelyTitle(text: String, pageNumber: Int): Boolean {
        return pageNumber == 0 || 
               (text.length < 100 && text.count { it.isUpperCase() } > text.length * 0.3)
    }
    
    /**
     * 화면 크기별 패딩값 반환
     */
    @Composable
    fun getResponsivePadding(): Int {
        val scaleFactor = getScreenScaleFactor()
        return (16 * scaleFactor).toInt()
    }
    
    /**
     * 설정 화면용 반응형 텍스트 스타일들
     */
    
    /**
     * 설정 화면 카드 제목용 스타일
     */
    @Composable
    fun getSettingCardTitleStyle(): TextStyle {
        val scaleFactor = getScreenScaleFactor()
        return AppTextStyles.gummyMediumSemibold.copy(
            fontSize = (20.sp * scaleFactor),
            lineHeight = (32.sp * scaleFactor)
        )
    }
    
    /**
     * 설정 화면 일반 텍스트용 스타일
     */
    @Composable
    fun getSettingTextStyle(): TextStyle {
        val scaleFactor = getScreenScaleFactor()
        return AppTextStyles.gummySmallSemibold.copy(
            fontSize = (16.sp * scaleFactor),
            lineHeight = (32.sp * scaleFactor)
        )
    }
    
    /**
     * 설정 화면 작은 텍스트용 스타일 (버전 정보 등)
     */
    @Composable
    fun getSettingSmallTextStyle(): TextStyle {
        val scaleFactor = getScreenScaleFactor()
        return AppTextStyles.gummyVSmallMediumItalic.copy(
            fontSize = (14.sp * scaleFactor),
            lineHeight = (28.sp * scaleFactor)
        )
    }
    
    /**
     * 설정 화면 매우 작은 텍스트용 스타일 (라이센스 등)
     */
    @Composable
    fun getSettingVerySmallTextStyle(): TextStyle {
        val scaleFactor = getScreenScaleFactor()
        return AppTextStyles.pretendardVSmall.copy(
            fontSize = (10.sp * scaleFactor),
            lineHeight = (12.sp * scaleFactor)
        )
    }
    
    /**
     * 설정 화면 토글 버튼 텍스트용 스타일
     */
    @Composable
    fun getSettingToggleTextStyle(): TextStyle {
        val scaleFactor = getScreenScaleFactor()
        return AppTextStyles.gummySmallSemibold.copy(
            fontSize = (16.sp * scaleFactor),
            lineHeight = (32.sp * scaleFactor)
        )
    }
    
    /**
     * 반응형 아이콘 크기 반환
     */
    @Composable
    fun getResponsiveIconSize(): Int {
        val scaleFactor = getScreenScaleFactor()
        return (24 * scaleFactor).toInt()
    }
    
    /**
     * 반응형 큰 아이콘 크기 반환 (헤더 등)
     */
    @Composable
    fun getResponsiveLargeIconSize(): Int {
        val scaleFactor = getScreenScaleFactor()
        return (32 * scaleFactor).toInt()
    }
}
