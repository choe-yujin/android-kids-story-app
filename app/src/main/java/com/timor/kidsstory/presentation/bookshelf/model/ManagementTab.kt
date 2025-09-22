package com.timor.kidsstory.presentation.bookshelf.model

/**
 * 관리 모드에서 사용되는 탭 종류
 */
enum class ManagementTab(val displayName: String) {
    /**
     * 전체 탭 - 모든 책 표시 (기본 FilterBar와 동일)
     */
    ALL("전체"),
    
    /**
     * 다운로드 탭 - 다운로드 가능한 새로운 책들
     */
    DOWNLOAD("다운로드"),
    
    /**
     * 업데이트 탭 - 업데이트 가능한 기존 책들
     */
    UPDATE("업데이트")
}
