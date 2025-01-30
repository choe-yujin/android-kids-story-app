package com.timor.kidsstory.domain.usecase

import com.timor.kidsstory.domain.model.NavigationDirection

// 페이지 네비게이션 유스케이스
class PageNavigationUseCase {
    operator fun invoke(currentPage: Int, totalPages: Int, direction: NavigationDirection): Int {
        return when (direction) {
            NavigationDirection.NEXT -> (currentPage + 1).coerceAtMost(totalPages - 1)
            NavigationDirection.PREVIOUS -> (currentPage - 1).coerceAtLeast(0)
        }
    }
}