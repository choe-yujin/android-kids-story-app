package com.timor.kidsstory.data.local.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey

/**
 * 출석 체크 시스템을 위한 Entity
 * - 일별 출석 기록 및 연속 출석 일수(streak) 관리
 * - 사용자별 출석 데이터 분리
 */
@Entity(
    tableName = "attendance",
    primaryKeys = ["userId", "date"],
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class AttendanceEntity(
    val userId: String = "default_user",
    val date: String, // yyyy-MM-dd format
    val timestamp: Long = System.currentTimeMillis(),
    val streakCount: Int = 1
)
