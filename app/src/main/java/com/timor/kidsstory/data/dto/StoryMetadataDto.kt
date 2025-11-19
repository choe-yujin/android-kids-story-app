package com.timor.kidsstory.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class StoriesResponse(
    val stories: List<BookMetadata>
)

@Serializable
data class BookMetadata(
    val storyId: String,
    val ageRange: String,
    val imageCount: Int,
    val pageCount: Int,
    val level: Int,
    val maker: String,
    val titles: Map<String, String>,
    val version: String,
    val tags: List<String>,
    val size: Long,
    val coverImage: String,
    val category: String,
    val region: String,
    val estimatedReadTime: Int,
    val bookVersion: Int,
    val contributors: Map<String, Map<String, List<String>>>? = null,
    val sponsors: Map<String, String?>? = null,
    val copyright: String,
    val originalCopyright: String? = null
)
