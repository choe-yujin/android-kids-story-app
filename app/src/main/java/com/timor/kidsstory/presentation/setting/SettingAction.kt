package com.timor.kidsstory.presentation.setting

/**
 * 설정 화면에서 발생하는 사용자 액션 정의
 * - 사용자 인터랙션에 따른 액션을 뷰모델에 전달하기 위한 sealed 인터페이스
 */
sealed interface SettingAction {
    /**
     * 음악 스위치 클릭 액션
     * - 배경 음악 켜기/끄기 토글 처리
     *
     * @property isMusicOn 변경할 음악 상태 (true: 켜기, false: 끄기)
     */
    data class MusicSwitchClick(val isMusicOn: Boolean): SettingAction

    /**
     * 효과음 스위치 클릭 액션
     * - 효과음 켜기/끄기 토글 처리
     *
     * @property isSoundEffectOn 변경할 효과음 상태 (true: 켜기, false: 끄기)
     */
    data class SoundEffectSwitchClick(val isSoundEffectOn: Boolean): SettingAction

    /**
     * 이메일 아이콘 클릭 액션
     * - 이메일 다이얼로그 표시
     */
    data object EmailIconClick : SettingAction

    /**
     * 웹사이트 링크 클릭 액션
     * - 브라우저에서 웹사이트 열기
     */
    data object WebsiteLinkClick : SettingAction

    /**
     * 이메일 다이얼로그 닫기 액션
     * - 이메일 다이얼로그 숨김
     */
    data object DismissEmailDialog : SettingAction

    /**
     * 배경음악 음량 조절 액션
     * - 배경음악 음량 설정 변경
     *
     * @property volume 변경할 음량 (0.0 ~ 1.0)
     */
    data class MusicVolumeChange(val volume: Float): SettingAction

    /**
     * 효과음 음량 조절 액션
     * - 효과음 음량 설정 변경
     *
     * @property volume 변경할 음량 (0.0 ~ 1.0)
     */
    data class SoundEffectVolumeChange(val volume: Float): SettingAction

    /**
     * 뒤로가기 버튼 클릭 액션
     * - 설정 화면에서 이전 화면으로 돌아가기
     */
    data object BackButtonClick : SettingAction
    
    /**
     * 🔧 디버깅용: 앱 데이터 초기화 액션 (문제 해결 후 제거 예정)
     */
    data object ResetAppData : SettingAction
    
    /**
     * 초기화 확인 다이얼로그 닫기 액션
     */
    data object DismissResetConfirmation : SettingAction
}
