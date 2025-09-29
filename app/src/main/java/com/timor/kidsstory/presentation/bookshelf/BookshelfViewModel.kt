package com.timor.kidsstory.presentation.bookshelf

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.timor.kidsstory.domain.model.Language
import com.timor.kidsstory.domain.usecase.MusicSettingUseCase
import com.timor.kidsstory.domain.usecase.book.FilterBooksUseCase
import com.timor.kidsstory.domain.usecase.book.GetAllBooksUseCase
import com.timor.kidsstory.domain.usecase.book.GetStoryInfoUseCase
import com.timor.kidsstory.domain.usecase.language.ChangeLanguageUseCase
import com.timor.kidsstory.domain.usecase.preference.GetUserPreferenceUseCase
import com.timor.kidsstory.domain.repository.ReadingProgressRepository
import com.timor.kidsstory.domain.util.LanguageConstants
import com.timor.kidsstory.domain.util.LanguageManager
import com.timor.kidsstory.domain.util.MusicManager
import com.timor.kidsstory.domain.util.SoundEffectManager
import com.timor.kidsstory.presentation.bookshelf.BookShelfAction
import com.timor.kidsstory.presentation.bookshelf.model.BookshelfUiState
import com.timor.kidsstory.presentation.bookshelf.model.LocalizedMessage
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
import com.timor.kidsstory.domain.usecase.update.CheckAvailableUpdatesUseCase
import com.timor.kidsstory.domain.usecase.book.CheckUnlockStatusUseCase
import com.timor.kidsstory.domain.usecase.book.GetManagementBooksUseCase
import com.timor.kidsstory.domain.usecase.book.ExecuteBookDownloadUseCase
import com.timor.kidsstory.domain.usecase.book.DeleteBookUseCase
import com.timor.kidsstory.data.remote.BookDownloader // 🆕 BookDownloader import 추가
import com.timor.kidsstory.presentation.bookshelf.model.ManagementActionType
import com.timor.kidsstory.domain.event.UnlockEventManager

import androidx.lifecycle.SavedStateHandle
import com.timor.kidsstory.domain.model.Book

