package com.timor.kidsstory.domain.model

/**
 * 사용자 설정을 위한 도메인 모델
 * 모든 사용자 설정을 담고 있는 데이터 클래스
 */
data class UserPreference(
    val languageCode: String = "en-ph", // 기본 언어 코드
    val isMusicOn: Boolean = false      // 배경음 toggle 상태
)