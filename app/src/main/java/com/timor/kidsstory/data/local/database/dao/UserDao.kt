package com.timor.kidsstory.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.timor.kidsstory.data.local.database.entity.UserEntity

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE userId = :userId")
    suspend fun getUser(userId: String): UserEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)
    
    @Query("SELECT userId FROM users LIMIT 1")
    suspend fun getCurrentUserId(): String?
    
    @Query("UPDATE users SET lastActiveAt = :timestamp WHERE userId = :userId")
    suspend fun updateLastActive(userId: String, timestamp: Long)
}
