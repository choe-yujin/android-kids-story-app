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
    data class SwitchClick(val isMusicOn: Boolean): SettingAction

    /**
     * 뒤로가기 버튼 클릭 액션
     * - 설정 화면에서 이전 화면으로 돌아가기
     */
    data object BackButtonClick : SettingAction
}