package com.timor.kidsstory.domain.usecase.progress

import android.util.Log
import com.timor.kidsstory.domain.manager.BookInteractionManager
import com.timor.kidsstory.domain.repository.BookRepository
import javax.inject.Inject

/**
 * 사용자의 읽기 진도를 가져오는 UseCase
 * 
 * 기존 BookshelfViewModel의 updateReadingProgress() 로직을 Domain Layer로 이동
 * - 언어별 완독한 책 개수 계산
 * - 전체 책 개수 대비 진도율 계산
 * - 읽기 통계 정보 제공
 */
class GetReadingProgressUseCase @Inject constructor(
    private val bookInteractionManager: BookInteractionManager,
    private val bookRepository: BookRepository
) {
    /**
     * 특정 언어의 읽기 진도 요약 정보 조회
     * 
     * @param languageCode 언어 코드 (예: "ko-kr", "en-ph", "tetum")
     * @return ReadingProgressSummary 읽기 진도 요약
     */
    suspend operator fun invoke(languageCode: String): ReadingProgressSummary {
        return try {
            Log.d("GetReadingProgressUseCase", "Getting reading progress for language: $languageCode")
            
            // 1. 완독한 책 개수 조회
            val completedBooks = bookInteractionManager.getCompletedBooksCount(languageCode)
            Log.d("GetReadingProgressUseCase", "Completed books for $languageCode: $completedBooks")
            
            // 2. 전체 책 개수 조회 (로컬 Asset 책 기준)
            val totalBooksResult = bookRepository.getLocalBooks(languageCode)
            val totalBooks = totalBooksResult.getOrElse { 
                Log.w("GetReadingProgressUseCase", "Failed to get total books count for $languageCode", it)
                emptyList()
            }.size
            Log.d("GetReadingProgressUseCase", "Total books for $languageCode: $totalBooks")
            
            // 3. 진행률 계산
            val progressPercentage = if (totalBooks > 0) {
                completedBooks.toFloat() / totalBooks.toFloat()
            } else {
                0f
            }
            
            // 4. 추가 통계 정보 조회
            val readingStats = bookInteractionManager.getReadingStats(languageCode)
            Log.d("GetReadingProgressUseCase", "Reading stats for $languageCode: inProgress=${readingStats.inProgressBooks}, started=${readingStats.totalBooksStarted}")
            
            val summary = ReadingProgressSummary(
                languageCode = languageCode,
                completedBooks = completedBooks,
                totalBooks = totalBooks,
                progressPercentage = progressPercentage,
                inProgressBooks = readingStats.inProgressBooks,
                totalBooksStarted = readingStats.totalBooksStarted
            )
            
            Log.d("GetReadingProgressUseCase", 
                "Reading progress summary for $languageCode: $completedBooks/$totalBooks (${(progressPercentage * 100).toInt()}%)")
            
            summary
            
        } catch (e: Exception) {
            Log.e("GetReadingProgressUseCase", "Error getting reading progress for $languageCode", e)
            ReadingProgressSummary(languageCode = languageCode) // 오류 시 기본값 반환
        }
    }
    
    /**
     * 모든 언어의 전체 읽기 진도 조회
     * 
     * @return 전체 읽기 통계
     */
    suspend fun getTotalReadingProgress(): TotalReadingProgress {
        return try {
            Log.d("GetReadingProgressUseCase", "Getting total reading progress")
            
            val totalCompleted = bookInteractionManager.getTotalCompletedBooksCount()
            val recentBookIds = bookInteractionManager.getRecentlyReadBooks(limit = 5)
            
            // recentBookIds는 String 리스트이므로 간단한 정보만 생성
            val recentBooks = recentBookIds.map { bookId ->
                // bookId에서 언어 코드 추출 (예: "801_ko-kr" -> "ko-kr")
                val languageCode = if (bookId.contains("_")) {
                    bookId.split("_").getOrNull(1) ?: "unknown"
                } else {
                    "unknown"
                }
                
                RecentBookInfo(
                    bookId = bookId.split("_").firstOrNull()?.toIntOrNull() ?: 0,
                    languageCode = languageCode,
                    lastReadAt = System.currentTimeMillis(), // 임시값 - 실제로는 Repository에서 가져와야 함
                    isCompleted = false, // 임시값
                    progressPercentage = 0f // 임시값
                )
            }
            
            TotalReadingProgress(
                totalCompletedBooks = totalCompleted,
                recentlyReadBooks = recentBooks
            )
            
        } catch (e: Exception) {
            Log.e("GetReadingProgressUseCase", "Error getting total reading progress", e)
            TotalReadingProgress()
        }
    }
}

/**
 * 특정 언어의 읽기 진도 요약 정보
 * 
 * @property languageCode 언어 코드
 * @property completedBooks 완독한 책 개수
 * @property totalBooks 전체 책 개수 (해당 언어)
 * @property progressPercentage 진도율 (0.0 ~ 1.0)
 * @property inProgressBooks 읽는 중인 책 개수
 * @property totalBooksStarted 시작한 책 개수 (읽기 시작한 모든 책)
 */
data class ReadingProgressSummary(
    val languageCode: String = "",
    val completedBooks: Int = 0,
    val totalBooks: Int = 0,
    val progressPercentage: Float = 0f,
    val inProgressBooks: Int = 0,
    val totalBooksStarted: Int = 0
) {
    /**
     * 진도율을 백분율로 반환 (0~100)
     */
    val progressPercentageInt: Int
        get() = (progressPercentage * 100).toInt()
    
    /**
     * 읽기 레벨 계산 (초급/중급/고급)
     */
    val readingLevel: ReadingLevel
        get() = when {
            completedBooks >= totalBooks * 0.8 -> ReadingLevel.ADVANCED
            completedBooks >= totalBooks * 0.5 -> ReadingLevel.INTERMEDIATE  
            completedBooks >= totalBooks * 0.2 -> ReadingLevel.BEGINNER
            else -> ReadingLevel.STARTER
        }
    
    /**
     * 다음 책 권장 여부
     */
    val shouldRecommendNextBook: Boolean
        get() = inProgressBooks == 0 && completedBooks < totalBooks
}

/**
 * 전체 읽기 진도 정보
 * 
 * @property totalCompletedBooks 전체 완독한 책 개수 (모든 언어)
 * @property recentlyReadBooks 최근 읽은 책 목록
 */
data class TotalReadingProgress(
    val totalCompletedBooks: Int = 0,
    val recentlyReadBooks: List<RecentBookInfo> = emptyList()
)

/**
 * 최근 읽은 책 정보
 * 
 * @property bookId 책 ID
 * @property languageCode 언어 코드  
 * @property lastReadAt 마지막 읽은 시간
 * @property isCompleted 완독 여부
 * @property progressPercentage 읽기 진도율
 */
data class RecentBookInfo(
    val bookId: Int,
    val languageCode: String,
    val lastReadAt: Long,
    val isCompleted: Boolean,
    val progressPercentage: Float
)

/**
 * 읽기 레벨 enum
 */
enum class ReadingLevel {
    STARTER,       // 초보 (0-20%)
    BEGINNER,      // 초급 (20-50%)
    INTERMEDIATE,  // 중급 (50-80%)
    ADVANCED       // 고급 (80-100%)
}