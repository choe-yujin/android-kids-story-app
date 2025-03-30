package com.timor.kidsstory.presentation.chatbot

/**
 * 챗봇 화면에서 발생하는 사용자 액션 정의
 * - 사용자 인터랙션에 따른 액션을 뷰모델에 전달하기 위한 봉인된 인터페이스
 */
sealed interface ChatbotAction {
    /**
     * 음성 인식 다이얼로그 표시/숨김 액션
     *
     * @property isShow 다이얼로그 표시 여부
     */
    data class ShowDialog(val isShow: Boolean) : ChatbotAction

    /**
     * 메시지 전송 액션
     * - 사용자가 작성한 메시지를 챗봇에 전송
     *
     * @property message 전송할 메시지 내용
     */
    data class SendMessage(val message: String): ChatbotAction

    /**
     * 입력 텍스트 변경 액션
     * - 사용자가 입력 필드에 텍스트를 입력할 때 발생
     *
     * @property message 변경된 입력 텍스트
     */
    data class InputChange(val message: String): ChatbotAction

    /**
     * 음성 검색 시작 액션
     * - 사용자가 음성 입력 버튼을 클릭할 때 발생
     */
    data object VoiceSearch: ChatbotAction

    /**
     * 뒤로가기 액션
     * - 챗봇 화면에서 이전 화면으로 돌아가기
     */
    data object BackScreen: ChatbotAction
}