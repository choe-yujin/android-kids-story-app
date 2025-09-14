package com.timor.kidsstory.data.local.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey

/**
 * 사용자별 책 상호작용 데이터를 저장하는 핵심 Entity
 * - 읽기 진도, 북마크, 완독 여부 등 모든 사용자 행동 추적
 * - 책이 다운로드되지 않아도 북마크/읽음 상태 추적 가능
 * - 다중 사용자 지원을 위한 확장 가능한 구조
 */
@Entity(
    tableName = "user_book_interactions",
    primaryKeys = ["userId", "bookId", "language"],
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class UserBookInteractionEntity(
    val userId: String = "default_user",
    val bookId: Int,
    val language: String,
    val currentPage: Int = 0,
    val totalPages: Int = 0,
    val isCompleted: Boolean = false,
    val completedAt: Long? = null,
    val startedAt: Long? = null,
    val lastReadAt: Long? = null,
    val readCount: Int = 0,
    val isBookmarked: Boolean = false,
    val bookmarkDate: Long? = null
)
