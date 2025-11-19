package com.timor.kidsstory.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey 
    val userId: String = "default_user",
    val username: String = "Guest",
    val userMode: String = "OFFLINE", // "OFFLINE", "ONLINE", "TEACHER"
    val createdAt: Long = System.currentTimeMillis(),
    val lastActiveAt: Long = System.currentTimeMillis()
)
