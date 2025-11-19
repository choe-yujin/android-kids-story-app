package com.timor.kidsstory.data.dto

import kotlinx.serialization.Serializable

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


