package com.timor.kidsstory.presentation.chatbot

data class ChatbotUiState(
    val messages: List<ChatMessage> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentInput: String = "",
    val voiceInput: String = "",
    val isRecording: Boolean = false,   // 음성 인식 진행 여부
)

// text: 메세지, isFromUser: 사용자인지 봇인지
data class ChatMessage(
    val text: String,
    val isFromUser: Boolean,
)