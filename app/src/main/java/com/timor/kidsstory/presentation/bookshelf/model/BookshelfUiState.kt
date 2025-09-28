package com.timor.kidsstory.presentation.bookshelf.model

import com.timor.kidsstory.domain.model.Book
import com.timor.kidsstory.domain.model.Language
import com.timor.kidsstory.domain.model.StoryInfo
import com.timor.kidsstory.domain.util.LanguageConstants
import com.timor.kidsstory.data.remote.model.RemoteBook
import com.timor.kidsstory.presentation.bookshelf.components.ReadingStatusFilter
import com.timor.kidsstory.domain.model.AppVersionInfo

/**
 * 책장 화면 UI 상태 클래스
 */
data class BookshelfUiState(
    val books: List<Book> = emptyList(),
    val filteredBooks: List<Book> = emptyList(),
    val remoteBooks: List<RemoteBook> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentLanguage: Language = LanguageConstants.DEFAULT_LANGUAGE,
    val filterBarState: FilterBarState = FilterBarState(),
    val showLanguageDialog: Boolean = false,
    val isMusicOn: Boolean = false,
    val selectedReadingStatus: ReadingStatusFilter? = null,
    
    // 앱 버전 관련
    val showUpdateDialog: Boolean = false,
    val appVersionInfo: AppVersionInfo? = null,
    
    // 레벨 테스트 결과 팝업 관련
    val showLevelResultPopup: Boolean = false,
    val levelResultPopupMessage: LocalizedMessage? = null,
    
    // 🆕 관리 모드 관련 상태
    val isManagementMode: Boolean = false,
    val selectedManagementTab: ManagementTab = ManagementTab.DOWNLOAD,
    val selectedBookIds: Set<String> = emptySet(),
    val totalSelectedSize: Long = 0L,
    val showConfirmationPopup: Boolean = false,
    val pendingActionType: ManagementActionType? = null,
    
    // 다운로드/업데이트 가능 항목
    val downloadableBooks: List<Book> = emptyList(),
    val updatableBooks: List<Book> = emptyList(),
    val downloadableItemsCount: Int = 0,
    val isCheckingUpdates: Boolean = false,
    val hasCheckedUpdates: Boolean = false,
    
    // 🆕 다운로드 진행 상태
    val isDownloading: Boolean = false,
    val downloadingBookIds: Set<String> = emptySet(),
    
    // Unlock 정책 관련
    val unlockedSteps: Map<String, Int> = emptyMap(),
    val showLockedBookPopup: Boolean = false,
    
    // 🆕 Unlock 축하 팝업 관련
    val showUnlockPopup: Boolean = false,
    val unlockedLevelGroup: String? = null,
    
    // 🆕 작업 완료 다이얼로그 관련
    val showActionCompletionDialog: Boolean = false,
    val completionActionType: ManagementActionType? = null,
    val completedItemsCount: Int = 0,
    
    // 동화 정보 팝업 관련
    val showStoryInfoDialog: Boolean = false,
    val currentStoryInfo: StoryInfo? = null,
    val isLoadingStoryInfo: Boolean = false
)

/**
 * 다국어 메시지를 위한 데이터 클래스
 */
data class LocalizedMessage(
    val resId: Int,
    val formatArgs: Array<Any> = emptyArray()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LocalizedMessage

        if (resId != other.resId) return false
        if (!formatArgs.contentEquals(other.formatArgs)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = resId
        result = 31 * result + formatArgs.contentHashCode()
        return result
    }
}
