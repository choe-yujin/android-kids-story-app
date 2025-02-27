package com.timor.kidsstory.presentation.reader

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.timor.kidsstory.domain.model.NavigationDirection
import com.timor.kidsstory.domain.usecase.GetStoryUseCase
import com.timor.kidsstory.domain.usecase.PageNavigationUseCase
import com.timor.kidsstory.domain.usecase.book.GetBookDetailUseCase
import com.timor.kidsstory.domain.util.TextToSpeechHelper
import com.timor.kidsstory.presentation.reader.model.PageUiState
import com.timor.kidsstory.presentation.reader.model.ReaderUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// 읽기 화면 뷰모델
@HiltViewModel
class ReaderViewModel @Inject constructor(
    private val getBookDetailUseCase: GetBookDetailUseCase,
    private val pageNavigationUseCase: PageNavigationUseCase,
    private val textToSpeechHelper: TextToSpeechHelper,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _state = MutableStateFlow(ReaderUiState())
    val state = _state.asStateFlow()

    private val _isTTSInitialized = MutableStateFlow(false)
    val isTTSInitialized = _isTTSInitialized.asStateFlow()

    // storyId 가져오기
    private val storyId: String = savedStateHandle.get<String>("storyId") ?: ""

    init {
        Log.d("ReaderViewModel", "Initializing with storyId: $storyId")

        if (storyId.isNotEmpty()) {
            // 짧은 지연 후 로드
            viewModelScope.launch {
                kotlinx.coroutines.delay(200)
                loadStory(storyId)
            }
        } else {
            Log.e("ReaderViewModel", "No storyId provided")
            _state.update { it.copy(error = "책 ID가 제공되지 않았습니다") }
        }

        // TTS 초기화
        viewModelScope.launch {
            textToSpeechHelper.isTTSInitialized.collect { isInitialized ->
                _isTTSInitialized.value = isInitialized
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        textToSpeechHelper.stop()
        textToSpeechHelper.shutDown()
    }

    private fun loadStory(storyId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            try {
                // ID에서 기본 부분만 추출 (예: 801_en-ph -> 801)
                val baseId = storyId.split("_").firstOrNull() ?: storyId
                Log.d("ReaderViewModel", "Loading story with base ID: $baseId")

                getBookDetailUseCase(baseId).fold(
                    onSuccess = { storyDetail ->
                        Log.d("ReaderViewModel", "Story loaded with ${storyDetail.pages.size} pages")

                        if (storyDetail.pages.isEmpty()) {
                            Log.e("ReaderViewModel", "Story has no pages")
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    error = "이 책에는 페이지가 없습니다."
                                )
                            }
                            return@fold
                        }

                        _state.update {
                            it.copy(
                                currentPage = 0,
                                pages = storyDetail.pages.map { page ->
                                    PageUiState(
                                        imageUrl = page.imageUrl,
                                        texts = page.texts,
                                        pageNumber = page.pageNumber + 1,
                                        totalPages = storyDetail.pages.size
                                    )
                                },
                                isLoading = false,
                                error = null
                            )
                        }
                    },
                    onFailure = { error ->
                        Log.e("ReaderViewModel", "Error loading story: ${error.message}", error)
                        _state.update {
                            it.copy(
                                isLoading = false,
                                error = "책을 불러올 수 없습니다: ${error.message}"
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                Log.e("ReaderViewModel", "Exception loading story", e)
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "오류가 발생했습니다: ${e.message}"
                    )
                }
            }
        }
    }

    fun onPageChanged(newPage: Int) {
        _state.update { it.copy(currentPage = newPage) }
    }

    fun ttsSpeak(content: List<String>) {
        if (_isTTSInitialized.value) {
            content.forEach { text ->
                textToSpeechHelper.speak(text)
            }
        } else {
            Log.w("ReaderViewModel", "TTS가 초기화되지 않았습니다")
        }
    }
}