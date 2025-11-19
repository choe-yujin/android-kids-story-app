package com.timor.kidsstory.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.timor.kidsstory.data.local.database.entity.UnlockProgressEntity

@Dao
interface UnlockProgressDao {
    
    @Query("SELECT currentStep FROM unlock_progress WHERE userId = :userId AND levelGroup = :group AND language = :language")
    suspend fun getCurrentStep(userId: String, group: String, language: String): Int?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateStep(progress: UnlockProgressEntity)
    
    @Query("SELECT * FROM unlock_progress WHERE userId = :userId AND language = :language")
    suspend fun getAllProgress(userId: String, language: String): List<UnlockProgressEntity>
    
    // 특정 레벨 그룹의 진행 상황 조회
    @Query("SELECT * FROM unlock_progress WHERE userId = :userId AND levelGroup = :group AND language = :language")
    suspend fun getProgressForGroup(userId: String, group: String, language: String): UnlockProgressEntity?
    
    // 다음 단계로 업데이트
    @Query("UPDATE unlock_progress SET currentStep = currentStep + 1, lastUpdated = :timestamp WHERE userId = :userId AND levelGroup = :group AND language = :language AND currentStep < 3")
    suspend fun incrementStep(userId: String, group: String, language: String, timestamp: Long): Int
}
