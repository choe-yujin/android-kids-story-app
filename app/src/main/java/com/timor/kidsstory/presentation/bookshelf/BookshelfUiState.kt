package com.timor.kidsstory.presentation.bookshelf

import com.timor.kidsstory.domain.model.Book
import com.timor.kidsstory.domain.model.Category
import com.timor.kidsstory.domain.model.ReadingLevel

/**
 * 책장 화면의 UI 상태
 *
 * @property books 표시할 책 목록
 * @property filteredBooks 필터링된 책 목록
 * @property isLoading 로딩 중 여부
 * @property error 에러 메시지
 * @property selectedCategory 선택된 카테고리
 * @property selectedLevel 선택된 레벨
 * @property selectedTags 선택된 태그들
 * @property showCategoryFilter 카테고리 필터 표시 여부
 * @property showLevelFilter 레벨 필터 표시 여부
 */
data class BookshelfUiState(
    val books: List<Book> = emptyList(),
    val filteredBooks: List<Book> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedCategory: Category? = null,
    val selectedLevel: ReadingLevel? = null,
    val selectedTags: List<String> = emptyList(),
    val showCategoryFilter: Boolean = false,
    val showLevelFilter: Boolean = false,
    val currentLanguage: String = "ko"
)