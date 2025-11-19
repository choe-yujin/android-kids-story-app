package com.timor.kidsstory.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.timor.kidsstory.data.local.database.entity.AttendanceEntity

/**
 * 출석 데이터 접근을 위한 DAO
 * - userId를 포함한 복합 키 지원
 */
@Dao
interface AttendanceDao {
    
    /**
     * 출석 기록 추가 (중복 시 무시)
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAttendance(attendance: AttendanceEntity)
    
    /**
     * 출석 기록 업데이트
     */
    @Update
    suspend fun updateAttendance(attendance: AttendanceEntity)
    
    /**
     * 특정 사용자의 특정 날짜 출석 기록 조회
     */
    @Query("SELECT * FROM attendance WHERE userId = :userId AND date = :date LIMIT 1")
    suspend fun getAttendanceByUserAndDate(userId: String, date: String): AttendanceEntity?
    
    /**
     * 특정 사용자의 모든 출석 기록 조회 (날짜 순 정렬)
     */
    @Query("SELECT * FROM attendance WHERE userId = :userId ORDER BY date ASC")
    suspend fun getAllAttendancesByUser(userId: String): List<AttendanceEntity>
    
    /**
     * 특정 사용자의 특정 기간 출석 기록 조회
     */
    @Query("SELECT * FROM attendance WHERE userId = :userId AND date BETWEEN :startDate AND :endDate ORDER BY date ASC")
    suspend fun getAttendancesBetweenByUser(userId: String, startDate: String, endDate: String): List<AttendanceEntity>
    
    /**
     * 특정 사용자의 총 출석 일수 조회
     */
    @Query("SELECT COUNT(*) FROM attendance WHERE userId = :userId")
    suspend fun getTotalAttendanceCountByUser(userId: String): Int
    
    /**
     * 특정 사용자의 최근 N일간 출석 기록 조회
     */
    @Query("SELECT * FROM attendance WHERE userId = :userId ORDER BY date DESC LIMIT :limit")
    suspend fun getRecentAttendancesByUser(userId: String, limit: Int = 30): List<AttendanceEntity>
    
    /**
     * 특정 사용자의 마지막 출석일 조회
     */
    @Query("SELECT * FROM attendance WHERE userId = :userId ORDER BY date DESC LIMIT 1")
    suspend fun getLastAttendanceByUser(userId: String): AttendanceEntity?
    
    /**
     * 특정 사용자의 특정 날짜 이후 출석 기록 조회 (연속성 확인용)
     */
    @Query("SELECT * FROM attendance WHERE userId = :userId AND date >= :fromDate ORDER BY date ASC")
    suspend fun getAttendancesFromByUser(userId: String, fromDate: String): List<AttendanceEntity>
    
    /**
     * 특정 사용자의 가장 긴 연속 출석 기록 조회
     */
    @Query("SELECT MAX(streakCount) FROM attendance WHERE userId = :userId")
    suspend fun getMaxStreakByUser(userId: String): Int?
    
    // 호환성을 위한 기존 메서드들 (기본 사용자 사용)
    /**
     * 특정 날짜의 출석 기록 조회 (기본 사용자)
     */
    @Query("SELECT * FROM attendance WHERE userId = 'default_user' AND date = :date LIMIT 1")
    suspend fun getAttendanceByDate(date: String): AttendanceEntity?
    
    /**
     * 모든 출석 기록 조회 (기본 사용자)
     */
    @Query("SELECT * FROM attendance WHERE userId = 'default_user' ORDER BY date ASC")
    suspend fun getAllAttendances(): List<AttendanceEntity>
    
    /**
     * 특정 기간의 출석 기록 조회 (기본 사용자)
     */
    @Query("SELECT * FROM attendance WHERE userId = 'default_user' AND date BETWEEN :startDate AND :endDate ORDER BY date ASC")
    suspend fun getAttendancesBetween(startDate: String, endDate: String): List<AttendanceEntity>
    
    /**
     * 총 출석 일수 조회 (기본 사용자)
     */
    @Query("SELECT COUNT(*) FROM attendance WHERE userId = 'default_user'")
    suspend fun getTotalAttendanceCount(): Int
    
    /**
     * 최근 N일간의 출석 기록 조회 (기본 사용자)
     */
    @Query("SELECT * FROM attendance WHERE userId = 'default_user' ORDER BY date DESC LIMIT :limit")
    suspend fun getRecentAttendances(limit: Int = 30): List<AttendanceEntity>
    
    /**
     * 마지막 출석일 조회 (기본 사용자)
     */
    @Query("SELECT * FROM attendance WHERE userId = 'default_user' ORDER BY date DESC LIMIT 1")
    suspend fun getLastAttendance(): AttendanceEntity?
    
    /**
     * 특정 날짜 이후의 연속된 출석 기록 조회 (기본 사용자)
     */
    @Query("SELECT * FROM attendance WHERE userId = 'default_user' AND date >= :fromDate ORDER BY date ASC")
    suspend fun getAttendancesFrom(fromDate: String): List<AttendanceEntity>
}
