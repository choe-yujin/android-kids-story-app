package com.timor.kidsstory.presentation.progress

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.timor.kidsstory.domain.manager.BookInteractionManager
import com.timor.kidsstory.domain.usecase.progress.GetReadingProgressUseCase
import com.timor.kidsstory.domain.usecase.progress.UpdateReadingProgressUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 읽기 진도 관리 전용 ViewModel
 * 
 * 단일 책임: 읽기 진도 UI 상태 관리
 * - 완독한 책 개수 추적
 * - 전체 진도율 계산
 * - 읽기 통계 관리
 */
@HiltViewModel
class ProgressViewModel @Inject constructor(
    private val getReadingProgressUseCase: GetReadingProgressUseCase,
    private val updateReadingProgressUseCase: UpdateReadingProgressUseCase,
    private val bookInteractionManager: BookInteractionManager
) : ViewModel() {

    // 읽기 진도 UI 상태
    private val _progressState = MutableStateFlow(ProgressUiState())
    val progressState: StateFlow<ProgressUiState> = _progressState.asStateFlow()

    /**
     * 특정 언어의 읽기 진도 로드
     * 
     * @param languageCode 언어 코드
     * @param totalBooks 전체 책 개수 (외부에서 전달)
     */
    fun loadProgress(languageCode: String, totalBooks: Int = 0) {
        viewModelScope.launch {
            try {
                Log.d("ProgressViewModel", "Loading progress for language: $languageCode, totalBooks: $totalBooks")
                
                _progressState.update { it.copy(isLoading = true, error = null) }
                
                // UseCase를 통한 읽기 진도 조회
                val progressSummary = getReadingProgressUseCase(languageCode)
                
                Log.d("ProgressViewModel", "Progress summary: completed=${progressSummary.completedBooks}, total=${progressSummary.totalBooks}")
                
                // totalBooks가 외부에서 전달된 경우 해당 값 사용
                val finalTotalBooks = if (totalBooks > 0) totalBooks else progressSummary.totalBooks
                
                val finalProgressPercentage = if (finalTotalBooks > 0) {
                    progressSummary.completedBooks.toFloat() / finalTotalBooks.toFloat()
                } else {
                    0f
                }
                
                _progressState.update { currentState ->
                    currentState.copy(
                        languageCode = languageCode,
                        completedBooks = progressSummary.completedBooks,
                        totalBooks = finalTotalBooks,
                        progressPercentage = finalProgressPercentage,
                        inProgressBooks = progressSummary.inProgressBooks,
                        totalBooksStarted = progressSummary.totalBooksStarted,
                        readingLevel = calculateReadingLevel(finalProgressPercentage),
                        isLoading = false
                    )
                }
                
                Log.d("ProgressViewModel", 
                    "Progress loaded: ${progressSummary.completedBooks}/$finalTotalBooks " +
                    "(${(finalProgressPercentage * 100).toInt()}%) for $languageCode"
                )
                
            } catch (e: Exception) {
                Log.e("ProgressViewModel", "Error loading progress for $languageCode", e)
                _progressState.update { 
                    it.copy(
                        isLoading = false, 
                        error = "읽기 진도를 불러올 수 없습니다: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * 특정 책의 읽기 진도 업데이트
     * 
     * @param bookId 책 ID (storyId)
     * @param languageCode 언어 코드
     * @param currentPage 현재 페이지
     * @param totalPages 전체 페이지 수
     * @param isCompleted 완독 여부
     */
    fun updateBookProgress(
        bookId: String,
        languageCode: String,
        currentPage: Int,
        totalPages: Int,
        isCompleted: Boolean = false
    ) {
        viewModelScope.launch {
            try {
                Log.d("ProgressViewModel", 
                    "Updating progress for book $bookId: $currentPage/$totalPages (completed: $isCompleted)")
                
                // BookInteractionManager를 통한 진도 업데이트
                bookInteractionManager.updateBookProgress(
                    bookId = bookId,
                    languageCode = languageCode,
                    currentPage = currentPage,
                    totalPages = totalPages,
                    isCompleted = isCompleted
                )
                
                // 진도 업데이트 후 전체 진도 다시 로드
                loadProgress(languageCode, _progressState.value.totalBooks)
                
                Log.d("ProgressViewModel", "Book progress updated successfully")
                
            } catch (e: Exception) {
                Log.e("ProgressViewModel", "Error updating book progress", e)
                _progressState.update { 
                    it.copy(error = "읽기 진도 업데이트에 실패했습니다: ${e.message}")
                }
            }
        }
    }

    /**
     * 책 완독 처리
     * 
     * @param bookId 책 ID
     * @param languageCode 언어 코드
     * @param totalPages 전체 페이지 수
     */
    fun markBookAsCompleted(bookId: String, languageCode: String, totalPages: Int) {
        updateBookProgress(
            bookId = bookId,
            languageCode = languageCode,
            currentPage = totalPages,
            totalPages = totalPages,
            isCompleted = true
        )
    }

    /**
     * 전체 읽기 통계 조회 (모든 언어)
     */
    fun loadTotalProgress() {
        viewModelScope.launch {
            try {
                Log.d("ProgressViewModel", "Loading total reading progress")
                
                val totalProgress = getReadingProgressUseCase.getTotalReadingProgress()
                
                _progressState.update { currentState ->
                    currentState.copy(
                        totalCompletedAllLanguages = totalProgress.totalCompletedBooks,
                        recentBooks = totalProgress.recentlyReadBooks,
                        error = null
                    )
                }
                
                Log.d("ProgressViewModel", "Total progress loaded: ${totalProgress.totalCompletedBooks} books completed")
                
            } catch (e: Exception) {
                Log.e("ProgressViewModel", "Error loading total progress", e)
                _progressState.update { 
                    it.copy(error = "전체 통계를 불러올 수 없습니다: ${e.message}")
                }
            }
        }
    }

    /**
     * 진도율에 따른 읽기 레벨 계산
     */
    private fun calculateReadingLevel(progressPercentage: Float): ReadingLevel {
        return when {
            progressPercentage >= 0.8f -> ReadingLevel.ADVANCED   // 80% 이상
            progressPercentage >= 0.5f -> ReadingLevel.INTERMEDIATE // 50% 이상  
            progressPercentage >= 0.2f -> ReadingLevel.BEGINNER     // 20% 이상
            progressPercentage > 0f -> ReadingLevel.STARTER      // 1% 이상
            else -> ReadingLevel.NEW_READER                      // 0%
        }
    }

    /**
     * 에러 메시지 클리어
     */
    fun clearError() {
        _progressState.update { it.copy(error = null) }
    }

    /**
     * 진도 새로고침
     */
    fun refreshProgress() {
        val currentLanguage = _progressState.value.languageCode
        if (currentLanguage.isNotBlank()) {
            loadProgress(currentLanguage, _progressState.value.totalBooks)
        }
    }
}

/**
 * 읽기 진도 UI 상태 데이터 클래스
 */
data class ProgressUiState(
    val languageCode: String = "",                    // 현재 언어 코드
    val completedBooks: Int = 0,                      // 완독한 책 개수
    val totalBooks: Int = 0,                          // 전체 책 개수
    val progressPercentage: Float = 0f,               // 진도율 (0.0 ~ 1.0)
    val inProgressBooks: Int = 0,                     // 읽는 중인 책 개수
    val totalBooksStarted: Int = 0,                   // 시작한 책 개수
    val totalCompletedAllLanguages: Int = 0,          // 모든 언어 완독 책 수
    val recentBooks: List<com.timor.kidsstory.domain.usecase.progress.RecentBookInfo> = emptyList(), // 최근 읽은 책
    val readingLevel: ReadingLevel = ReadingLevel.NEW_READER, // 읽기 레벨
    val isLoading: Boolean = false,                   // 로딩 상태
    val error: String? = null                         // 에러 메시지
) {
    /**
     * 진도율을 백분율 정수로 반환 (0~100)
     */
    val progressPercentageInt: Int
        get() = (progressPercentage * 100).toInt()

    /**
     * 다음 책 권장 여부
     */
    val shouldRecommendNextBook: Boolean
        get() = inProgressBooks == 0 && completedBooks < totalBooks

    /**
     * 읽기 진도 설명 메시지
     */
    val progressDescription: String
        get() = when {
            completedBooks == 0 -> "첫 번째 책을 읽어보세요!"
            completedBooks == totalBooks -> "모든 책을 완독했어요! 대단해요!"
            progressPercentage >= 0.8f -> "거의 다 읽었어요! 조금만 더!"
            progressPercentage >= 0.5f -> "절반 이상 읽었어요! 잘하고 있어요!"
            progressPercentage >= 0.2f -> "좋은 시작이에요! 계속 읽어보세요!"
            else -> "읽기 여행을 시작해보세요!"
        }

    /**
     * 읽기 레벨 표시 텍스트
     */
    val readingLevelText: String
        get() = when (readingLevel) {
            ReadingLevel.NEW_READER -> "새로운 독자"
            ReadingLevel.STARTER -> "초보 독자"
            ReadingLevel.BEGINNER -> "초급 독자"
            ReadingLevel.INTERMEDIATE -> "중급 독자"
            ReadingLevel.ADVANCED -> "고급 독자"
        }
}

/**
 * 읽기 레벨 enum
 */
enum class ReadingLevel {
    NEW_READER,      // 0% - 아직 읽기 시작 안함
    STARTER,         // 1-19% - 읽기 시작
    BEGINNER,        // 20-49% - 초급
    INTERMEDIATE,    // 50-79% - 중급  
    ADVANCED         // 80-100% - 고급
}
