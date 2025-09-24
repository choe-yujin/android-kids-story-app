package com.timor.kidsstory.domain.usecase.book

import com.timor.kidsstory.domain.model.Book
import com.timor.kidsstory.data.local.database.dao.UnlockProgressDao
import javax.inject.Inject

/**
 * Unlock 정책 UseCase
 * - 레벨 그룹별 unlock step 확인
 * - 다운로드 가능한 책 필터링 (unlock step 기준)
 */
class CheckUnlockStatusUseCase @Inject constructor(
    private val unlockProgressDao: UnlockProgressDao
) {
    /**
     * 🆕 DB에서 현재 Unlock된 Step 조회 (영구 저장된 값)
     */
    suspend fun getUnlockedSteps(
        userId: String,
        languageCode: String
    ): Map<String, Int> {
        val unlockedSteps = mutableMapOf<String, Int>()
        
        // DB에서 조회
        val allProgress = unlockProgressDao.getAllProgress(userId, languageCode)
        
        allProgress.forEach { progress ->
            unlockedSteps[progress.levelGroup] = progress.currentStep
        }
        
        // 기본값: 없으면 0 (아직 아무것도 안 읽음)
        if ("level_1" !in unlockedSteps) unlockedSteps["level_1"] = 0
        if ("level_2_3" !in unlockedSteps) unlockedSteps["level_2_3"] = 0
        if ("level_4_5" !in unlockedSteps) unlockedSteps["level_4_5"] = 0
        
        return unlockedSteps
    }
    
    /**
     * 책이 현재 Unlock 되어 있는지 확인
     */
    fun isBookUnlocked(
        book: Book,
        unlockedSteps: Map<String, Int>
    ): Boolean {
        // unlockStep = 0 인 책은 항상 unlock
        if (book.unlockStep == 0) return true
        
        // 해당 레벨 그룹의 unlock step 확인
        val groupKey = when (book.level) {
            1 -> "level_1"
            in 2..3 -> "level_2_3"
            in 4..5 -> "level_4_5"
            else -> return false
        }
        
        val currentUnlockedStep = unlockedSteps[groupKey] ?: 0
        
        // 현재 unlock된 step이 책의 요구 step 이상이어야 함
        return currentUnlockedStep >= book.unlockStep
    }
    
    /**
     * Unlock 가능한 책 개수 계산 (관리 버튼 배지용)
     */
    suspend fun getUnlockableCount(
        userId: String,
        languageCode: String,
        allBooks: List<Book>
    ): Int {
        val unlockedSteps = getUnlockedSteps(userId, languageCode)
        
        // unlock되었지만 아직 다운로드하지 않은 책 개수
        return allBooks.count { book ->
            !book.isDownloaded && isBookUnlocked(book, unlockedSteps)
        }
    }
}
