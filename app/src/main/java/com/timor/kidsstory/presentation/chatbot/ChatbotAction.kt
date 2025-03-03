package com.timor.kidsstory.presentation.chatbot

sealed interface ChatbotAction {
    data class ShowDialog(val isShow: Boolean) : ChatbotAction
    data class SendMessage(val message: String): ChatbotAction
    data class InputChange(val message: String): ChatbotAction
    data object VoiceSearch: ChatbotAction
    data object BackScreen: ChatbotAction
}