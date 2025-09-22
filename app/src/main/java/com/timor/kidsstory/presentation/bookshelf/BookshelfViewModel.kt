package com.timor.kidsstory.presentation.bookshelf

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.timor.kidsstory.domain.model.Language
import com.timor.kidsstory.domain.usecase.MusicSettingUseCase
import com.timor.kidsstory.domain.usecase.book.FilterBooksUseCase
import com.timor.kidsstory.domain.usecase.book.GetAllBooksUseCase
import com.timor.kidsstory.domain.usecase.language.ChangeLanguageUseCase
import com.timor.kidsstory.domain.usecase.preference.GetUserPreferenceUseCase
import com.timor.kidsstory.domain.repository.ReadingProgressRepository
import com.timor.kidsstory.domain.util.LanguageConstants
import com.timor.kidsstory.domain.util.LanguageManager
import com.timor.kidsstory.domain.util.MusicManager
import com.timor.kidsstory.domain.util.SoundEffectManager
import com.timor.kidsstory.presentation.bookshelf.BookShelfAction
import com.timor.kidsstory.presentation.bookshelf.BookshelfUiState
import com.timor.kidsstory.presentation.bookshelf.components.ReadingStatusFilter
import com.timor.kidsstory.presentation.bookshelf.model.FilterBarCategory
import com.timor.kidsstory.presentation.bookshelf.model.FilterBookCategory
import com.timor.kidsstory.presentation.bookshelf.model.FilterLevel
import com.timor.kidsstory.presentation.bookshelf.model.ManagementTab
import com.timor.kidsstory.domain.model.UserPreference
import com.timor.kidsstory.presentation.bookshelf.model.FilterBarState
import com.timor.kidsstory.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.app.Application

/**
 * 책장 화면 전용 ViewModel (슬림화됨)
 * 
 * 단일 책임: 책장 UI 상태 관리
 * - 책 목록 로드 및 표시
 * - 책 필터링
 * - 언어 변경
 * - 배경 음악 제어
 * - 사운드 이펙트 관리
 * 
 * 제거된 책임들:
 * - 출석 관리 → AttendanceViewModel
 * - 읽기 진도 관리 → ProgressViewModel
 * - 복잡한 다운로드 로직 → 향후 분리 예정
 */
import com.timor.kidsstory.domain.usecase.CheckAppVersionUseCase
import com.timor.kidsstory.domain.usecase.PostponeUpdateUseCase

import androidx.lifecycle.SavedStateHandle

