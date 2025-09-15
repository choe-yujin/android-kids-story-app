package com.timor.kidsstory.domain.manager

import com.timor.kidsstory.data.local.database.dao.UserBookInteractionDao
import com.timor.kidsstory.data.local.database.entity.UserBookInteractionEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 사용자-책 상호작용을 관리하는 Manager
 * - 읽기 진도 추적
 * - 완독한 책 통계
 * - 북마크 관리
 */
@Singleton
class BookInteractionManager @Inject constructor(
    private val userBookInteractionDao: UserBookInteractionDao,
    private val userManager: UserManager
) {

    /**
     * 특정 언어의 완독한 책 개수 조회
     * 
     * @param languageCode 언어 코드
     * @return 완독한 책 개수
     */
    suspend fun getCompletedBooksCount(languageCode: String): Int {
        val userId = userManager.getCurrentUserId()
        return userBookInteractionDao.getCompletedBooksCount(userId, languageCode)
    }

    /**
     * 사용자의 모든 완독한 책 개수 조회
     * 
     * @return 전체 완독한 책 개수
     */
    suspend fun getTotalCompletedBooksCount(): Int {
        val userId = userManager.getCurrentUserId()
        return userBookInteractionDao.getTotalCompletedBooksCount(userId)
    }

    /**
     * 특정 책의 읽기 진도 저장/업데이트
     * 
     * @param bookId 책 ID (storyId에서 책 번호만 추출)
     * @param languageCode 언어 코드
     * @param currentPage 현재 페이지
     * @param totalPages 전체 페이지 수
     * @param isCompleted 완독 여부
     */
    suspend fun updateBookProgress(
        bookId: Int,
        languageCode: String,
        currentPage: Int,
        totalPages: Int,
        isCompleted: Boolean = false
    ) {
        val userId = userManager.getCurrentUserId()
        val currentTime = System.currentTimeMillis()
        
        // 기존 기록 조회
        val existingInteraction = userBookInteractionDao.getUserBookInteraction(
            userId, bookId, languageCode
        )
        
        if (existingInteraction != null) {
            // 기존 기록 업데이트
            val updatedInteraction = existingInteraction.copy(
                currentPage = currentPage,
                totalPages = totalPages,
                isCompleted = isCompleted,
                lastReadAt = currentTime,
                completedAt = if (isCompleted) currentTime else existingInteraction.completedAt
            )
            userBookInteractionDao.updateUserBookInteraction(updatedInteraction)
        } else {
            // 새 기록 생성
            val newInteraction = UserBookInteractionEntity(
                userId = userId,
                bookId = bookId,
                language = languageCode,
                currentPage = currentPage,
                totalPages = totalPages,
                isCompleted = isCompleted,
                startedAt = currentTime,
                lastReadAt = currentTime,
                completedAt = if (isCompleted) currentTime else null
            )
            userBookInteractionDao.insertUserBookInteraction(newInteraction)
        }
    }

    /**
     * 특정 책을 완독으로 표시
     * 
     * @param bookId 책 ID
     * @param languageCode 언어 코드
     * @param totalPages 전체 페이지 수
     */
    suspend fun markBookAsCompleted(
        bookId: Int,
        languageCode: String,
        totalPages: Int
    ) {
        updateBookProgress(
            bookId = bookId,
            languageCode = languageCode,
            currentPage = totalPages,
            totalPages = totalPages,
            isCompleted = true
        )
    }

    /**
     * 특정 책의 읽기 진도 조회
     * 
     * @param bookId 책 ID
     * @param languageCode 언어 코드
     * @return 읽기 진도 정보 또는 null
     */
    suspend fun getBookProgress(
        bookId: Int,
        languageCode: String
    ): UserBookInteractionEntity? {
        val userId = userManager.getCurrentUserId()
        return userBookInteractionDao.getUserBookInteraction(userId, bookId, languageCode)
    }

    /**
     * 특정 언어의 진행 중인 책 목록 조회
     * 
     * @param languageCode 언어 코드
     * @return 진행 중인 책 목록
     */
    suspend fun getInProgressBooks(languageCode: String): List<UserBookInteractionEntity> {
        val userId = userManager.getCurrentUserId()
        return userBookInteractionDao.getInProgressBooks(userId, languageCode)
    }

    /**
     * 최근 읽은 책 목록 조회
     * 
     * @param limit 조회할 책 개수
     * @return 최근 읽은 책 목록
     */
    suspend fun getRecentlyReadBooks(limit: Int = 10): List<UserBookInteractionEntity> {
        val userId = userManager.getCurrentUserId()
        return userBookInteractionDao.getRecentlyReadBooks(userId, limit)
    }

    /**
     * 특정 언어의 전체 읽기 통계 조회
     * 
     * @param languageCode 언어 코드
     * @return 읽기 통계 정보
     */
    suspend fun getReadingStats(languageCode: String): ReadingStats {
        val userId = userManager.getCurrentUserId()
        val completedCount = userBookInteractionDao.getCompletedBooksCount(userId, languageCode)
        val inProgressCount = userBookInteractionDao.getInProgressBooks(userId, languageCode).size
        val totalInteractions = userBookInteractionDao.getTotalBooksStarted(userId, languageCode)
        
        return ReadingStats(
            completedBooks = completedCount,
            inProgressBooks = inProgressCount,
            totalBooksStarted = totalInteractions
        )
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
}

/**
 * 읽기 통계 데이터 클래스
 */
data class ReadingStats(
    val completedBooks: Int,
    val inProgressBooks: Int,
    val totalBooksStarted: Int
)
