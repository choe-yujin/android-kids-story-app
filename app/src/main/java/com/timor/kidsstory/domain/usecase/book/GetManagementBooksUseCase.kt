package com.timor.kidsstory.domain.usecase.book

import android.util.Log
import com.timor.kidsstory.data.local.assets.UnifiedDataSource
import com.timor.kidsstory.data.local.database.dao.HybridBooksDao
import com.timor.kidsstory.domain.model.Book
import javax.inject.Inject

/**
 * 다운로드/업데이트/삭제 가능한 책 목록 조회 UseCase (하이브리드 시스템)
 * 
 * - 다운로드 가능: GitHub에만 있고 HybridBooksDao에 없는 책
 * - 업데이트 가능: HybridBooksDao에 있지만 버전이 다른 책
 */
class GetManagementBooksUseCase @Inject constructor(
    private val unifiedDataSource: UnifiedDataSource,
    private val hybridBooksDao: HybridBooksDao,
    private val checkUnlockStatusUseCase: CheckUnlockStatusUseCase
) {
    
    /**
     * 다운로드 가능한 책 목록 조회
     * - 🆕 GitHub 메타데이터에 있는 책 중에서
     * - HybridBooksDao에 없고 
     * - unlock된 step (현재 + 1)까지의 책만 표시
     */
    suspend fun getDownloadableBooks(
        userId: String,
        languageCode: String
    ): Result<List<Book>> {
        return try {
            val normalizedLang = normalizeLanguageCode(languageCode)
            
            // 1. 🆕 unlock 상태 확인
            val unlockedStepsMap = checkUnlockStatusUseCase.getUnlockedSteps(
                userId = userId,
                languageCode = languageCode
            )
            
            // 2. GitHub 메타데이터 로드
            val metadataResult = unifiedDataSource.loadRemoteBooksMetadata()
            if (metadataResult.isFailure) {
                return Result.failure(metadataResult.exceptionOrNull()!!)
            }
            
            val metadata = metadataResult.getOrNull()!!
            
            // 3. HybridBooksDao에서 현재 DB에 있는 책 목록 조회
            val existingBooks = hybridBooksDao.getAvailableBooksByLanguage(normalizedLang)
            val existingBookIds = existingBooks.map { it.id }.toSet()
            
            // 4. 🔥 올바른 순서: GitHub에 있는 책 중에서 → 로컬에 없고 → unlock 정책에 맞는 책
            val downloadableBooks = metadata.books.filter { bookMeta ->
                // 📋 1단계: GitHub 메타데이터에 해당 언어가 있는가?
                if (!bookMeta.languages.containsKey(normalizedLang)) {
                    return@filter false
                }
                
                // 📋 2단계: 로컬 DB에 이미 있는가? (있으면 제외)
                if (bookMeta.id in existingBookIds) {
                    return@filter false
                }
                
                // 📋 3단계: unlock 정책에 맞는가?
                val groupKey = when (bookMeta.level) {
                    1 -> "level_1"
                    in 2..3 -> "level_2_3"
                    in 4..5 -> "level_4_5"
                    else -> return@filter false
                }
                
                val currentUnlockedStep = unlockedStepsMap[groupKey] ?: 0
                
                // unlock된 step + 1까지만 다운로드 가능
                bookMeta.unlockStep <= currentUnlockedStep + 1
                
            }.mapNotNull { bookMeta ->
                val languageContent = bookMeta.languages[normalizedLang] ?: return@mapNotNull null
                
                // 간단한 Book 객체 생성 (다운로드 전이므로 콘텐츠 없음)
                Book(
                    storyId = "${bookMeta.id}_$normalizedLang",
                    title = languageContent.title,
                    coverImage = "https://raw.githubusercontent.com/choe-yujin/android-kids-story-app/main/app/src/main/assets/images/${bookMeta.id}/cover_${bookMeta.id}_${normalizedLang}.jpg",
                    category = bookMeta.category,
                    level = bookMeta.level,
                    unlockStep = bookMeta.unlockStep,
                    pageCount = 0,
                    contributors = emptyList(),
                    copyright = "",
                    pages = emptyList(),
                    isDownloaded = false,
                    totalSize = 0L
                )
            }
            
            Log.d("GetManagementBooksUseCase", "📥 Downloadable books (GitHub + not in local + unlocked): ${downloadableBooks.size}")
            Log.d("GetManagementBooksUseCase", "🔓 Unlock status: $unlockedStepsMap")
            Log.d("GetManagementBooksUseCase", "💾 Local books: ${existingBookIds.size} items")
            Result.success(downloadableBooks)
            
        } catch (e: Exception) {
            Log.e("GetManagementBooksUseCase", "❌ Error getting downloadable books", e)
            Result.failure(e)
        }
    }
    
    /**
     * 업데이트 가능한 책 목록 조회
     * - HybridBooksDao에 있지만 GitHub 버전과 다른 책
     */
    suspend fun getUpdatableBooks(
        languageCode: String
    ): Result<List<Book>> {
        return try {
            val normalizedLang = normalizeLanguageCode(languageCode)
            
            // 1. GitHub 메타데이터 로드
            val metadataResult = unifiedDataSource.loadBooksMetadata()
            if (metadataResult.isFailure) {
                return Result.failure(metadataResult.exceptionOrNull()!!)
            }
            
            val metadata = metadataResult.getOrNull()!!
            
            // 2. 🆕 HybridBooksDao에서 현재 DB에 있는 책 목록 조회
            val existingBooks = hybridBooksDao.getAvailableBooksByLanguage(normalizedLang)
            
            // 3. 버전이 다른 책 필터링
            val updatableBooks = existingBooks.mapNotNull { localBook ->
                val remoteMeta = metadata.books.find { it.id == localBook.id } ?: return@mapNotNull null
                val remoteLanguage = remoteMeta.languages[normalizedLang] ?: return@mapNotNull null
                
                // 버전 비교 (contentVersion으로 비교)
                val needsUpdate = remoteLanguage.contentVersion != localBook.contentVersion
                
                if (!needsUpdate) return@mapNotNull null
                
                // Book 객체 생성
                Book(
                    storyId = "${localBook.id}_$normalizedLang",
                    title = remoteLanguage.title,
                    coverImage = "https://raw.githubusercontent.com/choe-yujin/android-kids-story-app/main/app/src/main/assets/images/${remoteMeta.id}/cover_${remoteMeta.id}_${normalizedLang}.jpg",
                    category = remoteMeta.category,
                    level = remoteMeta.level,
                    unlockStep = remoteMeta.unlockStep,
                    pageCount = 0,
                    contributors = emptyList(),
                    copyright = "",
                    pages = emptyList(),
                    isDownloaded = true,
                    totalSize = 0L
                )
            }
            
            Log.d("GetManagementBooksUseCase", "🔄 Updatable books: ${updatableBooks.size}")
            Result.success(updatableBooks)
            
        } catch (e: Exception) {
            Log.e("GetManagementBooksUseCase", "❌ Error getting updatable books", e)
            Result.failure(e)
        }
    }
    
    private fun normalizeLanguageCode(language: String): String {
        return when {
            language.startsWith("ko") -> "ko"
            language.startsWith("tet") -> "tet"
            language.startsWith("en") -> "en"
            language.startsWith("mn") -> "mn"
            else -> "en"
        }
    }
}
