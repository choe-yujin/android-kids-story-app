package com.timor.kidsstory.domain.manager.content

import android.content.Context
import android.util.Log
import com.timor.kidsstory.data.dto.HybridBooksMetadata
import com.timor.kidsstory.data.dto.UnifiedBookContent
import com.timor.kidsstory.data.local.database.dao.HybridBooksDao
import com.timor.kidsstory.data.local.database.entity.BookSource
import com.timor.kidsstory.data.local.database.entity.HybridBookEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 하이브리드 콘텐츠 매니저 (DB 통합 버전)
 * - 첫 실행 시 assets → 내부저장소 복사
 * - 모든 책 정보를 Room DB로 관리 
 * - 버전 체크 및 업데이트 관리
 * - 항상 내부저장소에서 읽기
 */
@Singleton
class HybridContentManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val hybridBooksDao: HybridBooksDao
) {
    
    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }
    
    /**
     * 내부 저장소 기본 경로
     */
    private val internalStorageDir: File by lazy {
        File(context.filesDir, "hybrid_content").apply { mkdirs() }
    }
    
    /**
     * 메타데이터 파일 경로
     */
    private val metadataFile: File by lazy {
        File(internalStorageDir, "books_metadata.json")
    }
    
    /**
     * 콘텐츠 디렉토리
     */
    private val contentDir: File by lazy {
        File(internalStorageDir, "content").apply { mkdirs() }
    }
    
    /**
     * 이미지 디렉토리  
     */
    private val imagesDir: File by lazy {
        File(internalStorageDir, "images").apply { mkdirs() }
    }
    
    /**
     * 하이브리드 콘텐츠 초기화
     * - 첫 실행 시 assets에서 내부저장소로 복사
     * - 모든 책 정보를 Room DB에 등록
     * - 버전 체크 및 필요 시 업데이트
     */
    suspend fun initializeHybridContent(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "🚀 Initializing hybrid content system with DB integration...")
            
            // 1. 메타데이터 초기화/업데이트 체크
            val metadataInitResult = initializeMetadata()
            if (metadataInitResult.isFailure) {
                return@withContext metadataInitResult
            }
            
            // 2. 내장 책들을 DB에 등록
            val dbInitResult = initializeBooksInDatabase()
            if (dbInitResult.isFailure) {
                return@withContext dbInitResult
            }
            
            // 3. 콘텐츠 파일들 초기화
            val contentInitResult = initializeContentFiles()
            if (contentInitResult.isFailure) {
                return@withContext contentInitResult
            }
            
            // 4. 이미지 파일들 초기화  
            val imagesInitResult = initializeImageFiles()
            if (imagesInitResult.isFailure) {
                return@withContext imagesInitResult
            }
            
            Log.d(TAG, "✅ Hybrid content initialization completed with DB integration")
            Result.success(Unit)
            
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to initialize hybrid content", e)
            Result.failure(e)
        }
    }
    
    /**
     * 내장 책들을 Room DB에 등록
     */
    private suspend fun initializeBooksInDatabase(): Result<Unit> {
        return try {
            val metadata = loadMetadataFromInternal().getOrThrow()
            
            for (book in metadata.books) {
                for ((languageCode, languageContent) in book.languages) {
                    if (languageContent.isBundled) {
                        
                        // 이미 DB에 등록된 책인지 확인
                        val existsInDb = hybridBooksDao.isBookExists(book.id, languageCode)
                        
                        if (!existsInDb) {
                            // 새로운 내장 책을 DB에 등록
                            val hybridBookEntity = HybridBookEntity(
                                id = book.id,
                                language = languageCode,
                                title = languageContent.title,
                                level = book.level,
                                category = book.category,
                                countryOfOrigin = book.countryOfOrigin,
                                
                                // 파일 경로 (내부저장소 기준)
                                contentPath = File(contentDir, "${book.id}_$languageCode.json").absolutePath,
                                coverImagePath = File(imagesDir, "${book.id}/cover_${book.id}_$languageCode.jpg").absolutePath,
                                imagesDirectoryPath = File(imagesDir, book.id.toString()).absolutePath,
                                
                                // 버전 정보
                                contentVersion = languageContent.contentVersion,
                                coverVersion = languageContent.coverVersion,
                                imageAssetsVersion = book.imageAssetsVersion,
                                
                                // 내장 책으로 설정
                                source = BookSource.BUNDLED,
                                isAvailable = true,
                                downloadDate = null,
                                
                                // AI 기능 및 태그
                                aiFeatures = book.aiFeatures,
                                tags = languageContent.tags
                            )
                            
                            hybridBooksDao.insertBook(hybridBookEntity)
                            Log.d(TAG, "📚 Registered bundled book: ${book.id}/$languageCode")
                        } else {
                            Log.d(TAG, "✅ Book already in DB: ${book.id}/$languageCode")
                        }
                    }
                }
            }
            
            Log.d(TAG, "✅ Books database initialization completed")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to initialize books database", e)
            Result.failure(e)
        }
    }
    
    /**
     * 메타데이터 초기화 및 버전 체크
     */
    private suspend fun initializeMetadata(): Result<Unit> {
        return try {
            val assetsMetadata = loadMetadataFromAssets()
            val localMetadata = if (metadataFile.exists()) {
                loadMetadataFromFile(metadataFile) 
            } else null
            
            // 버전 비교 및 업데이트 결정
            val needsUpdate = localMetadata == null || 
                            assetsMetadata.version > localMetadata.version
            
            if (needsUpdate) {
                Log.d(TAG, "📥 Updating metadata: ${localMetadata?.version ?: 0} → ${assetsMetadata.version}")
                
                // assets에서 내부저장소로 복사
                copyMetadataToInternal(assetsMetadata)
                Log.d(TAG, "✅ Metadata updated successfully")
            } else {
                Log.d(TAG, "✅ Metadata is up to date (v${localMetadata.version})")
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to initialize metadata", e)
            Result.failure(e)
        }
    }
    
    /**
     * 콘텐츠 파일들 초기화
     */
    private suspend fun initializeContentFiles(): Result<Unit> {
        return try {
            val metadata = loadMetadataFromInternal().getOrThrow()
            
            for (book in metadata.books) {
                for ((languageCode, languageContent) in book.languages) {
                    if (languageContent.isBundled) {
                        val contentFileName = "${book.id}_$languageCode.json"
                        val localContentFile = File(contentDir, contentFileName)
                        
                        // 버전 체크 (간단화: 파일 존재 여부로 판단)
                        if (!localContentFile.exists()) {
                            Log.d(TAG, "📥 Copying content: $contentFileName")
                            copyContentFileFromAssets(contentFileName, localContentFile)
                        }
                    }
                }
            }
            
            Log.d(TAG, "✅ Content files initialization completed")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to initialize content files", e)
            Result.failure(e)
        }
    }
    
    /**
     * 이미지 파일들 초기화
     */
    private suspend fun initializeImageFiles(): Result<Unit> {
        return try {
            val metadata = loadMetadataFromInternal().getOrThrow()
            
            for (book in metadata.books) {
                val bookImagesDir = File(imagesDir, book.id.toString()).apply { mkdirs() }
                
                // 이미지 디렉토리가 비어있으면 assets에서 복사
                if (bookImagesDir.listFiles()?.isEmpty() != false) {
                    Log.d(TAG, "📥 Copying images for book ${book.id}")
                    copyBookImagesFromAssets(book.id, bookImagesDir)
                }
            }
            
            Log.d(TAG, "✅ Image files initialization completed")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to initialize image files", e)
            Result.failure(e)
        }
    }
    
    /**
     * Room DB에서 모든 책 조회 (언어별)
     */
    suspend fun getAllBooksFromDb(languageCode: String): Result<List<HybridBookEntity>> {
        return try {
            val books = hybridBooksDao.getAvailableBooksByLanguage(languageCode)
            Log.d(TAG, "✅ Loaded ${books.size} books from DB for language: $languageCode")
            Result.success(books)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to load books from DB", e)
            Result.failure(e)
        }
    }
    
    /**
     * 다운로드한 책을 DB에 등록
     */
    suspend fun registerDownloadedBook(
        bookId: Int,
        languageCode: String,
        title: String,
        contentPath: String,
        coverImagePath: String,
        imagesDirectoryPath: String,
        contentVersion: Int,
        coverVersion: Int,
        imageAssetsVersion: Int,
        level: Int,
        category: String,
        countryOfOrigin: String,
        aiFeatures: List<String> = emptyList(),
        tags: List<String> = emptyList()
    ): Result<Unit> {
        return try {
            val downloadedBook = HybridBookEntity(
                id = bookId,
                language = languageCode,
                title = title,
                level = level,
                category = category,
                countryOfOrigin = countryOfOrigin,
                contentPath = contentPath,
                coverImagePath = coverImagePath,
                imagesDirectoryPath = imagesDirectoryPath,
                contentVersion = contentVersion,
                coverVersion = coverVersion,
                imageAssetsVersion = imageAssetsVersion,
                source = BookSource.DOWNLOADED,
                isAvailable = true,
                downloadDate = System.currentTimeMillis(),
                aiFeatures = aiFeatures,
                tags = tags
            )
            
            hybridBooksDao.insertBook(downloadedBook)
            Log.d(TAG, "✅ Registered downloaded book: $bookId/$languageCode")
            
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to register downloaded book", e)
            Result.failure(e)
        }
    }
    
    /**
     * 항상 내부저장소에서 메타데이터 로드
     */
    suspend fun loadMetadata(): Result<HybridBooksMetadata> {
        return loadMetadataFromInternal()
    }
    
    /**
     * 항상 내부저장소에서 콘텐츠 로드
     */
    suspend fun loadBookContent(bookId: Int, languageCode: String): Result<UnifiedBookContent> {
        return withContext(Dispatchers.IO) {
            try {
                val contentFileName = "${bookId}_$languageCode.json"
                val contentFile = File(contentDir, contentFileName)
                
                if (!contentFile.exists()) {
                    return@withContext Result.failure(
                        IllegalStateException("Content file not found: $contentFileName")
                    )
                }
                
                val contentJson = contentFile.readText()
                val content = json.decodeFromString<UnifiedBookContent>(contentJson)
                
                Log.d(TAG, "✅ Loaded content from internal storage: $contentFileName")
                Result.success(content)
            } catch (e: Exception) {
                Log.e(TAG, "❌ Failed to load content from internal storage", e)
                Result.failure(e)
            }
        }
    }
    
    /**
     * 이미지 파일 경로 반환 (내부저장소 기준)
     */
    fun getImagePath(bookId: Int, imageName: String): String {
        return File(imagesDir, "$bookId/$imageName").absolutePath
    }
    
    /**
     * 이미지 URL 반환 (내부저장소 우선, assets fallback)
     */
    fun getImageUrl(bookId: Int, imageName: String): String {
        val internalImageFile = File(imagesDir, "$bookId/$imageName")
        return if (internalImageFile.exists()) {
            "file://${internalImageFile.absolutePath}"  // 내부저장소 우선
        } else {
            "file:///android_asset/images/$bookId/$imageName"  // assets fallback
        }
    }
    
    // ========== Private Helper Methods ==========
    
    /**
     * Assets에서 메타데이터 로드
     */
    private suspend fun loadMetadataFromAssets(): HybridBooksMetadata {
        return withContext(Dispatchers.IO) {
            context.assets.open("books_metadata_hybrid.json").use { inputStream ->
                val jsonString = inputStream.bufferedReader().readText()
                json.decodeFromString<HybridBooksMetadata>(jsonString)
            }
        }
    }
    
    /**
     * 파일에서 메타데이터 로드
     */
    private suspend fun loadMetadataFromFile(file: File): HybridBooksMetadata? {
        return withContext(Dispatchers.IO) {
            try {
                val jsonString = file.readText()
                json.decodeFromString<HybridBooksMetadata>(jsonString)
            } catch (e: Exception) {
                Log.w(TAG, "Failed to load metadata from file: ${file.path}", e)
                null
            }
        }
    }
    
    /**
     * 내부저장소에서 메타데이터 로드
     */
    private suspend fun loadMetadataFromInternal(): Result<HybridBooksMetadata> {
        return withContext(Dispatchers.IO) {
            try {
                if (!metadataFile.exists()) {
                    return@withContext Result.failure(
                        IllegalStateException("Metadata file not found in internal storage")
                    )
                }
                
                val metadata = loadMetadataFromFile(metadataFile)
                if (metadata != null) {
                    Result.success(metadata)
                } else {
                    Result.failure(IllegalStateException("Failed to parse metadata file"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * 메타데이터를 내부저장소에 복사
     */
    private suspend fun copyMetadataToInternal(metadata: HybridBooksMetadata) {
        withContext(Dispatchers.IO) {
            val jsonString = json.encodeToString(HybridBooksMetadata.serializer(), metadata)
            metadataFile.writeText(jsonString)
        }
    }
    
    /**
     * Assets에서 콘텐츠 파일 복사
     */
    private suspend fun copyContentFileFromAssets(fileName: String, targetFile: File) {
        withContext(Dispatchers.IO) {
            try {
                context.assets.open("content/$fileName").use { inputStream ->
                    targetFile.outputStream().use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
                Log.d(TAG, "✅ Copied content file: $fileName")
            } catch (e: Exception) {
                Log.e(TAG, "❌ Failed to copy content file: $fileName", e)
                throw e
            }
        }
    }
    
    /**
     * Assets에서 책 이미지들 복사
     */
    private suspend fun copyBookImagesFromAssets(bookId: Int, targetDir: File) {
        withContext(Dispatchers.IO) {
            try {
                val assetsImagesPath = "images/$bookId"
                val imageFiles = context.assets.list(assetsImagesPath) ?: emptyArray()
                
                for (imageFile in imageFiles) {
                    context.assets.open("$assetsImagesPath/$imageFile").use { inputStream ->
                        File(targetDir, imageFile).outputStream().use { outputStream ->
                            inputStream.copyTo(outputStream)
                        }
                    }
                }
                
                Log.d(TAG, "✅ Copied ${imageFiles.size} images for book $bookId")
            } catch (e: Exception) {
                Log.e(TAG, "❌ Failed to copy images for book $bookId", e)
                throw e
            }
        }
    }
    
    companion object {
        private const val TAG = "HybridContentManager"
    }
}
