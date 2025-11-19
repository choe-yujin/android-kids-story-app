package com.timor.kidsstory.domain.usecase.book

import android.util.Log
import com.timor.kidsstory.data.local.database.dao.UnlockProgressDao
import com.timor.kidsstory.data.local.database.entity.UnlockProgressEntity
import com.timor.kidsstory.domain.event.UnlockEventManager
import com.timor.kidsstory.domain.repository.BookRepository
import com.timor.kidsstory.domain.repository.ReadingProgressRepository
import javax.inject.Inject

class CheckAndProcessUnlockUseCase @Inject constructor(
    private val bookRepository: BookRepository,
    private val readingProgressRepository: ReadingProgressRepository,
    private val unlockProgressDao: UnlockProgressDao
) {
    suspend operator fun invoke(userId: String, languageCode: String, completedBookStoryId: String): Result<Boolean> {
        return try {
            // 1. Get the completed book's details to find its level
            val bookResult = bookRepository.getBookById(completedBookStoryId, languageCode)
            val book = bookResult.getOrNull() ?: return Result.success(false)

            // 2. Determine the level group
            val level = book.level
            val levelGroup = when (level) {
                1 -> "level_1"
                2, 3 -> "level_2_3"
                4, 5 -> "level_4_5"
                else -> return Result.success(false)
            }

            // 3. Get current unlock status for this level group
            val currentUnlockProgress = unlockProgressDao.getProgressForGroup(userId, levelGroup, languageCode)
            val currentUnlockedStep = currentUnlockProgress?.currentStep ?: 0
            Log.d("CheckUnlock", "Level Group: $levelGroup, Current Unlocked Step: $currentUnlockedStep")

            // 4. 🆕 간단한 로직: 각 unlockStep마다 3개씩 고정으로 체크
            val BOOKS_PER_UNLOCK_STEP = 3
            
            Log.d("CheckUnlock", "Level Group: $levelGroup, Current Unlocked Step: $currentUnlockedStep")
            Log.d("CheckUnlock", "Checking if $BOOKS_PER_UNLOCK_STEP books are completed for unlockStep $currentUnlockedStep")
            
            // 해당 레벨 그룹과 unlockStep의 모든 책 찾기
            val allBooks = bookRepository.getBooks(languageCode).getOrNull() ?: emptyList()
            val booksForCurrentUnlockStep = allBooks.filter { bookInGroup ->
                val group = when (bookInGroup.level) {
                    1 -> "level_1"
                    2, 3 -> "level_2_3" 
                    4, 5 -> "level_4_5"
                    else -> ""
                }
                group == levelGroup && bookInGroup.unlockStep == currentUnlockedStep
            }
            
            Log.d("CheckUnlock", "Found ${booksForCurrentUnlockStep.size} books for unlockStep $currentUnlockedStep")
            booksForCurrentUnlockStep.forEach { book ->
                Log.d("CheckUnlock", "  - Book: ${book.title} (downloaded: ${book.isDownloaded})")
            }
            
            if (booksForCurrentUnlockStep.size < BOOKS_PER_UNLOCK_STEP) {
                Log.d("CheckUnlock", "Not enough books for unlockStep $currentUnlockedStep. Found: ${booksForCurrentUnlockStep.size}, Required: $BOOKS_PER_UNLOCK_STEP")
                return Result.success(false)
            }
            
            // 앞에서부터 3개 책만 선택 (순서 보장)
            val requiredBooks = booksForCurrentUnlockStep.take(BOOKS_PER_UNLOCK_STEP)
            Log.d("CheckUnlock", "Checking completion status for first $BOOKS_PER_UNLOCK_STEP books:")
            requiredBooks.forEach { book ->
                Log.d("CheckUnlock", "  - Required Book: ${book.title} (${book.storyId})")
            }

            // 5. 3개 책이 모두 완독되었는지 체크
            val allProgress = readingProgressRepository.getAllProgressByLanguage(userId, languageCode)
            Log.d("CheckUnlock", "Reading progress for ${allProgress.size} books.")

            // 고정된 3개 책이 다 읽었는지 확인
            val allRequiredBooksCompleted = requiredBooks.all { requiredBook ->
                val isCompleted = allProgress[requiredBook.storyId]?.isCompleted == true
                Log.d("CheckUnlock", "  - Required Book: ${requiredBook.title} (${requiredBook.storyId}), completed: $isCompleted")
                isCompleted
            }
            Log.d("CheckUnlock", "All $BOOKS_PER_UNLOCK_STEP required books completed: $allRequiredBooksCompleted")

            // 6. 3개 책 모두 완독 시 다음 단계 해제
            if (allRequiredBooksCompleted) {
                Log.d("CheckUnlock", "All $BOOKS_PER_UNLOCK_STEP books for group $levelGroup, unlockStep $currentUnlockedStep are completed. Unlocking next step!")
                
                val nextStep = currentUnlockedStep + 1

                // Avoid redundant updates
                if (currentUnlockProgress == null || currentUnlockProgress.currentStep < nextStep) {
                     unlockProgressDao.updateStep(
                        UnlockProgressEntity(
                            userId = userId,
                            levelGroup = levelGroup,
                            language = languageCode,
                            currentStep = nextStep
                        )
                    )
                    UnlockEventManager.postUnlockEvent(levelGroup)
                    Log.d("CheckUnlock", "Updated unlock step for group $levelGroup to $nextStep.")
                    return Result.success(true)
                }
            }

            Result.success(false)
        } catch (e: Exception) {
            Log.e("CheckUnlock", "Error processing unlock status", e)
            Result.failure(e)
        }
    }
}
