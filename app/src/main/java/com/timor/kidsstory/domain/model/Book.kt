package com.timor.kidsstory.domain.model

data class Book(
    val storyId: String,
    val title: String,
    val coverImage: String,
    val level: Int,
    val category: String,
    val pageCount: Int,
    val isDownloaded: Boolean = true,
    val isBookmarked: Boolean = false
)