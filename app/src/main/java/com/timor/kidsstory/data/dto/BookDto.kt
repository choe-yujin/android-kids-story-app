package com.timor.kidsstory.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class StoriesResponse(
    val stories: List<BookDto>
)

@Serializable
data class BookDto(
    val storyId: String,
    val ageRange: String,
    val imageCount: Int,
    val pageCount: Int,
    val level: Int,
    val maker: String,
    val titles: TitlesDto,
    val version: String,
    val tags: List<String>,
    val size: Long,
    val coverImage: String,
    val category: String,
    val region: String,
    val estimatedReadTime: Int
)

@Serializable
data class TitlesDto(
    val tet: String,
    val ko: String,
    val en: String
)