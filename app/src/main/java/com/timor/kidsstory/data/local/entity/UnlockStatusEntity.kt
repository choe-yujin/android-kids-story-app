package com.timor.kidsstory.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 레벨 그룹별 Unlock Step 저장
 * 
 * - userId + languageCode + levelGroup 조합으로 고유 식별
 * - 각 레벨 그룹의 현재 unlock된 step 저장
 * - 앱 재시작 시에도 유지됨
 * 
 * Example:
 * - userId=user1, languageCode=ko-kr, levelGroup=level_1, unlockedStep=2
 * - userId=user1, languageCode=ko-kr, levelGroup=level_2_3, unlockedStep=1
 */
@Entity(tableName = "unlock_status")
data class UnlockStatusEntity(
    @PrimaryKey
    val id: String, // "{userId}_{languageCode}_{levelGroup}"
    
    val userId: String,
    val languageCode: String,
    
    /**
     * 레벨 그룹 식별자
     * - "level_1": 레벨 1
     * - "level_2_3": 레벨 2-3
     * - "level_4_5": 레벨 4-5
     */
    val levelGroup: String,
    
    /**
     * 현재 unlock된 step
     * - 0: 아직 아무것도 완독 안 함 (기본값)
     * - 1: unlockStep 1 해금됨 (3권 완독)
     * - 2: unlockStep 2 해금됨 (unlockStep 1 책 3권 완독)
     * - ...
     */
    val unlockedStep: Int,
    
    /**
     * 마지막 업데이트 시각
     */
    val lastUpdatedAt: Long = System.currentTimeMillis()
) {
    companion object {
        fun createId(userId: String, languageCode: String, levelGroup: String): String {
            return "${userId}_${languageCode}_${levelGroup}"
        }
    }
}
