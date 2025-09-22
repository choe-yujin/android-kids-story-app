package com.timor.kidsstory.domain.service

import android.content.Context
import android.util.Log
import com.timor.kidsstory.data.dto.HybridBooksMetadata
import com.timor.kidsstory.data.dto.HybridBookMetadata
import com.timor.kidsstory.data.remote.network.BookNetworkService
import com.timor.kidsstory.domain.manager.content.HybridContentManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 콘텐츠 업데이트 서비스
 * - GitHub에서 최신 메타데이터 확인
 * - 개별 파일별 버전 체크 및 업데이트
 * - 선택적 다운로드 (필요한 것만)
 */
@Singleton
class ContentUpdateService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val hybridContentManager: HybridContentManager,
    private val networkService: BookNetworkService
) {
    
    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }
    
    /**
     * 업데이트 체크 및 실행
     * @param forceUpdate 강제 업데이트 여부
     * @return 업데이트된 항목 수
     */
    suspend fun checkAndUpdateContent(forceUpdate: Boolean = false): Result<Int> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "🔍 Checking for content updates...")
                
                // 1. 원격 메타데이터 가져오기
                val remoteMetadataResult = fetchRemoteMetadata()
                if (remoteMetadataResult.isFailure) {
                    Log.w(TAG, "Failed to fetch remote metadata, using local version")
                    return@withContext Result.success(0)
                }
                
                val remoteMetadata = remoteMetadataResult.getOrThrow()
                
                // 2. 로컬 메타데이터와 비교
                val localMetadataResult = hybridContentManager.loadMetadata()
                if (localMetadataResult.isFailure) {
                    Log.w(TAG, "No local metadata found, performing full download")
                    return@withContext performFullUpdate(remoteMetadata)
                }
                
                val localMetadata = localMetadataResult.getOrThrow()
                
                // 3. 버전 비교
                if (!forceUpdate && remoteMetadata.version <= localMetadata.version) {
                    Log.d(TAG, "✅ Content is up to date (v${localMetadata.version})")
                    return@withContext Result.success(0)
                }
                
                // 4. 선택적 업데이트
                val updateResult = performSelectiveUpdate(localMetadata, remoteMetadata)
                val updatedCount = updateResult.getOrElse { 0 }
                
                Log.d(TAG, "✅ Update completed: $updatedCount items updated")
                Result.success(updatedCount)
                
            } catch (e: Exception) {
                Log.e(TAG, "❌ Failed to check/update content", e)
                Result.failure(e)
            }
        }
    }
    
    /**
     * 원격 메타데이터 가져오기 (GitHub)
     */
    private suspend fun fetchRemoteMetadata(): Result<HybridBooksMetadata> {
        return try {
            val metadataUrl = "${GITHUB_RAW_BASE_URL}/app/src/main/assets/books_metadata_hybrid.json"
            val tempFile = File(context.cacheDir, "remote_metadata.json")
            
            val downloadSuccess = networkService.downloadFile(metadataUrl, tempFile)
            if (!downloadSuccess) {
                return Result.failure(Exception("Failed to download remote metadata"))
            }
            
            val jsonString = tempFile.readText()
            val metadata = json.decodeFromString<HybridBooksMetadata>(jsonString)
            
            tempFile.delete()
            
            Log.d(TAG, "📥 Fetched remote metadata v${metadata.version}")
            Result.success(metadata)
            
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to fetch remote metadata", e)
            Result.failure(e)
        }
    }
    
    /**
     * 전체 업데이트 수행
     */
    private suspend fun performFullUpdate(remoteMetadata: HybridBooksMetadata): Result<Int> {
        Log.d(TAG, "🔄 Performing full content update...")
        
        var updatedCount = 0
        
        // 메타데이터 업데이트
        updateMetadata(remoteMetadata)
        updatedCount++
        
        // 모든 콘텐츠 파일 업데이트
        for (book in remoteMetadata.books) {
            for ((languageCode, languageContent) in book.languages) {
                if (languageContent.isBundled) {
                    val contentUpdated = updateContentFile(book.id, languageCode, languageContent.contentVersion)
                    if (contentUpdated) updatedCount++
                    
                    val coverUpdated = updateCoverImage(book.id, languageCode, languageContent.coverVersion)
                    if (coverUpdated) updatedCount++
                }
            }
            
            // 이미지 에셋 업데이트
            val imagesUpdated = updateImageAssets(book.id, book.imageAssetsVersion)
            if (imagesUpdated) updatedCount++
        }
        
        return Result.success(updatedCount)
    }
    
    /**
     * 선택적 업데이트 수행
     */
    private suspend fun performSelectiveUpdate(
        localMetadata: HybridBooksMetadata, 
        remoteMetadata: HybridBooksMetadata
    ): Result<Int> {
        Log.d(TAG, "🔄 Performing selective content update...")
        
        var updatedCount = 0
        
        // 메타데이터 업데이트 (항상)
        updateMetadata(remoteMetadata)
        updatedCount++
        
        // 책별 업데이트 체크
        for (remoteBook in remoteMetadata.books) {
            val localBook = localMetadata.books.find { it.id == remoteBook.id }
            
            if (localBook == null) {
                // 새로운 책 - 전체 다운로드
                Log.d(TAG, "📚 New book found: ${remoteBook.id}")
                updatedCount += downloadNewBook(remoteBook)
                continue
            }
            
            // 언어별 업데이트 체크
            for ((languageCode, remoteLanguageContent) in remoteBook.languages) {
                val localLanguageContent = localBook.languages[languageCode]
                
                if (localLanguageContent == null) {
                    // 새로운 언어 버전
                    Log.d(TAG, "🌍 New language found: ${remoteBook.id}/$languageCode")
                    val contentUpdated = updateContentFile(remoteBook.id, languageCode, remoteLanguageContent.contentVersion)
                    if (contentUpdated) updatedCount++
                    
                    val coverUpdated = updateCoverImage(remoteBook.id, languageCode, remoteLanguageContent.coverVersion)
                    if (coverUpdated) updatedCount++
                    continue
                }
                
                // 콘텐츠 버전 체크
                if (remoteLanguageContent.contentVersion > localLanguageContent.contentVersion) {
                    Log.d(TAG, "📝 Content update: ${remoteBook.id}/$languageCode v${localLanguageContent.contentVersion} → v${remoteLanguageContent.contentVersion}")
                    val updated = updateContentFile(remoteBook.id, languageCode, remoteLanguageContent.contentVersion)
                    if (updated) updatedCount++
                }
                
                // 커버 이미지 버전 체크
                if (remoteLanguageContent.coverVersion > localLanguageContent.coverVersion) {
                    Log.d(TAG, "🖼️ Cover update: ${remoteBook.id}/$languageCode v${localLanguageContent.coverVersion} → v${remoteLanguageContent.coverVersion}")
                    val updated = updateCoverImage(remoteBook.id, languageCode, remoteLanguageContent.coverVersion)
                    if (updated) updatedCount++
                }
            }
            
            // 이미지 에셋 버전 체크
            if (remoteBook.imageAssetsVersion > localBook.imageAssetsVersion) {
                Log.d(TAG, "🎨 Images update: ${remoteBook.id} v${localBook.imageAssetsVersion} → v${remoteBook.imageAssetsVersion}")
                val updated = updateImageAssets(remoteBook.id, remoteBook.imageAssetsVersion)
                if (updated) updatedCount++
            }
        }
        
        return Result.success(updatedCount)
    }
    
    /**
     * 새로운 책 전체 다운로드
     */
    private suspend fun downloadNewBook(bookMetadata: HybridBookMetadata): Int {
        var downloadedCount = 0
        
        // 모든 언어 버전 다운로드
        for ((languageCode, languageContent) in bookMetadata.languages) {
            if (languageContent.isBundled) {
                val contentUpdated = updateContentFile(bookMetadata.id, languageCode, languageContent.contentVersion)
                if (contentUpdated) downloadedCount++
                
                val coverUpdated = updateCoverImage(bookMetadata.id, languageCode, languageContent.coverVersion)
                if (coverUpdated) downloadedCount++
            }
        }
        
        // 이미지 에셋 다운로드
        val imagesUpdated = updateImageAssets(bookMetadata.id, bookMetadata.imageAssetsVersion)
        if (imagesUpdated) downloadedCount++
        
        return downloadedCount
    }
    
    /**
     * 메타데이터 업데이트
     */
    private suspend fun updateMetadata(metadata: HybridBooksMetadata) {
        // HybridContentManager를 통해 내부저장소에 저장
        hybridContentManager.initializeHybridContent()
    }
    
    /**
     * 콘텐츠 파일 업데이트
     */
    private suspend fun updateContentFile(bookId: Int, languageCode: String, version: Int): Boolean {
        return try {
            val fileName = "${bookId}_$languageCode.json"
            val remoteUrl = "$GITHUB_RAW_BASE_URL/app/src/main/assets/content/$fileName"
            val localFile = File(context.filesDir, "hybrid_content/content/$fileName")
            
            val success = networkService.downloadFile(remoteUrl, localFile)
            if (success) {
                Log.d(TAG, "✅ Updated content: $fileName")
            } else {
                Log.w(TAG, "❌ Failed to update content: $fileName")
            }
            
            success
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error updating content file: $bookId/$languageCode", e)
            false
        }
    }
    
    /**
     * 커버 이미지 업데이트
     */
    private suspend fun updateCoverImage(bookId: Int, languageCode: String, version: Int): Boolean {
        return try {
            val fileName = "cover_${bookId}_$languageCode.jpg"
            val remoteUrl = "$GITHUB_RAW_BASE_URL/app/src/main/assets/images/$bookId/$fileName"
            val localFile = File(context.filesDir, "hybrid_content/images/$bookId/$fileName")
            
            localFile.parentFile?.mkdirs()
            
            val success = networkService.downloadFile(remoteUrl, localFile)
            if (success) {
                Log.d(TAG, "✅ Updated cover: $fileName")
            } else {
                Log.w(TAG, "❌ Failed to update cover: $fileName")
            }
            
            success
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error updating cover image: $bookId/$languageCode", e)
            false
        }
    }
    
    /**
     * 이미지 에셋 업데이트
     */
    private suspend fun updateImageAssets(bookId: Int, version: Int): Boolean {
        return try {
            // 개별 이미지 파일들을 다운로드 (ZIP 대신 간단화)
            val imagesDir = File(context.filesDir, "hybrid_content/images/$bookId")
            imagesDir.mkdirs()
            
            // 기본적인 이미지 파일들 다운로드 시도
            val commonImageFiles = listOf("cover_${bookId}_ko.jpg", "cover_${bookId}_en.jpg", "cover_${bookId}_tet.jpg")
            var successCount = 0
            
            for (imageFile in commonImageFiles) {
                val remoteUrl = "$GITHUB_RAW_BASE_URL/app/src/main/assets/images/$bookId/$imageFile"
                val localFile = File(imagesDir, imageFile)
                
                val success = networkService.downloadFile(remoteUrl, localFile)
                if (success) successCount++
            }
            
            Log.d(TAG, "✅ Updated images for book $bookId: $successCount files")
            successCount > 0
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error updating image assets: $bookId", e)
            false
        }
    }
    
    companion object {
        private const val TAG = "ContentUpdateService"
        private const val GITHUB_RAW_BASE_URL = "https://raw.githubusercontent.com/choe-yujin/android-kids-story-app/main"
    }
}
