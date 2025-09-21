package com.timor.kidsstory.domain.repository.leveltest

import com.timor.kidsstory.domain.model.leveltest.LevelTestQuestion

/**
 * 레벨 테스트 Repository 인터페이스
 */
interface LevelTestRepository {
    
    /**
     * 특정 언어와 레벨의 랜덤 문제 가져오기
     */
    suspend fun getRandomQuestion(language: String, level: Int): LevelTestQuestion?
    
    /**
     * 특정 언어와 레벨에 문제가 있는지 확인
     */
    suspend fun hasQuestionsForLevel(language: String, level: Int): Boolean
    
    /**
     * 지원하는 언어 목록
     */
    suspend fun getSupportedLanguages(): List<String>
    
    /**
     * 특정 언어에서 지원하는 레벨 목록
     */
    suspend fun getSupportedLevels(language: String): List<Int>
}
