package com.timor.kidsstory.domain.util

import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer
import com.timor.kidsstory.presentation.chatbot.SpeechStateCallback
import com.timor.kidsstory.presentation.chatbot.VoiceRecognitionState
import javax.inject.Inject
import javax.inject.Singleton


// 음성 인식 객체
@Singleton
class SpeechRecognizerHelper @Inject constructor(
    private val speechRecognizer: SpeechRecognizer,
    private val recognizerIntent: Intent
) {

    // 음성 인식 시작
    fun startListening(callback: SpeechStateCallback) {
        // Listening 상태로 업데이트

        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onResults(results: Bundle?) {
                val speechText = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull()
                speechText?.let { text ->
                    callback.onSpeechResult(text)
                }
            }

            override fun onReadyForSpeech(params: Bundle?) {
                callback.onListeningStarted()
            }

            override fun onBeginningOfSpeech() {}

            override fun onRmsChanged(p0: Float) {}

            override fun onBufferReceived(p0: ByteArray?) {}

            override fun onEndOfSpeech() {
                callback.onListeningEnded()
            }

            override fun onError(p0: Int) {
                // TODO: 추후 에러코드에 따라 메시징 처리나 혹은 다른 처리가 필요해보임 임시로 듣기가 끝났다는 신호를 .
                callback.onListeningEnded()
            }


            override fun onPartialResults(p0: Bundle?) {}

            override fun onEvent(p0: Int, p1: Bundle?) {}

        })
        speechRecognizer.startListening(recognizerIntent)
    }

    // 음성 인식 멈춤
    fun stopListening() {
        speechRecognizer.stopListening()
    }

    // 음성 인식 취소
    fun cancelListening() {
        speechRecognizer.cancel()
    }
}