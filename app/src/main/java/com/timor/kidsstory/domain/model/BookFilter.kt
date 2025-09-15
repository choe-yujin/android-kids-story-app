package com.timor.kidsstory.domain.model

import com.timor.kidsstory.presentation.bookshelf.model.FilterBookCategory
import com.timor.kidsstory.presentation.bookshelf.model.FilterLevel

/**
 * 책 필터링을 위한 Domain Model
 */
data class BookFilter(
    val type: FilterType,
    val level: FilterLevel? = null,
    val category: FilterBookCategory? = null
)

/**
 * 필터 타입 enum
 */
enum class FilterType {
    ALL,        // 모든 책
    LEVEL,      // 레벨별 필터
    CATEGORY    // 카테고리별 필터
}
