package com.timor.kidsstory.domain.usecase.progress

import android.util.Log
import com.timor.kidsstory.domain.manager.BookInteractionManager
import javax.inject.Inject

/**
 * 사용자의 읽기 진도를 업데이트하는 UseCase
 * 
 * 책 읽기 진도 업데이트와 관련된 비즈니스 로직을 처리
 * - 페이지 진도 업데이트
 * - 완독 처리  
 * - 읽기 시간 기록
 */
class UpdateReadingProgressUseCase @Inject constructor(
    private val bookInteractionManager: BookInteractionManager
) {
    /**
     * 책의 읽기 진도 업데이트
     * 
     * @param storyId 책 ID (예: "801_ko-kr")
     * @param currentPage 현재 페이지
     * @param totalPages 전체 페이지 수
     * @return 업데이트 성공 여부
     */
    suspend operator fun invoke(
        storyId: String,
        currentPage: Int,
        totalPages: Int
    ): Result<ReadingProgressUpdate> {
        return try {
            Log.d("UpdateReadingProgressUseCase", 
                "Updating progress for $storyId: $currentPage/$totalPages")
            
            // 1. storyId에서 책 ID와 언어 코드 추출
            val bookId = bookInteractionManager.extractBookIdFromStoryId(storyId)
            val languageCode = extractLanguageFromStoryId(storyId)
            
            if (bookId == 0) {
                Log.e("UpdateReadingProgressUseCase", "Invalid storyId: $storyId")
                return Result.failure(IllegalArgumentException("Invalid storyId: $storyId"))
            }
            
            // 2. 완독 여부 확인
            val isCompleted = currentPage >= totalPages
            
            // 3. 진도 업데이트
            bookInteractionManager.updateBookProgress(
                bookId = bookId,
                languageCode = languageCode,
                currentPage = currentPage,
                totalPages = totalPages,
                isCompleted = isCompleted
            )
            
            val update = ReadingProgressUpdate(
                bookId = bookId,
                languageCode = languageCode,
                currentPage = currentPage,
                totalPages = totalPages,
                isCompleted = isCompleted,
                progressPercentage = if (totalPages > 0) {
                    currentPage.toFloat() / totalPages.toFloat()
                } else {
                    0f
                },
                wasJustCompleted = isCompleted && currentPage == totalPages
            )
            
            Log.d("UpdateReadingProgressUseCase", 
                "Progress updated successfully: ${update.progressPercentage * 100}%")
            
            if (update.wasJustCompleted) {
                Log.d("UpdateReadingProgressUseCase", "Book completed: $storyId")
            }
            
            Result.success(update)
            
        } catch (e: Exception) {
            Log.e("UpdateReadingProgressUseCase", "Error updating reading progress", e)
            Result.failure(e)
        }
    }
    
    /**
     * 책을 완독으로 표시 (마지막 페이지가 아니어도 강제 완독)
     * 
     * @param storyId 책 ID
     * @param totalPages 전체 페이지 수
     * @return 업데이트 성공 여부
     */
    suspend fun markAsCompleted(
        storyId: String,
        totalPages: Int
    ): Result<ReadingProgressUpdate> {
        return invoke(storyId, totalPages, totalPages)
    }
    
    /**
     * 여러 책의 진도를 일괄 업데이트 (배치 처리)
     * 
     * @param progressUpdates 업데이트할 진도 목록
     * @return 업데이트 결과 목록
     */
    suspend fun updateMultipleProgress(
        progressUpdates: List<ProgressUpdateRequest>
    ): List<Result<ReadingProgressUpdate>> {
        return progressUpdates.map { request ->
            invoke(request.storyId, request.currentPage, request.totalPages)
        }
    }
    
    /**
     * storyId에서 언어 코드 추출
     * 
     * @param storyId "801_ko-kr" 형태의 스토리 ID
     * @return 언어 코드 (예: "ko-kr")
     */
    private fun extractLanguageFromStoryId(storyId: String): String {
        return storyId.split("_").getOrNull(1) ?: "en-ph"
    }
}

/**
 * 읽기 진도 업데이트 결과
 * 
 * @property bookId 책 ID
 * @property languageCode 언어 코드
 * @property currentPage 현재 페이지
 * @property totalPages 전체 페이지 수
 * @property isCompleted 완독 여부
 * @property progressPercentage 진도율 (0.0 ~ 1.0)
 * @property wasJustCompleted 방금 완독되었는지 여부
 */
data class ReadingProgressUpdate(
    val bookId: Int,
    val languageCode: String,
    val currentPage: Int,
    val totalPages: Int,
    val isCompleted: Boolean,
    val progressPercentage: Float,
    val wasJustCompleted: Boolean
) {
    /**
     * 진도율을 백분율로 반환 (0~100)
     */
    val progressPercentageInt: Int
        get() = (progressPercentage * 100).toInt()
}

/**
 * 진도 업데이트 요청 데이터
 * 
 * @property storyId 책 ID
 * @property currentPage 현재 페이지
 * @property totalPages 전체 페이지 수
 */
data class ProgressUpdateRequest(
    val storyId: String,
    val currentPage: Int,
    val totalPages: Int
)