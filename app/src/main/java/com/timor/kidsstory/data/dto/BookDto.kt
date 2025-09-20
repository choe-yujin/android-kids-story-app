package com.timor.kidsstory.data.dto

import kotlinx.serialization.Serializable

/**
 * 통합 메타데이터 구조 - 내장/원격 동일
 * 파일: books_metadata.json
 */
@Serializable
data class UnifiedBooksMetadata(
    val version: Int,
    val lastUpdated: String,
    val books: List<UnifiedBookMetadata>
)

@Serializable
data class UnifiedBookMetadata(
    val id: Int,
    val level: Int,
    val category: String,
    val countryOfOrigin: String,
    val aiFeatures: List<String> = emptyList(),
    val imageAssetsVersion: Int = 100,
    val languages: Map<String, LanguageContent>
)

@Serializable
data class LanguageContent(
    val title: String,
    val contentVersion: Int,
    val isBundled: Boolean = true,
    val tags: List<String> = emptyList()
)

/**
 * 개별 동화책 콘텐츠 구조 - 내장/원격 동일
 * 파일: content/{bookId}_{language}.json
 */
@Serializable
data class UnifiedBookContent(
    val bookId: Int,
    val language: String,
    val title: String,
    val pages: List<UnifiedPageDto>,
    val contributors: Map<String, List<String>> = emptyMap(),
    val copyright: String = "",
    val summary: String = "", // AI 기능용
    val comprehensionChecks: ComprehensionChecks? = null,
    val vocabulary: List<VocabularyItem> = emptyList(),
    val missions: List<Mission> = emptyList(),
    val sourceLanguage: String = "",
    val license: String = "",
    val copyrightHolder: String = "",
    val attributionText: String = ""
)

@Serializable
data class UnifiedPageDto(
    val pageNumber: Int,
    val image: String = "",
    val texts: List<String> = emptyList(),
    val pageType: String = "SPLIT"
)

/**
 * AI 기능 관련 데이터 클래스들
 */
@Serializable
data class ComprehensionChecks(
    val preQuestions: List<Question> = emptyList(),
    val postQuestions: List<Question> = emptyList()
)

@Serializable
data class Question(
    val question: String,
    val keyConcepts: List<String>
)

@Serializable
data class VocabularyItem(
    val word: String,
    val definition: String,
    val pageNumber: Int
)

@Serializable
data class Mission(
    val title: String,
    val description: String,
    val type: String,
    val relatedPages: List<Int> = emptyList()
)

/**
 * 기존 호환성용 DTO (하위 호환성 - 점진적 제거 예정)
 */
@Serializable
data class PageContentResponse(
    val storyId: String,
    val title: String,
    val pages: List<PageDto>,
    val contributors: Map<String, Map<String, List<String>>>? = null,
    val sponsors: Map<String, List<String>>? = null,
    val copyright: String = "",
    val originalCopyright: String? = null,
)

@Serializable
data class PageDto(
    val pageNumber: Int,
    val texts: List<String>
)
