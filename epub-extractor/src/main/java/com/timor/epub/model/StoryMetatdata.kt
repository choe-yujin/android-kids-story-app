package com.timor.epub.model

data class StoryMetadata(
    val storyId: String,
    val category: String,
    val level: Int,
    val ageRange: String,
    val maker: String,
    val region: String,
    val titles: Map<String, String>,
    val tags: List<String>,
    val size: Long,
    val version: String,
    val imageCount: Int,
    val pageCount: Int,
    val estimatedReadTime: Int,
    val coverImage: String
)