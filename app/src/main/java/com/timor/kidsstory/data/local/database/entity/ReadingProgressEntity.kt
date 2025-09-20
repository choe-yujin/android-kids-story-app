package com.timor.kidsstory.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

/**
 * 읽기 진도를 저장하는 Room Entity
 */
@Entity(tableName = "reading_progress")
data class ReadingProgressEntity(
    @PrimaryKey
    val id: String, // userId + "_" + bookId 형태의 복합 키
    
    /**
     * 사용자 ID
     */
    val userId: String,
    
    /**
     * 책 ID
     */
    val bookId: String,
    
    /**
     * 언어 코드
     */
    val languageCode: String,
    
    /**
     * 현재 페이지
     */
    val currentPage: Int = 0,
    
    /**
     * 총 페이지 수
     */
    val totalPages: Int = 0,
    
    /**
     * 완료 여부
     */
    val isCompleted: Boolean = false,
    
    /**
     * 마지막 읽은 시간 (timestamp)
     */
    val lastReadAtTimestamp: Long? = null,
    
    /**
     * 읽기 시작 시간 (timestamp)
     */
    val startedAtTimestamp: Long? = null,
    
    /**
     * 완료 시간 (timestamp)
     */
    val completedAtTimestamp: Long? = null,
    
    /**
     * 생성 시간
     */
    val createdAt: Long = System.currentTimeMillis(),
    
    /**
     * 업데이트 시간
     */
    val updatedAt: Long = System.currentTimeMillis()
) {
    companion object {
        /**
         * Domain 모델과 기본 정보로 Entity 생성
         */
        fun create(
            userId: String,
            bookId: String,
            languageCode: String,
            progress: com.timor.kidsstory.domain.model.ReadingProgress
        ): ReadingProgressEntity {
            return ReadingProgressEntity(
                id = "${userId}_${bookId}_${languageCode}",
                userId = userId,
                bookId = bookId,
                languageCode = languageCode,
                currentPage = progress.currentPage,
                totalPages = progress.totalPages,
                isCompleted = progress.isCompleted,
                lastReadAtTimestamp = progress.lastReadAt?.let { 
                    java.time.ZoneOffset.UTC.let { offset ->
                        it.toEpochSecond(offset) * 1000
                    }
                },
                startedAtTimestamp = progress.startedAt?.let {
                    java.time.ZoneOffset.UTC.let { offset ->
                        it.toEpochSecond(offset) * 1000
                    }
                },
                completedAtTimestamp = progress.completedAt?.let {
                    java.time.ZoneOffset.UTC.let { offset ->
                        it.toEpochSecond(offset) * 1000
                    }
                }
            )
        }
    }
    
    /**
     * Entity를 Domain 모델로 변환
     */
    fun toDomainModel(): com.timor.kidsstory.domain.model.ReadingProgress {
        return com.timor.kidsstory.domain.model.ReadingProgress(
            currentPage = currentPage,
            totalPages = totalPages,
            isCompleted = isCompleted,
            lastReadAt = lastReadAtTimestamp?.let {
                LocalDateTime.ofEpochSecond(it / 1000, 0, java.time.ZoneOffset.UTC)
            },
            startedAt = startedAtTimestamp?.let {
                LocalDateTime.ofEpochSecond(it / 1000, 0, java.time.ZoneOffset.UTC)
            },
            completedAt = completedAtTimestamp?.let {
                LocalDateTime.ofEpochSecond(it / 1000, 0, java.time.ZoneOffset.UTC)
            }
        )
    }
}
