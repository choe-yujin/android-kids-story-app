package com.timor.kidsstory.presentation.chatbot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.GenerateContentResponse
import com.google.ai.client.generativeai.type.content
import com.timor.kidsstory.BuildConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ChatbotScreenViewModel @Inject constructor() : ViewModel() {
    private val _state = MutableStateFlow(ChatbotUiState())
    val state = _state.asStateFlow()

    private val chatModel: GenerativeModel = GenerativeModel(modelName = "gemini-1.5-pro", apiKey = BuildConfig.GEMINI_API_KEY)


    // 사용자가 입력할때 마다 호출
    fun onInputChange(newInput: String) {
        _state.update { it.copy(currentInput = newInput) }
    }


    // 메시지 전송
    fun sendMessage() {
        val userMessage = state.value.currentInput
        if (userMessage.isBlank()) return        // 내용이 비어있으면 return

        val newMessage = ChatMessage(
            text = userMessage, isFromUser = true,
        )

        _state.update {
            it.copy(
                messages = it.messages + newMessage, currentInput = "", isLoading = true, error = null,
            )
        }

        viewModelScope.launch {
            try {
                // Gemini의 응답을 받음
                val response = receiveGeminiResponse(userMessage)

                val botMessage = ChatMessage(
                    text = response, isFromUser = false
                )

                _state.update {
                    it.copy(
                        messages = it.messages + botMessage, isLoading = false, error = null,
                    )
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
}