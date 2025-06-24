package com.timor.kidsstory.presentation.book.model

/**
 * 페이지 텍스트 섹션의 UI 상태 클래스 (Clean Architecture 적용)
 * - 페이지 텍스트 영역의 스크롤 및 표시 상태 정보
 * - 모든 상태는 불변이며, ViewModel에서만 업데이트
 *
 * @property contentHeight 텍스트 콘텐츠의 전체 높이
 * @property containerHeight 텍스트 컨테이너의 높이
 * @property scrollOffset 현재 스크롤 위치
 * @property maxScrollOffset 최대 스크롤 가능 위치
 * @property canScrollUp 위로 스크롤 가능 여부 (계산된 값)
 * @property canScrollDown 아래로 스크롤 가능 여부 (계산된 값)
 */
data class PageTextSectionUiState(
    val contentHeight: Int = 0,
    val containerHeight: Int = 0,
    val scrollOffset: Int = 0,
    val maxScrollOffset: Int = 0,
    val canScrollUp: Boolean = false,
    val canScrollDown: Boolean = false
) {
    /**
     * 스크롤 가능 여부
     */
    val isScrollable: Boolean
        get() = contentHeight > containerHeight && containerHeight > 0
    
    companion object {
        /**
         * 새로운 레이아웃 정보로 상태 업데이트
         */
        fun PageTextSectionUiState.updateLayout(
            contentHeight: Int, 
            containerHeight: Int
        ): PageTextSectionUiState {
            // 스크롤 가능 여부 판단
            val isScrollable = contentHeight > containerHeight && containerHeight > 0
            val newMaxScrollOffset = if (isScrollable) {
                kotlin.math.max(0, contentHeight - containerHeight)
            } else {
                0
            }
            
            return copy(
                contentHeight = contentHeight,
                containerHeight = containerHeight,
                maxScrollOffset = newMaxScrollOffset,
                canScrollUp = isScrollable && scrollOffset > 0,
                canScrollDown = isScrollable && scrollOffset < newMaxScrollOffset
            )
        }
        
        /**
         * 새로운 스크롤 정보로 상태 업데이트
         */
        fun PageTextSectionUiState.updateScroll(
            scrollOffset: Int,
            maxScrollOffset: Int
        ): PageTextSectionUiState {
            val isScrollable = contentHeight > containerHeight && containerHeight > 0
            return copy(
                scrollOffset = scrollOffset,
                maxScrollOffset = maxScrollOffset,
                canScrollUp = isScrollable && scrollOffset > 0,
                canScrollDown = isScrollable && scrollOffset < maxScrollOffset
            )
        }
    }
}
