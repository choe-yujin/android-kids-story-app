package com.timor.kidsstory.domain.usecase.update

import android.content.Context
import android.util.Log
import com.timor.kidsstory.data.local.assets.UnifiedDataSource
import com.timor.kidsstory.data.local.database.dao.HybridBooksDao
import com.timor.kidsstory.domain.usecase.book.CheckUnlockStatusUseCase
import com.timor.kidsstory.data.dto.HybridBooksMetadata
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 업데이트 체크 결과 데이터 클래스
 */
data class UpdateCheckResult(
    val downloadableCount: Int,  // 새 책 수
    val updatableCount: Int,     // 업데이트 가능한 책 수
    val totalCount: Int          // 전체 수 (downloadableCount + updatableCount)
)

/**
 * 네트워크 효율적인 업데이트 확인 UseCase
 * 
 * 최적화 전략:
 * - 1일 1회만 체크 (SharedPreferences 캐시)
 * - 메타데이터만 다운로드 (실제 콘텐츠 X)
 * - 버전 번호만 비교
 * - 백그라운드에서 실행
 */
class CheckAvailableUpdatesUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val unifiedDataSource: UnifiedDataSource,
    private val hybridBooksDao: HybridBooksDao,
    private val checkUnlockStatusUseCase: CheckUnlockStatusUseCase
) {
    
    companion object {
        private const val PREF_NAME = "update_check_cache"
        private const val KEY_LAST_CHECK_TIME = "last_check_time"
        private const val KEY_CACHED_TOTAL_COUNT = "cached_total_count"
        private const val KEY_CACHED_DOWNLOADABLE_COUNT = "cached_downloadable_count" // 🆕 새 책 수
        private const val KEY_CACHED_UPDATABLE_COUNT = "cached_updatable_count"     // 🆕 업데이트 수
        private const val CACHE_DURATION_MS = 24 * 60 * 60 * 1000L // 24시간
    }
    
    /**
     * 사용 가능한 업데이트 수 확인 (캐시 우선)
     * 
     * @param userId 사용자 ID
     * @param languageCode 언어 코드
     * @param forceRefresh 강제 새로고침 여부
     * @return 다운로드/업데이트 개별 카운트 정보
     */
    suspend fun getAvailableUpdatesCount(
        userId: String,
        languageCode: String,
        forceRefresh: Boolean = false
    ): Result<UpdateCheckResult> {
        return try {
            withContext(Dispatchers.IO) {
                val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                val lastCheckTime = prefs.getLong(KEY_LAST_CHECK_TIME, 0)
                val currentTime = System.currentTimeMillis()
                val cacheValid = (currentTime - lastCheckTime) < CACHE_DURATION_MS
                
                // 🚀 캐시가 유효하고 강제 새로고침이 아니면 캐시된 값 반환
                if (cacheValid && !forceRefresh) {
                    val cachedTotalCount = prefs.getInt(KEY_CACHED_TOTAL_COUNT, 0)
                    val cachedDownloadableCount = prefs.getInt(KEY_CACHED_DOWNLOADABLE_COUNT, 0)
                    val cachedUpdatableCount = prefs.getInt(KEY_CACHED_UPDATABLE_COUNT, 0)
                    
                    Log.d("CheckAvailableUpdatesUseCase", "📦 Using cached update counts: D=$cachedDownloadableCount, U=$cachedUpdatableCount, Total=$cachedTotalCount")
                    
                    return@withContext Result.success(
                        UpdateCheckResult(
                            downloadableCount = cachedDownloadableCount,
                            updatableCount = cachedUpdatableCount,
                            totalCount = cachedTotalCount
                        )
                    )
                }
                
                Log.d("CheckAvailableUpdatesUseCase", "🔄 Checking for updates (network call)")
                
                // 🌐 네트워크에서 메타데이터만 가져오기
                val metadataResult = unifiedDataSource.loadRemoteBooksMetadata()
                if (metadataResult.isFailure) {
                    Log.w("CheckAvailableUpdatesUseCase", "❌ Failed to load remote metadata")
                    // 네트워크 실패시 캐시된 값 반환 (오래된 값이라도)
                    val cachedTotalCount = prefs.getInt(KEY_CACHED_TOTAL_COUNT, 0)
                    val cachedDownloadableCount = prefs.getInt(KEY_CACHED_DOWNLOADABLE_COUNT, 0)
                    val cachedUpdatableCount = prefs.getInt(KEY_CACHED_UPDATABLE_COUNT, 0)
                    
                    return@withContext Result.success(
                        UpdateCheckResult(
                            downloadableCount = cachedDownloadableCount,
                            updatableCount = cachedUpdatableCount,
                            totalCount = cachedTotalCount
                        )
                    )
                }
                
                val normalizedLang = normalizeLanguageCode(languageCode)
                val metadata = metadataResult.getOrNull()!!
                
                // 📚 다운로드 가능한 책 수 계산
                val downloadableCount = calculateDownloadableCount(userId, normalizedLang, metadata)
                
                // 🔄 업데이트 가능한 책 수 계산
                val updatableCount = calculateUpdatableCount(normalizedLang, metadata)
                
                val totalCount = downloadableCount + updatableCount
                
                // 💾 캐시에 저장
                prefs.edit()
                    .putLong(KEY_LAST_CHECK_TIME, currentTime)
                    .putInt(KEY_CACHED_TOTAL_COUNT, totalCount)
                    .putInt(KEY_CACHED_DOWNLOADABLE_COUNT, downloadableCount)  // 🆕 새 책 수
                    .putInt(KEY_CACHED_UPDATABLE_COUNT, updatableCount)        // 🆕 업데이트 수
                    .apply()
                
                Log.d("CheckAvailableUpdatesUseCase", "✅ Update check completed: $downloadableCount new + $updatableCount updates = $totalCount total")
                
                Result.success(
                    UpdateCheckResult(
                        downloadableCount = downloadableCount,
                        updatableCount = updatableCount,
                        totalCount = totalCount
                    )
                )
            }
            
        } catch (e: Exception) {
            Log.e("CheckAvailableUpdatesUseCase", "❌ Error checking for updates", e)
            // 에러 발생시 캐시된 값 반환
            val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            val cachedTotalCount = prefs.getInt(KEY_CACHED_TOTAL_COUNT, 0)
            val cachedDownloadableCount = prefs.getInt(KEY_CACHED_DOWNLOADABLE_COUNT, 0)
            val cachedUpdatableCount = prefs.getInt(KEY_CACHED_UPDATABLE_COUNT, 0)
            
            Result.success(
                UpdateCheckResult(
                    downloadableCount = cachedDownloadableCount,
                    updatableCount = cachedUpdatableCount,
                    totalCount = cachedTotalCount
                )
            )
        }
    }
    
    /**
     * 다운로드 가능한 책 수 계산 (unlock 정책 적용)
     */
    private suspend fun calculateDownloadableCount(
        userId: String,
        languageCode: String,
        metadata: HybridBooksMetadata
    ): Int {
        return try {
            // Unlock 상태 확인
            val unlockedStepsMap = checkUnlockStatusUseCase.getUnlockedSteps(
                userId = userId,
                languageCode = languageCode
            )
            
            // 로컬에 존재하는 책 ID 목록
            val existingBookIds = hybridBooksDao.getAvailableBooksByLanguage(languageCode)
                .map { it.id }
                .toSet()
            
            // GitHub에 있지만 로컬에 없고 unlock된 책 계산
            metadata.books.count { bookMeta ->
                // 해당 언어가 있는가?
                if (!bookMeta.languages.containsKey(languageCode)) return@count false
                
                // 로컬에 이미 있는가?
                if (bookMeta.id in existingBookIds) return@count false
                
                // unlock 정책에 맞는가?
                val groupKey = when (bookMeta.level) {
                    1 -> "level_1"
                    in 2..3 -> "level_2_3"
                    in 4..5 -> "level_4_5"
                    else -> return@count false
                }
                
                val currentUnlockedStep = unlockedStepsMap[groupKey] ?: 0
                bookMeta.unlockStep <= currentUnlockedStep + 1
            }
            
        } catch (e: Exception) {
            Log.e("CheckAvailableUpdatesUseCase", "Error calculating downloadable count", e)
            0
        }
    }
    
    /**
     * 업데이트 가능한 책 수 계산
     */
    private suspend fun calculateUpdatableCount(
        languageCode: String,
        metadata: HybridBooksMetadata
    ): Int {
        return try {
            val existingBooks = hybridBooksDao.getAvailableBooksByLanguage(languageCode)
            
            existingBooks.count { localBook ->
                val remoteMeta = metadata.books.find { it.id == localBook.id } ?: return@count false
                val remoteLanguage = remoteMeta.languages[languageCode] ?: return@count false
                
                // 버전이 다른가?
                remoteLanguage.contentVersion != localBook.contentVersion
            }
            
        } catch (e: Exception) {
            Log.e("CheckAvailableUpdatesUseCase", "Error calculating updatable count", e)
            0
        }
    }
    
    /**
     * 캐시 무효화 (관리 모드에서 실제 다운로드/업데이트 후 호출)
     */
    fun invalidateCache() {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putLong(KEY_LAST_CHECK_TIME, 0) // 캐시 무효화
            .putInt(KEY_CACHED_TOTAL_COUNT, 0)
            .putInt(KEY_CACHED_DOWNLOADABLE_COUNT, 0)
            .putInt(KEY_CACHED_UPDATABLE_COUNT, 0)
            .apply()
        
        Log.d("CheckAvailableUpdatesUseCase", "🗑️ Update cache invalidated")
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
