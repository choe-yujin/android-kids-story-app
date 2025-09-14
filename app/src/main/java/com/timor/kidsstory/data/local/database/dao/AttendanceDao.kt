package com.timor.kidsstory.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.timor.kidsstory.data.local.database.entity.AttendanceEntity

@Dao
interface AttendanceDao {
    
    @Query("SELECT * FROM attendance WHERE userId = :userId ORDER BY date DESC LIMIT 1")
    suspend fun getLatestAttendance(userId: String): AttendanceEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun markAttendance(attendance: AttendanceEntity)
    
    @Query("SELECT MAX(streakCount) FROM attendance WHERE userId = :userId")
    suspend fun getMaxStreak(userId: String): Int?
    
    @Query("SELECT COUNT(*) FROM attendance WHERE userId = :userId")
    suspend fun getTotalAttendanceDays(userId: String): Int
    
    // 특정 날짜의 출석 여부 확인
    @Query("SELECT COUNT(*) > 0 FROM attendance WHERE userId = :userId AND date = :date")
    suspend fun hasAttendanceOnDate(userId: String, date: String): Boolean
    
    // 최근 N일간의 출석 기록 조회
    @Query("SELECT * FROM attendance WHERE userId = :userId ORDER BY date DESC LIMIT :limit")
    suspend fun getRecentAttendance(userId: String, limit: Int): List<AttendanceEntity>
}
