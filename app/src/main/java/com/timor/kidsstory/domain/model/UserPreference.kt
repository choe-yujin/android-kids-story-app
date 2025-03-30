package com.timor.kidsstory.domain.model

/**
 * 사용자 설정을 위한 도메인 모델
 * - 앱의 모든 사용자 설정 정보를 담는 데이터 클래스
 * - 언어 설정, 배경음악 설정 등의 사용자 기본 설정 관리
 *
 * @property languageCode 사용자가 선택한 언어 코드 (기본값: en-ph)
 * @property isMusicOn 배경음악 켜기/끄기 상태 (기본값: false)
 */
data class UserPreference(
    val languageCode: String = "en-ph",
    val isMusicOn: Boolean = false
)