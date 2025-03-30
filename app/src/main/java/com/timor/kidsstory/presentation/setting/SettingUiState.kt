package com.timor.kidsstory.presentation.setting

/**
 * 설정 화면 UI 상태 클래스
 * - 설정 화면의 모든 상태 정보를 담는 불변 데이터 클래스
 *
 * @property isMusicOn 배경 음악 켜기/끄기 상태
 */
data class SettingUiState(
    val isMusicOn: Boolean = false  // 기본값: 음악 꺼짐
)