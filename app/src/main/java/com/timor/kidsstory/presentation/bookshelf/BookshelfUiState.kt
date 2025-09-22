package com.timor.kidsstory.presentation.bookshelf

import com.timor.kidsstory.domain.model.Book
import com.timor.kidsstory.domain.model.Language
import com.timor.kidsstory.presentation.bookshelf.components.ReadingStatusFilter
import com.timor.kidsstory.presentation.bookshelf.model.FilterBarState
import com.timor.kidsstory.presentation.bookshelf.model.ManagementTab
import com.timor.kidsstory.R

/**
 * 지역화된 메시지를 위한 데이터 클래스
 */
data class LocalizedMessage(val resId: Int, val formatArgs: Array<Any>)

/**
 * 책장 화면의 UI 상태
 */
data class BookshelfUiState(
    val books: List<Book> = emptyList(),
    val filteredBooks: List<Book> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val filterBarState: FilterBarState = FilterBarState(),
    val currentLanguage: Language = com.timor.kidsstory.domain.util.LanguageConstants.DEFAULT_LANGUAGE,
    val showLanguageDialog: Boolean = false,
    val isMusicOn: Boolean = false,
    val selectedReadingStatus: ReadingStatusFilter? = null,
    
    // 🆕 관리 모드 관련 상태
    val isManagementMode: Boolean = false,
    val selectedManagementTab: ManagementTab = ManagementTab.ALL,
    val selectedBookIds: Set<String> = emptySet(),
    val downloadableBooks: List<Book> = emptyList(),
    val updatableBooks: List<Book> = emptyList(),
    val isCheckingUpdates: Boolean = false,
    val showConfirmationPopup: Boolean = false,
    val hasCheckedUpdates: Boolean = false, // 🆕 업데이트 확인 여부 (한번 확인하면 뱃지 숨김)
    
    // 🆕 통계 정보
    val downloadableItemsCount: Int = 0,
    val totalSelectedSize: Long = 0L,

    val appVersionInfo: com.timor.kidsstory.domain.model.AppVersionInfo? = null,
    val showUpdateDialog: Boolean = false,
    val showLevelResultPopup: Boolean = false,
    val levelResultPopupMessage: LocalizedMessage? = null
)
