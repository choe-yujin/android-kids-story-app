package com.timor.kidsstory.domain.model

/**
 * 단순한 업데이트 정보
 * - 복잡한 우선순위나 전역 통계 제거
 * - 사용자가 직접 판단할 수 있는 최소 정보만 제공
 */
data class SimpleUpdateInfo(
    val storyId: String,
    val language: String,
    val bookTitle: String,
    val availableUpdates: List<UpdateComponent>
)

/**
 * 업데이트 가능한 컴포넌트
 */
data class UpdateComponent(
    val type: UpdateComponentType,
    val currentVersion: Int,
    val newVersion: Int,
    val size: Long, // bytes
    val description: String,
    val date: String // "1/27" 형식
)

/**
 * 업데이트 컴포넌트 타입
 */
enum class UpdateComponentType {
    CONTENT,    // 텍스트 콘텐츠
    COVER,      // 커버 이미지  
    IMAGES      // 페이지 이미지들
}

/**
 * 다운로드 관리 정보
 */
data class DownloadManagement(
    val newBooks: List<Book>,           // 아직 다운로드 안된 새로운 책들
    val updatableBooks: List<SimpleUpdateInfo>, // 업데이트 가능한 기존 책들
    val totalNewBooksSize: Long,        // 새로운 책들 전체 크기
    val hasNetworkConnection: Boolean   // 네트워크 연결 상태
)
