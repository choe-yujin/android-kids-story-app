package com.timor.kidsstory.domain.usecase.leveltest

import com.timor.kidsstory.domain.model.leveltest.LevelTestQuestion
import com.timor.kidsstory.domain.repository.leveltest.LevelTestRepository
import javax.inject.Inject

/**
 * 레벨 테스트 문제 조회 UseCase
 */
class GetLevelTestQuestionUseCase @Inject constructor(
    private val levelTestRepository: LevelTestRepository
) {
    
    /**
     * 특정 언어와 레벨의 랜덤 문제 조회
     * 
     * @param language 언어 코드 (en, ko, tet)
     * @param level 레벨 (1-5)
     * @return 문제 또는 null
     */
    suspend operator fun invoke(language: String, level: Int): Result<LevelTestQuestion> {
        return try {
            val question = levelTestRepository.getRandomQuestion(language, level)
            if (question != null) {
                Result.success(question)
            } else {
                Result.failure(Exception("No questions found for language: $language, level: $level"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
