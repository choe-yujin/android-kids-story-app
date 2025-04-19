package com.timor.kidsstory.domain.util

import android.content.Context
import android.speech.tts.TextToSpeech
import com.orhanobut.logger.Logger
import dagger.hilt.android.qualifiers.ApplicationContext
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
     * 초기화 - TTS 엔진 생성
     */
    init {
        Logger.e("TTS 초기화 타니?")
        tts = TextToSpeech(context, this)
    }

    /**
     * TTS 초기화 완료 콜백
     * - 초기화 성공 시 언어 설정 및 상태 업데이트
     *
     * @param status TTS 초기화 상태 코드
     */
    override fun onInit(status: Int) {
        Logger.e("TTS OnInit 초기화")
        if (status == TextToSpeech.SUCCESS) {
            // 영어 설정 - 기본 언어로 사용
            tts?.language = Locale.ENGLISH

            // 초기화 완료 상태로 업데이트
            _isTTSInitialized.value = true
        }
    }


    /*
    * 재초기화 로직
    * */
    fun reInitialize() {
        if(tts == null) {
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