package com.timor.kidsstory.data.mapper.leveltest

import com.timor.kidsstory.data.dto.leveltest.LevelTestQuestionsDto
import com.timor.kidsstory.data.dto.leveltest.QuestionDto
import com.timor.kidsstory.domain.model.leveltest.LevelTestQuestion

/**
 * 레벨 테스트 관련 DTO ↔ Domain 모델 변환
 */
object LevelTestMapper {
    
    /**
     * QuestionDto를 LevelTestQuestion으로 변환
     */
    fun QuestionDto.toDomain(): LevelTestQuestion {
        return LevelTestQuestion(
            question = this.question,
            options = this.options,
            correctAnswerIndex = this.answerIndex
        )
    }
    
    /**
     * LevelTestQuestion을 QuestionDto로 변환
     */
    fun LevelTestQuestion.toDto(): QuestionDto {
        return QuestionDto(
            question = this.question,
            options = this.options,
            answerIndex = this.correctAnswerIndex
        )
    }
    
    /**
     * DTO 리스트를 Domain 모델 리스트로 변환
     */
    fun List<QuestionDto>.toDomain(): List<LevelTestQuestion> {
        return this.map { it.toDomain() }
    }
    
    /**
     * Domain 모델 리스트를 DTO 리스트로 변환
     */
    fun List<LevelTestQuestion>.toDto(): List<QuestionDto> {
        return this.map { it.toDto() }
    }
}
