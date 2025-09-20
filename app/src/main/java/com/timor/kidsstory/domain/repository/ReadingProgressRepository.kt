package com.timor.kidsstory.domain.repository

import com.timor.kidsstory.domain.model.ReadingProgress
import com.timor.kidsstory.domain.model.ReadingProgressSummary

/**
 * 읽기 진도 관련 데이터 접근을 위한 Repository 인터페이스
 * - 사용자의 책 읽기 진도 관리
 * - Domain Layer의 인터페이스
 */
interface ReadingProgressRepository {
    
    /**
     * 완료된 책 수 조회
     * @param userId 사용자 ID
     * @param languageCode 언어 코드
     * @return 완료된 책 수
     */
    suspend fun getCompletedBooksCount(userId: String, languageCode: String): Int
    
    /**
     * 특정 책의 읽기 진도 조회
     * @param userId 사용자 ID
     * @param bookId 책 ID
     * @return 읽기 진도 정보
     */
    suspend fun getReadingProgress(userId: String, bookId: String): ReadingProgress?
    
    /**
     * 읽기 진도 업데이트
     * @param userId 사용자 ID
     * @param bookId 책 ID
     * @param progress 진도 정보
     * @param languageCode 언어 코드 (선택사항)
     */
    suspend fun updateReadingProgress(
        userId: String, 
        bookId: String, 
        progress: ReadingProgress,
        languageCode: String? = null
    )
    
    /**
     * 책 완료 처리
     * @param userId 사용자 ID
     * @param bookId 책 ID
     */
    suspend fun markBookCompleted(userId: String, bookId: String)
    
    /**
     * 사용자의 전체 읽기 진도 요약 조회
     * @param userId 사용자 ID
     * @param languageCode 언어 코드
     * @return 읽기 진도 요약
     */
    suspend fun getReadingProgressSummary(userId: String, languageCode: String): ReadingProgressSummary
    
    /**
     * 특정 언어의 모든 책 진도 조회
     * @param userId 사용자 ID
     * @param languageCode 언어 코드
     * @return 책별 진도 정보 맵
     */
    suspend fun getAllProgressByLanguage(userId: String, languageCode: String): Map<String, ReadingProgress>
    
    /**
     * 최근 읽은 책 목록 조회
     * @param userId 사용자 ID
     * @param limit 조회할 개수
     * @return 최근 읽은 책 ID 목록
     */
    suspend fun getRecentlyReadBooks(userId: String, limit: Int = 10): List<String>
    
    /**
     * 책 읽기 시작 기록
     * @param userId 사용자 ID
     * @param bookId 책 ID
     */
    suspend fun startReading(userId: String, bookId: String)
}
