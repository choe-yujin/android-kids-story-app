package com.timor.kidsstory.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class ContributorDto(
    val role: String,
    val name: String,
)