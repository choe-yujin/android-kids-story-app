package com.timor.kidsstory.presentation.chatbot

/**
 * 음성 인식 상태 및 결과 콜백 인터페이스
 * - 음성 인식 과정의 다양한 상태 변화와 결과를 UI에 전달
 */
interface SpeechStateCallback {
    /**
     * 음성 인식 시작 시 호출
     * - 사용자에게 음성 입력을 시작할 수 있음을 알림
     */
    fun onListeningStarted()

    /**
     * 음성 인식 종료 시 호출
     * - 음성 입력이 끝났거나 인식 과정이 종료됨을 알림
     */
    fun onListeningEnded()

    /**
     * 음성 인식 결과 전달
     * - 인식된 텍스트를 UI에 전달
     *
     * @param result 인식된 텍스트
     */
    fun onSpeechResult(result: String)
}