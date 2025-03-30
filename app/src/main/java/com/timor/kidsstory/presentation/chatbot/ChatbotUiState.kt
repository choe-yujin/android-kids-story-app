package com.timor.kidsstory.presentation.chatbot

/**
 * 챗봇 화면 UI 상태 클래스
 * - 챗봇 화면의 모든 상태 정보를 담는 불변 데이터 클래스
 *
 * @property messages 대화 메시지 목록
 * @property isLoading 로딩 중 상태
 * @property error 오류 메시지 (있을 경우)
 * @property currentInput 현재 입력 필드 텍스트
 * @property voiceInput 음성 입력으로 인식된 텍스트
 * @property isRecording 음성 인식 진행 여부
 * @property isShowDialog 음성 인식 다이얼로그 표시 여부
 */
data class ChatbotUiState(
    val messages: List<ChatMessage> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentInput: String = "",
    val voiceInput: String = "",
    val isRecording: Boolean = false,   // 음성 인식 진행 여부
    val isShowDialog: Boolean = false,
)

/**
 * 채팅 메시지 데이터 클래스
 * - 챗봇과 사용자 간의 대화 메시지 표현
 *
 * @property text 메시지 내용
 * @property isFromUser 사용자 메시지 여부 (true: 사용자, false: 챗봇)
 */
data class ChatMessage(
    val text: String,
    val isFromUser: Boolean,
)