package com.timor.kidsstory.data.remote.model

data class BookMetadata(
    val id: Int,
    val title: Map<String, String>,
    val cover: Map<String, String>,
    val download: Map<String, String>,
    val images: String,
    val languages: List<String>
)