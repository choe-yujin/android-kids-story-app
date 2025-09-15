package com.timor.kidsstory.domain.model

import java.time.LocalDateTime

/**
 * 개별 책의 읽기 진도 정보
 */
data class ReadingProgress(
    /**
     * 현재 읽고 있는 페이지
     */
    val currentPage: Int = 0,
    
    /**
     * 총 페이지 수
     */
    val totalPages: Int = 0,
    
    /**
     * 읽기 완료 여부
     */
    val isCompleted: Boolean = false,
    
    /**
     * 마지막 읽은 시간
     */
    val lastReadAt: LocalDateTime? = null,
    
    /**
     * 읽기 시작 시간
     */
    val startedAt: LocalDateTime? = null,
    
    /**
     * 완료 시간
     */
    val completedAt: LocalDateTime? = null
) {
    /**
     * 진도율 계산 (0.0 ~ 1.0)
     */
    fun getProgressPercentage(): Float {
        return if (totalPages > 0) {
            currentPage.toFloat() / totalPages.toFloat()
        } else {
            0f
        }
    }
    
    /**
     * 진도율 계산 (0 ~ 100)
     */
    fun getProgressPercentageInt(): Int {
        return (getProgressPercentage() * 100).toInt()
    }
    
    companion object {
        /**
         * 새로운 읽기 시작
         */
        fun startNew(totalPages: Int): ReadingProgress {
            return ReadingProgress(
                currentPage = 0,
                totalPages = totalPages,
                startedAt = LocalDateTime.now(),
                lastReadAt = LocalDateTime.now()
            )
        }
        
        /**
         * 읽기 완료
         */
        fun completed(totalPages: Int): ReadingProgress {
            val now = LocalDateTime.now()
            return ReadingProgress(
                currentPage = totalPages,
                totalPages = totalPages,
                isCompleted = true,
                lastReadAt = now,
                completedAt = now
            )
        }
    }
}

/**
 * 사용자의 전체 읽기 진도 요약
 */
data class ReadingProgressSummary(
    /**
     * 완료된 책 수
     */
    val completedBooks: Int = 0,
    
    /**
     * 총 책 수
     */
    val totalBooks: Int = 0,
    
    /**
     * 읽고 있는 책 수
     */
    val readingBooks: Int = 0,
    
    /**
     * 전체 진도율 (0.0 ~ 1.0)
     */
    val overallProgressPercentage: Float = 0f,
    
    /**
     * 총 읽은 페이지 수
     */
    val totalPagesRead: Int = 0,
    
    /**
     * 총 페이지 수
     */
    val totalPages: Int = 0
) {
    /**
     * 전체 진도율 (퍼센트)
     */
    fun getOverallProgressPercentageInt(): Int {
        return (overallProgressPercentage * 100).toInt()
    }
    
    companion object {
        /**
         * 기본 진도 요약 (처음 사용자)
         */
        fun default() = ReadingProgressSummary()
    }
}
