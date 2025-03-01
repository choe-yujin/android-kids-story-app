package com.timor.kidsstory.presentation.chatbot

interface SpeechStateCallback {
    // 음성 인식이 시작될 때 호출
    fun onListeningStarted()

    // 음성 인식이 끝났을때 호출
    fun onListeningEnded()

    // 음성 인식이 결과가 나왔을때 호출
    fun onSpeechResult(result: String)
}