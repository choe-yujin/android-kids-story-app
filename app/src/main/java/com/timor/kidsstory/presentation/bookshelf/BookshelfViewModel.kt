package com.timor.kidsstory.presentation.bookshelf

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.timor.kidsstory.data.local.assets.AssetDataSource
import com.timor.kidsstory.data.local.database.dao.DownloadedBooksDao
import com.timor.kidsstory.data.mapper.toBook
import com.timor.kidsstory.domain.model.Book
import com.timor.kidsstory.domain.model.DownloadProgress
import com.timor.kidsstory.domain.model.DownloadStatus
import com.timor.kidsstory.domain.model.Language
import com.timor.kidsstory.domain.usecase.MusicSettingUseCase
import com.timor.kidsstory.domain.usecase.book.GetBooksUseCase
import com.timor.kidsstory.domain.usecase.preference.GetUserPreferenceUseCase
import com.timor.kidsstory.domain.usecase.preference.SaveUserPreferenceUseCase
import com.timor.kidsstory.domain.util.LanguageConstants
import com.timor.kidsstory.domain.util.LanguageManager
import com.timor.kidsstory.domain.util.MusicManager
import com.timor.kidsstory.domain.util.SoundEffectManager
import com.timor.kidsstory.domain.manager.AttendanceManager
import com.timor.kidsstory.domain.manager.BookInteractionManager
import com.timor.kidsstory.presentation.bookshelf.model.BookshelfUiState
import com.timor.kidsstory.presentation.bookshelf.model.FilterBarCategory
import com.timor.kidsstory.presentation.bookshelf.model.FilterBookCategory
import com.timor.kidsstory.presentation.bookshelf.model.FilterLevel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

/**
 * 책장 화면의 상태 관리 및 비즈니스 로직 처리 뷰모델
 * - 책 목록 로드 및 필터링
 * - 언어 설정 관리
 * - 배경 음악 제어
 * - 출석 체크 및 읽기 진도 관리
 */
