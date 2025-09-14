package com.timor.kidsstory.data.local.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey

/**
 * 레벨별 잠금 해제 시스템을 위한 Entity
 * - 레벨 그룹별 현재 진행 단계 관리
 * - 3권씩 순차 오픈 로직 지원
 */
@Entity(
    tableName = "unlock_progress",
    primaryKeys = ["userId", "levelGroup", "language"],
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class UnlockProgressEntity(
    val userId: String = "default_user",
    val levelGroup: String, // "Level 1", "Level 2-3", "Level 4-5"
    val language: String,
    val currentStep: Int = 1, // 1-3 단계
    val lastUpdated: Long = System.currentTimeMillis()
)
