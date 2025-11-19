package com.timor.kidsstory.domain.model



/**
 * 책의 기본 정보를 담는 도메인 모델 클래스
 *
 * @property storyId 언어별 책 식별자 (예: "801_ko-kr")
 * @property title 책 제목
 * @property coverImage 표지 이미지 경로
 * @property level 난이도 레벨 (1-5)
 * @property category 카테고리 (문자열 - 기존 호환성)
 * @property pageCount 총 페이지 수
 * @property contributors 제작 참여자 정보
 * @property sponsors 후원사 정보
 * @property copyright 저작권 정보
 * @property originalCopyright 원 저작권 정보
 * @property pages 책의 모든 페이지 정보
 * @property missions 완독 시 표시할 미션 정보 리스트
 * @property isDownloaded 다운로드 여부
 * @property downloadProgress 다운로드 진행 상태
 * @property bookVersion 책 버전
 * 
 * // 사용자 상호작용 관련 필드들 (Repository에서 UserBookInteraction과 조인하여 설정)
 * @property isBookmarked 북마크 여부
 * @property currentPage 현재 읽고 있는 페이지
 * @property isCompleted 완독 여부
 * @property readCount 읽은 횟수
 * @property lastReadAt 마지막 읽은 날짜
 * @property unlockStep 잠금 해제 단계 (메타데이터에서 가져옴)
 * @property tags 태그 목록 (메타데이터에서 가져옴)
 *
 * @property level 난이도 레벨 (1-5)
 * @property category 카테고리 (문자열 - 기존 호환성)
 * @property pageCount 총 페이지 수
 * @property contributors 제작 참여자 정보
 * @property sponsors 후원사 정보
 * @property copyright 저작권 정보
 * @property originalCopyright 원 저작권 정보
 * @property pages 책의 모든 페이지 정보
 * @property isDownloaded 다운로드 여부
 * @property downloadProgress 다운로드 진행 상태
 * @property bookVersion 책 버전
 * 
 * // 사용자 상호작용 관련 필드들 (Repository에서 UserBookInteraction과 조인하여 설정)
 * @property isBookmarked 북마크 여부
 * @property currentPage 현재 읽고 있는 페이지
 * @property isCompleted 완독 여부
 * @property readCount 읽은 횟수
 * @property lastReadAt 마지막 읽은 날짜
 * @property unlockStep 잠금 해제 단계 (메타데이터에서 가져옴)
 * @property tags 태그 목록 (메타데이터에서 가져옴)
 */
data class Book(
    val storyId: String,
    val title: String,
    val coverImage: String,
    val level: Int,
    val category: String, // 기존 호환성 유지를 위해 String으로 유지
    val pageCount: Int,
    val contributors: List<Contributor>,
    val sponsors: List<String>? = null,
    val copyright: String,
    val originalCopyright: String? = null,
    val pages: List<Page>,
    val missions: List<Mission> = emptyList(), // 미션 정보 리스트 추가
    val isDownloaded: Boolean = true,
    val downloadProgress: DownloadProgress = DownloadProgress(),
    val bookVersion: Int = 1,
    val totalSize: Long = 0L, // 총 파일 크기 (바이트)
    
    // 사용자 상호작용 관련 필드들 (기본값 제공)
    val isBookmarked: Boolean = false,
    val currentPage: Int = 0,
    val isCompleted: Boolean = false,
    val readCount: Int = 0,
    val lastReadAt: Long? = null,
    val unlockStep: Int = 0, // 기본값 0: 즐시 사용 가능
    val tags: List<String> = emptyList() // 메타데이터에서 가져옴
) {
    // TODO: 필요한 경우 FilterBookCategory와 매핑하는 로직 추가 가능
    
    // 책 ID 추출 (숫자)
    val bookId: Int?
        get() = storyId.split("_").firstOrNull()?.toIntOrNull()
    
    // 언어 코드 추출 (단순화된 형태: ko, en, tet)
    val languageCode: String
        get() = storyId.split("_").getOrNull(1) ?: "ko"
    
    // 읽기 상태 계산
    val readingStatus: ReadingStatus
        get() = when {
            isCompleted -> ReadingStatus.COMPLETED
            currentPage > 0 -> ReadingStatus.READING  
            else -> ReadingStatus.UNREAD
        }
    
    // 읽기 진행률 계산 (0.0 ~ 1.0)
    val readingProgress: Float
        get() = if (pageCount > 0) currentPage.toFloat() / pageCount.toFloat() else 0f
    
    // 잠금 여부 계산 (Repository에서 잠금 해제 로직과 함께 사용)
    val isLocked: Boolean
        get() = false // 기본값, Repository에서 실제 잠금 로직 적용
}

/**
 * 책의 읽기 상태를 나타내는 enum
 */
enum class ReadingStatus {
    UNREAD,     // 안 읽음
    READING,    // 읽는 중
    COMPLETED   // 다 읽음
}
