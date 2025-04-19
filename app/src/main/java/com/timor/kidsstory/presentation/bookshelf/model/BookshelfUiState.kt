package com.timor.kidsstory.presentation.bookshelf.model

import com.timor.kidsstory.domain.model.Book
import com.timor.kidsstory.domain.model.Language
import com.timor.kidsstory.domain.util.LanguageConstants
import com.timor.kidsstory.data.remote.model.RemoteBook

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
)