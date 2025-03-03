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

@HiltViewModel
class ChatbotScreenViewModel @Inject constructor(
    private val chatModel: GenerativeModel,
    private val speechRecognizerHelper: SpeechRecognizerHelper?
) : ViewModel() {

    private val _state = MutableStateFlow(ChatbotUiState())
    val state = _state.asStateFlow()

    override fun onCleared() {
        super.onCleared()
        _state.update { it.copy(isRecording = false) }
        speechRecognizerHelper?.destroyListening()
    }

    // 사용자가 입력할때 마다 호출
    private fun onInputChange(newInput: String) {
        _state.update { it.copy(currentInput = newInput) }
    }

    // 메시지 전송
    private fun sendMessage(inputText: String) {

        if (inputText.isBlank()) return        // 내용이 비어있으면 return

        // 중복 메시지 전송 방지 (마지막 메시지와 동일한지 확인)
        if (state.value.messages.isNotEmpty() && state.value.messages.last().text == inputText) {
            return
        }

        val newMessage = ChatMessage(
            text = inputText, isFromUser = true,
        )

        _state.update { currentState ->
            val updatedMessages = currentState.messages.toMutableList()
            updatedMessages.add(newMessage)
            currentState.copy(messages = updatedMessages, currentInput = "", isLoading = true, error = null)
        }

        viewModelScope.launch {
            try {
                // Gemini의 응답을 받음
                val response = receiveGeminiResponse(inputText)

                val botMessage = ChatMessage(
                    text = response, isFromUser = false
                )

                _state.update { currentState ->
                    val updatedMessages = currentState.messages.toMutableList()
                    updatedMessages.add(botMessage)
                    currentState.copy(messages = updatedMessages, isLoading = false, error = null)
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false, error = e.message
                    )
                }
            }


        }
    }

    // gemini의 응답을 받음
    private suspend fun receiveGeminiResponse(userMessage: String): String = withContext(Dispatchers.IO) {
        val prompt = content { text(userMessage) }

        val response: GenerateContentResponse = chatModel.generateContent(prompt)
        response.text ?: "NO Response"
    }


    /*
    * ------------ 음성 인식 관련 ---------------
    * */
    private fun showVoiceDialog(isShow: Boolean) {
        _state.update {
            it.copy(
                isShowDialog = isShow
            )
        }
    }


    private fun startVoiceSearch() {
        if (speechRecognizerHelper == null) {
            _state.update { it.copy(isRecording = false, error = "음성 인식 초기화 불가") }
            return
        }
        _state.update { it.copy(isRecording = true) }

        speechRecognizerHelper.startListening(object : SpeechStateCallback {
            override fun onListeningStarted() {
                // 음성 인식 시작시 처리 (UI 업데이트 처리 필요)
                _state.update { it.copy(isRecording = true) }
            }

            override fun onListeningEnded() {
                // 음성 인식 종료 시 처리
                _state.update { it.copy(isRecording = false) }
            }

            override fun onSpeechResult(result: String) {
                // 음성 인식 결과 받기
                _state.update { it.copy(voiceInput = result) }
                sendMessage(result)
            }

        })
    }


    fun stopVoiceSearch() {
        _state.update { it.copy(isRecording = false) }
        speechRecognizerHelper?.stopListening()
    }


    fun cancelVoiceSearch() {
        _state.update { it.copy(isRecording = false) }
        speechRecognizerHelper?.cancelListening()
    }


    // Action에 따른 동작 정의
    fun onAction(action: ChatbotAction) {
        when (action) {
            is ChatbotAction.ShowDialog -> {
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

            is ChatbotAction.BackScreen -> {}
        }
    }

}