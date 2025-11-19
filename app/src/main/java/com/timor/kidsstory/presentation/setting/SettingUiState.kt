package com.timor.kidsstory.presentation.setting

import com.timor.kidsstory.domain.model.Language
import com.timor.kidsstory.domain.util.LanguageConstants

/**
 * 설정 화면 UI 상태 클래스
 * - 설정 화면의 모든 상태 정보를 담는 불변 데이터 클래스
 *
 * @property isMusicOn 배경 음악 켜기/끄기 상태
 * @property isSoundEffectOn 효과음 켜기/끄기 상태
 * @property musicVolume 배경음악 음량 (0.0 ~ 1.0)
 * @property soundEffectVolume 효과음 음량 (0.0 ~ 1.0)
 * @property showEmailDialog 이메일 다이얼로그 표시 여부
 * @property currentLanguage 현재 선택된 언어
 * @property showResetConfirmation 초기화 확인 다이얼로그 표시 여부
 */
data class SettingUiState(
    val isMusicOn: Boolean = true,  // 기본값: 음악 켜짐
    val isSoundEffectOn: Boolean = true,  // 기본값: 효과음 켜짐
    val musicVolume: Float = 0.1f,  // 기본값: 10% 음량
    val soundEffectVolume: Float = 0.7f,  // 기본값: 70% 음량
    val showEmailDialog: Boolean = false,  // 기본값: 이메일 다이얼로그 숨김
    val currentLanguage: Language = LanguageConstants.DEFAULT_LANGUAGE,  // 기본값: 영어
    val showResetConfirmation: Boolean = false  // 기본값: 초기화 확인 다이얼로그 숨김
)
