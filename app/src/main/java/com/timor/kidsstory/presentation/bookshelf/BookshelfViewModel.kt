package com.timor.kidsstory.presentation.bookshelf

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.orhanobut.logger.Logger
import com.timor.kidsstory.data.dto.StoriesResponse
import com.timor.kidsstory.data.remote.BookDownloader
import com.timor.kidsstory.data.remote.model.RemoteBook
import com.timor.kidsstory.data.remote.network.BookNetworkService
import com.timor.kidsstory.data.remote.worker.DownloadWorker
import com.timor.kidsstory.data.local.database.dao.DownloadedBooksDao
import com.timor.kidsstory.domain.model.Book
import com.timor.kidsstory.domain.model.DownloadStatus
import com.timor.kidsstory.domain.model.DownloadProgress
import com.timor.kidsstory.domain.model.Language
import com.timor.kidsstory.domain.usecase.MusicSettingUseCase
import com.timor.kidsstory.domain.usecase.book.DownloadBookUseCase
import com.timor.kidsstory.domain.usecase.book.GetBooksUseCase
import com.timor.kidsstory.domain.usecase.book.GetRemoteBooksUseCase
import com.timor.kidsstory.domain.usecase.preference.GetUserPreferenceUseCase
import com.timor.kidsstory.domain.usecase.preference.SaveUserPreferenceUseCase
import com.timor.kidsstory.domain.util.LanguageConstants
import com.timor.kidsstory.domain.util.LanguageManager
import com.timor.kidsstory.domain.util.MusicManager
import com.timor.kidsstory.domain.util.SoundEffectManager
import com.timor.kidsstory.presentation.bookshelf.model.BookshelfUiState
import com.timor.kidsstory.presentation.bookshelf.model.FilterBarCategory
import com.timor.kidsstory.presentation.bookshelf.model.FilterBarState
import com.timor.kidsstory.presentation.bookshelf.model.FilterBookCategory
import com.timor.kidsstory.presentation.bookshelf.model.FilterLevel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import javax.inject.Inject

/**
 * 책장 화면의 상태 관리 및 비즈니스 로직 처리 뷰모델
 * - 책 목록 로드 및 필터링
 * - 언어 설정 관리
 * - 배경 음악 제어
 * - 책 다운로드 관리
 */
