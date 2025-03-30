package com.timor.kidsstory.domain.model

/**
 * 앱에서 지원하는 언어에 대한 도메인 모델
 * - 다국어 지원을 위한 언어 속성 정의
 *
 * @property code 언어 코드 (en-ph, ko-kr, tet)
 * @property displayName 언어 표시 이름 (English, 한국어, Tetum)
 * @property flagResId 국기 리소스 ID (언어 선택 UI에 표시)
 */
data class Language(
    val code: String,
    val displayName: String,
    val flagResId: Int
)