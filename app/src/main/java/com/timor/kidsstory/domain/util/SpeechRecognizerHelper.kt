package com.timor.kidsstory.domain.util

import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer
import javax.inject.Inject
import javax.inject.Singleton


// 음성 인식 객체
@Singleton
class SpeechRecognizerHelper @Inject constructor(
    private val speechRecognizer: SpeechRecognizer,
    private val recognizerIntent: Intent
) {

    // 음성 인식 시작
    fun startListening(onResult: (String) -> Unit) {
        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onResults(results: Bundle?) {
                val speechText = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull()
                speechText?.let { text ->
                    onResult.invoke(text)
                }
            }

            override fun onReadyForSpeech(p0: Bundle?) {}

            override fun onBeginningOfSpeech() {}

            override fun onRmsChanged(p0: Float) {}

            override fun onBufferReceived(p0: ByteArray?) {}

            override fun onEndOfSpeech() {}

            override fun onError(p0: Int) {}


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