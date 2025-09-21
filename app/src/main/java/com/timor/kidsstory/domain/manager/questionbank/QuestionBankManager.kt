package com.timor.kidsstory.domain.manager.questionbank

import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 질문 은행 파일 관리자
 * - assets에서 내부 저장소로 질문 파일 복사 (최초 실행시)
 * - 향후 원격 업데이트 기능을 위한 기반 제공
 */
@Singleton
class QuestionBankManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val questionsDir = File(context.filesDir, "questions")
    
    /**
     * 최초 실행시 assets에서 내부 저장소로 질문 파일들 복사
     * - 각 언어별 질문 파일을 내부 저장소로 복사하여 일관된 파일 접근 구조 구축
     */
    suspend fun initializeQuestionBanks() = withContext(Dispatchers.IO) {
        Log.d(TAG, "🎯 Initializing question banks...")
        
        if (!questionsDir.exists()) {
            questionsDir.mkdirs()
            Log.d(TAG, "📁 Created questions directory: ${questionsDir.absolutePath}")
        }
        
        // 지원하는 언어들의 질문 파일 복사
        SUPPORTED_LANGUAGES.forEach { language ->
            val assetFileName = "level_test_questions_$language.json"
            val targetFile = File(questionsDir, assetFileName)
            
            if (!targetFile.exists()) {
                try {
                    copyAssetToInternal(assetFileName, targetFile)
                    Log.d(TAG, "✅ Copied question bank for language: $language")
                } catch (e: Exception) {
                    Log.e(TAG, "❌ Failed to copy question bank for language: $language", e)
                    throw e
                }
            } else {
                Log.d(TAG, "📝 Question bank already exists for language: $language")
            }
        }
        
        Log.d(TAG, "🎉 Question bank initialization completed")
    }
    
    /**
     * 특정 언어의 질문 은행 파일이 존재하는지 확인
     */
    fun isQuestionBankAvailable(language: String): Boolean {
        val questionFile = File(questionsDir, "level_test_questions_$language.json")
        return questionFile.exists()
    }
    
    /**
     * 질문 은행 파일의 경로 반환
     */
    fun getQuestionBankFile(language: String): File {
        return File(questionsDir, "level_test_questions_$language.json")
    }
    
    /**
     * v2.0에서 추가될 원격 업데이트 함수 (현재는 빈 구현)
     * - 향후 메타데이터 버전 체크 후 GitHub에서 다운로드
     * - 기존 파일을 덮어쓰기만 하면 됨
     */
    suspend fun checkAndUpdateQuestionBank(language: String) {
        // TODO: v2.0에서 구현
        // 1. 메타데이터에서 서버 버전 확인
        // 2. 로컬 버전과 비교
        // 3. 새 버전이 있으면 다운로드하여 기존 파일 덮어쓰기
        Log.d(TAG, "🔄 Question bank update check (v2.0 feature) - language: $language")
    }
    
    /**
     * assets에서 내부 저장소로 파일 복사
     */
    private fun copyAssetToInternal(assetFileName: String, targetFile: File) {
        context.assets.open(assetFileName).use { input ->
            targetFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }
    }
    
    companion object {
        private const val TAG = "QuestionBankManager"
        
        // 지원하는 언어 목록
        private val SUPPORTED_LANGUAGES = listOf("ko", "en", "tet")
    }
}
