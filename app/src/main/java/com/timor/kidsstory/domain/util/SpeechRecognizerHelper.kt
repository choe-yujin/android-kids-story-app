package com.timor.kidsstory.domain.util

import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer
import com.timor.kidsstory.presentation.chatbot.SpeechStateCallback
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 음성 인식 기능을 관리하는 유틸리티 클래스
 * - 안드로이드 음성 인식 API를 활용한 음성 인식 관리
 * - 챗봇 기능에서 음성 명령을 텍스트로 변환하는데 사용
 * - 싱글톤으로 앱 전체에서 하나의 인스턴스 공유
 *
 * @property speechRecognizer 안드로이드 음성 인식 객체
 * @property recognizerIntent 음성 인식 설정을 담은 인텐트
 */
@Singleton
class SpeechRecognizerHelper @Inject constructor(
    private val speechRecognizer: SpeechRecognizer?,
    private val recognizerIntent: Intent
) {

    /**
     * 음성 인식 시작
     * - RecognitionListener 설정 및 음성 인식 시작
     * - 인식 결과 및 상태 변화를 콜백으로 전달
     *
     * @param callback 음성 인식 상태 및 결과 콜백
     */
    fun startListening(callback: SpeechStateCallback) {
        speechRecognizer?.setRecognitionListener(object : RecognitionListener {

            /**
             * 음성 인식 결과 처리
             * - 인식된 텍스트를 콜백으로 전달
             */
            override fun onResults(results: Bundle?) {
                val speechText = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull()
                speechText?.let { text ->
                    callback.onSpeechResult(text)
                }
            }

            /**
             * 음성 인식 준비 완료
             * - 음성 인식 시작 상태를 콜백으로 알림
             */
            override fun onReadyForSpeech(params: Bundle?) {
                callback.onListeningStarted()
            }

            override fun onBeginningOfSpeech() {}

            override fun onRmsChanged(p0: Float) {}

            override fun onBufferReceived(p0: ByteArray?) {}

            /**
             * 음성 입력 종료
             * - 음성 인식 종료 상태를 콜백으로 알림
             */
            override fun onEndOfSpeech() {
                callback.onListeningEnded()
            }

            /**
             * 오류 발생 처리
             * - 오류 코드에 따른 처리 (현재는 단순히 종료 신호만 전달)
             */
            override fun onError(p0: Int) {
                // TODO: 추후 에러코드에 따라 메시징 처리나 혹은 다른 처리가 필요해보임 임시로 듣기가 끝났다는 신호를 .
                callback.onListeningEnded()
            }

            override fun onPartialResults(p0: Bundle?) {}
            override fun onEvent(p0: Int, p1: Bundle?) {}

        })
        // 음성 인식 시작
        speechRecognizer?.startListening(recognizerIntent)
    }

    /**
     * 음성 인식 일시 중지
     * - 현재 진행 중인 음성 인식을 중지하고 결과 대기
     */
    fun stopListening() {
        speechRecognizer?.stopListening()
    }

    /**
     * 음성 인식 취소
     * - 현재 진행 중인 음성 인식을 취소하고 모든 처리 중단
     */
    fun cancelListening() {
        speechRecognizer?.cancel()
    }

    /**
     * 음성 인식 자원 해제
     * - SpeechRecognizer 자원 해제 및 메모리 정리
     */
    fun destroyListening() {
        speechRecognizer?.destroy()
    }
}