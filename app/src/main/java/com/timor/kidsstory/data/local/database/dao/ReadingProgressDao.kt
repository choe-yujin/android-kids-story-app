package com.timor.kidsstory.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.timor.kidsstory.data.local.database.entity.ReadingProgressEntity

/**
 * 읽기 진도 데이터 접근을 위한 DAO
 */
@Dao
interface ReadingProgressDao {
    
    /**
     * 읽기 진도 추가 또는 업데이트
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProgress(progress: ReadingProgressEntity)
    
    /**
     * 읽기 진도 업데이트
     */
    @Update
    suspend fun updateProgress(progress: ReadingProgressEntity)
    
    /**
     * 특정 사용자의 특정 책 진도 조회
     */
    @Query("SELECT * FROM reading_progress WHERE userId = :userId AND bookId = :bookId LIMIT 1")
    suspend fun getProgressByUserAndBook(userId: String, bookId: String): ReadingProgressEntity?
    
    /**
     * 특정 사용자의 모든 진도 조회
     */
    @Query("SELECT * FROM reading_progress WHERE userId = :userId")
    suspend fun getAllProgressByUser(userId: String): List<ReadingProgressEntity>
    
    /**
     * 특정 사용자의 특정 언어 진도 조회
     */
    @Query("SELECT * FROM reading_progress WHERE userId = :userId AND languageCode = :languageCode")
    suspend fun getProgressByUserAndLanguage(userId: String, languageCode: String): List<ReadingProgressEntity>
    
    /**
     * 완료된 책 수 조회
     */
    @Query("SELECT COUNT(*) FROM reading_progress WHERE userId = :userId AND languageCode = :languageCode AND isCompleted = 1")
    suspend fun getCompletedBooksCount(userId: String, languageCode: String): Int
    
    /**
     * 읽고 있는 책 수 조회 (진도가 0보다 크고 완료되지 않은 책)
     */
    @Query("SELECT COUNT(*) FROM reading_progress WHERE userId = :userId AND languageCode = :languageCode AND currentPage > 0 AND isCompleted = 0")
    suspend fun getReadingBooksCount(userId: String, languageCode: String): Int
    
    /**
     * 최근 읽은 책 목록 조회
     */
    @Query("SELECT * FROM reading_progress WHERE userId = :userId ORDER BY lastReadAtTimestamp DESC LIMIT :limit")
    suspend fun getRecentlyReadBooks(userId: String, limit: Int): List<ReadingProgressEntity>
    
    /**
     * 특정 언어의 총 읽은 페이지 수 조회
     */
    @Query("SELECT SUM(currentPage) FROM reading_progress WHERE userId = :userId AND languageCode = :languageCode")
    suspend fun getTotalPagesRead(userId: String, languageCode: String): Int?
    
    /**
     * 특정 언어의 총 페이지 수 조회
     */
    @Query("SELECT SUM(totalPages) FROM reading_progress WHERE userId = :userId AND languageCode = :languageCode")
    suspend fun getTotalPages(userId: String, languageCode: String): Int?
    
    /**
     * 완료된 책 목록 조회
     */
    @Query("SELECT * FROM reading_progress WHERE userId = :userId AND languageCode = :languageCode AND isCompleted = 1 ORDER BY completedAtTimestamp DESC")
    suspend fun getCompletedBooks(userId: String, languageCode: String): List<ReadingProgressEntity>
    
    /**
     * 진도 삭제
     */
    @Query("DELETE FROM reading_progress WHERE userId = :userId AND bookId = :bookId")
    suspend fun deleteProgress(userId: String, bookId: String)
    
    /**
     * 사용자의 모든 진도 삭제
     */
    @Query("DELETE FROM reading_progress WHERE userId = :userId")
    suspend fun deleteAllProgressByUser(userId: String)
}
