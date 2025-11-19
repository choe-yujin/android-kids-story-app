package com.timor.kidsstory.presentation.book

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.orhanobut.logger.Logger
import com.timor.kidsstory.domain.manager.BookInteractionManager
import com.timor.kidsstory.domain.model.Mission
import com.timor.kidsstory.domain.model.Page
import com.timor.kidsstory.domain.model.ReadingProgress
import com.timor.kidsstory.domain.usecase.book.GetBookDetailUseCase
import com.timor.kidsstory.domain.usecase.preference.GetUserPreferenceUseCase
import com.timor.kidsstory.domain.util.LanguageConstants
import com.timor.kidsstory.domain.util.SoundEffectManager
import com.timor.kidsstory.domain.util.TextToSpeechHelper
import com.timor.kidsstory.domain.util.ai.TtsManager
import com.timor.kidsstory.domain.util.ai.TtsState
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

@HiltViewModel
class BookViewModel @Inject constructor(
    application: Application,
    private val getBookDetailUseCase: GetBookDetailUseCase,
    private val getUserPreferenceUseCase: GetUserPreferenceUseCase,
    private val bookInteractionManager: BookInteractionManager,
    private val textToSpeechHelper: TextToSpeechHelper,
    private val soundEffectManager: SoundEffectManager,
    private val ttsManager: TtsManager,
    savedStateHandle: SavedStateHandle,
) : AndroidViewModel(application) {

    private val _state = MutableStateFlow(BookUiState())
    val state = _state.asStateFlow()

    private val _isTTSInitialized = MutableStateFlow(false)
    val isTTSInitialized = _isTTSInitialized.asStateFlow()

    // TTS UI State
    private val _isTetumTtsReady = MutableStateFlow(false)
    val isTetumTtsReady = _isTetumTtsReady.asStateFlow()

    private val _isDownloadingModel = MutableStateFlow(false)
    val isDownloadingModel = _isDownloadingModel.asStateFlow()

    private val _downloadProgress = MutableStateFlow(0f)
    val downloadProgress = _downloadProgress.asStateFlow()

    private val _showTtsDownloadDialog = MutableStateFlow(false)
    val showTtsDownloadDialog = _showTtsDownloadDialog.asStateFlow()

    private val _ttsErrorMessage = MutableStateFlow<String?>(null)
    val ttsErrorMessage = _ttsErrorMessage.asStateFlow()

    private val storyId: String = savedStateHandle.get<String>("storyId") ?: ""
    private var pages: List<Page> = emptyList()
    private var currentBookId: String = ""
    private var currentLanguageCode: String = ""
    private var bookMissions: List<Mission> = emptyList()

    init {
        Log.d("ReaderViewModel", "Initializing with storyId: $storyId")
        if (storyId.isNotEmpty()) {
            loadStory(storyId)
        } else {
            _state.update { it.copy(error = "책 ID가 제공되지 않았습니다") }
        }
        observeTtsState()
    }

    private fun observeTtsState() {
        viewModelScope.launch {
            ttsManager.state.collect { ttsState ->
                Logger.d("TTS State Changed: $ttsState")
                _isTetumTtsReady.value = ttsState is TtsState.Ready
                _isDownloadingModel.value = ttsState is TtsState.Downloading

                if (ttsState is TtsState.Downloading) {
                    _downloadProgress.value = ttsState.progress
                }

                // Hide dialog on successful completion
                if (ttsState is TtsState.Ready) {
                    _showTtsDownloadDialog.value = false
                }

                if (ttsState is TtsState.Error) {
                    _ttsErrorMessage.value = ttsState.message
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        textToSpeechHelper.shutDown()
        soundEffectManager.release()
    }

    private fun loadStory(storyId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                getBookDetailUseCase(storyId).fold(
                    onSuccess = { book ->
                        Log.d("ReaderViewModel", "Story loaded: ${book.title} with ${book.pages.size} pages")
                        if (book.pages.isEmpty()) {
                            _state.update { it.copy(isLoading = false, error = "이 책에는 페이지가 없습니다.") }
                            return@fold
                        }

                        currentBookId = storyId
                        currentLanguageCode = storyId.split("_").getOrNull(1) ?: "ko"
                        bookMissions = book.missions
                        settingTTSLanguageForBook(currentLanguageCode)
                        pages = book.pages

                        _state.update {
                            it.copy(
                                pages = book.pages.map { page ->
                                    PageUiState(
                                        imageUrl = page.imageUrl,
                                        texts = page.texts,
                                        pageNumber = page.pageNumber + 1,
                                        totalPages = page.totalPages,
                                        pageType = page.pageType,
                                        textSectionState = PageTextSectionUiState(),
                                        contributors = if (page.pageNumber == 0) book.contributors else emptyList(),
                                        sponsors = if (page.pageNumber == 0) book.sponsors else null,
                                        copyright = if (page.pageNumber == 0) book.copyright else "",
                                        originalCopyright = if (page.pageNumber == 0) book.originalCopyright else null,
                                        title = if (page.pageNumber == 0) book.title else "",
                                        currentLanguageCode = book.languageCode
                                    )
                                },
                                isLoading = false,
                                error = null
                            )
                        }
                    },
                    onFailure = { error ->
                        _state.update { it.copy(isLoading = false, error = "책을 불러올 수 없습니다: ${error.message}") }
                    }
                )
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = "오류가 발생했습니다: ${e.message}") }
            }
        }
    }

    private fun onPageChanged(newPageIndex: Int) {
        if (newPageIndex >= pages.size) {
            val firstMission = bookMissions.firstOrNull()
            _state.update { it.copy(showCompletionScreen = true, mission = firstMission) }
            updateReadingProgress(currentBookId, pages.size, pages.size, currentLanguageCode)
            return
        }
        _state.update { it.copy(currentPageIndex = newPageIndex) }
        updateReadingProgress(currentBookId, newPageIndex + 1, pages.size, currentLanguageCode)
    }

    private fun ttsSpeak(content: List<String>) {
        Logger.d("[TTS] ttsSpeak called - Language: $currentLanguageCode")
        if (currentLanguageCode == "tet") {
            val currentState = ttsManager.state.value
            Logger.d("[TTS] Tetum detected - Current State: $currentState")
            when (currentState) {
                is TtsState.Ready -> content.forEach { ttsManager.speak(it) }
                is TtsState.NotDownloaded -> _showTtsDownloadDialog.value = true
                else -> Logger.d("[TTS] TTS is not ready or busy. State: $currentState")
            }
        } else {
            if (_isTTSInitialized.value) {
                content.forEach { textToSpeechHelper.speak(it) }
            }
        }
    }

    fun onAction(action: BookAction) {
        when (action) {
            is BookAction.TextToSpeak -> {
                soundEffectManager.playButtonClick()
                ttsSpeak(action.textList)
            }
            is BookAction.PageChange -> {
                soundEffectManager.playPageFlip()
                onPageChanged(action.page)
                if (currentLanguageCode != "tet") {
                    textToSpeechHelper.stop()
                }
            }
            BookAction.DownloadTtsModel -> {
                ttsManager.downloadAndInitialize()
            }
            BookAction.DismissTtsDialog -> {
                _showTtsDownloadDialog.value = false
                _ttsErrorMessage.value = null
            }
            BookAction.DismissTtsError -> {
                _ttsErrorMessage.value = null
                _showTtsDownloadDialog.value = false
            }
            BookAction.BackBookShelf -> soundEffectManager.playButtonClick()
            BookAction.CompletionConfirmed -> _state.update { it.copy(showCompletionScreen = false, mission = null) }
            is BookAction.UpdateTextSectionLayout -> updateTextSectionLayout(action.pageIndex, action.contentHeight, action.containerHeight)
            is BookAction.UpdateTextSectionScroll -> updateTextSectionScroll(action.pageIndex, action.scrollOffset, action.maxScrollOffset)
            is BookAction.UpdateReadingProgress -> updateReadingProgress(action.bookId, action.currentPage, action.totalPages, action.languageCode)
            else -> {}
        }
    }
    
    private fun updateTextSectionLayout(pageIndex: Int, contentHeight: Int, containerHeight: Int) {
        _state.update { currentState ->
            val updatedPages = currentState.pages.mapIndexed { index, page ->
                if (index == pageIndex) {
                    page.copy(textSectionState = page.textSectionState.updateLayout(contentHeight, containerHeight))
                } else page
            }
            currentState.copy(pages = updatedPages)
        }
    }

    private fun updateTextSectionScroll(pageIndex: Int, scrollOffset: Int, maxScrollOffset: Int) {
        _state.update { currentState ->
            val updatedPages = currentState.pages.mapIndexed { index, page ->
                if (index == pageIndex) {
                    page.copy(textSectionState = page.textSectionState.updateScroll(scrollOffset, maxScrollOffset))
                } else page
            }
            currentState.copy(pages = updatedPages)
        }
    }

    private fun updateReadingProgress(bookId: String, currentPage: Int, totalPages: Int, languageCode: String) {
        viewModelScope.launch {
            // Simplified for brevity
            bookInteractionManager.updateBookProgress(bookId, languageCode, currentPage, totalPages, currentPage >= totalPages)
        }
    }

    private fun settingTTSLanguageForBook(bookLanguageCode: String) {
        viewModelScope.launch {
            val locale: Locale? = when (bookLanguageCode) {
                "ko" -> Locale("ko", "KR")
                "mn" -> Locale("mn", "MN")
                "en" -> Locale.ENGLISH
                else -> null
            }
            if (locale != null) {
                textToSpeechHelper.initializeWithLanguage(locale).collect { success ->
                    _isTTSInitialized.value = success
                }
            } else {
                _isTTSInitialized.value = false
            }
        }
    }
}
