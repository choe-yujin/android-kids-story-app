package com.timor.kidsstory.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class PageContentResponse(
    val storyId: String,
    val title: String,
    val pages: List<PageDto>
)

@Serializable
data class PageDto(
    val pageNumber: Int,
    val texts: List<String>
)