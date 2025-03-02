package com.timor.kidsstory.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

// 다운로드 가능한 책 정보를 캐시하는 엔티티
@Entity(tableName = "available_books")
@Serializable
data class AvailableBookEntity(
    @PrimaryKey val id: Int,
    @Serializable
    val titles: Map<String, String>,
    @Serializable
    val covers: Map<String, String>,
    @Serializable
    val downloads: Map<String, String>,
    @Serializable
    val languages: List<String>,
    val lastUpdated: Long = System.currentTimeMillis()
)