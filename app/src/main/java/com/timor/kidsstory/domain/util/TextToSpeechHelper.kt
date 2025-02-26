package com.timor.kidsstory.domain.util

import android.content.Context
import android.speech.tts.TextToSpeech
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/*
* TTS 기능 클래스
* */
@Singleton
class TextToSpeechHelper @Inject constructor(
    @ApplicationContext private val context: Context,
) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    // 초기화 상태를 viewModel에서 판단하기위함
    private val _isTTSInitialized = MutableStateFlow(false)
    val isTTSInitialized = _isTTSInitialized.asStateFlow()

    init {
        tts = TextToSpeech(context, this)
    }

    // tts 초기화 되었을때 호출
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {

            // 영어 설정
            tts?.language = Locale.ENGLISH

            _isTTSInitialized.value = true
        }
    }

    // 말하기 기능
    fun speak(text: String) {
        tts?.speak(text, TextToSpeech.QUEUE_ADD, null, null)
    }

    // 말하기 멈추기
    fun stop() {
        tts?.stop()
    }

    // 리소스 해제를 위한 메소드
    fun shutDown() {
        if (tts != null) {
            tts?.stop()
            tts?.shutdown()
            tts = null
        }

    }
}