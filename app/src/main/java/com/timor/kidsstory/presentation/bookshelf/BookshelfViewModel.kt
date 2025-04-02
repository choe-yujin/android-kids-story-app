package com.timor.kidsstory.presentation.bookshelf

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orhanobut.logger.Logger
import com.timor.kidsstory.domain.model.Book
import com.timor.kidsstory.domain.model.Language
import com.timor.kidsstory.domain.usecase.MusicSettingUseCase
import com.timor.kidsstory.domain.usecase.book.GetBooksUseCase
import com.timor.kidsstory.domain.usecase.preference.GetUserPreferenceUseCase
import com.timor.kidsstory.domain.usecase.preference.SaveUserPreferenceUseCase
import com.timor.kidsstory.domain.util.LanguageConstants
import com.timor.kidsstory.domain.util.MusicManager
import com.timor.kidsstory.presentation.bookshelf.model.BookshelfUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 책장 화면의 상태 관리 및 비즈니스 로직 처리 뷰모델
 * - 책 목록 로드 및 필터링
 * - 언어 설정 관리
 * - 배경 음악 제어
 *
 * @property getBooksUseCase 책 목록 가져오기 유스케이스
 * @property getUserPreferenceUseCase 사용자 설정 가져오기 유스케이스
 * @property saveUserPreferenceUseCase 사용자 설정 저장 유스케이스
 * @property musicSettingUseCase 음악 설정 유스케이스
 * @property musicManager 배경 음악 관리자
 */
@HiltViewModel
class BookshelfViewModel @Inject constructor(
    private val getBooksUseCase: GetBooksUseCase,
    private val getUserPreferenceUseCase: GetUserPreferenceUseCase,
    private val saveUserPreferenceUseCase: SaveUserPreferenceUseCase,
    private val musicSettingUseCase: MusicSettingUseCase,
    private val musicManager: MusicManager,
) : ViewModel() {
    // UI 상태 관리
    private val _state = MutableStateFlow(BookshelfUiState())
    val state = _state.asStateFlow()

//    // 실제 Book 객체 저장 (UI 상태와 별도 관리)
//    private var bookList = listOf<Book>()

    /**
     * 초기화 - 앱 시작 시 필요한 데이터 로드
     */
    init {
        // 앱 시작 시 초기 데이터 로딩
        loadInitialData()
        // 이후 사용자 설정 변경 관찰
        observeUserPreference()

        // 스위치 상태를 불러오고 배경음 재생 여부 판단
        loadMusicSetting()
    }

    /**
     * 뷰모델 종료 시 리소스 해제
     */
    override fun onCleared() {
        super.onCleared()
        musicManager.release()
    }

    /**
     * 음악 설정 로드 및 관찰
     */
    private fun loadMusicSetting() {
        viewModelScope.launch {
            musicSettingUseCase.isMusicOn.collect { isMusicOn ->
                _state.update { it.copy(isMusicOn = isMusicOn) }
            }
        }
    }

    /**
     * 초기 데이터 로드
     * - 사용자 언어 설정 가져오기
     * - 해당 언어로 책 목록 로드
     */
    private fun loadInitialData() {
        viewModelScope.launch {
            val preference = getUserPreferenceUseCase().first() // 현재 설정값 가져오기

            // 언어 코드 결정 (저장된 값이 없으면 기본값 사용)
            val languageCode = if (preference.languageCode.isBlank()) {
                LanguageConstants.DEFAULT_LANGUAGE.code
            } else {
                preference.languageCode
            }

            // 언어 객체 찾기
            val language = LanguageConstants.SUPPORTED_LANGUAGES.find {
                it.code == languageCode
            } ?: LanguageConstants.DEFAULT_LANGUAGE

            _state.update { it.copy(currentLanguage = language) }
            loadStories(language.code)  // 해당 언어로 책 로드
        }
    }

    /**
     * 사용자 설정 변경 관찰
     * - 언어 변경 시 책 목록 다시 로드
     */
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

    /**
     * 책 목록 로드
     *
     * @param languageCode 언어 코드
     */

    private fun loadStories(languageCode: String) {
        viewModelScope.launch {
            try {
                _state.update { it.copy(isLoading = true, error = null) }

                Log.d("BookshelfViewModel", "Loading books with language: $languageCode")
                val result = getBooksUseCase(languageCode)

                Logger.e("북 리스트 확인: $result")

                result.fold(
                    onSuccess = { books ->
                        // 책 목록 저장
                        _state.update { it.copy(books = books) }
                        Log.d("BookshelfViewModel", "Books loaded: ${books.size}")
//
//                        // UI 상태 업데이트
//                        _state.update {
//                            it.copy(
//                                books = books.map { book ->
//                                    BookCoverUiState(
//                                        imageUrl = book.coverImage, // 이미 정확한 경로 포함
//                                        title = book.title,
//                                        storyId = book.storyId
//                                    )
//                                },
//                                isLoading = false
//                            )
//                        }
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

    /**
     * 선택된 책 정보 반환
     *
     * @param index 책 목록 인덱스
     * @return 선택된 책 객체 또는 null
     */
    fun onBookSelected(index: Int): Book? {
        if (index < 0 || index >= _state.value.books.size) {
            Log.e("BookshelfViewModel", "Invalid book index: $index")
            return null
        }

        val selectedBook = _state.value.books[index]
        Log.d("BookshelfViewModel", "Book selected: ${selectedBook.storyId}")
        return selectedBook
    }

    /**
     * 언어 선택 다이얼로그 표시 제어
     *
     * @param isShow 다이얼로그 표시 여부
     */
    private fun handleLanguageSelector(isShow: Boolean) {
        _state.update { it.copy(showLanguageDialog = isShow) }
    }


    /**
     * 언어 변경
     *
     * @param language 변경할 언어
     */
    private fun changeLanguage(language: Language) {
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


    /**
     * 음악 재생 시작
     */
    private fun startMusic() {
        musicManager.startMusic()
    }

    /**
     * 음악 재생 정지
     */
    private fun stopMusic() {
        musicManager.stopMusic()
    }

    /**
     * UI 액션 처리
     *
     * @param action 처리할 액션
     */
    fun onAction(action: BookShelfAction) {
        when (action) {
            is BookShelfAction.BookSelect -> onBookSelected(action.index)
            is BookShelfAction.ChatbotClick -> {}
            is BookShelfAction.SettingClick -> {}
            is BookShelfAction.StartMusic -> startMusic()
            is BookShelfAction.StopMusic -> stopMusic()
            is BookShelfAction.ChangeLanguage -> changeLanguage(action.language)
            is BookShelfAction.ShowLanguageDialog -> handleLanguageSelector(action.isShow)
        }
    }
}