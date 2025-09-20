package com.timor.kidsstory.presentation.book

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orhanobut.logger.Logger
import com.timor.kidsstory.domain.model.Page
import com.timor.kidsstory.domain.usecase.book.GetBookDetailUseCase
import com.timor.kidsstory.domain.usecase.preference.GetUserPreferenceUseCase
import com.timor.kidsstory.domain.repository.ReadingProgressRepository
import com.timor.kidsstory.domain.model.ReadingProgress
import com.timor.kidsstory.domain.util.LanguageConstants
import com.timor.kidsstory.domain.util.TextToSpeechHelper
import com.timor.kidsstory.domain.util.SoundEffectManager
import com.timor.kidsstory.presentation.book.model.BookUiState
import com.timor.kidsstory.presentation.book.model.PageTextSectionUiState
import com.timor.kidsstory.presentation.book.model.PageTextSectionUiState.Companion.updateLayout
import com.timor.kidsstory.presentation.book.model.PageTextSectionUiState.Companion.updateScroll
import com.timor.kidsstory.presentation.book.model.PageUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

/**
 * 책 읽기 화면의 상태 관리 및 비즈니스 로직 처리 뷰모델 (Clean Architecture 적용)
 * - 책 페이지 로드 및 관리
 * - 페이지 네비게이션 처리
 * - 텍스트 섹션 스크롤 상태 관리
 * - 텍스트 음성 변환(TTS) 기능 제공
 *
 * @property getBookDetailUseCase 책 상세 정보 가져오기 유스케이스
 * @property textToSpeechHelper 텍스트 음성 변환 도우미
 * @property soundEffectManager 효과음 관리자
 * @property savedStateHandle 네비게이션 인자 저장소
 */
