package com.timor.kidsstory.presentation.bookshelf

import com.timor.kidsstory.domain.model.Book
import com.timor.kidsstory.domain.model.Language
import com.timor.kidsstory.presentation.bookshelf.components.ReadingStatusFilter
import com.timor.kidsstory.presentation.bookshelf.model.FilterBarState

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
    val selectedReadingStatus: ReadingStatusFilter? = null
)