@HiltViewModel
class BookshelfViewModel @Inject constructor(
    private val getBooksUseCase: GetBooksUseCase,
    private val getUserPreferenceUseCase: GetUserPreferenceUseCase,
    private val saveUserPreferenceUseCase: SaveUserPreferenceUseCase,
    private val musicSettingUseCase: MusicSettingUseCase,
    private val musicManager: MusicManager,
    private val soundEffectManager: SoundEffectManager,
    private val networkService: BookNetworkService,
    private val getRemoteBooksUseCase: GetRemoteBooksUseCase,
    private val downloadBookUseCase: DownloadBookUseCase,
    private val bookDownloader: BookDownloader,
    private val downloadedBooksDao: DownloadedBooksDao,
    @ApplicationContext private val context: Context
) : ViewModel() {

    // UI 상태 관리
    private val _state = MutableStateFlow(BookshelfUiState())
    val state = _state.asStateFlow()

    // 다운로드 진행률 작업 관리용 맵
    private val downloadProgressJobs = mutableMapOf<Int, Job>()

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
     * 언어 전환 전용 책 목록 로드 (로딩 상태 없이 자연스럽게 전환)
     */
    private suspend fun loadStoriesForLanguageSwitch(languageCode: String) {
        viewModelScope.launch {
            try {
                // 로딩 상태를 표시하지 않고 백그라운드에서 로드

                // 1. 로컬 asset 책 로드
                val result = getBooksUseCase(languageCode)

                // 2. 추가로 다운로드된 책도 함께 로드
                val downloadedBooks = loadDownloadedBooks(languageCode)

                result.fold(
                    onSuccess = { localBooks ->
                        // 로컬 책(assets에 있는 책)은 항상 다운로드된 상태로 설정
                        val localBooksWithDownloadStatus = localBooks.map { localBook ->
                            localBook.copy(
                                isDownloaded = true,
                                downloadProgress = DownloadProgress(DownloadStatus.DOWNLOADED, 1f)
                            )
                        }

                        // 로컬 책과 다운로드된 책을 합친 목록 생성 (중복 제거)
                        val localStoryIds =
                            localBooksWithDownloadStatus.map { it.storyId.split("_").first() }
                                .toSet()
                        val newDownloadedBooks = downloadedBooks.filter {
                            !localStoryIds.contains(it.storyId.split("_").first())
                        }

                        // 전체 책 목록 (로컬 + 다운로드)
                        val combinedBooks = localBooksWithDownloadStatus + newDownloadedBooks

                        // 현재 필터 상태 확인
                        val currentFilter = _state.value.filterBarState.selectedFilter
                        val shouldApplyFilters = currentFilter != FilterBarCategory.All

                        // UI 상태 업데이트 (로딩 상태 없이)
                        _state.update {
                            if (shouldApplyFilters) {
                                // 현재 필터 상태가 있으면 books만 업데이트
                                it.copy(books = combinedBooks)
                            } else {
                                // 필터 상태가 ALL이면 filteredBooks도 함께 업데이트
                                it.copy(books = combinedBooks, filteredBooks = combinedBooks)
                            }
                        }

                        // 필터링이 필요한 경우 applyFilters 호출
                        if (shouldApplyFilters) {
                            applyFilters()
                        }

                        Log.d(
                            "BookshelfViewModel",
                            "Language switch: Loaded total ${combinedBooks.size} books (${localBooksWithDownloadStatus.size} local + ${newDownloadedBooks.size} downloaded)"
                        )

                        // 백그라운드에서 원격 책 확인 (다운로드 가능한 새 책 확인)
                        checkAndLoadRemoteBooks(languageCode)
                    },
                    onFailure = { error ->
                        Log.e("BookshelfViewModel", "Error loading books for language switch", error)
                        // 에러 발생 시에도 로딩 상태를 표시하지 않음
                    }
                )
            } catch (e: Exception) {
                Log.e("BookshelfViewModel", "Exception in loadStoriesForLanguageSwitch", e)
            }
        }
    }

    /**
     * 뷰모델 종료 시 리소스 해제
     */
    override fun onCleared() {
        super.onCleared()
        musicManager.release()
        soundEffectManager.release()
        // 모든 진행률 작업 취소
        downloadProgressJobs.values.forEach { it.cancel() }
        downloadProgressJobs.clear()
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

            // LanguageManager에도 초기 언어 설정
            LanguageManager.setCurrentLanguageCode(language.code)

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
                    Log.d("BookshelfViewModel", "Language changed from ${_state.value.currentLanguage.code} to ${language.code}")

                    // LanguageManager에도 언어 설정
                    LanguageManager.setCurrentLanguageCode(language.code)

                    _state.update {
                        it.copy(currentLanguage = language)
                    }
                    loadStories(language.code)
                }
            }
        }
    }

    private suspend fun loadStories(languageCode: String) {
        viewModelScope.launch {
            try {
                _state.update { it.copy(isLoading = true, error = null) }

                // 1. 로컬 asset 책 로드
                val result = getBooksUseCase(languageCode)

                // 2. 추가로 다운로드된 책도 함께 로드
                val downloadedBooks = loadDownloadedBooks(languageCode)

                result.fold(
                    onSuccess = { localBooks ->
                        // 로컬 책(assets에 있는 책)은 항상 다운로드된 상태로 설정
                        val localBooksWithDownloadStatus = localBooks.map { localBook ->
                            localBook.copy(
                                isDownloaded = true,
                                downloadProgress = DownloadProgress(DownloadStatus.DOWNLOADED, 1f)
                            )
                        }

                        // 로컬 책과 다운로드된 책을 합친 목록 생성 (중복 제거)
                        val localStoryIds =
                            localBooksWithDownloadStatus.map { it.storyId.split("_").first() }
                                .toSet()
                        val newDownloadedBooks = downloadedBooks.filter {
                            !localStoryIds.contains(it.storyId.split("_").first())
                        }

                        // 전체 책 목록 (로컬 + 다운로드)
                        val combinedBooks = localBooksWithDownloadStatus + newDownloadedBooks

                        // 현재 필터 상태 확인
                        val currentFilter = _state.value.filterBarState.selectedFilter
                        val shouldApplyFilters = currentFilter != FilterBarCategory.All

                        // UI 상태 업데이트
                        _state.update {
                            if (shouldApplyFilters) {
                                // 현재 필터 상태가 있으면 books만 업데이트
                                it.copy(books = combinedBooks, isLoading = false)
                            } else {
                                // 필터 상태가 ALL이면 filteredBooks도 함께 업데이트
                                it.copy(books = combinedBooks, filteredBooks = combinedBooks, isLoading = false)
                            }
                        }

                        // 필터링이 필요한 경우 applyFilters 호출
                        if (shouldApplyFilters) {
                            applyFilters()
                        }

                        Log.d(
                            "BookshelfViewModel",
                            "Loaded total ${combinedBooks.size} books (${localBooksWithDownloadStatus.size} local + ${newDownloadedBooks.size} downloaded)"
                        )

                        // 백그라운드에서 원격 책 확인 (다운로드 가능한 새 책 확인)
                        checkAndLoadRemoteBooks(languageCode)
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

    /**
     * 다운로드한 책 목록 로드
     */
    private suspend fun loadDownloadedBooks(languageCode: String): List<Book> {
        return try {
            // Get downloaded books directly
            val downloadedEntities = downloadedBooksDao.getDownloadedBooksByLanguage(languageCode)

            // Convert entities to Book objects
            downloadedEntities.map { entity ->
                Book(
                    storyId = "${entity.id}_$languageCode",
                    title = entity.title,
                    coverImage = entity.coverImagePath,
                    level = entity.level,  // DB에서 level 정보 가져오기
                    category = entity.category,
                    pageCount = 0,
                    isDownloaded = true,
                    isBookmarked = false,
                    downloadProgress = DownloadProgress(DownloadStatus.DOWNLOADED, 1f)
                )
            }
        } catch (e: Exception) {
            Log.e("BookshelfViewModel", "Error loading downloaded books", e)
            emptyList()
        }
    }

    /**
     * 원격 책 로드 및 다운로드 상태 확인
     */
    private fun checkAndLoadRemoteBooks(languageCode: String) {
        viewModelScope.launch {
            try {
                // 원격 데이터 가져오기
                loadRemoteBooks(languageCode)

                // 모든 책의 다운로드 상태 확인
                verifyAllBooksDownloadStatus(languageCode)
            } catch (e: Exception) {
                Log.e("BookshelfViewModel", "Error in checking remote books", e)
            }
        }
    }

    /**
     * 모든 책의 다운로드 상태 검증
     */
    private suspend fun verifyAllBooksDownloadStatus(languageCode: String) {
        try {
            val currentBooks = _state.value.books.toMutableList()
            var hasChanges = false

            Log.d(
                "BookshelfViewModel",
                "Verifying download status for ${currentBooks.size} books in language: $languageCode"
            )

            // 로컬 책의 ID 목록을 가져옴 (로컬 assets에 포함된 책)
            val localAssetBookIds = try {
                val jsonString =
                    context.assets.open("metadata/stories-metadata.json").bufferedReader()
                        .use { it.readText() }
                val localResponse = Json.decodeFromString<StoriesResponse>(jsonString)
                val ids =
                    localResponse.stories.map { it.storyId.split("_").first().toInt() }.distinct()
                Log.d("BookshelfViewModel", "Local asset book IDs (unique): $ids")
                ids
            } catch (e: Exception) {
                Log.e("BookshelfViewModel", "Error loading local metadata", e)
                emptyList<Int>()
            }

            // 각 책의 다운로드 상태 확인
            for (i in currentBooks.indices) {
                val book = currentBooks[i]

                // 책 ID 추출
                val bookId = book.storyId.split("_").firstOrNull()?.toIntOrNull()
                if (bookId != null) {
                    // 현재 책의 스토리 ID와 현재 언어 코드가 일치하는지 확인
                    val bookLanguageCode = book.storyId.split("_").getOrElse(1) { languageCode }

                    // 현재 언어에 맞는 책이 아니면 건너뛰기
                    if (!book.storyId.endsWith("_$languageCode") && bookLanguageCode != languageCode) {
                        Log.d(
                            "BookshelfViewModel",
                            "Skipping book ${book.storyId} - wrong language (current: $languageCode)"
                        )
                        continue
                    }

                    // 로컬 asset 책인 경우 항상 다운로드된 상태로 설정
                    val isLocalAssetBook = localAssetBookIds.contains(bookId)
                    if (isLocalAssetBook) {
                        // 로컬 책은 항상 다운로드된 상태로 설정
                        if (!book.isDownloaded) {
                            Log.d(
                                "BookshelfViewModel",
                                "Setting local book ${book.storyId} as downloaded"
                            )
                            currentBooks[i] = book.copy(
                                isDownloaded = true,
                                downloadProgress = DownloadProgress(DownloadStatus.DOWNLOADED, 1f)
                            )
                            hasChanges = true
                        }
                        continue // 로컬 책은 더 이상 처리할 필요 없음
                    }

                    // 원격 책인 경우만 DB에서 다운로드 상태 확인
                    val downloadedBook = try {
                        downloadedBooksDao.getDownloadedBook(bookId, languageCode)
                    } catch (e: Exception) {
                        Log.e(
                            "BookshelfViewModel",
                            "Error checking download status for book $bookId",
                            e
                        )
                        null
                    }

                    val isDownloaded = downloadedBook != null

                    // 현재 상태와 다르면 업데이트
                    if (book.isDownloaded != isDownloaded) {
                        Log.d(
                            "BookshelfViewModel",
                            "Updating book ${book.storyId} status from ${if (book.isDownloaded) "DOWNLOADED" else "NOT_DOWNLOADED"} to ${if (isDownloaded) "DOWNLOADED" else "NOT_DOWNLOADED"}"
                        )
                        val downloadProgress = if (isDownloaded) {
                            DownloadProgress(DownloadStatus.DOWNLOADED, 1f)
                        } else {
                            DownloadProgress(DownloadStatus.AVAILABLE, 0f)
                        }
                        currentBooks[i] = book.copy(
                            isDownloaded = isDownloaded,
                            downloadProgress = downloadProgress
                        )
                        hasChanges = true
                    }
                }
            }

            // 변경사항이 있으면 UI 업데이트
            if (hasChanges) {
                Log.d("BookshelfViewModel", "Updated download status for books")

                // 현재 필터 상태 확인
                val selectedFilter = _state.value.filterBarState.selectedFilter
                val shouldApplyFilters = selectedFilter != FilterBarCategory.All

                // 필터 상태에 따라 업데이트 방식 결정
                if (shouldApplyFilters) {
                    // 필터링된 상태면 필터 적용을 위해 books 업데이트 후 필터 적용
                    _state.update { it.copy(books = currentBooks) }
                    applyFilters()
                } else {
                    // All 상태면 books와 filteredBooks 모두 업데이트
                    _state.update { it.copy(books = currentBooks, filteredBooks = currentBooks) }
                }
            } else {
                Log.d("BookshelfViewModel", "No books status changes detected")
            }
        } catch (e: Exception) {
            Log.e("BookshelfViewModel", "Error verifying book download status", e)
        }
    }

    /**
     * 원격 서버에서 책 목록 로드
     * - GitHub에서 메타데이터 가져오기
     * - 로컬 DB와 비교하여 다운로드 가능한 책 필터링
     *
     * @param languageCode 언어 코드
     */
    private suspend fun loadRemoteBooks(languageCode: String) {
        viewModelScope.launch {
            try {
                // 원격 데이터 가져오기
                val remoteResult = getRemoteBooksUseCase(languageCode)
                remoteResult.fold(
                    onSuccess = { remoteBooksResult ->
                        // 원격 책 목록 저장
                        _state.update { currentState ->
                            currentState.copy(remoteBooks = remoteBooksResult)
                        }

                        // 다운로드 가능한 책 필터링 및 처리
                        val downloadableBooks =
                            filterDownloadableBooks(remoteBooksResult, languageCode)

                        // UI에 다운로드 가능 책 표시
                        if (downloadableBooks.isNotEmpty()) {
                            processBooksForUI(downloadableBooks, languageCode)
                        }
                    },
                    onFailure = { error ->
                        Log.e("BookshelfViewModel", "원격 책 데이터 로드 실패", error)
                    }
                )
            } catch (e: Exception) {
                Log.e("BookshelfViewModel", "원격 책 로딩 중 예외 발생", e)
            }
        }
    }

    /**
     * 다운로드 가능한 책을 UI에 표시하기 위해 처리
     * - 책 표지 이미지 다운로드 및 UI 상태 업데이트
     *
     * @param downloadableBooks 다운로드 가능한 책 목록
     * @param languageCode 언어 코드
     */
    private fun processBooksForUI(downloadableBooks: List<RemoteBook>, languageCode: String) {
        val currentBooks = _state.value.books.toMutableList()

        Log.d("BookshelfViewModel", "Processing ${downloadableBooks.size} remote books for UI")

        // 로컬 책의 ID 목록을 가져옴 (로컬 assets에 포함된 책)
        val localAssetBookIds = try {
            val jsonString = context.assets.open("metadata/stories-metadata.json").bufferedReader()
                .use { it.readText() }
            val localResponse = Json.decodeFromString<StoriesResponse>(jsonString)
            val ids = localResponse.stories.map { it.storyId.split("_").first().toInt() }.distinct()
            Log.d("BookshelfViewModel", "Local asset book IDs for UI: $ids")
            ids
        } catch (e: Exception) {
            Log.e("BookshelfViewModel", "Error loading local metadata for UI", e)
            emptyList<Int>()
        }

        viewModelScope.launch {
            downloadableBooks.forEach { remoteBook ->
                // 로컬 책인지 확인 (로컬 책은 항상 다운로드된 상태)
                val isLocalAssetBook = localAssetBookIds.contains(remoteBook.id)

                // 로컬 책이면 처리하지 않음 (이미 로컬에 있음)
                if (isLocalAssetBook) {
                    return@forEach
                }

                // 이미 리스트에 있는지 확인 - 같은 ID와 언어인 경우만 체크
                val existingIndex = currentBooks.indexOfFirst {
                    val bookId = it.storyId.split("_").firstOrNull()?.toIntOrNull() ?: -1
                    bookId == remoteBook.id && it.storyId.endsWith("_$languageCode")
                }
                if (existingIndex == -1) {
                    // 책 표지 이미지 미리 다운로드
                    val langKey = when {
                        languageCode.startsWith("ko") -> "ko"
                        languageCode.startsWith("tet") -> "tet"
                        else -> "en"
                    }

                    val coverUrl = remoteBook.cover[langKey] ?: ""
                    val title = remoteBook.title[langKey] ?: "Book ${remoteBook.id}"
                    val category = remoteBook.category

                    Log.d("BookshelfViewModel", "Processing book ${remoteBook.id}: $title, cover URL: $coverUrl")

                    if (coverUrl.isNotEmpty()) {
                        try {
                            Log.d("BookshelfViewModel", "Attempting to download cover image from: $coverUrl")
                            val localCoverPath = bookDownloader.preloadCoverImage(coverUrl)
                            Log.d("BookshelfViewModel", "Cover download result: $localCoverPath")

                            if (localCoverPath != null) {
                                // Book 객체 생성 (원격 책은 처음에 isDownloaded = false)
                                val newBook = Book(
                                    storyId = "${remoteBook.id}_$languageCode",
                                    title = title,
                                    coverImage = localCoverPath,
                                    level = remoteBook.level,  // 원격 데이터에서 level 정보 가져오기
                                    category = category,
                                    pageCount = 0,
                                    isDownloaded = false,  // 원격 책은 다운로드 필요
                                    isBookmarked = false,
                                    downloadProgress = DownloadProgress(DownloadStatus.AVAILABLE, 0f)
                                )

                                currentBooks.add(newBook)
                                Log.d(
                                    "BookshelfViewModel",
                                    "Added new remote book to UI: $title (id: ${remoteBook.id})"
                                )

                                // UI 갱신 - books와 filteredBooks 모두 업데이트
                                val updatedBooks = currentBooks.toList()
                                val selectedFilter = _state.value.filterBarState.selectedFilter
                                val shouldApplyFilters = selectedFilter != FilterBarCategory.All

                                _state.update {
                                    if (shouldApplyFilters) {
                                        // 현재 필터가 All이 아니면 필터 적용
                                        it.copy(books = updatedBooks)
                                    } else {
                                        // All 필터면 filteredBooks도 함께 업데이트
                                        it.copy(books = updatedBooks, filteredBooks = updatedBooks)
                                    }
                                }

                                // 필터가 적용된 상태라면 applyFilters 호출하여 filteredBooks 업데이트
                                if (shouldApplyFilters) {
                                    applyFilters()
                                }
                            } else {
                                Log.e("BookshelfViewModel", "Failed to download cover image: localCoverPath is null")
                            }
                        } catch (e: Exception) {
                            Log.e("BookshelfViewModel", "Error preloading cover: $coverUrl", e)
                        }
                    } else {
                        Log.w("BookshelfViewModel", "Empty cover URL for book ${remoteBook.id}")
                    }
                }
            }
        }
    }

    /**
     * 다운로드할 수 있는 책만 필터링
     * - 이미 다운로드된 책은 제외
     *
     * @param remoteBooks 원격 책 목록
     * @param languageCode 언어 코드
     * @return 다운로드 가능한 책 목록
     */
    private suspend fun filterDownloadableBooks(remoteBooks: List<RemoteBook>, languageCode: String): List<RemoteBook> {
        // 로컬 assets에서 메타데이터 로드
        val localAssetBookIds = try {
            val jsonString = context.assets.open("metadata/stories-metadata.json").bufferedReader().use { it.readText() }
            val localResponse = Json.decodeFromString<StoriesResponse>(jsonString)
            val ids = localResponse.stories.map { it.storyId.split("_").first().toInt() }.distinct()
            Log.d("BookshelfViewModel", "Local asset book IDs (unique): $ids")
            ids
        } catch (e: Exception) {
            Log.e("BookshelfViewModel", "Error loading local metadata", e)
            emptyList<Int>()
        }

        val currentLanguageCode = _state.value.currentLanguage.code
        Log.d("BookshelfViewModel", "Current language code: $currentLanguageCode")

        // 각 책의 다운로드 상태를 확인하고 필터링
        val downloadableBooks = remoteBooks.filter { remoteBook ->
            val isNotInLocalAssets = !localAssetBookIds.contains(remoteBook.id)

            // 언어 코드를 고려하여 정확한 다운로드 상태 확인
            val downloadedBookEntity =
                downloadedBooksDao.getDownloadedBook(remoteBook.id, currentLanguageCode)
            val isNotDownloaded = downloadedBookEntity == null

            Log.d(
                "BookshelfViewModel",
                "Book ${remoteBook.id}: isNotInLocalAssets=$isNotInLocalAssets, isNotDownloaded=$isNotDownloaded, language=$currentLanguageCode"
            )

            // 로컬 assets에 없고 아직 다운로드되지 않은 책만 필터
            isNotInLocalAssets && isNotDownloaded
        }

        Log.d(
            "BookshelfViewModel",
            "Found ${downloadableBooks.size} downloadable books for language: $currentLanguageCode"
        )
        return downloadableBooks
    }

    /**
     * 네트워크 연결 상태 확인
     *
     * @return 네트워크 연결 가능 여부
     */
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        } else {
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            activeNetworkInfo != null && activeNetworkInfo.isConnected
        }
    }

    /**
     * 선택된 책 정보 반환
     *
     * @param index 책 목록 인덱스
     * @return 선택된 책 객체 또는 null
     */
    fun onBookSelected(index: Int): Book {
        if (index < 0 || index >= _state.value.books.size) {
            Log.e("BookshelfViewModel", "Invalid book index: $index")
            return Book(
                storyId = "",
                title = "",
                coverImage = "",
                level = 0,
                category = "",
                pageCount = 0,
                isDownloaded = false,
                isBookmarked = false
            )
        }

        // 상태에서 직접 책 객체를 반환
        return _state.value.books[index]
    }

    /**
     * 책 리스트 필터링 UI 반영용
     */
    private fun onFilterOptionSelected(
        filter: FilterBarCategory? = null,
        stage: FilterLevel? = null,
        category: FilterBookCategory? = null
    ) {
        _state.update {
            val isNewFilterSelected = filter != null && filter != it.filterBarState.selectedFilter      // 대분류중 하나를 선택했는지와 기존에 선택된 필터와 다른 필터인지 확인
            val isStageSelected = filter == FilterBarCategory.STAGE
            val isCategorySelected = filter == FilterBarCategory.CATEGORY

            // Stage 필터 클릭시 기본사항
            val updatedStage = when {
                // Stage 카테고리를 처음 전환시 기본값 One
                isStageSelected && isNewFilterSelected -> FilterLevel.ONE
                stage != null -> stage
                else -> it.filterBarState.selectedStage
            }

            // Category 필터 기본 사항 적용
            val updatedCategory = when {
                isCategorySelected && isNewFilterSelected -> FilterBookCategory.LEGEND
                category != null -> category
                else -> it.filterBarState.selectedCategory
            }

            it.copy(
                filterBarState = it.filterBarState.copy(
                    selectedFilter = filter ?: it.filterBarState.selectedFilter,
                    selectedStage = updatedStage,
                    selectedCategory = updatedCategory,
                    isStageFilterExpanded = when {
                        isNewFilterSelected -> isStageSelected  // 새로운 대분류 선택시 필터 닫기
                        stage != null -> true // 하위 필터일 경우 유지
                        else -> it.filterBarState.isStageFilterExpanded
                    },
                    isCategoryFilterExpanded = when {
                        isNewFilterSelected -> isCategorySelected // 위와 동일
                        category != null -> true // 위와 동일
                        else -> it.filterBarState.isCategoryFilterExpanded
                    }
                )
            )
        }
        applyFilters()
    }

    /**
     * 책 리스트 필터링 실제 리스트 반영
     */
    private fun applyFilters() {
        val selectedFilter = _state.value.filterBarState.selectedFilter
        val selectedStage = _state.value.filterBarState.selectedStage
        val selectedCategory = _state.value.filterBarState.selectedCategory

        val filteredList = _state.value.books.filter { book ->
            when (selectedFilter) {
                FilterBarCategory.STAGE -> selectedStage?.let { book.level == (it.ordinal + 1) }
                    ?: true

                FilterBarCategory.CATEGORY -> selectedCategory?.let {
                    // 대소문자 무시하고 비교 또는 displayName도 함께 확인
                    book.category.equals(it.name, ignoreCase = true) ||
                            book.category.equals(it.displayName, ignoreCase = true)
                } ?: true

                else -> true // ALL일 경우 필터 적용 없음
            }
        }

        _state.update { it.copy(filteredBooks = filteredList) }
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
            // 선택한 언어를 저장
            viewModelScope.launch {
                try {
                    // 다이얼로그만 닫고 기존 책 목록은 유지 (블랙아웃 방지)
                    _state.update {
                        it.copy(
                            showLanguageDialog = false,
                            currentLanguage = language
                        )
                    }

                    saveUserPreferenceUseCase.updateLanguage(language.code)

                    // LanguageManager에 언어 코드 설정
                    LanguageManager.setCurrentLanguageCode(language.code)

                    Logger.e("현재 언어 코드: ${LanguageManager.getCurrentLanguageCode()}")

                    // 백그라운드에서 새 언어의 책 로드 (기존 UI는 유지)
                    // 잠시 지연 후 새로 로드 (DB 업데이트 되도록)
                    kotlinx.coroutines.delay(100)

                    // 현재 선택된 언어로 다운로드된 모든 책 ID 출력
                    val downloadedIds =
                        downloadedBooksDao.getDownloadedBooksByLanguage(language.code)
                    Log.d(
                        "BookshelfViewModel", "====== 언어 변경 후 ${language.code}로 다운로드된 책 ID: " +
                                "${
                                    downloadedIds.map { it.id }.toSet()
                                }, 수: ${downloadedIds.size} ======"
                    )

                    // 새 언어로 책 목록 로드 (로딩 상태 없이)
                    loadStoriesForLanguageSwitch(language.code)
                } catch (e: Exception) {
                    Log.e("BookshelfViewModel", "언어 변경 중 오류 발생", e)
                    _state.update { it.copy(isLoading = false) }
                }
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
     * 책 다운로드 처리 - 90% 멈춤 문제 해결
     * - 선택한 책 다운로드 작업 시작
     * - WorkManager를 통한 백그라운드 다운로드
     * - 진행률 시뮬레이션과 실제 다운로드 완료 동기화
     *
     * @param index 다운로드할 책 인덱스
     */
    private fun downloadBook(index: Int) {
        val book = _state.value.books.getOrNull(index) ?: return
        val bookId = book.storyId.split("_").firstOrNull()?.toIntOrNull() ?: return
        val languageCode = _state.value.currentLanguage.code

        // 이미 다운로드 중이거나 다운로드된 책은 처리하지 않음
        if (book.isDownloaded || book.downloadProgress?.status == DownloadStatus.DOWNLOADING) {
            return
        }

        // 먼저 이미 다운로드되었는지 한 번 더 확인
        viewModelScope.launch {
            try {
                val downloadedBook = downloadedBooksDao.getDownloadedBook(bookId, languageCode)
                if (downloadedBook != null) {
                    // 이미 다운로드된 경우
                    Log.d(
                        "BookshelfViewModel",
                        "Book $bookId is already downloaded for language $languageCode"
                    )
                    updateBookDownloadStatus(index, DownloadStatus.DOWNLOADED, 1f)
                    return@launch
                }
            } catch (e: Exception) {
                Log.e("BookshelfViewModel", "Error checking download status", e)
            }

            // 해당 책 찾기
            val remoteBook = _state.value.remoteBooks.find { it.id == bookId } ?: run {
                Log.e("BookshelfViewModel", "Remote book not found for ID: $bookId")
                updateBookDownloadStatus(index, DownloadStatus.FAILED, 0f)
                return@launch
            }

            // UI 상태 업데이트 - 다운로드 중으로 변경
            updateBookDownloadStatus(index, DownloadStatus.DOWNLOADING, 0f)

            // 기존 진행률 작업이 있다면 취소
            downloadProgressJobs[index]?.cancel()

            // 새로운 진행률 시뮬레이션 작업 시작
            val progressJob = viewModelScope.launch {
                try {
                    // 0%에서 85%까지 점진적으로 업데이트 (90%에서 멈추는 문제 방지)
                    for (progress in 5..85 step 5) {
                        delay(300) // 0.3초마다 업데이트

                        // 작업이 취소되었는지 확인
                        if (!isActive) break

                        // 실제 다운로드가 완료되었는지 확인
                        val checkDownloaded = downloadedBooksDao.getDownloadedBook(bookId, languageCode)
                        if (checkDownloaded != null) {
                            // 실제 다운로드 완료되면 즉시 100%로 설정
                            updateBookDownloadStatus(index, DownloadStatus.DOWNLOADED, 1f)
                            break
                        }

                        updateBookDownloadStatus(index, DownloadStatus.DOWNLOADING, progress / 100f)
                    }

                    // 85% 이후에는 실제 다운로드 완료를 기다림
                    while (isActive) {
                        delay(500)
                        val checkDownloaded = downloadedBooksDao.getDownloadedBook(bookId, languageCode)
                        if (checkDownloaded != null) {
                            updateBookDownloadStatus(index, DownloadStatus.DOWNLOADED, 1f)
                            break
                        }
                    }
                } catch (e: Exception) {
                    if (e.message?.contains("CancellationException") != true) {
                        Log.e("BookshelfViewModel", "Error in progress simulation", e)
                    }
                }
            }

            // 진행률 작업을 맵에 저장
            downloadProgressJobs[index] = progressJob

            // WorkManager를 사용하여 백그라운드에서 다운로드
            // Input 데이터 설정
            val inputData = Data.Builder()
                .putInt(DownloadWorker.KEY_BOOK_ID, remoteBook.id)
                .putString(DownloadWorker.KEY_LANGUAGE, languageCode)
                .putString(DownloadWorker.KEY_METADATA, Json.encodeToString(remoteBook))
                .build()

            // WorkManager 작업 설정
            val downloadRequest = OneTimeWorkRequestBuilder<DownloadWorker>()
                .setInputData(inputData)
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .build()

            // 다운로드 작업 시작
            WorkManager.getInstance(context)
                .enqueueUniqueWork(
                    "download_book_${remoteBook.id}_$languageCode",
                    ExistingWorkPolicy.KEEP,
                    downloadRequest
                )

            // WorkManager 작업 결과 관찰
            WorkManager.getInstance(context)
                .getWorkInfoByIdLiveData(downloadRequest.id)
                .observeForever { workInfo ->
                    workInfo?.let { nonNullWorkInfo ->
                        when (nonNullWorkInfo.state) {
                            WorkInfo.State.SUCCEEDED -> {
                                Log.d(
                                    "BookshelfViewModel",
                                    "Download completed successfully for book $bookId with language $languageCode"
                                )

                                // 진행률 작업 취소 (실제 완료되었으므로)
                                downloadProgressJobs[index]?.cancel()
                                downloadProgressJobs.remove(index)

                                // 다운로드 상태 업데이트
                                updateBookDownloadStatus(index, DownloadStatus.DOWNLOADED, 1f)

                                // 다운로드 완료 후 다운로드 상태 재확인을 통해 UI 새로고침
                                viewModelScope.launch {
                                    try {
                                        val allDownloadedBookIds = downloadedBooksDao
                                            .getDownloadedBooksByLanguage(languageCode)
                                            .map { it.id }.toSet()

                                        Log.d(
                                            "BookshelfViewModel",
                                            "다운로드 완료 후 현재 언어($languageCode)로 다운로드된 책 수: ${allDownloadedBookIds.size}, " +
                                                    "책 ID 목록: $allDownloadedBookIds"
                                        )
                                    } catch (e: Exception) {
                                        Log.e("BookshelfViewModel", "다운로드 완료 후 상태 확인 실패", e)
                                    }
                                }
                            }
                            WorkInfo.State.FAILED -> {
                                Log.e(
                                    "BookshelfViewModel",
                                    "Download failed for book $bookId with language $languageCode"
                                )
                                // 진행률 작업 취소
                                downloadProgressJobs[index]?.cancel()
                                downloadProgressJobs.remove(index)

                                updateBookDownloadStatus(index, DownloadStatus.FAILED, 0f)
                            }
                            WorkInfo.State.CANCELLED -> {
                                Log.d(
                                    "BookshelfViewModel",
                                    "Download cancelled for book $bookId with language $languageCode"
                                )
                                // 진행률 작업 취소
                                downloadProgressJobs[index]?.cancel()
                                downloadProgressJobs.remove(index)

                                updateBookDownloadStatus(index, DownloadStatus.AVAILABLE, 0f)
                            }
                            else -> {
                                // 처리 중 상태는 무시
                            }
                        }
                    }
                }
        }
    }

    /**
     * 책 다운로드 상태 업데이트
     * - 다운로드 상태를 UI에 반영
     * - 다운로드 완료 시 책 데이터를 bookList에도 추가
     *
     * @param index 책 목록 인덱스
     * @param status 다운로드 상태
     * @param progress 다운로드 진행률 (0.0 ~ 1.0)
     */
    private fun updateBookDownloadStatus(index: Int, status: DownloadStatus, progress: Float = 0f) {
        if (index < 0 || index >= _state.value.books.size) return

        _state.update { currentState ->
            val updatedBooks = currentState.books.toMutableList()
            val bookToUpdate = updatedBooks[index]

            // Book 객체를 수정하여 다운로드 상태 업데이트
            val updatedBook = bookToUpdate.copy(
                isDownloaded = status == DownloadStatus.DOWNLOADED,
                downloadProgress = DownloadProgress(status, progress)
            )
            updatedBooks[index] = updatedBook

            // 로그로 상태 변경 기록
            Log.d(
                "BookshelfViewModel", "Book status updated: storyId=${bookToUpdate.storyId}, " +
                        "from=${if (bookToUpdate.isDownloaded) "DOWNLOADED" else "NOT_DOWNLOADED"}, " +
                        "to=${if (updatedBook.isDownloaded) "DOWNLOADED" else "NOT_DOWNLOADED"}, " +
                        "progress=${progress * 100}%, " +
                        "language=${_state.value.currentLanguage.code}"
            )

            // 현재 필터 상태 확인
            val selectedFilter = currentState.filterBarState.selectedFilter
            val shouldApplyFilters = selectedFilter != FilterBarCategory.All

            // 다운로드가 완료됐을 때, 새 책을 상태에 추가
            if (status == DownloadStatus.DOWNLOADED) {
                viewModelScope.launch {
                    val storyId = updatedBook.storyId
                    val bookId = storyId.split("_").firstOrNull()?.toIntOrNull()
                    val currentLanguage = _state.value.currentLanguage.code

                    // 다운로드된 책의 정보를 가져와 새 Book 객체 생성
                    val downloadedBookEntity = bookId?.let {
                        downloadedBooksDao.getDownloadedBook(it, currentLanguage)
                    }

                    if (downloadedBookEntity != null) {
                        val newBook = Book(
                            storyId = "${downloadedBookEntity.id}_$currentLanguage",
                            title = downloadedBookEntity.title,
                            coverImage = downloadedBookEntity.coverImagePath,
                            level = downloadedBookEntity.level,
                            category = downloadedBookEntity.category,
                            pageCount = 0,
                            isDownloaded = true,
                            isBookmarked = false,
                            downloadProgress = DownloadProgress(DownloadStatus.DOWNLOADED, 1f)
                        )

                        // 중복 방지
                        val existingIndex =
                            _state.value.books.indexOfFirst { it.storyId == newBook.storyId }
                        val newBookList = _state.value.books.toMutableList()

                        if (existingIndex >= 0) {
                            newBookList[existingIndex] = newBook
                        } else {
                            newBookList.add(newBook)
                        }

                        // books와 필요한 경우 filteredBooks 업데이트
                        if (shouldApplyFilters) {
                            _state.update { it.copy(books = newBookList) }
                            applyFilters()
                        } else {
                            _state.update {
                                it.copy(
                                    books = newBookList,
                                    filteredBooks = newBookList
                                )
                            }
                        }

                        Log.d("BookshelfViewModel", "Book added to UI: ${newBook.storyId}")
                    }
                }
            }

            // 업데이트된 상태 반환
            if (shouldApplyFilters) {
                // 필터가 적용된 상태면 books만 업데이트하고 applyFilters() 호출
                val result = currentState.copy(books = updatedBooks)
                // 비동기로 applyFilters() 호출
                viewModelScope.launch {
                    applyFilters()
                }
                result
            } else {
                // All 필터 상태면 books와 filteredBooks 모두 업데이트
                currentState.copy(books = updatedBooks, filteredBooks = updatedBooks)
            }
        }
    }

    /**
     * UI 액션 처리
     *
     * @param action 처리할 액션
     */
    fun onAction(action: BookShelfAction) {
        when (action) {
            is BookShelfAction.BookSelect -> {
                soundEffectManager.playButtonClick()
                onBookSelected(action.index)
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
                downloadBook(action.index)
            }
            is BookShelfAction.SelectFilter -> {
                soundEffectManager.playButtonClick()
                onFilterOptionSelected(
                    filter = action.filter,
                    stage = action.stage,
                    category = action.category,
                )
            }
        }
    }
}