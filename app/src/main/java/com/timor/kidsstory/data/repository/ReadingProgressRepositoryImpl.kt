package com.timor.kidsstory.data.repository

import com.timor.kidsstory.data.local.database.dao.ReadingProgressDao
import com.timor.kidsstory.data.local.database.entity.ReadingProgressEntity
import com.timor.kidsstory.domain.model.ReadingProgress
import com.timor.kidsstory.domain.model.ReadingProgressSummary
import com.timor.kidsstory.domain.repository.ReadingProgressRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

/**
 * ReadingProgressRepository 구현체
 * - Room Database를 통한 읽기 진도 데이터 관리
 */
@Singleton
class ReadingProgressRepositoryImpl @Inject constructor(
    private val readingProgressDao: ReadingProgressDao
) : ReadingProgressRepository {
    
    override suspend fun getCompletedBooksCount(userId: String, languageCode: String): Int = 
        withContext(Dispatchers.IO) {
            readingProgressDao.getCompletedBooksCount(userId, languageCode)
        }
    
    override suspend fun getReadingProgress(userId: String, bookId: String): ReadingProgress? = 
        withContext(Dispatchers.IO) {
            readingProgressDao.getProgressByUserAndBook(userId, bookId)?.toDomainModel()
        }
    
    override suspend fun updateReadingProgress(
        userId: String, 
        bookId: String, 
        progress: ReadingProgress,
        languageCode: String?
    ): Unit = withContext(Dispatchers.IO) {
        val finalLanguageCode = languageCode ?: extractLanguageCodeFromBookId(bookId)
        val existingEntity = readingProgressDao.getProgressByUserAndBook(userId, bookId)
        
        if (existingEntity != null) {
            // 기존 엔티티 업데이트
            val updatedEntity = existingEntity.copy(
                currentPage = progress.currentPage,
                totalPages = progress.totalPages,
                isCompleted = progress.isCompleted,
                lastReadAtTimestamp = progress.lastReadAt?.let {
                    it.toEpochSecond(java.time.ZoneOffset.UTC) * 1000
                },
                completedAtTimestamp = if (progress.isCompleted && !existingEntity.isCompleted) {
                    // 새로 완료된 경우에만 완료 시간 설정
                    System.currentTimeMillis()
                } else {
                    existingEntity.completedAtTimestamp
                },
                updatedAt = System.currentTimeMillis()
            )
            readingProgressDao.updateProgress(updatedEntity)
        } else {
            // 새 엔티티 생성
            val newEntity = ReadingProgressEntity.create(userId, bookId, finalLanguageCode, progress)
            readingProgressDao.insertOrUpdateProgress(newEntity)
        }
    }
    
    override suspend fun markBookCompleted(userId: String, bookId: String): Unit = 
        withContext(Dispatchers.IO) {
            val existingProgress = readingProgressDao.getProgressByUserAndBook(userId, bookId)
            
            if (existingProgress != null) {
                val completedEntity = existingProgress.copy(
                    currentPage = existingProgress.totalPages,
                    isCompleted = true,
                    completedAtTimestamp = System.currentTimeMillis(),
                    lastReadAtTimestamp = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )
                readingProgressDao.updateProgress(completedEntity)
            }
        }
    
    override suspend fun getReadingProgressSummary(
        userId: String, 
        languageCode: String
    ): ReadingProgressSummary = withContext(Dispatchers.IO) {
        val completedBooks = readingProgressDao.getCompletedBooksCount(userId, languageCode)
        val readingBooks = readingProgressDao.getReadingBooksCount(userId, languageCode)
        val totalPagesRead = readingProgressDao.getTotalPagesRead(userId, languageCode) ?: 0
        val totalPages = readingProgressDao.getTotalPages(userId, languageCode) ?: 0
        
        val overallProgressPercentage = if (totalPages > 0) {
            totalPagesRead.toFloat() / totalPages.toFloat()
        } else {
            0f
        }
        
        ReadingProgressSummary(
            completedBooks = completedBooks,
            totalBooks = completedBooks + readingBooks,
            readingBooks = readingBooks,
            overallProgressPercentage = overallProgressPercentage,
            totalPagesRead = totalPagesRead,
            totalPages = totalPages
        )
    }
    
    override suspend fun getAllProgressByLanguage(
        userId: String, 
        languageCode: String
    ): Map<String, ReadingProgress> = withContext(Dispatchers.IO) {
        readingProgressDao.getProgressByUserAndLanguage(userId, languageCode)
            .associate { entity ->
                entity.bookId to entity.toDomainModel()
            }
    }
    
    override suspend fun getRecentlyReadBooks(userId: String, limit: Int): List<String> = 
        withContext(Dispatchers.IO) {
            readingProgressDao.getRecentlyReadBooks(userId, limit)
                .map { it.bookId }
        }
    
    override suspend fun startReading(userId: String, bookId: String): Unit = 
        withContext(Dispatchers.IO) {
            val existingProgress = readingProgressDao.getProgressByUserAndBook(userId, bookId)
            
            if (existingProgress == null) {
                // 새로운 읽기 시작
                val languageCode = extractLanguageCodeFromBookId(bookId)
                val newProgress = ReadingProgress(
                    currentPage = 0,
                    totalPages = 0,
                    startedAt = LocalDateTime.now(),
                    lastReadAt = LocalDateTime.now()
                )
                val entity = ReadingProgressEntity.create(userId, bookId, languageCode, newProgress)
                readingProgressDao.insertOrUpdateProgress(entity)
            } else if (existingProgress.startedAtTimestamp == null) {
                // 시작 시간이 없는 경우 업데이트
                val updatedEntity = existingProgress.copy(
                    startedAtTimestamp = System.currentTimeMillis(),
                    lastReadAtTimestamp = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )
                readingProgressDao.updateProgress(updatedEntity)
            }
        }
    
    /**
     * bookId에서 언어 코드 추출
     * bookId 형식: "801_ko-kr", "819_en-ph" 등
     */
    private fun extractLanguageCodeFromBookId(bookId: String): String {
        return if (bookId.contains("_")) {
            bookId.substringAfterLast("_")
        } else {
            "unknown"
        }
    }
}
