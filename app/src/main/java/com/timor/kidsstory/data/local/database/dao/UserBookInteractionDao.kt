package com.timor.kidsstory.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.timor.kidsstory.data.local.database.entity.UserBookInteractionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserBookInteractionDao {
    
    @Query("SELECT * FROM user_book_interactions WHERE userId = :userId AND bookId = :bookId AND language = :language")
    suspend fun getInteraction(userId: String, bookId: Int, language: String): UserBookInteractionEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(interaction: UserBookInteractionEntity)
    
    // 완독 책 개수 조회 (읽기 진도용)
    @Query("SELECT COUNT(*) FROM user_book_interactions WHERE userId = :userId AND language = :language AND isCompleted = 1")
    suspend fun getCompletedBooksCount(userId: String, language: String): Int
    
    // 읽는 중인 책 개수 조회 (읽기 진도용)
    @Query("SELECT COUNT(*) FROM user_book_interactions WHERE userId = :userId AND language = :language AND currentPage > 0 AND isCompleted = 0")
    suspend fun getReadingBooksCount(userId: String, language: String): Int
    
    // 완독한 책 ID 목록 (잠금 해제 시스템용)
    @Query("SELECT bookId FROM user_book_interactions WHERE userId = :userId AND language = :language AND isCompleted = 1")
    suspend fun getCompletedBookIds(userId: String, language: String): List<Int>
    
    // 읽는 중인 책 ID 목록 (필터링용)
    @Query("SELECT bookId FROM user_book_interactions WHERE userId = :userId AND language = :language AND currentPage > 0 AND isCompleted = 0")
    suspend fun getReadingBookIds(userId: String, language: String): List<Int>
    
    // 북마크한 책 ID 목록 (필터링용)
    @Query("SELECT bookId FROM user_book_interactions WHERE userId = :userId AND language = :language AND isBookmarked = 1")
    suspend fun getBookmarkedBookIds(userId: String, language: String): List<Int>
    
    // 모든 상호작용 데이터 조회 (통계용)
    @Query("SELECT * FROM user_book_interactions WHERE userId = :userId AND language = :language")
    fun getAllInteractionsFlow(userId: String, language: String): Flow<List<UserBookInteractionEntity>>
    
    // 현재 페이지 업데이트
    @Query("UPDATE user_book_interactions SET currentPage = :page, lastReadAt = :timestamp WHERE userId = :userId AND bookId = :bookId AND language = :language")
    suspend fun updateCurrentPage(userId: String, bookId: Int, language: String, page: Int, timestamp: Long)
    
    // 완독 처리
    @Query("UPDATE user_book_interactions SET isCompleted = 1, completedAt = :timestamp WHERE userId = :userId AND bookId = :bookId AND language = :language")
    suspend fun markAsCompleted(userId: String, bookId: Int, language: String, timestamp: Long)
    
    // 북마크 토글
    @Query("UPDATE user_book_interactions SET isBookmarked = :isBookmarked, bookmarkDate = :timestamp WHERE userId = :userId AND bookId = :bookId AND language = :language")
    suspend fun toggleBookmark(userId: String, bookId: Int, language: String, isBookmarked: Boolean, timestamp: Long?)
}
