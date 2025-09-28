package com.timor.kidsstory.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.timor.kidsstory.data.local.database.entity.BookSource
import com.timor.kidsstory.data.local.database.entity.HybridBookEntity
import kotlinx.coroutines.flow.Flow

/**
 * 하이브리드 책 관리 DAO
 * - 내장 assets와 다운로드 책 통합 관리
 * - 버전별 업데이트 추적
 */
@Dao
interface HybridBooksDao {
    
    // ========== 기본 CRUD ==========
    
    /**
     * 책 정보 삽입/업데이트
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: HybridBookEntity)
    
    /**
     * 여러 책 정보 일괄 삽입/업데이트
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooks(books: List<HybridBookEntity>)
    
    /**
     * 책 정보 업데이트
     */
    @Update
    suspend fun updateBook(book: HybridBookEntity)
    
    /**
     * 특정 책 삭제
     */
    @Query("DELETE FROM hybrid_books WHERE id = :bookId AND language = :language")
    suspend fun deleteBook(bookId: Int, language: String): Int
    
    // ========== 조회 ==========
    
    /**
     * 모든 책 조회 (언어별)
     */
    @Query("SELECT * FROM hybrid_books WHERE language = :language ORDER BY level ASC, unlockStep ASC, id ASC")
    suspend fun getAllBooksByLanguage(language: String): List<HybridBookEntity>
    
    /**
     * 사용 가능한 책만 조회 (언어별)
     */
    @Query("SELECT * FROM hybrid_books WHERE language = :language AND isAvailable = 1 ORDER BY level ASC, unlockStep ASC, id ASC")
    suspend fun getAvailableBooksByLanguage(language: String): List<HybridBookEntity>
    
    /**
     * 특정 책 조회
     */
    @Query("SELECT * FROM hybrid_books WHERE id = :bookId AND language = :language")
    suspend fun getBook(bookId: Int, language: String): HybridBookEntity?
    
    /**
     * 특정 storyId의 모든 언어 버전 조회
     */
    @Query("SELECT * FROM hybrid_books WHERE id = :bookId")
    suspend fun getBooksByStoryId(bookId: Int): List<HybridBookEntity>
    
    /**
     * 특정 레벨의 책들 조회
     */
    @Query("SELECT * FROM hybrid_books WHERE language = :language AND level = :level AND isAvailable = 1 ORDER BY id ASC")
    suspend fun getBooksByLevel(language: String, level: Int): List<HybridBookEntity>
    
    /**
     * 출처별 책 조회 (내장/다운로드)
     */
    @Query("SELECT * FROM hybrid_books WHERE language = :language AND source = :source AND isAvailable = 1 ORDER BY level ASC, id ASC")
    suspend fun getBooksBySource(language: String, source: BookSource): List<HybridBookEntity>
    
    // ========== 상태 확인 ==========
    
    /**
     * 책 존재 여부 확인
     */
    @Query("SELECT EXISTS(SELECT 1 FROM hybrid_books WHERE id = :bookId AND language = :language)")
    suspend fun isBookExists(bookId: Int, language: String): Boolean
    
    /**
     * 다운로드된 책 여부 확인
     */
    @Query("SELECT EXISTS(SELECT 1 FROM hybrid_books WHERE id = :bookId AND language = :language AND source = 'DOWNLOADED')")
    suspend fun isBookDownloaded(bookId: Int, language: String): Boolean
    
    /**
     * 특정 언어로 사용 가능한 책 수
     */
    @Query("SELECT COUNT(*) FROM hybrid_books WHERE language = :language AND isAvailable = 1")
    suspend fun getAvailableBookCount(language: String): Int
    
    // ========== 버전 관리 ==========
    
    /**
     * 업데이트가 필요한 책들 조회 (콘텐츠 버전 기준)
     */
    @Query("SELECT * FROM hybrid_books WHERE contentVersion < :newContentVersion")
    suspend fun getBooksNeedingContentUpdate(newContentVersion: Int): List<HybridBookEntity>
    
    /**
     * 특정 책의 콘텐츠 버전 업데이트
     */
    @Query("UPDATE hybrid_books SET contentVersion = :newVersion, lastUpdated = :timestamp WHERE id = :bookId AND language = :language")
    suspend fun updateContentVersion(bookId: Int, language: String, newVersion: Int, timestamp: Long = System.currentTimeMillis())
    
    /**
     * 특정 책의 커버 버전 업데이트
     */
    @Query("UPDATE hybrid_books SET coverVersion = :newVersion, lastUpdated = :timestamp WHERE id = :bookId AND language = :language")
    suspend fun updateCoverVersion(bookId: Int, language: String, newVersion: Int, timestamp: Long = System.currentTimeMillis())
    
    /**
     * 특정 책의 이미지 에셋 버전 업데이트
     */
    @Query("UPDATE hybrid_books SET imageAssetsVersion = :newVersion, lastUpdated = :timestamp WHERE id = :bookId AND language = :language")
    suspend fun updateImageAssetsVersion(bookId: Int, language: String, newVersion: Int, timestamp: Long = System.currentTimeMillis())
    
    /**
     * 책 가용성 상태 업데이트
     */
    @Query("UPDATE hybrid_books SET isAvailable = :isAvailable, lastUpdated = :timestamp WHERE id = :bookId AND language = :language")
    suspend fun updateBookAvailability(bookId: Int, language: String, isAvailable: Boolean, timestamp: Long = System.currentTimeMillis())
    
    // ========== 실시간 관찰 ==========
    
    /**
     * 언어별 책 목록 실시간 관찰
     */
    @Query("SELECT * FROM hybrid_books WHERE language = :language AND isAvailable = 1 ORDER BY level ASC, unlockStep ASC, id ASC")
    fun observeBooksByLanguage(language: String): Flow<List<HybridBookEntity>>
    
    /**
     * 다운로드된 책 목록 실시간 관찰
     */
    @Query("SELECT * FROM hybrid_books WHERE language = :language AND source = 'DOWNLOADED' AND isAvailable = 1 ORDER BY downloadDate DESC")
    fun observeDownloadedBooks(language: String): Flow<List<HybridBookEntity>>
    
    // ========== 통계 ==========
    
    /**
     * 언어별 레벨별 책 수 통계
     */
    @Query("SELECT level, COUNT(*) as count FROM hybrid_books WHERE language = :language AND isAvailable = 1 GROUP BY level ORDER BY level")
    suspend fun getBookCountByLevel(language: String): List<LevelBookCount>
    
    /**
     * 전체 통계
     */
    @Query("""
        SELECT 
            COUNT(*) as total,
            COUNT(CASE WHEN source = 'BUNDLED' THEN 1 END) as bundled,
            COUNT(CASE WHEN source = 'DOWNLOADED' THEN 1 END) as downloaded
        FROM hybrid_books 
        WHERE language = :language AND isAvailable = 1
    """)
    suspend fun getBookStatistics(language: String): BookStatistics
}

/**
 * 레벨별 책 수
 */
data class LevelBookCount(
    val level: Int,
    val count: Int
)

/**
 * 책 통계
 */
data class BookStatistics(
    val total: Int,
    val bundled: Int,
    val downloaded: Int
)