@HiltViewModel
class BookViewModel @Inject constructor(
    private val getBookDetailUseCase: GetBookDetailUseCase,
    private val getUserPreferenceUseCase: GetUserPreferenceUseCase,
    private val readingProgressRepository: ReadingProgressRepository,
    private val textToSpeechHelper: TextToSpeechHelper,
    private val soundEffectManager: SoundEffectManager,
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
    
    // 현재 책 메타데이터
    private var currentBookId: String = ""
    private var currentLanguageCode: String = ""
    private var currentUserId: String = "default_user" // 기본 사용자 ID

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

        // TTS 기본 초기화 (책 로드 후 언어 설정됨)
    }

    /**
     * 뷰모델 종료 시 리소스 해제
     */
    override fun onCleared() {
        super.onCleared()
        textToSpeechHelper.shutDown()
        soundEffectManager.release()
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
                    onSuccess = { book -> // Changed from loadedPages to book
                        Log.d("ReaderViewModel", "Story loaded: ${book.title} with ${book.pages.size} pages")

                        if (book.pages.isEmpty()) { // Changed from loadedPages.isEmpty()
                            Log.e("ReaderViewModel", "Story has no pages")
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    error = "이 책에는 페이지가 없습니다."
                                )
                            }
                            return@fold
                        }

                        // 책 메타데이터 저장
                        currentBookId = storyId
                        // storyId에서 직접 언어 코드 추출 (단순화된 형태: ko, en, tet)
                        currentLanguageCode = storyId.split("_").getOrNull(1) ?: "ko"
                        
                        Log.d("BookViewModel", "Book metadata: storyId=$storyId, extractedLanguageCode=$currentLanguageCode, book.languageCode=${book.languageCode}")
                        
                        // 책 언어에 맞춰 TTS 언어 설정
                        settingTTSLanguageForBook(currentLanguageCode)
                        
                        // 페이지 정보 저장
                        pages = book.pages // Changed from loadedPages

                        // UI 상태 업데이트
                        _state.update {
                            it.copy(
                                pages = book.pages.map { page -> // Changed from loadedPages.map
                                    PageUiState(
                                        imageUrl = page.imageUrl,
                                        texts = page.texts,
                                        pageNumber = page.pageNumber + 1,
                                        totalPages = page.totalPages,
                                        textSectionState = PageTextSectionUiState(),
                                        // Pass new metadata to the first page
                                        contributors = if (page.pageNumber == 0) book.contributors else emptyList(),
                                        sponsors = if (page.pageNumber == 0) book.sponsors else null,
                                        copyright = if (page.pageNumber == 0) book.copyright else "",
                                        originalCopyright = if (page.pageNumber == 0) book.originalCopyright else null,
                                        title = if (page.pageNumber == 0) book.title else "",
                                        currentLanguageCode = book.languageCode // Added this line
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
        
        // 읽기 진도 업데이트 (페이지는 0부터 시작하지만 로직상 1부터 시작)
        val currentPage = newPageIndex + 1
        val totalPages = pages.size
        
        if (currentBookId.isNotEmpty() && currentLanguageCode.isNotEmpty()) {
            updateReadingProgress(currentBookId, currentPage, totalPages, currentLanguageCode)
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
     * 읽기 진도 업데이트
     * - 현재 페이지 = 총 페이지 수이면 완독 처리
     * - 0 < 현재 페이지 < 총 페이지 수이면 읽는 중 처리
     *
     * @param bookId 책 ID
     * @param currentPage 현재 페이지 (1부터 시작)
     * @param totalPages 총 페이지 수
     * @param languageCode 언어 코드
     */
    private fun updateReadingProgress(
        bookId: String,
        currentPage: Int,
        totalPages: Int,
        languageCode: String
    ) {
        viewModelScope.launch {
            try {
                Log.d("BookViewModel", "Updating reading progress: bookId=$bookId, page=$currentPage/$totalPages, language=$languageCode")
                
                val now = java.time.LocalDateTime.now()
                
                // 기존 진도 조회
                val existingProgress = readingProgressRepository.getReadingProgress(
                    userId = currentUserId,
                    bookId = bookId
                )
                
                val updatedProgress = when {
                    // 마지막 페이지에 도달하면 완독 처리
                    currentPage >= totalPages -> {
                        ReadingProgress(
                            currentPage = currentPage,
                            totalPages = totalPages,
                            isCompleted = true,
                            lastReadAt = now,
                            startedAt = existingProgress?.startedAt ?: now,
                            completedAt = if (existingProgress?.isCompleted != true) now else existingProgress.completedAt
                        )
                    }
                    // 읽는 중 상태
                    currentPage > 0 -> {
                        ReadingProgress(
                            currentPage = currentPage,
                            totalPages = totalPages,
                            isCompleted = existingProgress?.isCompleted ?: false, // 이미 완독한 책은 완독 상태 유지
                            lastReadAt = now,
                            startedAt = existingProgress?.startedAt ?: now,
                            completedAt = existingProgress?.completedAt // 기존 완독 시간 유지
                        )
                    }
                    // 첫 페이지 (0 페이지)는 업데이트하지 않음
                    else -> return@launch
                }
                
                // 진도 저장
                readingProgressRepository.updateReadingProgress(
                    userId = currentUserId,
                    bookId = bookId,
                    progress = updatedProgress,
                    languageCode = languageCode
                )
                
                Log.d("BookViewModel", "Reading progress updated: $bookId, page $currentPage/$totalPages, completed: ${updatedProgress.isCompleted}, language: $languageCode")
                
            } catch (e: Exception) {
                Log.e("BookViewModel", "Error updating reading progress", e)
            }
        }
    }
    /**
     * 책의 언어에 맞춰 TTS 언어 설정
     *
     * @param bookLanguageCode 책의 언어 코드 (ko, en, tet)
     */
    private fun settingTTSLanguageForBook(bookLanguageCode: String) {
        viewModelScope.launch {
            Log.d("BookViewModel", "Setting TTS language for book: $bookLanguageCode")

            // 단순화된 언어 코드에 따른 Locale 셋팅: ko, en, tet
            val locale: Locale = when (bookLanguageCode) {
                "ko" -> Locale("ko", "KR")
                "mn" -> Locale("mn", "MN") // Added for Mongolian
                else -> Locale.ENGLISH // en, tet 등은 영어 TTS 사용
            }

            // TTS 초기화 및 언어 설정을 함께 수행
            textToSpeechHelper.initializeWithLanguage(locale).collect { success ->
                _isTTSInitialized.value = success
                if (success) {
                    Logger.d("TTS 초기화 및 언어 설정 완료: $locale (book language: $bookLanguageCode)")
                } else {
                    Logger.e("TTS 초기화 또는 언어 설정 실패")
                }
            }
        }
    }

    private fun settingTTSLanguage() {
        viewModelScope.launch {
            val userInfo = getUserPreferenceUseCase().first()
            val languageCode = userInfo.languageCode.ifBlank {
                LanguageConstants.DEFAULT_LANGUAGE.code
            }

            // 영어: en-ph, 테툼어: tet, 한국어: ko-kr, 국가 code에 따른 Locale 셋팅
            val locale: Locale = when (languageCode) {
                "ko-kr" -> Locale("ko", "KR")
                "mn-MN" -> Locale("mn", "MN") // Added for Mongolian
                else -> Locale.ENGLISH
            }

            // 3. TTS 초기화 및 언어 설정을 함께 수행
            textToSpeechHelper.initializeWithLanguage(locale).collect { success ->
                _isTTSInitialized.value = success
                if (success) {
                    Logger.e("TTS 초기화 및 언어 설정 완료: $locale")
                } else {
                    Logger.e("TTS 초기화 또는 언어 설정 실패")
                }
            }
        }
    }

    /**
     * 완독 축하 화면 확인 처리
     * - 축하 화면을 닫고 책장으로 돌아가기 준비
     */
    private fun onCompletionConfirmed() {
        _state.update {
            it.copy(showCompletionScreen = false)
        }
        // 여기서 책장으로 돌아가는 로직은 상위 컴포너트에서 처리됨
    }

    /**
     * 텍스트 섹션 레이아웃 업데이트
     *
     * @param pageIndex 페이지 인덱스
     * @param contentHeight 콘텐츠 높이
     * @param containerHeight 컸테이너 높이
     */
    private fun updateTextSectionLayout(pageIndex: Int, contentHeight: Int, containerHeight: Int) {
        Log.d("BookViewModel", "updateTextSectionLayout: pageIndex=$pageIndex, contentHeight=$contentHeight, containerHeight=$containerHeight")
        
        _state.update { currentState ->
            val updatedPages = currentState.pages.mapIndexed { index, page ->
                if (index == pageIndex) {
                    page.copy(
                        textSectionState = page.textSectionState.updateLayout(
                            contentHeight = contentHeight,
                            containerHeight = containerHeight
                        )
                    )
                } else {
                    page
                }
            }
            
            // 디버그 로그 추가
            if (pageIndex < updatedPages.size) {
                val updatedState = updatedPages[pageIndex].textSectionState
                Log.d("BookViewModel", "Layout updated for page $pageIndex: " +
                    "content=${updatedState.contentHeight}, container=${updatedState.containerHeight}, " +
                    "canScrollUp=${updatedState.canScrollUp}, canScrollDown=${updatedState.canScrollDown}")
            }
            
            currentState.copy(pages = updatedPages)
        }
    }

    /**
     * 텍스트 섹션 스크롤 업데이트
     *
     * @param pageIndex 페이지 인덱스
     * @param scrollOffset 스크롤 오프셋
     * @param maxScrollOffset 최대 스크롤 오프셋
     */
    private fun updateTextSectionScroll(pageIndex: Int, scrollOffset: Int, maxScrollOffset: Int) {
        Log.d("BookViewModel", "updateTextSectionScroll: pageIndex=$pageIndex, scrollOffset=$scrollOffset, maxScrollOffset=$maxScrollOffset")
        
        _state.update { currentState ->
            val updatedPages = currentState.pages.mapIndexed { index, page ->
                if (index == pageIndex) {
                    page.copy(
                        textSectionState = page.textSectionState.updateScroll(
                            scrollOffset = scrollOffset,
                            maxScrollOffset = maxScrollOffset
                        )
                    )
                } else {
                    page
                }
            }
            
            // 디버그 로그 추가
            if (pageIndex < updatedPages.size) {
                val updatedState = updatedPages[pageIndex].textSectionState
                Log.d("BookViewModel", "Scroll updated for page $pageIndex: " +
                    "offset=${updatedState.scrollOffset}, max=${updatedState.maxScrollOffset}, " +
                    "canScrollUp=${updatedState.canScrollUp}, canScrollDown=${updatedState.canScrollDown}")
            }
            
            currentState.copy(pages = updatedPages)
        }
    }

    /**
     * UI 액션 처리
     *
     * @param action 처리할 액션
     */
    fun onAction(action: BookAction) {
        when (action) {
            is BookAction.TextToSpeak -> {
                soundEffectManager.playButtonClick()
                Logger.e("들어오는 컨텐츠 : ${action.textList}")
                ttsSpeak(action.textList)
            }
            BookAction.BackBookShelf -> {
                soundEffectManager.playButtonClick()
            }  // 네비게이션 처리는 컴포저블에서 함
            is BookAction.PageChange -> {
                soundEffectManager.playPageFlip()
                onPageChanged(action.page)
                // 음성 인식 중지
                textToSpeechHelper.stop()
            }
            BookAction.CompletionConfirmed -> {
                onCompletionConfirmed()
            }
            is BookAction.UpdateTextSectionLayout -> {
                updateTextSectionLayout(
                    action.pageIndex,
                    action.contentHeight,
                    action.containerHeight
                )
            }
            is BookAction.UpdateTextSectionScroll -> {
                updateTextSectionScroll(
                    action.pageIndex,
                    action.scrollOffset,
                    action.maxScrollOffset
                )
            }
            is BookAction.UpdateReadingProgress -> {
                updateReadingProgress(
                    action.bookId,
                    action.currentPage,
                    action.totalPages,
                    action.languageCode
                )
            }
        }
    }
}
