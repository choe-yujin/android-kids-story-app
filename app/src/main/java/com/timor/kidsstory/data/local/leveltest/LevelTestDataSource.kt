package com.timor.kidsstory.data.local.leveltest

import android.content.Context
import android.util.Log
import com.timor.kidsstory.data.dto.leveltest.LevelTestQuestionsDto
import com.timor.kidsstory.data.dto.leveltest.QuestionDto
import com.timor.kidsstory.domain.manager.questionbank.QuestionBankManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 레벨 테스트 질문 데이터 소스
 * - 내부 저장소에서 질문 파일 읽기 (assets에서 복사된 파일)
 * - QuestionBankManager를 통한 파일 관리
 */
@Singleton
class LevelTestDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
    private val questionBankManager: QuestionBankManager
) {
    
    // 언어별 질문 데이터 캐시
    private val questionsCache = mutableMapOf<String, LevelTestQuestionsDto>()
    
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }
    
    /**
     * 특정 언어의 질문 데이터 로드 (캐시 사용)
     */
    private suspend fun loadQuestions(language: String): LevelTestQuestionsDto {
        // 캐시에서 먼저 확인
        questionsCache[language]?.let { return it }
        
        Log.d(TAG, "📚 Loading questions for language: $language")
        
        try {
            // 최초 실행시 assets에서 내부 저장소로 복사 확인
            questionBankManager.initializeQuestionBanks()
            
            // 항상 내부 저장소에서 읽기
            val questionFile = questionBankManager.getQuestionBankFile(language)
            
            if (!questionFile.exists()) {
                throw IllegalStateException("Question bank file not found for language: $language")
            }
            
            val jsonString = questionFile.readText()
            val questionsDto = json.decodeFromString<LevelTestQuestionsDto>(jsonString)
            
            // 캐시에 저장
            questionsCache[language] = questionsDto
            
            Log.d(TAG, "✅ Successfully loaded ${questionsDto.levels.size} levels for language: $language")
            return questionsDto
            
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to load questions for language: $language", e)
            throw e
        }
    }
    
    /**
     * 특정 언어와 레벨의 모든 문제 반환
     * 
     * @param language 언어 코드 (en, ko, tet)
     * @param level 레벨 (1-5)
     * @return 문제 리스트 또는 빈 리스트
     */
    suspend fun getQuestionsForLevel(language: String, level: Int): List<QuestionDto> {
        return try {
            val data = loadQuestions(language)
            data.levels[level.toString()] ?: emptyList()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get questions for language: $language, level: $level", e)
            emptyList()
        }
    }
    
    /**
     * 특정 언어와 레벨의 랜덤 문제 반환
     * 
     * @param language 언어 코드
     * @param level 레벨
     * @return 랜덤 문제 또는 null
     */
    suspend fun getRandomQuestion(language: String, level: Int): QuestionDto? {
        val questions = getQuestionsForLevel(language, level)
        return if (questions.isNotEmpty()) {
            questions.random()
        } else {
            Log.w(TAG, "No questions found for language: $language, level: $level")
            null
        }
    }
    
    /**
     * 특정 언어와 레벨에 문제가 있는지 확인
     * 
     * @param language 언어 코드
     * @param level 레벨
     * @return 문제 존재 여부
     */
    suspend fun hasQuestionsForLevel(language: String, level: Int): Boolean {
        return getQuestionsForLevel(language, level).isNotEmpty()
    }
    
    /**
     * 지원하는 언어 목록 반환
     * 
     * @return 언어 코드 리스트
     */
    suspend fun getSupportedLanguages(): List<String> {
        return try {
            // 질문 은행 파일이 있는 언어들 반환
            listOf("ko", "en", "tet").filter { language ->
                questionBankManager.isQuestionBankAvailable(language)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get supported languages", e)
            emptyList()
        }
    }
    
    /**
     * 특정 언어에서 지원하는 레벨 목록 반환
     * 
     * @param language 언어 코드
     * @return 레벨 리스트
     */
    suspend fun getSupportedLevels(language: String): List<Int> {
        return try {
            val data = loadQuestions(language)
            data.levels.keys.mapNotNull { it.toIntOrNull() }.sorted()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get supported levels for language: $language", e)
            emptyList()
        }
    }
    
    companion object {
        private const val TAG = "LevelTestDataSource"
    }
}
