package com.timor.kidsstory.data.repository.leveltest

import com.timor.kidsstory.data.local.leveltest.LevelTestDataSource
import com.timor.kidsstory.domain.model.leveltest.LevelTestQuestion
import com.timor.kidsstory.domain.repository.leveltest.LevelTestRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 레벨 테스트 Repository 구현
 */
@Singleton
class LevelTestRepositoryImpl @Inject constructor(
    private val dataSource: LevelTestDataSource
) : LevelTestRepository {
    
    override suspend fun getRandomQuestion(language: String, level: Int): LevelTestQuestion? {
        return dataSource.getRandomQuestion(language, level)?.let { dto ->
            LevelTestQuestion(
                question = dto.question,
                options = dto.options,
                correctAnswerIndex = dto.answerIndex
            )
        }
    }
    
    override suspend fun hasQuestionsForLevel(language: String, level: Int): Boolean {
        return dataSource.hasQuestionsForLevel(language, level)
    }
    
    override suspend fun getSupportedLanguages(): List<String> {
        return dataSource.getSupportedLanguages()
    }
    
    override suspend fun getSupportedLevels(language: String): List<Int> {
        return dataSource.getSupportedLevels(language)
    }
}
