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
        code = "en-ph",
        displayName = "English",
        flagResId = R.drawable.flag_en
    )

    // 한국어
    val KOREAN = Language(
        code = "ko-kr",
        displayName = "한국어",
        flagResId = R.drawable.flag_ko
    )

    // 테툼어
    val TETUM = Language(
        code = "tet",
        displayName = "Tetum",
        flagResId = R.drawable.flag_tet
    )

    /**
     * 앱에서 지원하는 모든 언어 목록
     * - 언어 선택 UI에서 활용
     */
    val SUPPORTED_LANGUAGES = listOf(ENGLISH, KOREAN, TETUM)

    /**
     * 기본 언어 (첫 실행시 적용)
     */
    val DEFAULT_LANGUAGE = ENGLISH
}