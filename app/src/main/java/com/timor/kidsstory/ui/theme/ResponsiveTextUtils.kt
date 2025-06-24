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
     * @return 0.4 (작은 휴대폰) ~ 1.5 (태블릿) 범위의 스케일링 팩터
     */
    @Composable
    fun getScreenScaleFactor(): Float {
        val configuration = LocalConfiguration.current
        val screenWidth = configuration.screenWidthDp.toFloat()
        
        return when {
            screenWidth <= 360f -> 0.4f // 작은 휴대폰은 더 작게 (0.5f -> 0.4f)
            screenWidth <= PHONE_WIDTH_DP -> 0.5f // 일반 휴대폰 (Pixel 5 포함) (0.6f -> 0.5f)
            screenWidth >= TABLET_WIDTH_DP -> 1.5f // 태블릿
            else -> {
                // 중간 크기는 선형 보간
                0.5f + (screenWidth - PHONE_WIDTH_DP) / (TABLET_WIDTH_DP - PHONE_WIDTH_DP) * 1.0f
            }
        }
    }
    
    /**
     * 설정 화면 전용 스케일링 팩터 (더 보수적으로)
     */
    @Composable
    fun getSettingScaleFactor(): Float {
        val configuration = LocalConfiguration.current
        val screenWidth = configuration.screenWidthDp.toFloat()
        
        return when {
            screenWidth <= 360f -> 0.35f // 작은 휴대폰 (0.45f -> 0.35f)
            screenWidth <= PHONE_WIDTH_DP -> 0.4f // 일반 휴대폰 (Pixel 5 포함) (0.5f -> 0.4f)
            screenWidth >= TABLET_WIDTH_DP -> 1.4f // 태블릿 (1.8f에서 1.4f로 감소)
            else -> {
                // 중간 크기는 선형 보간
                0.4f + (screenWidth - PHONE_WIDTH_DP) / (TABLET_WIDTH_DP - PHONE_WIDTH_DP) * 1.0f // 1.4f에서 1.0f로 감소
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
            fontSize = (11.sp * scaleFactor), // 14sp에서 11sp로 감소
            lineHeight = (14.sp * scaleFactor)  // 18sp에서 14sp로 감소
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
     * 헤더 전용 패딩값 반환 (좀 더 넉넉하게)
     */
    @Composable
    fun getHeaderPadding(): Int {
        val configuration = LocalConfiguration.current
        val screenWidth = configuration.screenWidthDp.toFloat()
        val scaleFactor = getScreenScaleFactor()
        
        return when {
            screenWidth <= 360f -> (4 * scaleFactor).toInt() // 작은 화면은 패딩을 더 줄임 (6 -> 4)
            screenWidth <= PHONE_WIDTH_DP -> (8 * scaleFactor).toInt() // 일반 휴대폰 (Pixel 5 포함) (10 -> 8)
            else -> (48 * scaleFactor).toInt() // 태블릿은 더 넉넉하게
        }
    }
    
    /**
     * 헤더 높이 반환 (BookshelfHeader와 SettingScreen 헤더가 동일한 높이를 사용)
     */
    @Composable
    fun getHeaderHeight(): Int {
        val scaleFactor = getScreenScaleFactor()
        return (40 * scaleFactor).toInt() // 50dp에서 40dp로 추가 감소
    }
    
    /**
     * 헤더 아이콘 크기 반환
     */
    @Composable
    fun getHeaderIconSize(): Int {
        val scaleFactor = getScreenScaleFactor()
        return (24 * scaleFactor).toInt() // 32dp에서 24dp로 감소
    }
    
    /**
     * 헤더 로고 크기 반환
     */
    @Composable
    fun getHeaderLogoSize(): Int {
        val scaleFactor = getScreenScaleFactor()
        return (80 * scaleFactor).toInt() // 112dp에서 80dp로 대폭 감소
    }
    
    /**
     * 설정 화면용 반응형 텍스트 스타일들
     */
    
    /**
     * 설정 화면 카드 제목용 스타일
     */
    @Composable
    fun getSettingCardTitleStyle(): TextStyle {
        val scaleFactor = getSettingScaleFactor()
        val configuration = LocalConfiguration.current
        val screenWidth = configuration.screenWidthDp.toFloat()
        
        // 태블릿에서는 카드 제목을 더 크게
        val fontSize = when {
            screenWidth >= TABLET_WIDTH_DP -> 20.sp * scaleFactor // 태블릿에서 적당히 크게 (24sp -> 20sp)
            else -> 18.sp * scaleFactor
        }
        
        val lineHeight = when {
            screenWidth >= TABLET_WIDTH_DP -> 26.sp * scaleFactor // 32sp -> 26sp
            else -> 24.sp * scaleFactor
        }
        
        return AppTextStyles.gummyMediumSemibold.copy(
            fontSize = fontSize,
            lineHeight = lineHeight
        )
    }
    
    /**
     * 설정 화면 일반 텍스트용 스타일
     */
    @Composable
    fun getSettingTextStyle(): TextStyle {
        val scaleFactor = getSettingScaleFactor()
        val configuration = LocalConfiguration.current
        val screenWidth = configuration.screenWidthDp.toFloat()
        
        val fontSize = when {
            screenWidth >= TABLET_WIDTH_DP -> 16.sp * scaleFactor // 태블릿에서 적당히 크게 (18sp -> 16sp)
            else -> 14.sp * scaleFactor
        }
        
        val lineHeight = when {
            screenWidth >= TABLET_WIDTH_DP -> 22.sp * scaleFactor // 24sp -> 22sp
            else -> 20.sp * scaleFactor
        }
        
        return AppTextStyles.gummySmallSemibold.copy(
            fontSize = fontSize,
            lineHeight = lineHeight
        )
    }
    
    /**
     * 설정 화면 작은 텍스트용 스타일 (개발자 정보 등)
     */
    @Composable
    fun getSettingSmallTextStyle(): TextStyle {
        val scaleFactor = getSettingScaleFactor()
        val configuration = LocalConfiguration.current
        val screenWidth = configuration.screenWidthDp.toFloat()
        
        val fontSize = when {
            screenWidth >= TABLET_WIDTH_DP -> 14.sp * scaleFactor // 태블릿에서 적당히 크게 (16sp -> 14sp)
            else -> 12.sp * scaleFactor
        }
        
        val lineHeight = when {
            screenWidth >= TABLET_WIDTH_DP -> 18.sp * scaleFactor // 22sp -> 18sp
            else -> 16.sp * scaleFactor
        }
        
        return AppTextStyles.gummyVSmallMediumItalic.copy(
            fontSize = fontSize,
            lineHeight = lineHeight
        )
    }
    
    /**
     * 설정 화면 매우 작은 텍스트용 스타일 (라이센스 등)
     */
    @Composable
    fun getSettingVerySmallTextStyle(): TextStyle {
        val scaleFactor = getSettingScaleFactor()
        val configuration = LocalConfiguration.current
        val screenWidth = configuration.screenWidthDp.toFloat()
        
        // 작은 화면에서는 더욱 작게, 태블릿에서는 더 크게
        val fontSize = when {
            screenWidth <= 360f -> 8.sp
            screenWidth <= PHONE_WIDTH_DP -> 9.sp
            screenWidth >= TABLET_WIDTH_DP -> 12.sp * scaleFactor // 태블릿에서 적당히 크게 (14sp -> 12sp)
            else -> 10.sp * scaleFactor
        }
        
        val lineHeight = when {
            screenWidth <= 360f -> 10.sp
            screenWidth <= PHONE_WIDTH_DP -> 12.sp
            screenWidth >= TABLET_WIDTH_DP -> 16.sp * scaleFactor // 태블릿에서 적당히 크게 (18sp -> 16sp)
            else -> 14.sp * scaleFactor
        }
        
        return AppTextStyles.pretendardVSmall.copy(
            fontSize = fontSize,
            lineHeight = lineHeight
        )
    }
    
    /**
     * 라이센스 전용 텍스트 스타일 (기존 VerySmall의 2/3 크기)
     */
    @Composable
    fun getLicenseTextStyle(): TextStyle {
        val scaleFactor = getSettingScaleFactor()
        val configuration = LocalConfiguration.current
        val screenWidth = configuration.screenWidthDp.toFloat()
        
        // 기존 VerySmall 크기의 2/3로 설정
        val fontSize = when {
            screenWidth <= 360f -> (8.sp * 2f / 3f) // 5.33sp
            screenWidth <= PHONE_WIDTH_DP -> (9.sp * 2f / 3f) // 6sp
            screenWidth >= TABLET_WIDTH_DP -> (12.sp * scaleFactor * 2f / 3f) // 8sp * scaleFactor
            else -> (10.sp * scaleFactor * 2f / 3f) // 6.67sp * scaleFactor
        }
        
        val lineHeight = when {
            screenWidth <= 360f -> (10.sp * 2f / 3f) // 6.67sp
            screenWidth <= PHONE_WIDTH_DP -> (12.sp * 2f / 3f) // 8sp
            screenWidth >= TABLET_WIDTH_DP -> (16.sp * scaleFactor * 2f / 3f) // 10.67sp * scaleFactor
            else -> (14.sp * scaleFactor * 2f / 3f) // 9.33sp * scaleFactor
        }
        
        return AppTextStyles.pretendardVSmall.copy(
            fontSize = fontSize,
            lineHeight = lineHeight
        )
    }
    
    /**
     * 개발자 이름 전용 텍스트 스타일 (모바일에서 잘리지 않도록 작게)
     */
    @Composable
    fun getDeveloperNameTextStyle(): TextStyle {
        val scaleFactor = getSettingScaleFactor()
        val configuration = LocalConfiguration.current
        val screenWidth = configuration.screenWidthDp.toFloat()
        
        // 모바일에서 잘리지 않도록 기존 SmallTextStyle보다 더 작게 설정
        val fontSize = when {
            screenWidth <= 360f -> 8.sp // 작은 화면에서 더욱 작게 (9sp -> 8sp)
            screenWidth <= PHONE_WIDTH_DP -> 9.sp // 일반 휴대폰에서도 더 작게 (10sp -> 9sp)
            screenWidth >= TABLET_WIDTH_DP -> 12.sp * scaleFactor // 태블릿에서만 스케일 적용
            else -> 10.sp * scaleFactor // 중간 크기도 조금 줄임 (11sp -> 10sp)
        }
        
        val lineHeight = when {
            screenWidth <= 360f -> 11.sp // 라인 높이도 조금 줄임 (12sp -> 11sp)
            screenWidth <= PHONE_WIDTH_DP -> 12.sp // 라인 높이 줄임 (14sp -> 12sp)
            screenWidth >= TABLET_WIDTH_DP -> 16.sp * scaleFactor
            else -> 14.sp * scaleFactor // 라인 높이 줄임 (15sp -> 14sp)
        }
        
        return AppTextStyles.gummyVSmallMediumItalic.copy(
            fontSize = fontSize,
            lineHeight = lineHeight
        )
    }
    
    /**
     * 설정 화면 토글 버튼 텍스트용 스타일
     */
    @Composable
    fun getSettingToggleTextStyle(): TextStyle {
        val scaleFactor = getSettingScaleFactor()
        return AppTextStyles.gummySmallSemibold.copy(
            fontSize = (14.sp * scaleFactor),
            lineHeight = (20.sp * scaleFactor)
        )
    }
    
    /**
     * 반응형 아이콘 크기 반환
     */
    @Composable
    fun getResponsiveIconSize(): Int {
        val scaleFactor = getSettingScaleFactor()
        return (20 * scaleFactor).toInt()
    }
    
    /**
     * 반응형 큰 아이콘 크기 반환 (헤더 등)
     */
    @Composable
    fun getResponsiveLargeIconSize(): Int {
        val scaleFactor = getSettingScaleFactor()
        return (28 * scaleFactor).toInt()
    }
}
