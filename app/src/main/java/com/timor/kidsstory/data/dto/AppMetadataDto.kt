package com.timor.kidsstory.data.dto

import kotlinx.serialization.Serializable

/**
 * GitHub에서 가져올 앱 메타데이터 DTO
 */
@Serializable
data class AppMetadataDto(
    val latestAppVersionCode: Int,
    val latestAppVersionName: String,
    val updateMessage: Map<String, String>,
    val updateUrl: String,
    val isUpdateRequired: Boolean = false,
    val minimumSupportedVersion: Int? = null,
    val releaseNotes: Map<String, String>? = null
)