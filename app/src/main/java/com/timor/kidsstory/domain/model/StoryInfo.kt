package com.timor.kidsstory.domain.model

/**
 * 동화 정보를 담는 데이터 모델
 * - 줄거리, 사전/사후 질문 등을 포함
 */
data class StoryInfo(
    val storyId: String,
    val summary: String? = null,
    val preQuestions: List<String> = emptyList(),
    val postQuestions: List<String> = emptyList()
)

/**
 * 이해력 확인 질문 세트
 */
data class ComprehensionChecks(
    val preQuestions: List<ComprehensionQuestion> = emptyList(),
    val postQuestions: List<ComprehensionQuestion> = emptyList()
)

/**
 * 이해력 확인 질문
 */
data class ComprehensionQuestion(
    val question: String,
    val keyConcepts: List<String> = emptyList()
)