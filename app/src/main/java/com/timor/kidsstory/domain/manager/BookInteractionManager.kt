package com.timor.kidsstory.domain.manager

import com.timor.kidsstory.data.local.database.dao.UserBookInteractionDao
import com.timor.kidsstory.data.local.database.entity.UserBookInteractionEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 사용자의 책 상호작용을 관리하는 핵심 Manager
 * - 읽기 진도, 북마크, 완독 처리 등 모든 사용자 행동 추적
 * - 게이미피케이션 및 잠금 해제 시스템의 기반 데이터 제공
 */
@Singleton
class BookInteractionManager @Inject constructor(
    private val userBookInteractionDao: UserBookInteractionDao,
    private val userManager: UserManager
) {

    /**
     * 책 읽기 시작 처리
     */
    suspend fun startReading(bookId: Int, language: String, totalPages: Int) {
        val userId = userManager.getCurrentUserId()
        val interaction = getOrCreateInteraction(userId, bookId, language, totalPages)
        
        val updatedInteraction = interaction.copy(
            startedAt = interaction.startedAt ?: System.currentTimeMillis(),
            lastReadAt = System.currentTimeMillis(),
            readCount = interaction.readCount + 1
        )
        
        userBookInteractionDao.insertOrUpdate(updatedInteraction)
    }

    /**
     * 현재 페이지 업데이트
     */
    suspend fun updateCurrentPage(bookId: Int, language: String, page: Int, totalPages: Int) {
        val userId = userManager.getCurrentUserId()
        val currentTime = System.currentTimeMillis()
        
        // 기존 상호작용이 없으면 생성
        val interaction = getOrCreateInteraction(userId, bookId, language, totalPages)
        val updatedInteraction = interaction.copy(
            currentPage = page,
            lastReadAt = currentTime,
            startedAt = interaction.startedAt ?: currentTime
        )
        
        userBookInteractionDao.insertOrUpdate(updatedInteraction)
        
        // 마지막 페이지에 도달하면 완독 처리
        if (page >= totalPages) {
            markAsCompleted(bookId, language)
        }
    }

    /**
     * 완독 처리
     */
    suspend fun markAsCompleted(bookId: Int, language: String) {
        val userId = userManager.getCurrentUserId()
        val interaction = userBookInteractionDao.getInteraction(userId, bookId, language)
        
        interaction?.let {
            val updatedInteraction = it.copy(
                isCompleted = true,
                completedAt = System.currentTimeMillis(),
                currentPage = it.totalPages
            )
            userBookInteractionDao.insertOrUpdate(updatedInteraction)
        }
    }

    /**
     * 북마크 토글
     */
    suspend fun toggleBookmark(bookId: Int, language: String) {
        val userId = userManager.getCurrentUserId()
        val interaction = getOrCreateInteraction(userId, bookId, language, 0)
        
        val updatedInteraction = interaction.copy(
            isBookmarked = !interaction.isBookmarked,
            bookmarkDate = if (!interaction.isBookmarked) System.currentTimeMillis() else null
        )
        
        userBookInteractionDao.insertOrUpdate(updatedInteraction)
    }

    /**
     * 특정 책의 상호작용 데이터 조회
     */
    suspend fun getInteraction(bookId: Int, language: String): UserBookInteractionEntity? {
        val userId = userManager.getCurrentUserId()
        return userBookInteractionDao.getInteraction(userId, bookId, language)
    }

    /**
     * 완독한 책 개수 조회 (읽기 진도용)
     */
    suspend fun getCompletedBooksCount(language: String): Int {
        val userId = userManager.getCurrentUserId()
        return userBookInteractionDao.getCompletedBooksCount(userId, language)
    }

    /**
     * 읽는 중인 책 개수 조회
     */
    suspend fun getReadingBooksCount(language: String): Int {
        val userId = userManager.getCurrentUserId()
        return userBookInteractionDao.getReadingBooksCount(userId, language)
    }

    /**
     * 완독한 책 ID 목록 조회 (잠금 해제 시스템용)
     */
    suspend fun getCompletedBookIds(language: String): List<Int> {
        val userId = userManager.getCurrentUserId()
        return userBookInteractionDao.getCompletedBookIds(userId, language)
    }

    /**
     * 읽는 중인 책 ID 목록 조회 (필터링용)
     */
    suspend fun getReadingBookIds(language: String): List<Int> {
        val userId = userManager.getCurrentUserId()
        return userBookInteractionDao.getReadingBookIds(userId, language)
    }

    /**
     * 북마크한 책 ID 목록 조회 (필터링용)
     */
    suspend fun getBookmarkedBookIds(language: String): List<Int> {
        val userId = userManager.getCurrentUserId()
        return userBookInteractionDao.getBookmarkedBookIds(userId, language)
    }

    /**
     * 모든 상호작용 데이터 Flow 조회 (실시간 업데이트용)
     */
    fun getAllInteractionsFlow(language: String): Flow<List<UserBookInteractionEntity>> {
        val userId = userManager.getCurrentUserId()
        return userBookInteractionDao.getAllInteractionsFlow(userId, language)
    }

    /**
     * 상호작용 데이터 생성 또는 기존 데이터 반환
     */
    private suspend fun getOrCreateInteraction(
        userId: String, 
        bookId: Int, 
        language: String, 
        totalPages: Int
    ): UserBookInteractionEntity {
        return userBookInteractionDao.getInteraction(userId, bookId, language)
            ?: UserBookInteractionEntity(
                userId = userId,
                bookId = bookId,
                language = language,
                totalPages = totalPages
            )
    }
}
