package com.timor.kidsstory.presentation.bookshelf

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.timor.kidsstory.domain.model.Book
import com.timor.kidsstory.domain.model.Language
import com.timor.kidsstory.domain.usecase.MusicSettingUseCase
import com.timor.kidsstory.domain.usecase.book.GetBooksUseCase
import com.timor.kidsstory.domain.usecase.preference.GetUserPreferenceUseCase
import com.timor.kidsstory.domain.usecase.preference.SaveUserPreferenceUseCase
import com.timor.kidsstory.domain.util.LanguageConstants
import com.timor.kidsstory.domain.util.MusicManager
import com.timor.kidsstory.presentation.bookshelf.model.BookCoverUiState
import com.timor.kidsstory.presentation.bookshelf.model.BookshelfUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// 책장 화면 뷰모델
@HiltViewModel
class BookshelfViewModel @Inject constructor(
    private val getBooksUseCase: GetBooksUseCase,
    private val getUserPreferenceUseCase: GetUserPreferenceUseCase,
    private val saveUserPreferenceUseCase: SaveUserPreferenceUseCase,
    private val musicSettingUseCase: MusicSettingUseCase,
    private val musicManager: MusicManager,
) : ViewModel() {
    private val _state = MutableStateFlow(BookshelfUiState())
    val state = _state.asStateFlow()

    // 실제 Book 객체 저장
    private var bookList = listOf<Book>()

    init {
        // 앱 시작 시 초기 데이터 로딩
        loadInitialData()
        // 이후 사용자 설정 변경 관찰
        observeUserPreference()

        // 스위치 상태를 불러오고 배경음 재생 여부 판단
        loadMusicSetting()
    }

    override fun onCleared() {
        super.onCleared()
        musicManager.release()
    }


    private fun loadMusicSetting() {
        viewModelScope.launch {
            musicSettingUseCase.isMusicOn.collect { isMusicOn ->
                _state.update { it.copy(isMusicOn = isMusicOn) }
            }
        }
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            val preference = getUserPreferenceUseCase().first() // 현재 설정값 가져오기

            val languageCode = if (preference.languageCode.isBlank()) {
                LanguageConstants.DEFAULT_LANGUAGE.code
            } else {
                preference.languageCode
            }

            val language = LanguageConstants.SUPPORTED_LANGUAGES.find {
                it.code == languageCode
            } ?: LanguageConstants.DEFAULT_LANGUAGE

            _state.update { it.copy(currentLanguage = language) }
            loadStories(language.code)
        }
    }

    private fun observeUserPreference() {
        viewModelScope.launch {
            getUserPreferenceUseCase().collect { preference ->
                val languageCode = if (preference.languageCode.isBlank()) {
                    // 저장된 언어가 없으면 기본 언어 사용
                    LanguageConstants.DEFAULT_LANGUAGE.code
                } else {
                    preference.languageCode
                }

                val language = LanguageConstants.SUPPORTED_LANGUAGES.find {
                    it.code == languageCode
                } ?: LanguageConstants.DEFAULT_LANGUAGE

                // 언어가 변경되었을 때만 상태 업데이트 및 책 로딩
                if (language.code != _state.value.currentLanguage.code) {
                    _state.update { it.copy(currentLanguage = language) }
                    loadStories(language.code)
                }
            }
        }
    }

    private fun loadStories(languageCode: String) {
        viewModelScope.launch {
            try {
                _state.update { it.copy(isLoading = true, error = null) }

                Log.d("BookshelfViewModel", "Loading books with language: $languageCode")
                val result = getBooksUseCase(languageCode)

                result.fold(
                    onSuccess = { books ->
                        // 책 목록 저장
                        bookList = books
                        Log.d("BookshelfViewModel", "Books loaded: ${books.size}")

                        _state.update {
                            it.copy(
                                books = books.map { book ->
                                    BookCoverUiState(
                                        imageUrl = book.coverImage, // 이미 정확한 경로 포함
                                        title = book.title,
                                        storyId = book.storyId
                                    )
                                },
                                isLoading = false
                            )
                        }
                    },
                    onFailure = { error ->
                        Log.e("BookshelfViewModel", "Error loading books: ${error.message}", error)
                        _state.update {
                            it.copy(
                                isLoading = false,
                                error = "책을 불러올 수 없습니다: ${error.message}"
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                Log.e("BookshelfViewModel", "Exception in loadStories", e)
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "오류가 발생했습니다: ${e.message}"
                    )
                }
            }
        }
    }

    fun onBookSelected(index: Int): Book? {
        if (index < 0 || index >= bookList.size) {
            Log.e("BookshelfViewModel", "Invalid book index: $index")
            return null
        }

        val selectedBook = bookList[index]
        Log.d("BookshelfViewModel", "Book selected: ${selectedBook.storyId}")
        return selectedBook
    }

    // 언어 선택 다이얼로그 표시
    fun showLanguageSelector() {
        _state.update { it.copy(showLanguageDialog = true) }
    }

    // 언어 선택 다이얼로그 숨기기
    fun hideLanguageSelector() {
        _state.update { it.copy(showLanguageDialog = false) }
    }

    // 언어 변경
    fun changeLanguage(language: Language) {
        Log.d("BookshelfViewModel", "Changing language to: ${language.code}")

        if (language.code != _state.value.currentLanguage.code) {
            // 다이얼로그 닫기
            _state.update { it.copy(showLanguageDialog = false) }

            // 선택한 언어를 저장
            viewModelScope.launch {
                saveUserPreferenceUseCase.updateLanguage(language.code)
            }
        } else {
            _state.update { it.copy(showLanguageDialog = false) }
        }
    }

    // Maker 화면으로 이동
    fun onMakerClick() {
        // 추후 구현
        Log.d("BookshelfViewModel", "Maker button clicked")
    }

    // 음악 재생 및 정지
    fun startMusic() {
        musicManager.startMusic()
    }

    fun stopMusic() {
        musicManager.stopMusic()
    }
}