@HiltViewModel
class BookshelfViewModel @Inject constructor(
    private val getAllBooksUseCase: GetAllBooksUseCase,
    private val filterBooksUseCase: FilterBooksUseCase,
    private val getStoryInfoUseCase: GetStoryInfoUseCase,
    private val changeLanguageUseCase: ChangeLanguageUseCase,
    private val getUserPreferenceUseCase: GetUserPreferenceUseCase,
    private val readingProgressRepository: ReadingProgressRepository,
    private val musicSettingUseCase: MusicSettingUseCase,
    private val musicManager: MusicManager,
    private val soundEffectManager: SoundEffectManager,
    private val checkAppVersionUseCase: CheckAppVersionUseCase,
    private val postponeUpdateUseCase: PostponeUpdateUseCase,
    private val checkAvailableUpdatesUseCase: CheckAvailableUpdatesUseCase,
    private val checkUnlockStatusUseCase: CheckUnlockStatusUseCase,
    private val getManagementBooksUseCase: GetManagementBooksUseCase,
    private val executeBookDownloadUseCase: ExecuteBookDownloadUseCase,
    private val deleteBookUseCase: DeleteBookUseCase,
    private val bookDownloader: BookDownloader, // 🆕 BookDownloader 추가
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

            // 🆕 Unlock 이벤트 수신
            UnlockEventManager.unlockEvent.collect { levelGroup ->
                Log.d("BookshelfViewModel", "🎉 Unlock event received for level group: $levelGroup")
                _state.update { it.copy(showUnlockPopup = true, unlockedLevelGroup = levelGroup) }
                loadBooks(_state.value.currentLanguage.code) // 🆕 Refresh books to update unlockedSteps
            }
        }
    }
    
    /**
     * 🆕 백그라운드에서 업데이트 체크 (네트워크 최적화)
     * - 1일 1회만 체크
     * - 메타데이터만 다운로드  
     * - 캐시 활용
     */
    private fun checkForAvailableUpdates() {
        viewModelScope.launch {
            try {
                Log.d("BookshelfViewModel", "🔍 Checking for available updates in background...")
                
                val result = checkAvailableUpdatesUseCase.getAvailableUpdatesCount(
                    userId = "default_user", // TODO: 실제 사용자 ID 사용
                    languageCode = _state.value.currentLanguage.code,
                    forceRefresh = false // 캐시 우선
                )
                
                result.onSuccess { updateResult ->
                    Log.d("BookshelfViewModel", "📦 Available updates: D=${updateResult.downloadableCount}, U=${updateResult.updatableCount}, Total=${updateResult.totalCount}")
                    
                    // UI 상태 업데이트
                    _state.update { 
                        it.copy(
                            downloadableItemsCount = updateResult.totalCount,         // 관리 버튼용 전체 수
                            downloadableOnlyCount = updateResult.downloadableCount,   // 🆕 DOWNLOAD 탭용
                            updatableOnlyCount = updateResult.updatableCount,         // 🆕 UPDATE 탭용
                            hasCheckedUpdates = false // 아직 사용자가 다운로드 탭을 확인하지 않음
                        ) 
                    }
                    
                    if (updateResult.totalCount > 0) {
                        Log.d("BookshelfViewModel", "📢 배지 표시: ${updateResult.totalCount}개 항목 업데이트 가능")
                    }
                }.onFailure { error ->
                    Log.w("BookshelfViewModel", "Failed to check for updates", error)
                    // 실패시 무시 (네트워크 없는 환경 등)
                }
                
            } catch (e: Exception) {
                Log.e("BookshelfViewModel", "Error in background update check", e)
            }
        }
    }

    /**
     * 🆕 다운로드/업데이트 완료 후 배지 카운트 업데이트
     * - 실제 작업 완료시 호출
     * - 캐시 무효화 후 새로고침
     */
    fun refreshUpdateCounts() {
        viewModelScope.launch {
            try {
                Log.d("BookshelfViewModel", "🔄 다운로드/업데이트 완료 후 카운트 업데이트")
                
                // 캐시 무효화
                checkAvailableUpdatesUseCase.invalidateCache()
                
                // 새로고침
                val result = checkAvailableUpdatesUseCase.getAvailableUpdatesCount(
                    userId = "default_user",
                    languageCode = _state.value.currentLanguage.code,
                    forceRefresh = true // 강제 새로고침
                )
                
                result.onSuccess { updateResult ->
                    _state.update { 
                        it.copy(
                            downloadableItemsCount = updateResult.totalCount,
                            downloadableOnlyCount = updateResult.downloadableCount,
                            updatableOnlyCount = updateResult.updatableCount,
                            // 🆕 하이브리드: 실제 다운로드/업데이트 완료로 카운트 변경시 확인 상태 초기화
                            hasViewedDownloadTab = false,
                            hasViewedUpdateTab = false
                        ) 
                    }
                    
                    Log.d("BookshelfViewModel", "🔄 배지 카운트 업데이트 완료: D=${updateResult.downloadableCount}, U=${updateResult.updatableCount}")
                }
                
            } catch (e: Exception) {
                Log.e("BookshelfViewModel", "Error refreshing update counts", e)
            }
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
        
        // 🆕 앞 초기 진입 시 LanguageManager 업데이트
        LanguageManager.setCurrentLanguageCode(currentLanguage.code)

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
    private fun loadBooks(languageCode: String, updateFilteredBooks: Boolean = true) {
        viewModelScope.launch {
            try {
                _state.update { it.copy(isLoading = true, error = null) }
                
                Log.d("BookshelfViewModel", "Loading books for language: $languageCode")
                
                // GetAllBooksUseCase를 통한 통합 책 목록 조회
                val result = getAllBooksUseCase(languageCode)
                
                result.fold(
                    onSuccess = { books ->
                        // 🆕 unlock 상태 로드 (일반 모드에서도 필요)
                        val unlockedStepsMap = checkUnlockStatusUseCase.getUnlockedSteps(
                            userId = currentUserId,
                            languageCode = languageCode
                        )
                        
                        _state.update { currentState ->
                            currentState.copy(
                                books = books,
                                filteredBooks = if (updateFilteredBooks) books else currentState.filteredBooks,
                                unlockedSteps = unlockedStepsMap, // 🆕 unlock 상태 설정
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
                        Log.d("BookshelfViewModel", "🔓 Unlocked steps: $unlockedStepsMap") // 🆕 로그 추가
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
            
            // DownloadBook 액션 제거 - 관리 모드에서만 다운로드 처리

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
            
            is BookShelfAction.ShowLockedBookPopup -> {
                _state.update { it.copy(showLockedBookPopup = action.show) }
            }
            
            is BookShelfAction.CancelSelection -> {
                // 🆕 선택 취소: 선택 내역 초기화 + 팝업 닫기
                _state.update {
                    it.copy(
                        showConfirmationPopup = false,
                        pendingActionType = null
                    )
                }
            }
            
            is BookShelfAction.ShowStoryInfoDialog -> {
                loadStoryInfo(action.storyId)
            }
            
            is BookShelfAction.DismissStoryInfoDialog -> {
                dismissStoryInfoDialog()
            }
            
            is BookShelfAction.DismissActionCompletionDialog -> {
                dismissActionCompletionDialog()
            }
            is BookShelfAction.DismissUnlockPopup -> {
                dismissUnlockPopup()
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
                            selectedReadingStatus = ReadingStatusFilter.ALL, // ReadingStatus도 전체로 초기화
                            // 🆕 관리 모드 상태 초기화
                            selectedBookIds = emptySet(),
                            totalSelectedSize = 0L,
                            showConfirmationPopup = false,
                            pendingActionType = null,
                            // 🆕 관리 데이터 초기화 (새 언어로 다시 로드해야 함)
                            downloadableBooks = emptyList(),
                            updatableBooks = emptyList(),
                            hasCheckedUpdates = false,
                            downloadableItemsCount = 0,
                            filteredBooks = emptyList() // 🆕 버그 수정을 위해 추가
                        )
                    }

                    // ChangeLanguageUseCase를 통한 언어 변경
                    changeLanguageUseCase(language)
                    
                    // 🆕 LanguageManager 업데이트 (모든 LocalizedText가 재구성됨)
                    LanguageManager.setCurrentLanguageCode(language.code)

                    kotlinx.coroutines.delay(100)
                    loadBooks(language.code, updateFilteredBooks = !_state.value.isManagementMode)
                    
                    // 🆕 관리 모드인 경우 새 언어로 관리 데이터 다시 로드
                    if (_state.value.isManagementMode) {
                        // 언어 변경 후 지연을 두어 books 업데이트 완료 후 실행
                        kotlinx.coroutines.delay(200)
                        
                        Log.d("BookshelfViewModel", "💼 Language changed in management mode - reloading management data")
                        loadManagementData()
                        
                        // 추가 지연 후 현재 선택된 탭에 맞는 책 목록 로드
                        kotlinx.coroutines.delay(100)
                        when (_state.value.selectedManagementTab) {
                            ManagementTab.DOWNLOAD -> {
                                Log.d("BookshelfViewModel", "💼 Reloading DOWNLOAD tab for new language: ${language.code}")
                                loadDownloadableBooks(_state.value.books)
                            }
                            ManagementTab.UPDATE -> {
                                Log.d("BookshelfViewModel", "💼 Reloading UPDATE tab for new language: ${language.code}")
                                loadUpdatableBooks(_state.value.books)
                            }
                            ManagementTab.DELETE -> {
                                Log.d("BookshelfViewModel", "💼 Reloading DELETE tab for new language: ${language.code}")
                                loadDeletableBooks(_state.value.books)
                            }
                        }
                    }
                    
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
                var filteredBooks = filterBooksUseCase(
                    books = currentState.books,
                    selectedFilter = selectedFilter,
                    selectedStage = selectedStage,
                    selectedCategory = selectedCategory
                )
                
                // 🆕 일반 모드: 다운로드된 책만 표시 (unlock 여부 무관)
                // 관리 모드: 모든 책 표시
                if (!currentState.isManagementMode) {
                    filteredBooks = filteredBooks.filter { it.isDownloaded }
                    Log.d("BookshelfViewModel", "👁️ Normal mode filter applied: ${filteredBooks.size} downloaded books")
                } else {
                    // 🆕 관리 모드에서는 여기서 전체 책 목록을 바로 설정하지 말고
                    // 탭별 별도 로드 메서드가 처리하도록 함
                    Log.d("BookshelfViewModel", "💼 Management mode: filteredBooks will be set by tab-specific methods")
                }
                
                _state.update { 
                    it.copy(
                        filteredBooks = if (currentState.isManagementMode) it.filteredBooks else filteredBooks,
                        selectedReadingStatus = null // 필터 변경 시 ReadingStatus 리셋
                    ) 
                }
                
                Log.d("BookshelfViewModel", "Filtered books: ${if (currentState.isManagementMode) currentState.filteredBooks.size else filteredBooks.size}/${currentState.books.size} (isManagementMode=${currentState.isManagementMode})")
                
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
                selectedManagementTab = if (currentMode) ManagementTab.DOWNLOAD else currentState.selectedManagementTab,
                showConfirmationPopup = false,
                // 읽기 상태 필터도 초기화
                selectedReadingStatus = if (currentMode) null else currentState.selectedReadingStatus,
                // 🆕 관리 모드 진입 시 업데이트 확인 상태로 설정 (뱃지 숨김)
                hasCheckedUpdates = if (!currentMode) true else currentState.hasCheckedUpdates,
                // 🆕 관리 모드 진입 시 filteredBooks를 빈 리스트로 초기화 (버퍼링 효과)
                filteredBooks = if (!currentMode) emptyList() else currentState.filteredBooks
            )
        }
        
        // 관리 모드 진        // 관리 모드 진입 시 초기 데이터 로드
        if (!currentMode) {
            loadManagementData()
            // 🆕 기본 탭(DOWNLOAD)의 책 목록 로드 후 "확인됨" 처리
            viewModelScope.launch {
                loadDownloadableBooks(_state.value.books)
                // 로드 완료 후 DOWNLOAD 탭을 "확인됨"으로 설정
                _state.update { it.copy(hasViewedDownloadTab = true) }
                Log.d("BookshelfViewModel", "📁 Management mode entered - DOWNLOAD tab auto-viewed")
            }
        } else {
            // 🆕 관리 모드 종료 시 필터 재적용 (일반 모드는 isDownloaded=true만)
            applyCurrentFilter()
        }
        
        Log.d("BookshelfViewModel", "📁 Management mode toggled: ${!currentMode} (viewed: D=${_state.value.hasViewedDownloadTab}, U=${_state.value.hasViewedUpdateTab})")
    }
    
    /**
     * 관리 데이터 로드
     */
    private fun loadManagementData() {
        viewModelScope.launch {
            try {
                _state.update { it.copy(isCheckingUpdates = true) }
                
                val currentLanguage = _state.value.currentLanguage.code
                
                // 1. 다운로드 가능한 책 조회
                val downloadableResult = getManagementBooksUseCase.getDownloadableBooks(
                    userId = currentUserId,
                    languageCode = currentLanguage
                )
                val downloadableBooks = downloadableResult.getOrElse { emptyList() }
                
                // 2. 업데이트 가능한 책 조회
                val updatableResult = getManagementBooksUseCase.getUpdatableBooks(
                    languageCode = currentLanguage
                )
                val updatableBooks = updatableResult.getOrElse { emptyList() }
                
                // 3. unlock 상태 확인 (표시용)
                val unlockedStepsMap = checkUnlockStatusUseCase.getUnlockedSteps(
                    userId = currentUserId,
                    languageCode = currentLanguage
                )
                
                _state.update { 
                    it.copy(
                        isCheckingUpdates = false,
                        unlockedSteps = unlockedStepsMap, // ⚠️ Map<String, Int> 그대로 사용
                        downloadableItemsCount = downloadableBooks.size + updatableBooks.size,
                        downloadableBooks = downloadableBooks,
                        updatableBooks = updatableBooks,
                        hasCheckedUpdates = true
                    )
                }
                
                Log.d("BookshelfViewModel", "🔓 Unlocked steps by group: $unlockedStepsMap")
                Log.d("BookshelfViewModel", "⬇️ Downloadable books: ${downloadableBooks.size}")
                Log.d("BookshelfViewModel", "🔄 Updatable books: ${updatableBooks.size}")
                
            } catch (e: Exception) {
                Log.e("BookshelfViewModel", "❌ Error loading management data", e)
                _state.update { 
                    it.copy(
                        isCheckingUpdates = false,
                        error = "관리 데이터를 불러올 수 없습니다: ${e.message}"
                    )
                }
            }
        }
    }
    
    /**
     * 선택된 탭에 맞는 책 목록 로드
     */
    private fun selectManagementTab(tab: ManagementTab) {
        // 🆕 하이브리드: 탭 클릭시 “확인됨” 상태로 설정
        _state.update { currentState ->
            currentState.copy(
                selectedManagementTab = tab,
                // 🆕 버튼 클릭시 해당 탭 “확인됨” 처리
                hasViewedDownloadTab = if (tab == ManagementTab.DOWNLOAD) true else currentState.hasViewedDownloadTab,
                hasViewedUpdateTab = if (tab == ManagementTab.UPDATE) true else currentState.hasViewedUpdateTab,
                // 🆕 탭 변경 시 filteredBooks를 빈 리스트로 초기화 (버퍼링 효과)
                filteredBooks = emptyList(),
                // 🆕 선택 내역도 초기화
                selectedBookIds = emptySet(),
                totalSelectedSize = 0L
            )
        }

        when (tab) {
            ManagementTab.DOWNLOAD -> {
                // 다운로드 탭: 다운로드 가능한 책만 표시
                loadDownloadableBooks(_state.value.books)
            }
            ManagementTab.UPDATE -> {
                // 업데이트 탭: 업데이트 가능한 책만 표시
                loadUpdatableBooks(_state.value.books)
            }
            ManagementTab.DELETE -> {
                // 🆕 삭제 탭: 로컬에 저장된 책만 표시
                loadDeletableBooks(_state.value.books)
            }
        }

        Log.d("BookshelfViewModel", "📁 Management tab selected: $tab (viewed: D=${_state.value.hasViewedDownloadTab}, U=${_state.value.hasViewedUpdateTab})")
    }
    
    /**
     * 다운로드 가능한 책 로드
     */
    private fun loadDownloadableBooks(allLocalBooks: List<Book>? = null) {
        viewModelScope.launch {
            try {
                _state.update { it.copy(isCheckingUpdates = true) }
                
                val currentLanguage = _state.value.currentLanguage.code
                Log.d("BookshelfViewModel", "🔍 Loading downloadable books for language: $currentLanguage")
                
                // GetManagementBooksUseCase를 통한 다운로드 가능한 책 조회
                val downloadableResult = getManagementBooksUseCase.getDownloadableBooks(
                    userId = currentUserId,
                    languageCode = currentLanguage,
                    allLocalBooks = allLocalBooks
                )
                val downloadableBooks = downloadableResult.getOrElse { 
                    Log.e("BookshelfViewModel", "❌ Failed to get downloadable books: ${downloadableResult.exceptionOrNull()?.message}")
                    emptyList() 
                }
                
                Log.d("BookshelfViewModel", "📚 Downloadable books found: ${downloadableBooks.size}")
                downloadableBooks.take(3).forEach { book ->
                    Log.d("BookshelfViewModel", "  - ${book.storyId}: ${book.title} (downloaded: ${book.isDownloaded})")
                }
                
                _state.update { 
                    it.copy(
                        isCheckingUpdates = false,
                        downloadableBooks = downloadableBooks,
                        filteredBooks = downloadableBooks
                    )
                }
                
                Log.d("BookshelfViewModel", "✅ Downloadable books loaded: ${downloadableBooks.size}")
                
            } catch (e: Exception) {
                Log.e("BookshelfViewModel", "❌ Error loading downloadable books", e)
                _state.update { 
                    it.copy(
                        isCheckingUpdates = false,
                        filteredBooks = emptyList(), // 🆕 오류 시에도 빈 리스트로 설정
                        error = "다운로드 가능한 책을 불러올 수 없습니다: ${e.message}"
                    )
                }
            }
        }
    }
    
    /**
     * 삭제 가능한 책 로드
     */
    private fun loadDeletableBooks(books: List<com.timor.kidsstory.domain.model.Book>) {
        viewModelScope.launch {
            try {
                _state.update { it.copy(isCheckingUpdates = true) }

                val currentLanguage = _state.value.currentLanguage.code
                Log.d("BookshelfViewModel", "🔍 Loading deletable books for language: $currentLanguage")
                
                // 🆕 GetManagementBooksUseCase.getDeletableBooks 사용 (실제 삭제 용량 포함)
                val deletableResult = getManagementBooksUseCase.getDeletableBooks(
                    languageCode = currentLanguage
                )
                val deletableBooks = deletableResult.getOrElse {
                    Log.e("BookshelfViewModel", "❌ Failed to get deletable books: ${deletableResult.exceptionOrNull()?.message}")
                    emptyList()
                }
                
                Log.d("BookshelfViewModel", "📚 Deletable books found: ${deletableBooks.size}")
                deletableBooks.take(3).forEach { book ->
                    Log.d("BookshelfViewModel", "  - ${book.storyId}: ${book.title} (delete size: ${book.totalSize / 1024}KB)")
                }

                _state.update { 
                    it.copy(
                        isCheckingUpdates = false,
                        filteredBooks = deletableBooks
                    )
                }

                Log.d("BookshelfViewModel", "🗑️ Deletable books loaded: ${deletableBooks.size}")

            } catch (e: Exception) {
                Log.e("BookshelfViewModel", "Error loading deletable books", e)
                _state.update { 
                    it.copy(
                        isCheckingUpdates = false,
                        filteredBooks = emptyList(), // 🆕 오류 시에도 빈 리스트로 설정
                        error = "삭제 가능한 책을 불러올 수 없습니다: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * 업데이트 가능한 책 로드
     */
    private fun loadUpdatableBooks(books: List<com.timor.kidsstory.domain.model.Book>) {
        viewModelScope.launch {
            try {
                _state.update { it.copy(isCheckingUpdates = true) }

                val currentLanguage = _state.value.currentLanguage.code

                // GetManagementBooksUseCase를 통한 업데이트 가능한 책 조회
                val updatableResult = getManagementBooksUseCase.getUpdatableBooks(
                    languageCode = currentLanguage
                )
                val updatableBooks = updatableResult.getOrElse { emptyList() }

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
                        filteredBooks = emptyList(), // 🆕 오류 시에도 빈 리스트로 설정
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
        viewModelScope.launch {
            _state.update { currentState ->
                val newSelectedIds = if (isSelected) {
                    currentState.selectedBookIds + bookId
                } else {
                    currentState.selectedBookIds - bookId
                }
                
                currentState.copy(
                    selectedBookIds = newSelectedIds,
                    totalSelectedSize = 0L // 임시로 0으로 설정
                )
            }
            
            // 🆕 선택된 책들의 실제 용량 계산 (다운로드 vs 삭제)
            val currentState = _state.value
            val selectedBooks = currentState.filteredBooks.filter { it.storyId in currentState.selectedBookIds }
            
            val actualTotalSize = when (currentState.selectedManagementTab) {
                ManagementTab.DELETE -> {
                    // 삭제 탭: 실제 삭제될 파일 크기 계산
                    calculateTotalDeleteSize(selectedBooks, currentState.currentLanguage.code)
                }
                ManagementTab.DOWNLOAD, ManagementTab.UPDATE -> {
                    // 다운로드/업데이트 탭: 다운로드될 파일 크기 계산
                    calculateTotalDownloadSize(selectedBooks, currentState.currentLanguage.code)
                }
            }
            
            // 계산된 용량으로 업데이트
            _state.update { it.copy(totalSelectedSize = actualTotalSize) }
            
            val actionName = when (currentState.selectedManagementTab) {
                ManagementTab.DELETE -> "delete"
                ManagementTab.DOWNLOAD -> "download"
                ManagementTab.UPDATE -> "update"
            }
            
            Log.d("BookshelfViewModel", "Book selection toggled: $bookId -> $isSelected, actual $actionName size: ${actualTotalSize / 1024}KB")
        }
    }
    
    /**
     * 🆕 선택된 책들의 실제 다운로드 용량 계산
     */
    private suspend fun calculateTotalDownloadSize(
        selectedBooks: List<Book>,
        languageCode: String
    ): Long {
        return try {
            var totalSize = 0L
            
            selectedBooks.forEach { book ->
                val bookId = book.storyId.split("_")[0].toIntOrNull()
                if (bookId != null) {
                    val size = bookDownloader.calculateDownloadSize(bookId, languageCode)
                    totalSize += size
                    Log.d("BookshelfViewModel", "📊 Book ${book.title}: ${size / 1024}KB")
                }
            }
            
            Log.d("BookshelfViewModel", "📊 Total download size: ${totalSize / 1024}KB (${selectedBooks.size} books)")
            totalSize
            
        } catch (e: Exception) {
            Log.e("BookshelfViewModel", "Failed to calculate total download size", e)
            // 🆕 오류 시 기본 예상 크기 반환
            selectedBooks.size * 3L * 1024 * 1024 // 책당 3MB 예상
        }
    }
    
    /**
     * 🆕 선택된 책들의 실제 삭제 용량 계산
     */
    private suspend fun calculateTotalDeleteSize(
        selectedBooks: List<Book>, 
        languageCode: String
    ): Long {
        return try {
            var totalSize = 0L
            
            selectedBooks.forEach { book ->
                val bookId = book.storyId.split("_")[0].toIntOrNull()
                if (bookId != null) {
                    // 현재 상태의 전체 책 목록에서 이 책의 다른 버전이 있는지 확인
                    val versionCount = _state.value.books.count { it.storyId.startsWith("${bookId}_") }
                    
                    val size = bookDownloader.calculateDeleteSize(bookId, languageCode, versionCount)
                    totalSize += size
                    Log.d("BookshelfViewModel", "🗑️ Book ${book.title}: ${size / 1024}KB (delete), versions: $versionCount")
                }
            }
            
            Log.d("BookshelfViewModel", "🗑️ Total delete size: ${totalSize / 1024}KB (${selectedBooks.size} books)")
            totalSize
            
        } catch (e: Exception) {
            Log.e("BookshelfViewModel", "Failed to calculate total delete size", e)
            // 🆕 오류 시 기본 예상 크기 반환
            selectedBooks.size * 250L * 1024 // 책당 250KB 예상
        }
    }
    
    /**
     * 선택된 항목들 실행 (다운로드/업데이트/삭제)
     */
    private fun executeSelectedActions() {
        val selectedBooks = _state.value.filteredBooks.filter { 
            it.storyId in _state.value.selectedBookIds 
        }
        
        if (selectedBooks.isEmpty()) {
            Log.w("BookshelfViewModel", "No books selected")
            return
        }
        
        // 선택된 책들을 타입별로 분류
        val downloadBooks = selectedBooks.filter { !it.isDownloaded }
        val updateBooks = selectedBooks.filter { 
            it.storyId in _state.value.updatableBooks.map { b -> b.storyId } 
        }
        val deleteBooks = selectedBooks.filter { 
            it.isDownloaded && it.storyId !in _state.value.updatableBooks.map { b -> b.storyId }
        }
        
        // 확인 팝업 표시 (작업 타입 저장)
        _state.update { 
            it.copy(
                showConfirmationPopup = true,
                pendingActionType = when {
                    downloadBooks.isNotEmpty() -> ManagementActionType.DOWNLOAD
                    updateBooks.isNotEmpty() -> ManagementActionType.UPDATE
                    deleteBooks.isNotEmpty() -> ManagementActionType.DELETE
                    else -> null
                }
            )
        }
        
        Log.d("BookshelfViewModel", "Execute actions: download=${downloadBooks.size}, update=${updateBooks.size}, delete=${deleteBooks.size}")
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
     * 실제 다운로드/업데이트/삭제 실행
     */
    private fun performSelectedActions() {
        viewModelScope.launch {
            try {
                val selectedBooks = _state.value.filteredBooks.filter { 
                    it.storyId in _state.value.selectedBookIds 
                }
                
                if (selectedBooks.isEmpty()) {
                    Log.w("BookshelfViewModel", "No books selected for action")
                    return@launch
                }
                
                val currentLanguage = _state.value.currentLanguage.code
                val actionType = _state.value.pendingActionType
                
                Log.d("BookshelfViewModel", "🛠️ Performing action: $actionType for ${selectedBooks.size} books")
                Log.d("BookshelfViewModel", "📚 Selected book IDs: ${selectedBooks.map { it.storyId }}")
                
                // 🆕 다운로드 진행 상태 시작
                _state.update { 
                    it.copy(
                        isDownloading = true,
                        downloadingBookIds = selectedBooks.map { book -> book.storyId }.toSet()
                    )
                }
                
                when (actionType) {
                    ManagementActionType.DOWNLOAD -> {
                        // 다운로드 실행
                        executeBookDownloadUseCase(
                            books = selectedBooks,
                            languageCode = currentLanguage,
                            isUpdate = false // 새로운 다운로드
                        ).onSuccess {
                            Log.d("BookshelfViewModel", "✅ Download completed for ${selectedBooks.size} books")
                            showActionCompletionDialog(ManagementActionType.DOWNLOAD, selectedBooks.size)
                            resetSelection()
                        }.onFailure { error ->
                            handleActionError("다운로드", error)
                        }
                    }
                    
                    ManagementActionType.UPDATE -> {
                        // 업데이트 실행 (강제 업데이트 모드)
                        executeBookDownloadUseCase(
                            books = selectedBooks,
                            languageCode = currentLanguage,
                            isUpdate = true // 🔄 업데이트 모드
                        ).onSuccess {
                            Log.d("BookshelfViewModel", "✅ Update completed for ${selectedBooks.size} books")
                            showActionCompletionDialog(ManagementActionType.UPDATE, selectedBooks.size)
                            resetSelection()
                        }.onFailure { error ->
                            handleActionError("업데이트", error)
                        }
                    }
                    
                    ManagementActionType.DELETE -> {
                        // 삭제 실행
                        Log.d("BookshelfViewModel", "🗑️ Starting delete for: ${selectedBooks.map { it.storyId }}")
                        
                        deleteBookUseCase(
                            books = selectedBooks,
                            languageCode = currentLanguage
                        ).onSuccess {
                            Log.d("BookshelfViewModel", "🗑️ Delete completed for ${selectedBooks.size} books")
                            showActionCompletionDialog(ManagementActionType.DELETE, selectedBooks.size)
                            resetSelection()
                        }.onFailure { error ->
                            Log.e("BookshelfViewModel", "❌ Delete failed", error)
                            handleActionError("삭제", error)
                        }
                    }
                    
                    null -> {
                        Log.w("BookshelfViewModel", "⚠️ No action type specified")
                        // 🆕 작업 타입이 없으면 로딩 상태 즉시 해제
                        _state.update { 
                            it.copy(
                                isDownloading = false,
                                downloadingBookIds = emptySet()
                            )
                        }
                    }
                }
                
            } catch (e: Exception) {
                Log.e("BookshelfViewModel", "❌ Exception in performSelectedActions", e)
                // 🆕 예외 발생 시 로딩 상태 해제
                _state.update {
                    it.copy(
                        isDownloading = false,
                        downloadingBookIds = emptySet(),
                        error = "오류가 발생했습니다: ${e.message}"
                    )
                }
            }
        }
    }
    
    /**
     * 선택 초기화 및 책 목록 새로고침
     */
    private suspend fun resetSelection() {
        // 🆕 다운로드 진행 상태 해제
        _state.update {
            it.copy(
                isDownloading = false,
                downloadingBookIds = emptySet(),
                selectedBookIds = emptySet(),
                totalSelectedSize = 0L,
                pendingActionType = null
            )
        }

        // 1. DB에서 최신 책 목록을 다시 로드 (Source of Truth)
        val freshBooksResult = getAllBooksUseCase(_state.value.currentLanguage.code)
        if (freshBooksResult.isSuccess) {
            val freshBooks = freshBooksResult.getOrThrow()
            // 2. ViewModel의 기본 책 목록(books)을 최신 상태로 업데이트
            _state.update { it.copy(books = freshBooks) }

            // 3. 현재 활성화된 탭에 따라 UI 새로고침
        when (_state.value.selectedManagementTab) {
            ManagementTab.DOWNLOAD -> loadDownloadableBooks(freshBooks)
            ManagementTab.UPDATE -> loadUpdatableBooks(freshBooks)
            ManagementTab.DELETE -> loadDeletableBooks(freshBooks)
        }
        } else {
            handleActionError("책 목록 새로고침", freshBooksResult.exceptionOrNull() ?: Exception("Unknown error"))
        }

        Log.d("BookshelfViewModel", "✅ Selection reset and current management tab refreshed")
    }
    
    /**
     * 작업 오류 처리
     */
    private fun handleActionError(actionName: String, error: Throwable) {
        Log.e("BookshelfViewModel", "❌ Error in $actionName", error)
        _state.update {
            it.copy(
                isDownloading = false, // 🆕 오류 시 다운로드 상태 해제
                downloadingBookIds = emptySet(), // 🆕 다운로드 중인 책 ID 목록 초기화
                error = "${actionName} 작업을 수행할 수 없습니다: ${error.message}"
            )
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
    
    /**
     * 동화 정보 로드 및 팝업 표시
     */
    private fun loadStoryInfo(storyId: String) {
        viewModelScope.launch {
            try {
                _state.update { it.copy(isLoadingStoryInfo = true, showStoryInfoDialog = true) }
                
                val currentLanguage = _state.value.currentLanguage.code
                Log.d("BookshelfViewModel", "🔍 Loading story info for: $storyId, language: $currentLanguage")
                
                // 🔍 현재 책 목록에서 해당 책 찾기
                val book = _state.value.books.find { it.storyId == storyId }
                if (book != null) {
                    Log.d("BookshelfViewModel", "📚 Found book in current list: ${book.title} (isDownloaded: ${book.isDownloaded})")
                } else {
                    Log.w("BookshelfViewModel", "⚠️ Book not found in current list: $storyId")
                    Log.d("BookshelfViewModel", "📚 Available books: ${_state.value.books.map { it.storyId }}")
                }
                
                // 🔍 filteredBooks에서도 확인
                val filteredBook = _state.value.filteredBooks.find { it.storyId == storyId }
                if (filteredBook != null) {
                    Log.d("BookshelfViewModel", "📚 Found book in filtered list: ${filteredBook.title}")
                } else {
                    Log.w("BookshelfViewModel", "⚠️ Book not found in filtered list: $storyId")
                    Log.d("BookshelfViewModel", "📚 Filtered books: ${_state.value.filteredBooks.map { it.storyId }}")
                }
                
                val result = getStoryInfoUseCase(storyId, currentLanguage)
                
                result.fold(
                    onSuccess = { storyInfo ->
                        Log.d("BookshelfViewModel", "✅ Story info loaded successfully")
                        Log.d("BookshelfViewModel", "📖 StoryInfo - ID: ${storyInfo.storyId}, Summary: ${storyInfo.summary?.take(100)}...")
                        _state.update { 
                            it.copy(
                                currentStoryInfo = storyInfo,
                                isLoadingStoryInfo = false
                            )
                        }
                    },
                    onFailure = { error ->
                        Log.e("BookshelfViewModel", "❌ Error loading story info for $storyId", error)
                        _state.update {
                            it.copy(
                                isLoadingStoryInfo = false,
                                error = "동화 정보를 불러올 수 없습니다: ${error.message}",
                                showStoryInfoDialog = false
                            )
                        }
                    }
                )
                
            } catch (e: Exception) {
                Log.e("BookshelfViewModel", "❌ Exception in loadStoryInfo for $storyId", e)
                _state.update {
                    it.copy(
                        isLoadingStoryInfo = false,
                        error = "오류가 발생했습니다: ${e.message}",
                        showStoryInfoDialog = false
                    )
                }
            }
        }
    }
    
    /**
     * 동화 정보 팝업 닫기
     */
    private fun dismissStoryInfoDialog() {
        _state.update {
            it.copy(
                showStoryInfoDialog = false,
                currentStoryInfo = null,
                isLoadingStoryInfo = false
            )
        }
    }
    
    /**
     * 작업 완료 다이얼로그 닫기
     */
    private fun dismissActionCompletionDialog() {
        _state.update {
            it.copy(
                showActionCompletionDialog = false,
                completionActionType = null,
                completedItemsCount = 0
            )
        }
    }
    
    /**
     * 작업 완료 다이얼로그 표시
     */
    private fun showActionCompletionDialog(actionType: ManagementActionType, completedCount: Int) {
        _state.update {
            it.copy(
                showActionCompletionDialog = true,
                completionActionType = actionType,
                completedItemsCount = completedCount
            )
        }
    }
    
    /**
     * Unlock 팝업 닫기
     */
    private fun dismissUnlockPopup() {
        _state.update {
            it.copy(
                showUnlockPopup = false,
                unlockedLevelGroup = null
            )
        }
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
