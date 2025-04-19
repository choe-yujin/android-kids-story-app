package com.timor.kidsstory.domain.util

import android.content.Context
import android.speech.tts.TextToSpeech
import com.orhanobut.logger.Logger
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 텍스트 음성 변환(TTS) 기능 클래스
 * - 텍스트를 음성으로 변환하여 읽어주는 기능 제공
 * - 싱글톤으로 앱 전체에서 하나의 인스턴스 공유
 *
 * @property context 애플리케이션 컨텍스트
 */
@Singleton
class TextToSpeechHelper @Inject constructor(
    @ApplicationContext private val context: Context,
) : TextToSpeech.OnInitListener {

    // TTS 엔진 인스턴스
    private var tts: TextToSpeech? = null

    // TTS 초기화 상태를 관찰하기 위한 Flow
    private val _isTTSInitialized = MutableStateFlow(false)
    val isTTSInitialized = _isTTSInitialized.asStateFlow()

    /**
     * TTS 초기화 완료 콜백
     * - 초기화 성공 시 언어 설정 및 상태 업데이트
     *
     * @param status TTS 초기화 상태 코드
     */
    override fun onInit(status: Int) {

        if (status == TextToSpeech.SUCCESS) {

            // 초기화 완료 상태로 업데이트
            _isTTSInitialized.value = true
        }
    }

    /*
    * tts 초기화
    * */
    // Factory 메서드로 초기화 및 언어 설정을 함께 처리
    fun initializeWithLanguage(locale: Locale): Flow<Boolean> {
        val resultFlow = MutableSharedFlow<Boolean>(replay = 1)         // 단일 이벤트 콜백

        // 기존 TTS 인스턴스가 있으면 종료
        shutDown()

        // TTS 엔진 초기화와 언어 설정을 결합
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val langResult = tts?.isLanguageAvailable(locale) ?: TextToSpeech.LANG_NOT_SUPPORTED        // 언어 지원 여부 판단

                if (langResult >= TextToSpeech.LANG_AVAILABLE) {
                    tts?.language = locale
                    Logger.e("TTS 초기화 및 언어 설정 완료: $locale")
                    _isTTSInitialized.value = true
                    resultFlow.tryEmit(true)
                } else {
                    Logger.e("TTS 언어 미지원: $locale, 기본 영어로 설정")
                    tts?.language = Locale.ENGLISH
                    _isTTSInitialized.value = true
                    resultFlow.tryEmit(true)
                }
            } else {
                Logger.e("TTS 초기화 실패: $status")
                _isTTSInitialized.value = false
                resultFlow.tryEmit(false)
            }
        }

        return resultFlow
    }

    /*
    * 재초기화 로직
    * */
    fun reInitialize() {
        if (tts == null) {
            tts = TextToSpeech(context, this)
            _isTTSInitialized.value = true
        }
    }

    /**
     * 텍스트를 음성으로 읽기
     * - 큐에 추가하여 현재 읽고 있는 텍스트가 끝나면 이어서 읽음
     *
     * @param text 읽을 텍스트
     */
    fun speak(text: String) {
        tts?.speak(text, TextToSpeech.QUEUE_ADD, null, null)
    }

    /**
     * 음성 읽기 중지
     */
    fun stop() {
        tts?.stop()
    }

    /**
     * 리소스 해제
     * - 앱 종료 또는 뷰모델 소멸 시 호출
     */
    fun shutDown() {
        if (tts != null) {
            tts?.stop()
            tts?.shutdown()
            tts = null
        }

    }
}