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
 * 📂 새로운 폴더 구조를 지원하는 HybridContentManager:
 *
 * 내장 책 (Internal Storage):
 * hybrid_content/
 * ├── books_metadata.json
 * ├── content/{bookId}_{lang}.json
 * └── images/{bookId}/
 *     ├── cover_{bookId}_{lang}.webp
 *     └── book_{bookId}_page_*.webp
 *
 * 다운로드 책 (External Storage):
 * downloaded_books/{bookId}/
 * ├── cover_{bookId}_{lang}.webp    ← 북커버 (언어별, bookDir 직하위)
 * ├── {bookId}_{lang}.json         ← JSON 콘텐츠 (언어별, bookDir 직하위)
 * └── images/                      ← 페이지 이미지들 (공통, 하위 폴더)
 *     ├── book_{bookId}_page_0.webp
 *     └── book_{bookId}_page_*.webp
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
     * 내부 저장소 기본 경로 (내장 책용)
     */
    private val internalStorageDir: File by lazy {
        File(context.filesDir, "hybrid_content").apply { mkdirs() }
    }

    /**
     * 🆕 외부 저장소 기본 경로 (다운로드된 책용)
     */
    private val externalStorageDir: File by lazy {
        context.getExternalFilesDir(null) ?: context.filesDir
    }

    /**
     * 다운로드된 책 디렉토리
     */
    private val downloadedBooksDir: File by lazy {
        File(externalStorageDir, "downloaded_books").apply { mkdirs() }
    }

    /**
     * 메타데이터 파일 경로
     */
    private val metadataFile: File by lazy {
        File(internalStorageDir, "books_metadata.json")
    }

    /**
     * 내장 콘텐츠 디렉토리
     */
    private val contentDir: File by lazy {
        File(internalStorageDir, "content").apply { mkdirs() }
    }

    /**
     * 내장 이미지 디렉토리
     */
    private val imagesDir: File by lazy {
        File(internalStorageDir, "images").apply { mkdirs() }
    }

    /**
     * 하이브리드 콘텐츠 초기화
     */
    suspend fun initializeHybridContent(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "🚀 Initializing hybrid content system with new folder structure...")

            val prefs = context.getSharedPreferences("hybrid_content_prefs", Context.MODE_PRIVATE)
            val isFirstRun = !prefs.getBoolean("content_initialized", false)
            val storedVersion = prefs.getInt("content_version", 0)

            Log.d(TAG, "🔍 First run: $isFirstRun, Stored version: $storedVersion")

            // 1. 메타데이터 초기화/업데이트 체크
            val metadataInitResult = initializeMetadata(isFirstRun, storedVersion)
            if (metadataInitResult.isFailure) {
                return@withContext metadataInitResult
            }

            // 2. 내장 책들을 DB에 등록 (첫 실행시에만)
            if (isFirstRun) {
                val dbInitResult = initializeBooksInDatabase()
                if (dbInitResult.isFailure) {
                    return@withContext dbInitResult
                }

                val contentInitResult = initializeContentFiles(true)
                if (contentInitResult.isFailure) {
                    return@withContext contentInitResult
                }

                val imagesInitResult = initializeImageFiles(true)
                if (imagesInitResult.isFailure) {
                    return@withContext imagesInitResult
                }

                val currentMetadata = loadMetadataFromInternal().getOrThrow()
                prefs.edit()
                    .putBoolean("content_initialized", true)
                    .putInt("content_version", currentMetadata.version)
                    .apply()

                Log.d(TAG, "✅ First run initialization completed")
            } else {
                val contentInitResult = initializeContentFiles(false)
                if (contentInitResult.isFailure) {
                    return@withContext contentInitResult
                }

                val imagesInitResult = initializeImageFiles(false)
                if (imagesInitResult.isFailure) {
                    return@withContext imagesInitResult
                }

                Log.d(TAG, "✅ Subsequent run - only restored missing files")
            }

            Log.d(TAG, "✅ Hybrid content initialization completed with new folder structure")
            Result.success(Unit)

        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to initialize hybrid content", e)
            Result.failure(e)
        }
    }

    /**
     * 내장 책들을 Room DB에 등록 (새로운 경로 구조 반영)
     */
    private suspend fun initializeBooksInDatabase(): Result<Unit> {
        return try {
            val metadata = loadMetadataFromInternal().getOrThrow()

            for (book in metadata.books) {
                for ((languageCode, languageContent) in book.languages) {
                    if (languageContent.isBundled) {

                        val existsInDb = hybridBooksDao.isBookExists(book.id, languageCode)

                        if (!existsInDb) {
                            // 🔧 수정: 내장 책 경로 구조 (기존 유지)
                            val hybridBookEntity = HybridBookEntity(
                                id = book.id,
                                language = languageCode,
                                title = languageContent.title,
                                level = book.level,
                                category = book.category,
                                unlockStep = book.unlockStep,
                                countryOfOrigin = book.countryOfOrigin,

                                // 내장 책 파일 경로 (내부저장소 기준)
                                contentPath = File(contentDir, "${book.id}_$languageCode.json").absolutePath,
                                coverImagePath = File(imagesDir, "${book.id}/cover_${book.id}_$languageCode.webp").absolutePath,
                                imagesDirectoryPath = File(imagesDir, book.id.toString()).absolutePath,

                                contentVersion = languageContent.contentVersion,
                                coverVersion = languageContent.coverVersion,
                                imageAssetsVersion = book.imageAssetsVersion,
                                source = BookSource.BUNDLED,
                                isAvailable = true,
                                downloadDate = null,
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
    private suspend fun initializeMetadata(isFirstRun: Boolean, storedVersion: Int): Result<Unit> {
        return try {
            val assetsMetadata = loadMetadataFromAssets()
            val localMetadata = if (metadataFile.exists()) {
                loadMetadataFromFile(metadataFile)
            } else null

            val needsUpdate = isFirstRun || localMetadata == null ||
                    assetsMetadata.version > (localMetadata?.version ?: 0) ||
                    assetsMetadata.version > storedVersion

            if (needsUpdate) {
                Log.d(TAG, "📥 Updating metadata: ${localMetadata?.version ?: 0} → ${assetsMetadata.version}")
                copyMetadataToInternal(assetsMetadata)
                Log.d(TAG, "✅ Metadata updated successfully")
            } else {
                Log.d(TAG, "✅ Metadata is up to date (v${localMetadata?.version ?: 0})")
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
    private suspend fun initializeContentFiles(isFirstRun: Boolean): Result<Unit> {
        return try {
            val metadata = loadMetadataFromInternal().getOrThrow()

            for (book in metadata.books) {
                for ((languageCode, languageContent) in book.languages) {
                    if (languageContent.isBundled) {
                        val contentFileName = "${book.id}_$languageCode.json"
                        val localContentFile = File(contentDir, contentFileName)

                        if (isFirstRun) {
                            if (!localContentFile.exists()) {
                                Log.d(TAG, "📥 Copying initial content: $contentFileName")
                                copyContentFileFromAssets(contentFileName, localContentFile)
                            }
                        } else {
                            val bookExistsInDb = hybridBooksDao.isBookExists(book.id, languageCode)

                            if (!localContentFile.exists() && bookExistsInDb) {
                                Log.d(TAG, "📥 Restoring missing content: $contentFileName")
                                copyContentFileFromAssets(contentFileName, localContentFile)
                            }
                        }
                    }
                }
            }

            Log.d(TAG, "✅ Content files initialization completed (firstRun: $isFirstRun)")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to initialize content files", e)
            Result.failure(e)
        }
    }

    /**
     * 이미지 파일들 초기화
     */
    private suspend fun initializeImageFiles(isFirstRun: Boolean): Result<Unit> {
        return try {
            val metadata = loadMetadataFromInternal().getOrThrow()

            for (book in metadata.books) {
                val bookImagesDir = File(imagesDir, book.id.toString()).apply { mkdirs() }

                if (isFirstRun) {
                    val hasNoImages = bookImagesDir.listFiles()?.isEmpty() != false
                    if (hasNoImages) {
                        Log.d(TAG, "📥 Copying initial images for book ${book.id}")
                        copyBookImagesFromAssets(book.id, bookImagesDir)
                    }
                } else {
                    val bookExistsInDb = hybridBooksDao.getBooksByStoryId(book.id).isNotEmpty()
                    val hasNoImages = bookImagesDir.listFiles()?.isEmpty() != false
                    val needsCoverImage = !File(bookImagesDir, "cover_${book.id}_ko.webp").exists() &&
                            !File(bookImagesDir, "cover_${book.id}_en.webp").exists() &&
                            !File(bookImagesDir, "cover_${book.id}_tet.webp").exists()

                    if (hasNoImages && bookExistsInDb && needsCoverImage) {
                        Log.d(TAG, "📥 Restoring missing images for book ${book.id}")
                        copyBookImagesFromAssets(book.id, bookImagesDir)
                    }
                }
            }

            Log.d(TAG, "✅ Image files initialization completed (firstRun: $isFirstRun)")
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
     * 콘텐츠 로드 (내장/다운로드 책 모두 지원)
     */
    suspend fun loadBookContent(bookId: Int, languageCode: String, contentBasePath: String? = null): Result<UnifiedBookContent> {
        return withContext(Dispatchers.IO) {
            var contentFile: File? = null
            try {
                if (contentBasePath != null) {
                    // 🔧 수정: 다운로드 책용 - 새로운 경로 구조
                    contentFile = File(contentBasePath, "${bookId}_$languageCode.json")
                } else {
                    // 내장 책용 - 기존 경로 구조 유지
                    contentFile = File(contentDir, "${bookId}_$languageCode.json")
                }

                if (!contentFile.exists()) {
                    return@withContext Result.failure(
                        IllegalStateException("Content file not found: ${contentFile.absolutePath}")
                    )
                }

                val contentJson = contentFile.readText()
                val content = json.decodeFromString<UnifiedBookContent>(contentJson)

                Log.d(TAG, "✅ Loaded content from: ${contentFile.absolutePath}")
                Result.success(content)
            } catch (e: Exception) {
                Log.e(TAG, "❌ Failed to load content from: ${contentFile?.absolutePath ?: "unknown path"}", e)
                Result.failure(e)
            }
        }
    }

    /**
     * 콘텐츠 파일 경로 반환 (내부저장소 기준)
     */
    fun getContentPath(bookId: Int, languageCode: String): String {
        return File(contentDir, "${bookId}_$languageCode.json").absolutePath
    }

    /**
     * 이미지 파일 경로 반환 (내부저장소 기준)
     */
    fun getImagePath(bookId: Int, imageName: String): String {
        return File(imagesDir, "$bookId/$imageName").absolutePath
    }

    /**
     * 🔧 수정된 이미지 URL 반환 (언어별 정확한 이미지 찾기)
     */
    suspend fun getImageUrl(bookId: Int, imageName: String): String? = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "🔍 Resolving image: $bookId/$imageName")

            // 🆕 언어 코드 추출 (imageName에서)
            val targetLanguage = extractLanguageFromImageName(imageName)
            Log.d(TAG, "🌍 Target language from image name: $targetLanguage")

            // 1. DB에서 해당 언어의 책 정보 우선 조회
            val bookEntities = hybridBooksDao.getBooksByStoryId(bookId)
            if (bookEntities.isEmpty()) {
                Log.w(TAG, "❌ Book $bookId not found in database")
                return@withContext null
            }

            Log.d(TAG, "📚 Found ${bookEntities.size} book entries in DB")

            // 2. 🆕 해당 언어의 엔티티 우선 처리
            val targetEntity = if (targetLanguage != null) {
                bookEntities.find { it.language == targetLanguage }
                    ?: bookEntities.firstOrNull() // 폴백: 언어가 없으면 첫 번째
            } else {
                bookEntities.firstOrNull()
            }

            if (targetEntity == null) {
                Log.w(TAG, "❌ No suitable entity found for $bookId/$imageName")
                return@withContext null
            }

            Log.d(TAG, "🎯 Using entity: ${targetEntity.language} (${targetEntity.source})")

            // 3. 해당 엔티티에서 이미지 경로 확인
            val imagePath = resolveImagePath(imageName, targetEntity)
            if (imagePath != null) {
                val imageFile = File(imagePath)
                Log.d(TAG, "📷 Checking ${targetEntity.source} path: ${imageFile.absolutePath}")

                if (imageFile.exists()) {
                    Log.d(TAG, "✅ Found image via DB path: ${imageFile.absolutePath}")
                    return@withContext "file://${imageFile.absolutePath}"
                } else {
                    Log.w(TAG, "⚠️ Image file doesn't exist: ${imageFile.absolutePath}")
                }
            }

            // 4. 🆕 폴백: 다른 언어들에서도 찾아보기 (해당 언어가 없을 때)
            if (targetLanguage != null) {
                for (entity in bookEntities.filter { it.language != targetLanguage }) {
                    val fallbackImagePath = resolveImagePath(imageName, entity)
                    if (fallbackImagePath != null) {
                        val fallbackFile = File(fallbackImagePath)
                        if (fallbackFile.exists()) {
                            Log.d(TAG, "✅ Found image via fallback entity (${entity.language}): ${fallbackFile.absolutePath}")
                            return@withContext "file://${fallbackFile.absolutePath}"
                        }
                    }
                }
            }

            // 5. 최종 Fallback: 기존 하드코딩된 경로들 확인
            val fallbackPaths = listOf(
                // 내부저장소 기본 경로 (내장 책)
                File(imagesDir, "$bookId/$imageName"),
                // 외부저장소 패턴들 (다운로드 책 - 구버전 호환)
                File(externalStorageDir, "downloaded_books/$bookId/images/$imageName"),
                File(externalStorageDir, "downloaded_books/$bookId/$imageName"),
                // 새로운 다운로드 책 경로 구조
                File(downloadedBooksDir, "$bookId/images/$imageName"),
                File(downloadedBooksDir, "$bookId/$imageName")
            )

            for (fallbackFile in fallbackPaths) {
                if (fallbackFile.exists()) {
                    Log.d(TAG, "✅ Found image via fallback: ${fallbackFile.absolutePath}")
                    return@withContext "file://${fallbackFile.absolutePath}"
                }
            }

            Log.w(TAG, "❌ Image not found anywhere: $bookId/$imageName")
            return@withContext null

        } catch (e: Exception) {
            Log.e(TAG, "❌ Error resolving image: $bookId/$imageName", e)
            return@withContext null
        }
    }
    
    /**
     * 🆕 이미지 파일명에서 언어 코드 추출
     */
    private fun extractLanguageFromImageName(imageName: String): String? {
        // cover_801_ko.webp -> "ko"
        // book_801_page_1.webp -> null (언어 정보 없음)
        val coverPattern = Regex("cover_(\\d+)_([a-z]{2,3})\\.webp")
        val match = coverPattern.find(imageName)
        return match?.groupValues?.get(2) // 두 번째 그룹이 언어 코드
    }

    /**
     * 🆕 이미지 경로 결정 로직 (새로운 폴더 구조 반영)
     */
    private fun resolveImagePath(imageName: String, entity: HybridBookEntity): String? {
        return when (entity.source) {
            BookSource.BUNDLED -> {
                // 내장 책: 기존 경로 구조 유지
                File(entity.imagesDirectoryPath, imageName).absolutePath
            }
            BookSource.DOWNLOADED -> {
                // 다운로드 책: 새로운 경로 구조
                when {
                    imageName.startsWith("cover_") -> {
                        // 커버 이미지: bookDir 직하위
                        entity.coverImagePath
                    }
                    else -> {
                        // 페이지 이미지: bookDir/images/ 하위
                        File(entity.imagesDirectoryPath, imageName).absolutePath
                    }
                }
            }
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

    /**
     * 🔍 디버깅용: 특정 책의 이미지 경로 상태 조회
     */
    suspend fun debugImagePathsForBook(bookId: Int): String = withContext(Dispatchers.IO) {
        val debug = StringBuilder()
        debug.appendLine("🔍 Debugging image paths for book $bookId (New Structure):")

        try {
            // 1. DB 정보 확인
            val bookEntities = hybridBooksDao.getBooksByStoryId(bookId)
            debug.appendLine("📚 Books in DB: ${bookEntities.size}")

            for (entity in bookEntities) {
                debug.appendLine("  - ${entity.language}: ${entity.source}")
                debug.appendLine("    Images dir: ${entity.imagesDirectoryPath}")
                debug.appendLine("    Cover path: ${entity.coverImagePath}")

                val resolvedPath = when (entity.source) {
                    BookSource.BUNDLED -> {
                        File(entity.imagesDirectoryPath)
                    }
                    BookSource.DOWNLOADED -> {
                        // 새로운 구조: bookDir/images/
                        File(entity.imagesDirectoryPath)
                    }
                }

                debug.appendLine("    Resolved dir: ${resolvedPath.absolutePath}")
                debug.appendLine("    Dir exists: ${resolvedPath.exists()}")

                if (resolvedPath.exists()) {
                    val imageFiles = resolvedPath.listFiles()?.map { it.name }?.sorted() ?: emptyList()
                    debug.appendLine("    Files (${imageFiles.size}): ${imageFiles.take(10)}")
                }
            }

            // 2. 새로운 폴더 구조 확인
            debug.appendLine("\n📁 New structure paths:")
            val newStructurePaths = listOf(
                File(downloadedBooksDir, "$bookId"),
                File(downloadedBooksDir, "$bookId/images"),
                File(imagesDir, "$bookId")
            )

            for (path in newStructurePaths) {
                debug.appendLine("  Path: ${path.absolutePath}")
                debug.appendLine("    Exists: ${path.exists()}")
                if (path.exists()) {
                    val files = path.listFiles()?.map { it.name }?.take(5) ?: emptyList()
                    debug.appendLine("    Files: $files")
                }
            }

        } catch (e: Exception) {
            debug.appendLine("❌ Error during debug: ${e.message}")
        }

        debug.toString()
    }

    companion object {
        private const val TAG = "HybridContentManager"
    }
}