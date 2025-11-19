package com.timor.kidsstory.domain.util

import com.timor.kidsstory.R
import com.timor.kidsstory.domain.model.Language

/**
 * 앱에서 지원하는 언어 정의
 * - 지원 언어 목록 및 언어별 상수 정의
 * - 기본 언어 설정
 */
object LanguageConstants {
    // 영어
    val ENGLISH = Language(
        code = "en",
        displayName = "English",
        flagResId = R.drawable.flag_en
    )

    // 한국어
    val KOREAN = Language(
        code = "ko",
        displayName = "한국어",
        flagResId = R.drawable.flag_ko
    )

    // 테툼어
    val TETUM = Language(
        code = "tet",
        displayName = "Tetum",
        flagResId = R.drawable.flag_tet
    )

    // 몽골어
    /*
    val MONGOLIAN = Language(
        code = "mn-MN",
        displayName = "Мон골",
        flagResId = R.drawable.flag_mn // Assuming this drawable exists
    )
    */

    /**
     * 앱에서 지원하는 모든 언어 목록
     * - 언어 선택 UI에서 활용
     */
    val SUPPORTED_LANGUAGES = listOf(ENGLISH, KOREAN, TETUM/*, MONGOLIAN*/)

    /**
     * 기본 언어 (첫 실행시 적용)
     */
    val DEFAULT_LANGUAGE = ENGLISH

    /* 언어 코드를 다국어 리소스용 코드로 변환
    * 이미 단순화된 코드를 사용하므로 그대로 반환
    */
    fun getResourceLangCode(code: String): String {
        return when (code) {
            "en" -> "en"
            "ko" -> "ko"
            "tet" -> "tet"
            else -> "en" // 기본값
        }
    }
}