package com.timor.kidsstory.data.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

/**
 * 하이브리드 메타데이터 구조 - 완전한 버전 관리 지원
 */
@Serializable
data class HybridBooksMetadata(
    val version: Int,
    val lastUpdated: String,
    val aiModels: Map<String, AiModelInfo>? = null,
    val books: List<HybridBookMetadata>
)

@Serializable
data class HybridBookMetadata(
    val id: Int,
    val level: Int,
    val unlockStep: Int = 0,
    val category: String,
    val countryOfOrigin: String,
    val aiFeatures: List<String> = emptyList(),
    val imageAssetsUrl: String,
    val imageAssetsVersion: Int,
    val imageAssetsSize: Long = 0,
    val languages: Map<String, HybridLanguageContent>
)

@Serializable 
data class HybridLanguageContent(
    val title: String,
    val summary: String = "",
    val coverImageUrl: String,
    val contentUrl: String,
    val coverVersion: Int,
    val contentVersion: Int,
    val coverImageSize: Long = 0,
    val contentSize: Long = 0,
    val isBundled: Boolean = true,
    val tags: List<String> = emptyList()
)

@Serializable
data class AiModelInfo(
    @SerialName("glow_tts_url") val glowTtsUrl: String,
    @SerialName("hifigan_url") val hifiganUrl: String,
    @SerialName("config_url") val configUrl: String? = null,
    val version: Int,
    val size: Long
)