@HiltViewModel
class BookshelfViewModel @Inject constructor(
    private val getAllBooksUseCase: GetAllBooksUseCase,
    private val filterBooksUseCase: FilterBooksUseCase,
    private val changeLanguageUseCase: ChangeLanguageUseCase,
    private val getUserPreferenceUseCase: GetUserPreferenceUseCase,
    private val readingProgressRepository: ReadingProgressRepository,
    private val musicSettingUseCase: MusicSettingUseCase,
    private val musicManager: MusicManager,
    private val soundEffectManager: SoundEffectManager,
    private val checkAppVersionUseCase: CheckAppVersionUseCase,
    private val postponeUpdateUseCase: PostponeUpdateUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    // 핵심 UI 상태만 관리
    private val _state = MutableStateFlow(BookshelfUiState())
    val state = _state.asStateFlow()
    
    // 사용자 ID (기본값)
    private val currentUserId = "default_user"

    init {
        viewModelScope.launch {
            val initialLevel = savedStateHandle.get<Int>("level")
            val wasSkipped = savedStateHandle.get<Boolean>("wasSkipped") ?: false
            val showLevelResultPopup = savedStateHandle.get<Boolean>("showLevelResultPopup") ?: false

            loadUserPreferences(initialLevel, wasSkipped, showLevelResultPopup)
            loadBooks(_state.value.currentLanguage.code)
            loadMusicSetting()
        }
    }

    private suspend fun loadUserPreferences(
        initialLevel: Int?,
        wasSkipped: Boolean,
        showLevelResultPopup: Boolean
    ) {
        Log.d("BookshelfViewModel", "loadUserPreferences: initialLevel=$initialLevel, wasSkipped=$wasSkipped, showLevelResultPopup=$showLevelResultPopup")
        val prefs = getUserPreferenceUseCase().first()
        val currentLanguage = LanguageConstants.SUPPORTED_LANGUAGES.find { it.code == prefs.languageCode } ?: LanguageConstants.DEFAULT_LANGUAGE

        val initialFilterBarState = if (wasSkipped) {
            FilterBarState(selectedFilter = FilterBarCategory.All)
        } else if (initialLevel != null) {
            FilterBarState(selectedFilter = FilterBarCategory.STAGE, selectedStage = initialLevel.toFilterLevel(), isStageFilterExpanded = true)
        } else if (prefs.hasCompletedLevelTest) {
            FilterBarState(selectedFilter = FilterBarCategory.STAGE, selectedStage = prefs.selectedLevel.toFilterLevel())
        } else {
            FilterBarState(selectedFilter = FilterBarCategory.All)
        }

        val popupMessage = if (showLevelResultPopup && initialLevel != null) {
            LocalizedMessage(R.string.level_test_popup_message, arrayOf(initialLevel))
        } else {
            null
        }

        _state.value = _state.value.copy(
            currentLanguage = currentLanguage,
            isMusicOn = prefs.isMusicOn,
            filterBarState = initialFilterBarState,
            showLevelResultPopup = showLevelResultPopup,
            levelResultPopupMessage = popupMessage
        )
    }

    /**
     * 사용자 언어 설정 변화 관찰
     */
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
                    loadBooks(language.code)
                }
            }
        }
    }

    /**
     * 배경 음악 설정 로드 및 관찰
     */
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

    /**
     * 책 목록 로드 (UseCase 사용)
     */
    private fun loadBooks(languageCode: String) {
        viewModelScope.launch {
            try {
                _state.update { it.copy(isLoading = true, error = null) }
                
                Log.d("BookshelfViewModel", "Loading books for language: $languageCode")
                
                // GetAllBooksUseCase를 통한 통합 책 목록 조회
                val result = getAllBooksUseCase(languageCode)
                
                result.fold(
                    onSuccess = { books ->
                        _state.update { currentState ->
                            currentState.copy(
                                books = books,
                                filteredBooks = books, // 초기에는 필터링 없이 전체 표시
                                isLoading = false,
                                error = null
                            )
                        }
                        
                        // 현재 필터가 적용된 경우 다시 필터링
                        val currentFilter = _state.value.filterBarState.selectedFilter
                        if (currentFilter != FilterBarCategory.All) {
                            applyCurrentFilter()
                        }
                        
                        Log.d("BookshelfViewModel", "Successfully loaded ${books.size} books")
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
                Log.e("BookshelfViewModel", "Exception in loadBooks", e)
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
     * 사용자 액션 처리 (슬림화됨)
     */
    fun onAction(action: BookShelfAction) {
        when (action) {
            is BookShelfAction.BookSelect -> {
                soundEffectManager.playButtonClick()
                // 책 선택은 Navigation에서 처리
            }

            is BookShelfAction.ChatbotClick -> {
                soundEffectManager.playButtonClick()
                // Chatbot 네비게이션은 외부에서 처리
            }

            is BookShelfAction.SettingClick -> {
                soundEffectManager.playButtonClick()
                // Settings 네비게이션은 외부에서 처리
            }
            
            is BookShelfAction.MyPageClick -> {
                soundEffectManager.playButtonClick()
                // MyPage 네비게이션은 외부에서 처리
            }

            is BookShelfAction.ShowLanguageDialog -> {
                soundEffectManager.playButtonClick()
                handleLanguageDialog(action.isShow)
            }

            is BookShelfAction.ChangeLanguage -> {
                soundEffectManager.playButtonClick()
                changeLanguage(action.language)
            }

            is BookShelfAction.StartMusic -> startMusic()
            is BookShelfAction.StopMusic -> stopMusic()
            
            is BookShelfAction.DownloadBook -> {
                soundEffectManager.playButtonClick()
                // 다운로드 로직은 별도 ViewModel에서 처리 예정
            }

            is BookShelfAction.SelectFilter -> {
                soundEffectManager.playButtonClick()
                onFilterOptionSelected(
                    filter = action.filter,
                    stage = action.stage,
                    category = action.category,
                )
            }
            
            // 출석/진도 관련 액션들은 제거됨 (각각의 ViewModel에서 처리)
            is BookShelfAction.DismissAttendancePopup -> {
                // AttendanceViewModel에서 처리
            }

            is BookShelfAction.SelectReadingStatus -> {
                soundEffectManager.playButtonClick()
                selectReadingStatus(action.status)
            }
            
            // 🆕 관리 모드 관련 액션들
            is BookShelfAction.ToggleManagementMode -> {
                soundEffectManager.playButtonClick()
                toggleManagementMode()
            }
            
            is BookShelfAction.SelectManagementTab -> {
                soundEffectManager.playButtonClick()
                selectManagementTab(action.tab)
            }
            
            is BookShelfAction.ToggleBookSelection -> {
                soundEffectManager.playButtonClick()
                toggleBookSelection(action.bookId, action.isSelected)
            }
            
            is BookShelfAction.ExecuteSelectedActions -> {
                soundEffectManager.playButtonClick()
                executeSelectedActions()
            }
            
            is BookShelfAction.ShowConfirmationPopup -> {
                handleConfirmationPopup(action.show)
            }

            is BookShelfAction.CheckForUpdate -> {
                checkForUpdate()
            }

            is BookShelfAction.ShowUpdateDialog -> {
                _state.update { it.copy(showUpdateDialog = action.show) }
            }

            is BookShelfAction.PostponeUpdate -> {
                viewModelScope.launch {
                    _state.value.appVersionInfo?.let {
                        postponeUpdateUseCase(it.latestVersionCode)
                    }
                    _state.update { it.copy(showUpdateDialog = false) }
                }
            }

            is BookShelfAction.DismissLevelResultPopup -> {
                _state.update { it.copy(showLevelResultPopup = false, levelResultPopupMessage = null) }
            }
        }
    }

    private fun checkForUpdate() {
        viewModelScope.launch {
            val result = checkAppVersionUseCase()
            result.getOrNull()?.let { versionInfo ->
                _state.update {
                    it.copy(
                        appVersionInfo = versionInfo,
                        showUpdateDialog = true
                    )
                }
            }
        }
    }

    /**
     * 언어 선택 다이얼로그 표시/숨김
     */
    private fun handleLanguageDialog(isShow: Boolean) {
        _state.update { it.copy(showLanguageDialog = isShow) }
    }

    /**
     * 언어 변경 (UseCase 사용)
     */
    private fun changeLanguage(language: Language) {
        Log.d("BookshelfViewModel", "Changing language to: ${language.code}")

        if (language.code != _state.value.currentLanguage.code) {
            viewModelScope.launch {
                try {
                    _state.update {
                        it.copy(
                            showLanguageDialog = false,
                            isLoading = true,
                            currentLanguage = language,
                            // 언어 변경 시 필터 상태 초기화
                            filterBarState = it.filterBarState.copy(
                                selectedFilter = FilterBarCategory.All,
                                selectedStage = FilterLevel.ONE,
                                selectedCategory = FilterBookCategory.ENVIRONMENT,
                                isStageFilterExpanded = false,
                                isCategoryFilterExpanded = false
                            ),
                            selectedReadingStatus = ReadingStatusFilter.ALL // ReadingStatus도 전체로 초기화
                        )
                    }

                    // ChangeLanguageUseCase를 통한 언어 변경
                    changeLanguageUseCase(language)
                    LanguageManager.setCurrentLanguageCode(language.code)

                    kotlinx.coroutines.delay(100)
                    loadBooks(language.code)
                    
                    Log.d("BookshelfViewModel", "Language changed successfully to: ${language.code}")
                    
                } catch (e: Exception) {
                    Log.e("BookshelfViewModel", "언어 변경 중 오류 발생", e)
                    _state.update { 
                        it.copy(
                            isLoading = false,
                            error = "언어 변경에 실패했습니다: ${e.message}"
                        )
                    }
                }
            }
        } else {
            _state.update { it.copy(showLanguageDialog = false) }
        }
    }

    /**
     * 배경 음악 시작
     */
    private fun startMusic() {
        musicManager.startMusic()
    }

    /**
     * 배경 음악 정지
     */
    private fun stopMusic() {
        musicManager.stopMusic()
    }

    /**
     * 필터 옵션 선택 처리
     */
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
        applyCurrentFilter()
    }

    /**
     * 현재 선택된 필터 적용 (UseCase 사용)
     */
    private fun applyCurrentFilter() {
        viewModelScope.launch {
            try {
                val currentState = _state.value
                val selectedFilter = currentState.filterBarState.selectedFilter
                val selectedStage = currentState.filterBarState.selectedStage
                val selectedCategory = currentState.filterBarState.selectedCategory
                
                // FilterBooksUseCase를 통한 필터링 (Presentation 모델 사용)
                val filteredBooks = filterBooksUseCase(
                    books = currentState.books,
                    selectedFilter = selectedFilter,
                    selectedStage = selectedStage,
                    selectedCategory = selectedCategory
                )
                
                _state.update { 
                    it.copy(
                        filteredBooks = filteredBooks,
                        selectedReadingStatus = null // 필터 변경 시 ReadingStatus 리셋
                    ) 
                }
                
                Log.d("BookshelfViewModel", "Filtered books: ${filteredBooks.size}/${currentState.books.size}")
                
            } catch (e: Exception) {
                Log.e("BookshelfViewModel", "Error applying filter", e)
            }
        }
    }

    /**
     * 읽음 상태 필터 선택 처리
     */
    private fun selectReadingStatus(status: ReadingStatusFilter) {
        viewModelScope.launch {
            try {
                // 상태 업데이트
                _state.update {
                    it.copy(selectedReadingStatus = status)
                }
                
                // 상태에 따른 책 필터링
                applyReadingStatusFilter(status)
                
                Log.d("BookshelfViewModel", "Reading status filter applied: $status")
                
            } catch (e: Exception) {
                Log.e("BookshelfViewModel", "Error applying reading status filter", e)
            }
        }
    }
    
    /**
     * 읽기 상태에 따른 책 필터링
     */
    private suspend fun applyReadingStatusFilter(status: ReadingStatusFilter) {
        val currentState = _state.value
        val currentLanguage = currentState.currentLanguage.code
        
        // 현재 언어의 모든 읽기 진도 조회
        val allProgress = readingProgressRepository.getAllProgressByLanguage(
            userId = currentUserId,
            languageCode = currentLanguage
        )
        
        // 먼저 FilterBar에 의한 필터링 적용
        val filterBarFilteredBooks = filterBooksUseCase(
            books = currentState.books,
            selectedFilter = currentState.filterBarState.selectedFilter,
            selectedStage = currentState.filterBarState.selectedStage,
            selectedCategory = currentState.filterBarState.selectedCategory
        )
        
        // 그 다음 ReadingStatus에 따른 추가 필터링
        val finalFilteredBooks = when (status) {
            ReadingStatusFilter.ALL -> {
                // 전체: FilterBar만 적용된 결과 그대로 사용
                filterBarFilteredBooks
            }
            ReadingStatusFilter.UNREAD -> {
                // 안 읽음: 진도가 없거나 currentPage = 0인 책
                filterBarFilteredBooks.filter { book ->
                    val progress = allProgress[book.storyId]
                    progress == null || progress.currentPage == 0
                }
            }
            ReadingStatusFilter.READING -> {
                // 읽는 중: 0 < currentPage < totalPages 이고 완독되지 않은 책
                filterBarFilteredBooks.filter { book ->
                    val progress = allProgress[book.storyId]
                    progress != null && 
                    progress.currentPage > 0 && 
                    !progress.isCompleted
                }
            }
            ReadingStatusFilter.READ -> {
                // 다 읽음: isCompleted = true인 책
                filterBarFilteredBooks.filter { book ->
                    val progress = allProgress[book.storyId]
                    progress?.isCompleted == true
                }
            }
        }
        
        // 필터링된 결과 업데이트
        _state.update {
            it.copy(filteredBooks = finalFilteredBooks)
        }
        
        Log.d("BookshelfViewModel", "Filtered books by $status: ${finalFilteredBooks.size}/${currentState.books.size}")
    }
    
    // 🆕 관리 모드 관련 메서드들
    
    /**
     * 관리 모드 전환
     */
    private fun toggleManagementMode() {
        val currentMode = _state.value.isManagementMode
        
        _state.update { currentState ->
            currentState.copy(
                isManagementMode = !currentMode,
                // 관리 모드 종료 시 선택 내역 초기화
                selectedBookIds = if (currentMode) emptySet() else currentState.selectedBookIds,
                selectedManagementTab = if (currentMode) ManagementTab.ALL else currentState.selectedManagementTab,
                showConfirmationPopup = false,
                // 읽기 상태 필터도 초기화
                selectedReadingStatus = if (currentMode) null else currentState.selectedReadingStatus,
                // 🆕 관리 모드 진입 시 업데이트 확인 상태로 설정 (뱃지 숨김)
                hasCheckedUpdates = if (!currentMode) true else currentState.hasCheckedUpdates
            )
        }
        
        // 관리 모드 진입 시 초기 데이터 로드
        if (!currentMode) {
            loadManagementData()
        }
        
        Log.d("BookshelfViewModel", "Management mode toggled: ${!currentMode}")
    }
    
    /**
     * 관리 데이터 로드 (초기에는 전체 탭만 지원)
     */
    private fun loadManagementData() {
        viewModelScope.launch {
            try {
                // TODO: 나중에 실제 GitHub 메타데이터 체크 로직 추가
                // 현재는 더미 데이터로 설정
                _state.update { 
                    it.copy(
                        downloadableItemsCount = 5, // 더미: 다운로드 3개 + 업데이트 2개
                        downloadableBooks = emptyList(),
                        updatableBooks = emptyList()
                    )
                }
                
                Log.d("BookshelfViewModel", "Management data loaded")
                
            } catch (e: Exception) {
                Log.e("BookshelfViewModel", "Error loading management data", e)
            }
        }
    }
    
    /**
     * 관리 탭 선택 처리
     */
    private fun selectManagementTab(tab: ManagementTab) {
        _state.update { it.copy(selectedManagementTab = tab) }
        
        when (tab) {
            ManagementTab.ALL -> {
                // 전체 탭: 기존 FilterBar 동작과 동일
                applyCurrentFilter()
            }
            ManagementTab.DOWNLOAD -> {
                // 다운로드 탭: 네트워크 체크 후 다운로드 가능한 책 표시
                loadDownloadableBooks()
            }
            ManagementTab.UPDATE -> {
                // 업데이트 탭: 네트워크 체크 후 업데이트 가능한 책 표시
                loadUpdatableBooks()
            }
        }
        
        Log.d("BookshelfViewModel", "Management tab selected: $tab")
    }
    
    /**
     * 다운로드 가능한 책 로드
     */
    private fun loadDownloadableBooks() {
        viewModelScope.launch {
            try {
                _state.update { it.copy(isCheckingUpdates = true) }
                
                // TODO: 실제 GitHub 메타데이터 체크 로직
                kotlinx.coroutines.delay(1000) // 네트워크 요청 시뮬레이션
                
                // 실제 다운로드 가능한 책 목록 필터링
                val downloadableBooks = _state.value.books.filter { !it.isDownloaded }
                
                _state.update { 
                    it.copy(
                        isCheckingUpdates = false,
                        downloadableBooks = downloadableBooks,
                        filteredBooks = downloadableBooks
                    )
                }
                
                Log.d("BookshelfViewModel", "Downloadable books loaded: ${downloadableBooks.size}")
                
            } catch (e: Exception) {
                Log.e("BookshelfViewModel", "Error loading downloadable books", e)
                _state.update { 
                    it.copy(
                        isCheckingUpdates = false,
                        error = "다운로드 가능한 책을 불러올 수 없습니다: ${e.message}"
                    )
                }
            }
        }
    }
    
    /**
     * 업데이트 가능한 책 로드
     */
    private fun loadUpdatableBooks() {
        viewModelScope.launch {
            try {
                _state.update { it.copy(isCheckingUpdates = true) }
                
                // TODO: 실제 GitHub 메타데이터 체크 로직
                kotlinx.coroutines.delay(1500) // 네트워크 요청 시뮬레이션
                
                // 더미 데이터: 현재 책 목록에서 마지막 3개를 업데이트 가능로 설정
                val currentBooks = _state.value.books
                val updatableBooks = if (currentBooks.size >= 3) {
                    currentBooks.drop(currentBooks.size - 3)
                } else {
                    currentBooks
                }
                
                _state.update { 
                    it.copy(
                        isCheckingUpdates = false,
                        updatableBooks = updatableBooks,
                        filteredBooks = updatableBooks
                    )
                }
                
                Log.d("BookshelfViewModel", "Updatable books loaded: ${updatableBooks.size}")
                
            } catch (e: Exception) {
                Log.e("BookshelfViewModel", "Error loading updatable books", e)
                _state.update { 
                    it.copy(
                        isCheckingUpdates = false,
                        error = "업데이트 가능한 책을 불러올 수 없습니다: ${e.message}"
                    )
                }
            }
        }
    }
    
    /**
     * 책 선택 상태 변경
     */
    private fun toggleBookSelection(bookId: String, isSelected: Boolean) {
        _state.update { currentState ->
            val newSelectedIds = if (isSelected) {
                currentState.selectedBookIds + bookId
            } else {
                currentState.selectedBookIds - bookId
            }
            
            // 선택된 책들의 총 용량 계산
            val selectedBooks = currentState.filteredBooks.filter { it.storyId in newSelectedIds }
            val totalSize = selectedBooks.sumOf { it.totalSize }
            
            currentState.copy(
                selectedBookIds = newSelectedIds,
                totalSelectedSize = totalSize
            )
        }
        
        Log.d("BookshelfViewModel", "Book selection toggled: $bookId -> $isSelected")
    }
    
    /**
     * 선택된 항목들 실행 (다운로드/업데이트)
     */
    private fun executeSelectedActions() {
        val selectedCount = _state.value.selectedBookIds.size
        if (selectedCount > 0) {
            _state.update { it.copy(showConfirmationPopup = true) }
        }
        
        Log.d("BookshelfViewModel", "Execute selected actions: $selectedCount items")
    }
    
    /**
     * 확인 팝업 표시/숨김 처리
     */
    private fun handleConfirmationPopup(show: Boolean) {
        _state.update { it.copy(showConfirmationPopup = show) }
        
        if (!show) {
            // 팝업이 닫힐 때 실제 다운로드/업데이트 실행
            performSelectedActions()
        }
    }
    
    /**
     * 실제 다운로드/업데이트 실행
     */
    private fun performSelectedActions() {
        viewModelScope.launch {
            try {
                val selectedBooks = _state.value.filteredBooks.filter { 
                    it.storyId in _state.value.selectedBookIds 
                }
                
                // TODO: 실제 다운로드/업데이트 로직 구현
                selectedBooks.forEach { book ->
                    Log.d("BookshelfViewModel", "Performing action on: ${book.title}")
                }
                
                // 성공 후 선택 초기화
                _state.update {
                    it.copy(
                        selectedBookIds = emptySet(),
                        totalSelectedSize = 0L
                    )
                }
                
            } catch (e: Exception) {
                Log.e("BookshelfViewModel", "Error performing selected actions", e)
                _state.update {
                    it.copy(
                        error = "작업을 수행할 수 없습니다: ${e.message}"
                    )
                }
            }
        }
    }
    
    fun clearError() {
        _state.update { it.copy(error = null) }
    }

    /**
     * 책 목록 새로고침
     */
    fun refreshBooks() {
        val currentLanguage = _state.value.currentLanguage.code
        loadBooks(currentLanguage)
    }

    override fun onCleared() {
        super.onCleared()
        musicManager.release()
        soundEffectManager.release()
        Log.d("BookshelfViewModel", "ViewModel cleared")
    }
}

fun Int.toFilterLevel(): FilterLevel {
    return when (this) {
        1 -> FilterLevel.ONE
        2 -> FilterLevel.TWO
        3 -> FilterLevel.THREE
        4 -> FilterLevel.FOUR
        5 -> FilterLevel.FIVE
        else -> FilterLevel.THREE // Default or error handling
    }
}
