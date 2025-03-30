package com.timor.kidsstory.presentation.chatbot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.GenerateContentResponse
import com.google.ai.client.generativeai.type.content
import com.timor.kidsstory.domain.util.SpeechRecognizerHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * 챗봇 화면의 상태 관리 및 비즈니스 로직 처리 뷰모델
 * - 사용자와 AI 챗봇 간의 대화 관리
 * - 음성 인식 기능 제어
 * - Google Gemini AI 모델과 통신
 *
 * @property chatModel 생성형 AI 모델 (Google Gemini)
 * @property speechRecognizerHelper 음성 인식 도우미
 */
@HiltViewModel
class ChatbotScreenViewModel @Inject constructor(
    private val chatModel: GenerativeModel,
    private val speechRecognizerHelper: SpeechRecognizerHelper?
) : ViewModel() {
    // UI 상태 관리
    private val _state = MutableStateFlow(ChatbotUiState())
    val state = _state.asStateFlow()

    /**
     * 뷰모델 종료 시 리소스 해제
     */
    override fun onCleared() {
        super.onCleared()
        _state.update { it.copy(isRecording = false) }
        speechRecognizerHelper?.destroyListening()
    }

    /**
     * 사용자 입력 변경 처리
     * - 입력 필드의 텍스트가 변경될 때마다 호출
     *
     * @param newInput 새 입력 텍스트
     */
    private fun onInputChange(newInput: String) {
        _state.update { it.copy(currentInput = newInput) }
    }

    /**
     * 메시지 전송 처리
     * - 사용자 메시지를 목록에 추가하고 AI 응답 요청
     * - 빈 메시지나 중복 메시지는 처리하지 않음
     *
     * @param inputText 전송할 메시지 텍스트
     */
    private fun sendMessage(inputText: String) {
        // 내용이 비어있으면 처리하지 않음
        if (inputText.isBlank()) return

        // 중복 메시지 전송 방지 (마지막 메시지와 동일한지 확인)
        if (state.value.messages.isNotEmpty() && state.value.messages.last().text == inputText) {
            return
        }

        // 사용자 메시지 생성 및 추가
        val newMessage = ChatMessage(
            text = inputText, isFromUser = true,
        )

        // 메시지 목록 업데이트 및 로딩 상태로 전환
        _state.update { currentState ->
            val updatedMessages = currentState.messages.toMutableList()
            updatedMessages.add(newMessage)
            currentState.copy(messages = updatedMessages, currentInput = "", isLoading = true, error = null)
        }

        // AI 응답 요청
        viewModelScope.launch {
            try {
                // Gemini의 응답을 받음
                val response = receiveGeminiResponse(inputText)

                // AI 응답 메시지 생성 및 추가
                val botMessage = ChatMessage(
                    text = response.trim(), isFromUser = false
                )

                // 메시지 목록 업데이트 및 로딩 완료
                _state.update { currentState ->
                    val updatedMessages = currentState.messages.toMutableList()
                    updatedMessages.add(botMessage)
                    currentState.copy(messages = updatedMessages, isLoading = false, error = null)
                }
            } catch (e: Exception) {
                // 오류 처리
                _state.update {
                    it.copy(
                        isLoading = false, error = e.message
                    )
                }
            }


        }
    }

    /**
     * Gemini AI 모델의 응답 요청
     * - 사용자 메시지를 AI 모델에 전송하고 응답 수신
     *
     * @param userMessage 사용자 메시지
     * @return AI 모델의 응답 텍스트
     */
    private suspend fun receiveGeminiResponse(userMessage: String): String = withContext(Dispatchers.IO) {
        val prompt = content { text(userMessage) }

        val response: GenerateContentResponse = chatModel.generateContent(prompt)
        response.text ?: "NO Response"
    }


    // ----- 음성 인식 관련 기능 -----

    /**
     * 음성 인식 다이얼로그 표시/숨김 처리
     *
     * @param isShow 다이얼로그 표시 여부
     */
    private fun showVoiceDialog(isShow: Boolean) {
        _state.update {
            it.copy(
                isShowDialog = isShow
            )
        }
    }

    /**
     * 음성 인식 시작
     * - 음성 인식 서비스 초기화 및 리스너 설정
     */
    private fun startVoiceSearch() {
        // 음성 인식 서비스가 없는 경우 오류 처리
        if (speechRecognizerHelper == null) {
            _state.update { it.copy(isRecording = false, error = "음성 인식 초기화 불가") }
            return
        }
        _state.update { it.copy(isRecording = true) }

        // 음성 인식 시작 및 콜백 설정
        speechRecognizerHelper.startListening(object : SpeechStateCallback {
            // 음성 인식 시작시 호출
            override fun onListeningStarted() {
                _state.update { it.copy(isRecording = true) }
            }

            // 음성 인식 종료 시 호출
            override fun onListeningEnded() {
                _state.update { it.copy(isRecording = false, isShowDialog = false) }
                cancelVoiceSearch()
            }

            // 음성 인식 결과 수신 시 호출
            override fun onSpeechResult(result: String) {
                _state.update { it.copy(voiceInput = result, isShowDialog = false) }
                sendMessage(result)  // 인식된 텍스트로 메시지 전송
            }

        })
    }

    /**
     * 음성 인식 정지
     * - 진행 중인 음성 인식을 일시 중지
     */
    private fun stopVoiceSearch() {
        _state.update { it.copy(isRecording = false) }
        speechRecognizerHelper?.stopListening()
    }

    /**
     * 음성 인식 취소
     * - 진행 중인 음성 인식을 완전히 중단
     */
    private fun cancelVoiceSearch() {
        _state.update { it.copy(isRecording = false) }
        speechRecognizerHelper?.cancelListening()
    }

    /**
     * UI 액션 처리
     * - 사용자 인터랙션에 따른 상태 변경 및 비즈니스 로직 실행
     *
     * @param action 처리할 액션
     */
    fun onAction(action: ChatbotAction) {
        when (action) {
            is ChatbotAction.ShowDialog -> {
                if (!action.isShow) cancelVoiceSearch()
                showVoiceDialog(action.isShow)
            }

            is ChatbotAction.SendMessage -> {
                sendMessage(action.message)
            }

            is ChatbotAction.VoiceSearch -> {
                startVoiceSearch()
            }

            is ChatbotAction.InputChange -> {
                onInputChange(action.message)
            }

            is ChatbotAction.BackScreen -> {}  // 상위 컴포넌트에서 처리
        }
    }

}