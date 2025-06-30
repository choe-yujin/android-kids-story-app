package com.timor.epub.model

data class StoryMetadata(
    val storyId: String,
    val category: String? = null,        // nullable로 변경
    val level: Int? = null,              // nullable로 변경
    val ageRange: String? = null,        // nullable로 변경
    val maker: String? = null,           // nullable로 변경
    val region: String? = null,          // nullable로 변경
    val titles: Map<String, String> = emptyMap(),
    val tags: List<String> = emptyList(),
    val size: Long,
    val version: String = "1.0.0",
    val imageCount: Int,
    val pageCount: Int,
    val estimatedReadTime: Int,
    val coverImage: String = "",
    val credits: Credits = Credits()
)

data class AuthorInfo(
    val role: String,           // 역할 (글, 그림, 편집, 번역 등)
    val name: String,           // 이름
    val originalName: String? = null,  // 원어 이름 (괄호 안의 이름)
    val language: String? = null,      // 번역 언어 (번역자의 경우)
    val additionalInfo: String? = null // 추가 정보 (감수, 교정 등)
)

data class Credits(
    val authors: List<AuthorInfo> = emptyList(),
    val originalMaker: String? = null,
    val license: String? = null,
    val copyright: String? = null
)

/**
 * EPUB 파일에서 추출한 메타데이터
 */
data class EpubMetadata(
    val title: String? = null,
    val category: String? = null,
    val level: Int? = null,
    val ageRange: String? = null,
    val publisher: String? = null,
    val language: String? = null,
    val subjects: List<String> = emptyList(),
    val creators: List<String> = emptyList()
)