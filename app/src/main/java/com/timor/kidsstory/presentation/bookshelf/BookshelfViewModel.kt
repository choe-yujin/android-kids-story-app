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
import com.timor.kidsstory.data.remote.BookDownloader
import com.timor.kidsstory.data.remote.model.RemoteBook
import com.timor.kidsstory.data.remote.network.BookNetworkService
import com.timor.kidsstory.data.remote.worker.DownloadWorker
import com.timor.kidsstory.data.local.database.dao.DownloadedBooksDao
import com.timor.kidsstory.domain.model.Book
import com.timor.kidsstory.domain.model.DownloadStatus
import com.timor.kidsstory.domain.model.Language
import com.timor.kidsstory.domain.usecase.MusicSettingUseCase
import com.timor.kidsstory.domain.usecase.book.DownloadBookUseCase
import com.timor.kidsstory.domain.usecase.book.GetBooksUseCase
import com.timor.kidsstory.domain.usecase.book.GetRemoteBooksUseCase
import com.timor.kidsstory.domain.usecase.preference.GetUserPreferenceUseCase
import com.timor.kidsstory.domain.usecase.preference.SaveUserPreferenceUseCase
import com.timor.kidsstory.domain.util.LanguageConstants
import com.timor.kidsstory.domain.util.MusicManager
import com.timor.kidsstory.presentation.bookshelf.model.BookCoverUiState
import com.timor.kidsstory.presentation.bookshelf.model.BookshelfUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

/**
 * 책장 화면의 상태 관리 및 비즈니스 로직 처리 뷰모델
 * - 책 목록 로드 및 필터링
 * - 언어 설정 관리
 * - 배경 음악 제어
 * - 책 다운로드 관리
 *
 * @property getBooksUseCase 책 목록 가져오기 유스케이스
 * @property getUserPreferenceUseCase 사용자 설정 가져오기 유스케이스
 * @property saveUserPreferenceUseCase 사용자 설정 저장 유스케이스
 * @property musicSettingUseCase 음악 설정 유스케이스
 * @property musicManager 배경 음악 관리자
 * @property networkService 네트워크 서비스
 * @property getRemoteBooksUseCase 원격 책 가져오기 유스케이스
 * @property downloadBookUseCase 책 다운로드 유스케이스
 * @property bookDownloader 책 다운로더
 * @property downloadedBooksDao 다운로드된 책 DAO
 * @property context 애플리케이션 컨텍스트
 */
