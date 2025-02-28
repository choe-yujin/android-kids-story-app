package com.timor.kidsstory.presentation.bookshelf.model

import com.timor.kidsstory.domain.model.Language
import com.timor.kidsstory.domain.util.LanguageConstants

// 책장 화면 UI 상태
data class BookshelfUiState(
    val books: List<BookCoverUiState> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentLanguage: Language = LanguageConstants.DEFAULT_LANGUAGE,
    val showLanguageDialog: Boolean = false
)