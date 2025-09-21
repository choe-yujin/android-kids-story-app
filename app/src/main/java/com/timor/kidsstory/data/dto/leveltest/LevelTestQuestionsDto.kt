package com.timor.kidsstory.data.dto.leveltest

import kotlinx.serialization.Serializable

/**
 * Level Test Questions JSON DTO
 * 실제 assets JSON 파일 구조와 매칭
 */
@Serializable
data class LevelTestQuestionsDto(
    val version: Int,
    val language: String,
    val levels: Map<String, List<QuestionDto>>
)

/**
 * 개별 질문 DTO
 */
@Serializable
data class QuestionDto(
    val question: String,
    val options: List<String>,
    val answerIndex: Int
)