@HiltViewModel
class BookshelfViewModel @Inject constructor(
    private val getBooksUseCase: GetBooksUseCase,
    private val getUserPreferenceUseCase: GetUserPreferenceUseCase,
    private val saveUserPreferenceUseCase: SaveUserPreferenceUseCase,
    private val musicSettingUseCase: MusicSettingUseCase,
    private val musicManager: MusicManager,
    private val soundEffectManager: SoundEffectManager,
    private val downloadedBooksDao: DownloadedBooksDao,
    private val assetDataSource: AssetDataSource,
    private val attendanceManager: AttendanceManager,
    private val bookInteractionManager: BookInteractionManager,
    @ApplicationContext private val context: Context
) : ViewModel() {

    // UI 상태 관리
    private val _state = MutableStateFlow(BookshelfUiState())
    val state = _state.asStateFlow()

    // 출석 관련 상태
    private val _attendanceStreak = MutableStateFlow(0)
    val attendanceStreak = _attendanceStreak.asStateFlow()

    private val _shouldShowAttendancePopup = MutableStateFlow(false)
    val shouldShowAttendancePopup = _shouldShowAttendancePopup.asStateFlow()

    // 읽기 진도 관련 상태
    private val _readingProgress = MutableStateFlow(0f)
    val readingProgress = _readingProgress.asStateFlow()

    private val _completedBooksCount = MutableStateFlow(0)
    val completedBooksCount = _completedBooksCount.asStateFlow()

    private val _totalBooksCount = MutableStateFlow(0)
    val totalBooksCount = _totalBooksCount.asStateFlow()

    init {
        loadInitialData()
        observeUserPreference()
        loadMusicSetting()
        observeAttendanceAndProgress()
    }

    private fun observeAttendanceAndProgress() {
        viewModelScope.launch {
            attendanceManager.currentStreak.collect { streak ->
                _attendanceStreak.value = streak
            }
        }

        viewModelScope.launch {
            attendanceManager.shouldShowAttendancePopup.collect { shouldShow ->
                _shouldShowAttendancePopup.value = shouldShow
            }
        }

        viewModelScope.launch {
            try {
                attendanceManager.checkTodayAttendance()
            } catch (e: Exception) {
                Log.e("BookshelfViewModel", "Error checking attendance", e)
            }
        }
    }

    private fun updateReadingProgress() {
        viewModelScope.launch {
            try {
                val currentLanguage = _state.value.currentLanguage.code
                val completedBooks = bookInteractionManager.getCompletedBooksCount(currentLanguage)
                val totalBooks = _state.value.books.size

                _completedBooksCount.value = completedBooks
                _totalBooksCount.value = totalBooks

                val progress = if (totalBooks > 0) {
                    completedBooks.toFloat() / totalBooks.toFloat()
                } else {
                    0f
                }
                _readingProgress.value = progress

                Log.d("BookshelfViewModel", "Reading progress updated: $completedBooks/$totalBooks")
            } catch (e: Exception) {
                Log.e("BookshelfViewModel", "Error updating reading progress", e)
            }
        }
    }

    fun onAttendancePopupDismiss() {
        attendanceManager.onAttendancePopupShown()
    }

    fun onMyPageClick() {
        soundEffectManager.playButtonClick()
        Log.d("BookshelfViewModel", "MyPage clicked - navigation to be implemented")
    }

    override fun onCleared() {
        super.onCleared()
        musicManager.release()
        soundEffectManager.release()
    }

    private fun loadMusicSetting() {
        viewModelScope.launch {
            musicSettingUseCase.isMusicOn.collect { isMusicOn ->
                val previousState = _state.value.isMusicOn
                _state.update { it.copy(isMusicOn = isMusicOn) }

                if (previousState != isMusicOn || previousState == false) {
                    if (isMusicOn) {
                        musicManager.startMusic()
                    } else {
                        musicManager.stopMusic()
                    }
                }
            }
        }

        viewModelScope.launch {
            musicSettingUseCase.musicVolume.collect { volume ->
                val wasMusicPlaying = _state.value.isMusicOn
                musicManager.setVolume(volume)

                if (wasMusicPlaying) {
                    kotlinx.coroutines.delay(100)
                    musicManager.startMusic()
                }
            }
        }
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            val preference = getUserPreferenceUseCase().first()

            val languageCode = if (preference.languageCode.isBlank()) {
                LanguageConstants.DEFAULT_LANGUAGE.code
            } else {
                preference.languageCode
            }

            val language = LanguageConstants.SUPPORTED_LANGUAGES.find {
                it.code == languageCode
            } ?: LanguageConstants.DEFAULT_LANGUAGE

            LanguageManager.setCurrentLanguageCode(language.code)

            _state.update { it.copy(currentLanguage = language) }
            loadStories(language.code)
        }
    }

    private fun observeUserPreference() {
        viewModelScope.launch {
            getUserPreferenceUseCase().collect { preference ->
                val languageCode = if (preference.languageCode.isBlank()) {
                    LanguageConstants.DEFAULT_LANGUAGE.code
                } else {
                    preference.languageCode
                }

                val language = LanguageConstants.SUPPORTED_LANGUAGES.find {
                    it.code == languageCode
                } ?: LanguageConstants.DEFAULT_LANGUAGE

                if (language.code != _state.value.currentLanguage.code) {
                    LanguageManager.setCurrentLanguageCode(language.code)
                    _state.update { it.copy(currentLanguage = language) }
                    loadStories(language.code)
                }
            }
        }
    }

    private suspend fun loadStories(languageCode: String) {
        viewModelScope.launch {
            try {
                _state.update { it.copy(isLoading = true, error = null) }

                val result = getBooksUseCase(languageCode)
                val downloadedBooks = loadDownloadedBooks(languageCode)

                result.fold(
                    onSuccess = { localBooks ->
                        val localBooksWithDownloadStatus = localBooks.map { localBook ->
                            localBook.copy(
                                isDownloaded = true,
                                downloadProgress = DownloadProgress(DownloadStatus.DOWNLOADED)
                            )
                        }

                        val localStoryIds =
                            localBooksWithDownloadStatus.map { it.storyId.split("_").first() }
                                .toSet()
                        val newDownloadedBooks = downloadedBooks.filter {
                            !localStoryIds.contains(it.storyId.split("_").first())
                        }

                        val combinedBooks = localBooksWithDownloadStatus + newDownloadedBooks

                        val currentFilter = _state.value.filterBarState.selectedFilter
                        val shouldApplyFilters = currentFilter != FilterBarCategory.All

                        _state.update {
                            if (shouldApplyFilters) {
                                it.copy(books = combinedBooks, isLoading = false)
                            } else {
                                it.copy(
                                    books = combinedBooks,
                                    filteredBooks = combinedBooks,
                                    isLoading = false
                                )
                            }
                        }

                        if (shouldApplyFilters) {
                            applyFilters()
                        }

                        updateReadingProgress()

                        Log.d(
                            "BookshelfViewModel",
                            "Loaded total ${combinedBooks.size} books"
                        )
                    },
                    onFailure = { error ->
                        Log.e("BookshelfViewModel", "Error loading books", error)
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

    private suspend fun loadDownloadedBooks(languageCode: String): List<Book> {
        return try {
            val downloadedEntities = downloadedBooksDao.getDownloadedBooksByLanguage(languageCode)

            downloadedEntities.mapNotNull { entity ->
                val bookContentResult =
                    assetDataSource.loadExternalBookContent(entity.contentJsonPath)
                bookContentResult.getOrNull()?.let { response ->
                    val contentJsonFile = File(entity.contentJsonPath)
                    val bookRootDir = contentJsonFile.parentFile?.parentFile
                    val imageFolderPath = File(bookRootDir, "images").absolutePath
                    response.toBook(
                        languageCode,
                        entity.level,
                        entity.category,
                        entity.coverImagePath,
                        imageFolderPath
                    )
                }
            }
        } catch (e: Exception) {
            Log.e("BookshelfViewModel", "Error loading downloaded books", e)
            emptyList()
        }
    }

    fun onAction(action: BookShelfAction) {
        when (action) {
            is BookShelfAction.BookSelect -> {
                soundEffectManager.playButtonClick()
            }

            is BookShelfAction.ChatbotClick -> {
                soundEffectManager.playButtonClick()
            }

            is BookShelfAction.SettingClick -> {
                soundEffectManager.playButtonClick()
            }

            is BookShelfAction.ShowLanguageDialog -> {
                soundEffectManager.playButtonClick()
                handleLanguageSelector(action.isShow)
            }

            is BookShelfAction.ChangeLanguage -> {
                soundEffectManager.playButtonClick()
                changeLanguage(action.language)
            }

            is BookShelfAction.StartMusic -> startMusic()
            is BookShelfAction.StopMusic -> stopMusic()
            is BookShelfAction.DownloadBook -> {
                soundEffectManager.playButtonClick()
            }

            is BookShelfAction.SelectFilter -> {
                soundEffectManager.playButtonClick()
                onFilterOptionSelected(
                    filter = action.filter,
                    stage = action.stage,
                    category = action.category,
                )
            }

            is BookShelfAction.DismissAttendancePopup -> {
                onAttendancePopupDismiss()
            }

            is BookShelfAction.MyPageClick -> {
                onMyPageClick()
            }
        }
    }

    private fun handleLanguageSelector(isShow: Boolean) {
        _state.update { it.copy(showLanguageDialog = isShow) }
    }

    private fun changeLanguage(language: Language) {
        Log.d("BookshelfViewModel", "Changing language to: ${language.code}")

        if (language.code != _state.value.currentLanguage.code) {
            viewModelScope.launch {
                try {
                    _state.update {
                        it.copy(
                            showLanguageDialog = false,
                            currentLanguage = language
                        )
                    }

                    saveUserPreferenceUseCase.updateLanguage(language.code)
                    LanguageManager.setCurrentLanguageCode(language.code)

                    kotlinx.coroutines.delay(100)
                    loadStories(language.code)
                } catch (e: Exception) {
                    Log.e("BookshelfViewModel", "언어 변경 중 오류 발생", e)
                    _state.update { it.copy(isLoading = false) }
                }
            }
        } else {
            _state.update { it.copy(showLanguageDialog = false) }
        }
    }

    private fun startMusic() {
        musicManager.startMusic()
    }

    private fun stopMusic() {
        musicManager.stopMusic()
    }

    private fun onFilterOptionSelected(
        filter: FilterBarCategory? = null,
        stage: FilterLevel? = null,
        category: FilterBookCategory? = null
    ) {
        _state.update {
            val isNewFilterSelected =
                filter != null && filter != it.filterBarState.selectedFilter
            val isStageSelected = filter == FilterBarCategory.STAGE
            val isCategorySelected = filter == FilterBarCategory.CATEGORY

            val updatedStage = when {
                isStageSelected && isNewFilterSelected -> FilterLevel.ONE
                stage != null -> stage
                else -> it.filterBarState.selectedStage
            }

            val updatedCategory = when {
                isCategorySelected && isNewFilterSelected -> FilterBookCategory.ENVIRONMENT
                category != null -> category
                else -> it.filterBarState.selectedCategory
            }

            it.copy(
                filterBarState = it.filterBarState.copy(
                    selectedFilter = filter ?: it.filterBarState.selectedFilter,
                    selectedStage = updatedStage,
                    selectedCategory = updatedCategory,
                    isStageFilterExpanded = when {
                        isNewFilterSelected -> isStageSelected
                        stage != null -> true
                        else -> it.filterBarState.isStageFilterExpanded
                    },
                    isCategoryFilterExpanded = when {
                        isNewFilterSelected -> isCategorySelected
                        category != null -> true
                        else -> it.filterBarState.isCategoryFilterExpanded
                    }
                )
            )
        }
        applyFilters()
    }

    private fun applyFilters() {
        val selectedFilter = _state.value.filterBarState.selectedFilter
        val selectedStage = _state.value.filterBarState.selectedStage
        val selectedCategory = _state.value.filterBarState.selectedCategory

        val filteredList = _state.value.books.filter { book ->
            when (selectedFilter) {
                FilterBarCategory.STAGE -> selectedStage?.let { book.level == (it.ordinal + 1) }
                    ?: true

                FilterBarCategory.CATEGORY -> selectedCategory?.let {
                    it.matches(book.category)
                } ?: true

                else -> true
            }
        }

        _state.update { it.copy(filteredBooks = filteredList) }
    }
}