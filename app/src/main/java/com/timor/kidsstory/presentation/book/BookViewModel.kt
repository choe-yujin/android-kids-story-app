package com.timor.kidsstory.presentation.book

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orhanobut.logger.Logger
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

/**
 * 책 읽기 화면의 상태 관리 및 비즈니스 로직 처리 뷰모델
 * - 책 페이지 로드 및 관리
 * - 페이지 네비게이션 처리
 * - 텍스트 음성 변환(TTS) 기능 제공
 *
 * @property getBookDetailUseCase 책 상세 정보 가져오기 유스케이스
 * @property textToSpeechHelper 텍스트 음성 변환 도우미
 * @property savedStateHandle 네비게이션 인자 저장소
 */
@HiltViewModel
class BookViewModel @Inject constructor(
    private val getBookDetailUseCase: GetBookDetailUseCase,
    private val textToSpeechHelper: TextToSpeechHelper,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    // UI 상태 관리
    private val _state = MutableStateFlow(BookUiState())
    val state = _state.asStateFlow()

    // TTS 초기화 상태 관리
    private val _isTTSInitialized = MutableStateFlow(false)
    val isTTSInitialized = _isTTSInitialized.asStateFlow()

    // 네비게이션 인자에서 책 ID 추출
    private val storyId: String = savedStateHandle.get<String>("storyId") ?: ""

    // 원본 페이지 데이터
    private var pages: List<Page> = emptyList()

    /**
     * 초기화 - 책 데이터 로드 및 TTS 초기화
     */
    init {
        Log.d("ReaderViewModel", "Initializing with storyId: $storyId")

        if (storyId.isNotEmpty()) {
            loadStory(storyId)
        } else {
            Log.e("ReaderViewModel", "No storyId provided")
            _state.update { it.copy(error = "책 ID가 제공되지 않았습니다") }
        }

        textToSpeechHelper.reInitialize()

        // TTS 초기화 상태 관찰
        viewModelScope.launch {
            textToSpeechHelper.isTTSInitialized.collect { isInitialized ->
                _isTTSInitialized.value = isInitialized
            }
        }
    }

    /**
     * 뷰모델 종료 시 리소스 해제
     */
    override fun onCleared() {
        super.onCleared()
        textToSpeechHelper.shutDown()
    }

    /**
     * 책 데이터 로드
     *
     * @param storyId 책 ID
     */
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

                        // UI 상태 업데이트
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

    /**
     * 페이지 변경 처리
     *
     * @param newPageIndex 새 페이지 인덱스
     */
    private fun onPageChanged(newPageIndex: Int) {
        _state.update {
            it.copy(currentPageIndex = newPageIndex)
        }
    }

    /**
     * 텍스트 음성 변환 실행
     *
     * @param content 읽을 텍스트 목록
     */
    private fun ttsSpeak(content: List<String>) {
        if (_isTTSInitialized.value) {
            content.forEach { text ->
                textToSpeechHelper.speak(text)
            }
        }
    }


    /**
     * UI 액션 처리
     *
     * @param action 처리할 액션
     */
    fun onAction(action: BookAction) {
        when (action) {
            is BookAction.TextToSpeak -> ttsSpeak(action.textList)
            BookAction.BackBookShelf -> {}  // 네비게이션 처리는 컴포저블에서 함
            is BookAction.PageChange -> {
                onPageChanged(action.page)
                // 음성 인식 중지
                textToSpeechHelper.stop()
            }

        }
    }
}