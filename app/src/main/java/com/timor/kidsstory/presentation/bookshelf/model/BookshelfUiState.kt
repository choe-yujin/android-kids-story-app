package com.timor.kidsstory.presentation.bookshelf.model

import com.timor.kidsstory.domain.model.Book
import com.timor.kidsstory.domain.model.Language
import com.timor.kidsstory.domain.util.LanguageConstants
import com.timor.kidsstory.presentation.bookshelf.components.filter.FilterState

/**
 * 책장 화면 UI 상태 클래스
 * - 책장 화면의 모든 상태 정보를 담는 불변 데이터 클래스
 * - UI 상태와 로직 분리를 위한 패턴
 *
 * @property books 표시할 책 목록
 * @property isLoading 로딩 중 상태
 * @property error 오류 메시지 (있을 경우)
 * @property currentLanguage 현재 선택된 언어
 * @property showLanguageDialog 언어 선택 다이얼로그 표시 여부
 * @property isMusicOn 배경음악 재생 여부
 */
data class BookshelfUiState(
    // val books: List<BookCoverUiState> = emptyList(),
    val books: List<Book> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentLanguage: Language = LanguageConstants.DEFAULT_LANGUAGE,
    val filterBarState: FilterBarState = FilterBarState(),
    val showLanguageDialog: Boolean = false,
    val isMusicOn: Boolean = false,
)