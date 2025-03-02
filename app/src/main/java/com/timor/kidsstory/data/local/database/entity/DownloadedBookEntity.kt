package com.timor.kidsstory.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

// 다운로드된 책 정보를 저장하는 엔티티
@Entity(tableName = "downloaded_books")
@Serializable
data class DownloadedBookEntity(
    @PrimaryKey val id: Int,
    val storyId: String,
    val language: String,
    val title: String,
    val coverImagePath: String,
    val contentJsonPath: String,
    val hasImages: Boolean,
    val downloadDate: Long = System.currentTimeMillis()
)