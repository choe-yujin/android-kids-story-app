package com.timor.kidsstory.data.local.database.entity

import androidx.room.Entity
import kotlinx.serialization.Serializable

// 다운로드된 책 정보를 저장하는 엔티티
@Entity(
    tableName = "downloaded_books",
    primaryKeys = ["id", "language"]  // 책 ID와 언어를 복합 키로 사용
)
@Serializable
data class DownloadedBookEntity(
    val id: Int,
    val storyId: String,
    val language: String,
    val title: String,
    val coverImagePath: String,
    val contentJsonPath: String,
    val hasImages: Boolean,
    val downloadDate: Long = System.currentTimeMillis(),
    val category: String = ""
)