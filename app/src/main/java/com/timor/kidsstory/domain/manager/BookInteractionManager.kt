package com.timor.kidsstory.domain.manager

import com.timor.kidsstory.domain.model.ReadingProgress
import com.timor.kidsstory.domain.repository.ReadingProgressRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 사용자-책 상호작용을 관리하는 Manager
 * - ReadingProgressRepository를 통한 읽기 진도 관리
 * - UI 상태 관리
 */
@Singleton
class BookInteractionManager @Inject constructor(
    private val readingProgressRepository: ReadingProgressRepository
) {
    companion object {
        private const val DEFAULT_USER_ID = "default_user"
    }

    /**
     * 특정 언어의 완독한 책 개수 조회
     * 
     * @param languageCode 언어 코드
     * @return 완독한 책 개수
     */
    suspend fun getCompletedBooksCount(languageCode: String): Int {
        return readingProgressRepository.getCompletedBooksCount(DEFAULT_USER_ID, languageCode)
    }

    /**
     * 사용자의 모든 완독한 책 개수 조회
     * 
     * @return 전체 완독한 책 개수
     */
    suspend fun getTotalCompletedBooksCount(): Int {
        // 모든 언어에 대한 완료된 책 수를 합산하려면
        // 지원하는 언어 목록이 필요함. 임시로 주요 언어들 확인
        val mainLanguages = listOf("ko-kr", "en-ph", "tetum")
        var totalCompleted = 0
        
        for (languageCode in mainLanguages) {
            totalCompleted += readingProgressRepository.getCompletedBooksCount(DEFAULT_USER_ID, languageCode)
        }
        
        return totalCompleted
    }

    /**
     * 특정 책의 읽기 진도 저장/업데이트
     * 
     * @param bookId 책 ID (storyId)
     * @param languageCode 언어 코드
     * @param currentPage 현재 페이지
     * @param totalPages 전체 페이지 수
     * @param isCompleted 완독 여부
     */
    suspend fun updateBookProgress(
        bookId: String, // String으로 변경 (storyId 전체 사용)
        languageCode: String,
        currentPage: Int,
        totalPages: Int,
        isCompleted: Boolean = false
    ) {
        val progress = ReadingProgress(
            currentPage = currentPage,
            totalPages = totalPages,
            isCompleted = isCompleted,
            lastReadAt = LocalDateTime.now(),
            completedAt = if (isCompleted) LocalDateTime.now() else null
        )
        
        readingProgressRepository.updateReadingProgress(DEFAULT_USER_ID, bookId, progress)
    }

    /**
     * 특정 책을 완독으로 표시
     * 
     * @param bookId 책 ID
     * @param languageCode 언어 코드
     * @param totalPages 전체 페이지 수
     */
    suspend fun markBookAsCompleted(
        bookId: String,
        languageCode: String,
        totalPages: Int
    ) {
        readingProgressRepository.markBookCompleted(DEFAULT_USER_ID, bookId)
    }

    /**
     * 특정 책의 읽기 진도 조회
     * 
     * @param bookId 책 ID
     * @param languageCode 언어 코드
     * @return 읽기 진도 정보 또는 null
     */
    suspend fun getBookProgress(
        bookId: String,
        languageCode: String
    ): ReadingProgress? {
        return readingProgressRepository.getReadingProgress(DEFAULT_USER_ID, bookId)
    }

    /**
     * 특정 언어의 진행 중인 책 목록 조회
     * 
     * @param languageCode 언어 코드
     * @return 진행 중인 책 목록의 진도 정보
     */
    suspend fun getInProgressBooks(languageCode: String): Map<String, ReadingProgress> {
        val allProgress = readingProgressRepository.getAllProgressByLanguage(DEFAULT_USER_ID, languageCode)
        return allProgress.filter { (_, progress) ->
            progress.currentPage > 0 && !progress.isCompleted
        }
    }

    /**
     * 최근 읽은 책 목록 조회
     * 
     * @param limit 조회할 책 개수
     * @return 최근 읽은 책 ID 목록
     */
    suspend fun getRecentlyReadBooks(limit: Int = 10): List<String> {
        return readingProgressRepository.getRecentlyReadBooks(DEFAULT_USER_ID, limit)
    }

    /**
     * 특정 언어의 전체 읽기 통계 조회
     * 
     * @param languageCode 언어 코드
     * @return 읽기 통계 정보
     */
    suspend fun getReadingStats(languageCode: String): ReadingStats {
        val progressSummary = readingProgressRepository.getReadingProgressSummary(DEFAULT_USER_ID, languageCode)
        
        return ReadingStats(
            completedBooks = progressSummary.completedBooks,
            inProgressBooks = progressSummary.readingBooks,
            totalBooksStarted = progressSummary.completedBooks + progressSummary.readingBooks
        )
    }

    /**
     * 책 읽기 시작 기록
     * 
     * @param bookId 책 ID
     * @param languageCode 언어 코드
     */
    suspend fun startReadingBook(bookId: String, languageCode: String) {
        readingProgressRepository.startReading(DEFAULT_USER_ID, bookId)
    }

    /**
     * storyId에서 책 ID를 추출하는 유틸리티 함수
     * 
     * @param storyId "123_ko-kr" 형태의 스토리 ID
     * @return 책 ID (숫자 부분)
     */
    fun extractBookIdFromStoryId(storyId: String): Int {
        return storyId.split("_").firstOrNull()?.toIntOrNull() ?: 0
    }
    
    /**
     * 호환성을 위한 오버로드 메서드들 (기존 코드와의 호환성)
     */
    suspend fun updateBookProgress(
        bookId: Int,
        languageCode: String,
        currentPage: Int,
        totalPages: Int,
        isCompleted: Boolean = false
    ) {
        val storyId = "${bookId}_${languageCode}"
        updateBookProgress(storyId, languageCode, currentPage, totalPages, isCompleted)
    }
    
    suspend fun markBookAsCompleted(
        bookId: Int,
        languageCode: String,
        totalPages: Int
    ) {
        val storyId = "${bookId}_${languageCode}"
        markBookAsCompleted(storyId, languageCode, totalPages)
    }
    
    suspend fun getBookProgress(
        bookId: Int,
        languageCode: String
    ): ReadingProgress? {
        val storyId = "${bookId}_${languageCode}"
        return getBookProgress(storyId, languageCode)
    }
}

/**
 * 읽기 통계 데이터 클래스
 */
data class ReadingStats(
    val completedBooks: Int,
    val inProgressBooks: Int,
    val totalBooksStarted: Int
)
