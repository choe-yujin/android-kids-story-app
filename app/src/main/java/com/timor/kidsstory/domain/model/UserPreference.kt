package com.timor.kidsstory.domain.model

/**
 * 사용자 설정을 위한 도메인 모델
 * - 앱의 모든 사용자 설정 정보를 담는 데이터 클래스
 * - 언어 설정, 배경음악 설정, 음량 설정 등의 사용자 기본 설정 관리
 *
 * @property languageCode 사용자가 선택한 언어 코드 (기본값: en-ph)
 * @property isMusicOn 배경음악 켜기/끄기 상태 (기본값: true)
 * @property isSoundEffectOn 효과음 켜기/끄기 상태 (기본값: true)
 * @property musicVolume 배경음악 음량 (0.0 ~ 1.0, 기본값: 0.7)
 * @property soundEffectVolume 효과음 음량 (0.0 ~ 1.0, 기본값: 0.7)
 */
data class UserPreference(
    val languageCode: String = "en-ph",
    val isMusicOn: Boolean = true,  // 배경음악 기본적으로 켜짐
    val isSoundEffectOn: Boolean = true,
    val musicVolume: Float = 0.7f,  // 더 크게 들리도록 기본 음량 상향
    val soundEffectVolume: Float = 0.7f  // 더 크게 들리도록 기본 음량 상향
)