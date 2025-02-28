package com.timor.kidsstory.presentation.chatbot

data class ChatbotUiState(
    val messages: List<ChatMessage> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentInput: String = ""
)

data class ChatMessage(
    val text: String,
    val isFromUser: Boolean,
)