package com.timor.kidsstory.presentation.book

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.timor.kidsstory.domain.model.Page
import com.timor.kidsstory.domain.usecase.book.GetBookDetailUseCase
import com.timor.kidsstory.domain.util.TextToSpeechHelper
import com.timor.kidsstory.presentation.book.model.BookUiState
import com.timor.kidsstory.presentation.book.model.PageUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookViewModel @Inject constructor(
    private val getBookDetailUseCase: GetBookDetailUseCase,
    private val textToSpeechHelper: TextToSpeechHelper,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val _state = MutableStateFlow(BookUiState())
    val state = _state.asStateFlow()

    // 페이지 상태 관리를 ViewModel로 이동
    private val _currentPage = MutableStateFlow(0)
    val currentPage = _currentPage.asStateFlow()

    private val _isTTSInitialized = MutableStateFlow(false)
    val isTTSInitialized = _isTTSInitialized.asStateFlow()

    private val storyId: String = savedStateHandle.get<String>("storyId") ?: ""

    private var pages: List<Page> = emptyList()

    init {
        Log.d("ReaderViewModel", "Initializing with storyId: $storyId")

        if (storyId.isNotEmpty()) {
            loadStory(storyId)
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
    }

    private fun loadStory(storyId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            try {
                getBookDetailUseCase(storyId).fold(
                    onSuccess = { loadedPages ->
                        Log.d("ReaderViewModel", "Story loaded with ${loadedPages.size} pages")

                        if (loadedPages.isEmpty()) {
                            Log.e("ReaderViewModel", "Story has no pages")
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    error = "이 책에는 페이지가 없습니다."
                                )
                            }
                            return@fold
                        }

                        // 페이지 정보 저장
                        pages = loadedPages

                        _state.update {
                            it.copy(
                                pages = loadedPages.map { page ->
                                    PageUiState(
                                        imageUrl = page.imageUrl,
                                        texts = page.texts,
                                        pageNumber = page.pageNumber + 1,
                                        totalPages = page.totalPages
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

    fun onPageChanged(newPageIndex: Int) {
        _state.update {
            it.copy(currentPageIndex = newPageIndex)
        }
    }

    fun ttsSpeak(content: List<String>) {
        if (_isTTSInitialized.value) {
            content.forEach { text ->
                textToSpeechHelper.speak(text)
            }
        }
    }
}