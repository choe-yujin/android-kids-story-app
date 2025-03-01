package com.timor.kidsstory.presentation.chatbot


// 음성 인식 상태 판별용
sealed class VoiceRecognitionState {
    data object Idle : VoiceRecognitionState()
    data class Listening(val status: String) : VoiceRecognitionState()
    data class Done(val recognizedText: String) : VoiceRecognitionState()
}