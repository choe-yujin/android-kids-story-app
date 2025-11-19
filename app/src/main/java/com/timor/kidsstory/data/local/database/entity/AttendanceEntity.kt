package com.timor.kidsstory.data.local.database.entity

import androidx.room.Entity
import java.time.LocalDate

/**
 * 출석 기록을 저장하는 Room Entity
 * - 기존 Database 스키마에 맞춘 복합 키 구조
 */
@Entity(
    tableName = "attendance",
    primaryKeys = ["userId", "date"]
)
data class AttendanceEntity(
    /**
     * 사용자 ID
     */
    val userId: String = "default_user",
    
    /**
     * 출석 날짜 (yyyy-MM-dd 형식)
     */
    val date: String,
    
    /**
     * 출석 시간 (timestamp)
     */
    val timestamp: Long = System.currentTimeMillis(),
    
    /**
     * 연속 출석 일수 (해당 날짜까지의)
     */
    val streakCount: Int = 1
) {
    companion object {
        /**
         * LocalDate와 사용자 정보로 Entity 생성
         */
        fun fromDate(
            userId: String = "default_user",
            date: LocalDate, 
            streakCount: Int = 1
        ): AttendanceEntity {
            return AttendanceEntity(
                userId = userId,
                date = date.toString(),
                timestamp = System.currentTimeMillis(),
                streakCount = streakCount
            )
        }
    }
    
    /**
     * Entity를 LocalDate로 변환
     */
    fun toLocalDate(): LocalDate {
        return LocalDate.parse(date)
    }
}