@HiltViewModel
class BookshelfViewModel @Inject constructor(
    private val getBooksUseCase: GetBooksUseCase,
    private val getUserPreferenceUseCase: GetUserPreferenceUseCase,
    private val saveUserPreferenceUseCase: SaveUserPreferenceUseCase,
    private val musicSettingUseCase: MusicSettingUseCase,
    private val musicManager: MusicManager,
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

    // 원격 책 목록 저장
    private var remoteBooks: List<RemoteBook> = emptyList()

    // 실제 Book 객체 저장 (UI 상태와 별도 관리)
    private var bookList = listOf<Book>()

    // JSON 직렬화
    private val json = Json { ignoreUnknownKeys = true }

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
     * 책 목록 로드 (로컬 + 원격)
     *
     * @param languageCode 언어 코드
     */
    private fun loadStories(languageCode: String) {
        viewModelScope.launch {
            try {
                _state.update { it.copy(isLoading = true, error = null) }

                // 로컬 책 로드
                val result = getBooksUseCase(languageCode)

                // 백그라운드에서 원격 책 로드
                launch {
                    loadRemoteBooks(languageCode)
                }

                result.fold(
                    onSuccess = { books ->
                        // 기존 책 처리 유지
                        bookList = books

                        // UI 상태 업데이트
                        _state.update {
                            it.copy(
                                books = books.map { book ->
                                    BookCoverUiState(
                                        imageUrl = book.coverImage,
                                        title = book.title,
                                        storyId = book.storyId,
                                        downloadStatus = DownloadStatus.DOWNLOADED
                                    )
                                },
                                isLoading = false
                            )
                        }
                    },
                    onFailure = { error ->
                        // 오류 처리 유지
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
                // 예외 처리 유지
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
     * 원격 서버에서 책 목록 로드
     * - GitHub에서 메타데이터 가져오기
     * - 로컬 DB와 비교하여 다운로드 가능한 책 필터링
     *
     * @param languageCode 언어 코드
     */
    private fun loadRemoteBooks(languageCode: String) {
        viewModelScope.launch {
            try {
                // 네트워크 연결 상태 확인
                if (!isNetworkAvailable()) {
                    Log.d("BookshelfViewModel", "No network connection available")
                    return@launch
                }

                // 원격 메타데이터 가져오기
                val result = getRemoteBooksUseCase(languageCode)
                result.fold(
                    onSuccess = { books ->
                        Log.d("BookshelfViewModel", "Loaded ${books.size} remote books")
                        // 원격 책 목록 저장
                        remoteBooks = books

                        // 다운로드 가능한 책들만 필터링
                        val downloadableBooks = filterDownloadableBooks(books)
                        Log.d("BookshelfViewModel", "Found ${downloadableBooks.size} downloadable books")

                        // UI 상태 업데이트 - 기존 책 목록에 다운로드 가능한 책 추가
                        if (downloadableBooks.isNotEmpty()) {
                            processBooksForUI(downloadableBooks, languageCode)
                        }
                    },
                    onFailure = { error ->
                        Log.e("BookshelfViewModel", "Error loading remote books", error)
                    }
                )
            } catch (e: Exception) {
                Log.e("BookshelfViewModel", "Exception loading remote books", e)
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

        viewModelScope.launch {
            downloadableBooks.forEach { remoteBook ->
                // 이미 리스트에 있는지 확인
                val existingIndex = currentBooks.indexOfFirst { it.remoteId == remoteBook.id }
                if (existingIndex == -1) {
                    // 책 표지 이미지 미리 다운로드
                    val langKey = when {
                        languageCode.startsWith("ko") -> "ko"
                        languageCode.startsWith("tet") -> "tet"
                        else -> "en"
                    }

                    val coverUrl = remoteBook.cover[langKey] ?: ""
                    val title = remoteBook.title[langKey] ?: "Book ${remoteBook.id}"

                    if (coverUrl.isNotEmpty()) {
                        try {
                            val localCoverPath = bookDownloader.preloadCoverImage(coverUrl)

                            if (localCoverPath != null) {
                                val newBookState = BookCoverUiState(
                                    imageUrl = localCoverPath,
                                    title = title,
                                    storyId = "${remoteBook.id}_$languageCode",
                                    downloadStatus = DownloadStatus.AVAILABLE,
                                    remoteId = remoteBook.id
                                )

                                currentBooks.add(newBookState)

                                // UI 갱신
                                _state.update { it.copy(books = currentBooks.toList()) }
                            }
                        } catch (e: Exception) {
                            Log.e("BookshelfViewModel", "Error preloading cover: $coverUrl", e)
                        }
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
     * @return 다운로드 가능한 책 목록
     */
    private suspend fun filterDownloadableBooks(remoteBooks: List<RemoteBook>): List<RemoteBook> {
        return remoteBooks.filter { remoteBook ->
            // 이미 다운로드된 책인지 확인
            val downloadedBooks = downloadedBooksDao.getDownloadedBooksByStoryId(remoteBook.id.toString())
            downloadedBooks.isEmpty()
        }
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
    fun onBookSelected(index: Int): Book? {
        if (index < 0 || index >= bookList.size) {
            Log.e("BookshelfViewModel", "Invalid book index: $index")
            return null
        }

        val selectedBook = bookList[index]
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
     * 책 다운로드 처리
     * - 선택한 책 다운로드 작업 시작
     * - WorkManager를 통한 백그라운드 다운로드
     *
     * @param index 다운로드할 책 인덱스
     */
    private fun downloadBook(index: Int) {
        val bookState = _state.value.books.getOrNull(index) ?: return

        // 이미 다운로드 중이거나 다운로드된 책은 처리하지 않음
        if (bookState.downloadStatus == DownloadStatus.DOWNLOADING ||
            bookState.downloadStatus == DownloadStatus.DOWNLOADED) {
            return
        }

        // 해당 책 찾기
        val remoteBook = remoteBooks.find { it.id == bookState.remoteId } ?: return

        // UI 상태 업데이트 - 다운로드 중으로 변경
        updateBookDownloadStatus(index, DownloadStatus.DOWNLOADING)

        // WorkManager를 사용하여 백그라운드에서 다운로드
        val languageCode = _state.value.currentLanguage.code

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
                when (workInfo.state) {
                    WorkInfo.State.SUCCEEDED -> {
                        Log.d("BookshelfViewModel", "Download completed successfully")
                        updateBookDownloadStatus(index, DownloadStatus.DOWNLOADED)
                    }
                    WorkInfo.State.FAILED -> {
                        Log.e("BookshelfViewModel", "Download failed")
                        updateBookDownloadStatus(index, DownloadStatus.FAILED)
                    }
                    WorkInfo.State.CANCELLED -> {
                        Log.d("BookshelfViewModel", "Download cancelled")
                        updateBookDownloadStatus(index, DownloadStatus.AVAILABLE)
                    }
                    else -> {
                        // 처리 중 상태는 무시
                    }
                }
            }
    }

    // 책 다운로드 상태 업데이트
    private fun updateBookDownloadStatus(index: Int, status: DownloadStatus) {
        if (index < 0 || index >= _state.value.books.size) return

        _state.update { currentState ->
            val updatedBooks = currentState.books.toMutableList()
            val updatedBook = updatedBooks[index].copy(downloadStatus = status)
            updatedBooks[index] = updatedBook
            currentState.copy(books = updatedBooks)
        }
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
            is BookShelfAction.DownloadBook -> TODO()
        }
    }
}