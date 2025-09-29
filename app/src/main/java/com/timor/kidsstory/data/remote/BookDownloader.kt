package com.timor.kidsstory.data.remote

import android.content.Context
import android.util.Log
import com.timor.kidsstory.data.local.database.dao.HybridBooksDao
import com.timor.kidsstory.data.local.database.entity.BookSource
import com.timor.kidsstory.data.local.database.entity.HybridBookEntity
import com.timor.kidsstory.data.local.assets.UnifiedDataSource
import com.timor.kidsstory.data.mapper.BookMapper
import com.timor.kidsstory.data.remote.network.BookNetworkService
import com.timor.kidsstory.domain.model.Book
import com.timor.kidsstory.domain.model.DownloadProgress
import com.timor.kidsstory.domain.model.DownloadStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipInputStream
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "BookDownloader"

/**
 * 📂 새로운 폴더 구조:
 * downloaded_books/{bookId}/
 * ├── cover_{bookId}_{lang}.jpg    ← 북커버 (언어별, bookDir 직하위)
 * ├── {bookId}_{lang}.json         ← JSON 콘텐츠 (언어별, bookDir 직하위)
 * └── images/                      ← 페이지 이미지들 (공통, 하위 폴더)
 *     ├── book_{bookId}_page_0.jpg
 *     ├── book_{bookId}_page_1.jpg
 *     └── ...
 */
@Singleton
class BookDownloader @Inject constructor(
    private val context: Context,
    private val networkService: BookNetworkService,
    private val hybridBooksDao: HybridBooksDao,
    private val unifiedDataSource: UnifiedDataSource
) {
    /**
     * 외부 저장소 기본 경로
     */
    private val baseStorageDir: File by lazy {
        context.getExternalFilesDir(null) ?: context.filesDir
    }

    /**
     * 다운로드된 책 저장 디렉토리
     */
    private val downloadedBooksDir: File by lazy {
        File(baseStorageDir, "downloaded_books").apply { mkdirs() }
    }

    /**
     * GitHub에서 책 다운로드
     *
     * @param bookId 책 ID (예: 801)
     * @param languageCode 언어 코드 (예: "ko", "en", "tet")
     * @param forceUpdate 강제 업데이트 여부 (기존 버전 무시하고 새로 다운로드)
     * @return 다운로드된 Book 객체
     */
    suspend fun downloadBook(bookId: Int, languageCode: String, forceUpdate: Boolean = false): Result<Book> {
        return try {
            Log.d(TAG, "Starting download for book $bookId in $languageCode")

            val normalizedLang = normalizeLanguageCode(languageCode)

            // 1. 업데이트 모드가 아니면 이미 다운로드된 책인지 확인
            val isAlreadyDownloaded = hybridBooksDao.isBookExists(bookId, normalizedLang)
            if (isAlreadyDownloaded && !forceUpdate) {
                Log.d(TAG, "Book $bookId already downloaded, loading from local storage")
                val existingBook = loadDownloadedBook(bookId, normalizedLang)
                if (existingBook.isSuccess) {
                    return existingBook
                }
            } else if (isAlreadyDownloaded && forceUpdate) {
                Log.d(TAG, "🔄 Force update mode: Re-downloading book $bookId to update content")
            }

            val metadataResult = unifiedDataSource.loadRemoteBooksMetadata()
            if (metadataResult.isFailure) {
                return Result.failure(metadataResult.exceptionOrNull()!!)
            }

            val metadata = metadataResult.getOrNull()!!
            val bookMeta = metadata.books.find { it.id == bookId }
                ?: return Result.failure(IllegalArgumentException("Book $bookId not found in metadata"))

            val languageContent = bookMeta.languages[normalizedLang]
                ?: return Result.failure(IllegalArgumentException("Language $normalizedLang not supported for book $bookId"))

            // 3. Get URLs from metadata
            val contentUrl = languageContent.contentUrl
            val imagesZipUrl = bookMeta.imageAssetsUrl

            // 4. 📂 새로운 디렉토리 구조 생성
            val bookDir = File(downloadedBooksDir, bookId.toString()).apply { mkdirs() }
            val imagesDir = File(bookDir, "images").apply { mkdirs() }

            // 5. 📝 콘텐츠 JSON 다운로드 (bookDir 직하위)
            val contentFile = File(bookDir, "${bookId}_${normalizedLang}.json")
            val contentDownloaded = networkService.downloadFile(contentUrl, contentFile)
            if (!contentDownloaded) {
                return Result.failure(Exception("Failed to download content JSON"))
            }

            // 6. 🖼️ 커버 이미지 개별 다운로드 (bookDir 직하위)
            val coverFileName = "cover_${bookId}_${normalizedLang}.jpg"
            val coverFile = File(bookDir, coverFileName)
            if (!coverFile.exists()) {
                val coverDownloaded = networkService.downloadFile(languageContent.coverImageUrl, coverFile)
                if (coverDownloaded) {
                    Log.d(TAG, "📸 Cover image downloaded: ${coverFile.absolutePath}")
                } else {
                    Log.w(TAG, "⚠️ Failed to download cover image, will try from ZIP")
                }
            }

            // 7. 📦 이미지 ZIP 다운로드 조건 수정
            val existingVersions = hybridBooksDao.getBooksByStoryId(bookId)
            val hasOtherLanguage = existingVersions.isNotEmpty()

            // 🔧 수정: images 폴더에 페이지 이미지가 있는지만 확인
            val pageImagePattern = "book_${bookId}_page_"
            val hasPageImages = imagesDir.listFiles()?.any {
                it.name.startsWith(pageImagePattern)
            } == true

            Log.d(TAG, "📋 Images download check for book $bookId:")
            Log.d(TAG, "  - Existing versions in DB: ${existingVersions.size}")
            existingVersions.forEach { version ->
                Log.d(TAG, "    * ${version.language} (${version.source})")
            }
            Log.d(TAG, "  - Has other language: $hasOtherLanguage")
            Log.d(TAG, "  - Images dir exists: ${imagesDir.exists()}")
            if (imagesDir.exists()) {
                val imageFiles = imagesDir.listFiles() ?: emptyArray()
                Log.d(TAG, "  - Images dir file count: ${imageFiles.size}")
                imageFiles.take(5).forEach { file ->
                    Log.d(TAG, "    * ${file.name}")
                }
            }
            Log.d(TAG, "  - Has page images: $hasPageImages")
            Log.d(TAG, "  - Will download ZIP: ${!hasPageImages}")

            // 🔧 수정: 페이지 이미지가 없으면 ZIP 다운로드
            if (!hasPageImages) {
                val zipFile = File(bookDir, "images.zip")
                val imagesDownloaded = networkService.downloadFile(imagesZipUrl, zipFile)

                if (imagesDownloaded) {
                    Log.d(TAG, "📦 Starting ZIP extraction for book $bookId")
                    withContext(Dispatchers.IO) {
                        // 🔧 수정: images 디렉토리로 직접 압축 해제
                        extractZipFile(zipFile, imagesDir)
                    }

                    // 🗑️ ZIP 파일 삭제
                    if (zipFile.exists()) {
                        zipFile.delete()
                        Log.d(TAG, "🗑️ ZIP file deleted: ${zipFile.absolutePath}")
                    }

                    // ✅ 압축 해제 결과 확인
                    val imageFiles = imagesDir.listFiles()
                    Log.d(TAG, "✅ Images extracted for book $bookId: ${imageFiles?.size ?: 0} files")
                    imageFiles?.take(10)?.forEach { file ->
                        Log.d(TAG, "  - ${file.name}")
                    }
                } else {
                    Log.e(TAG, "❌ Failed to download images ZIP for book $bookId")
                }
            } else {
                Log.d(TAG, "⏭️ Skipping ZIP download for book $bookId (page images already exist)")
            }

            // 8. 📚 다운로드 정보를 데이터베이스에 저장 (🆕 실제 파일 크기 정보 포함)
            
            // 🔧 실제 파일 크기 측정
            val actualContentSize = if (contentFile.exists()) contentFile.length() else languageContent.contentSize * 1024L
            val actualCoverSize = if (coverFile.exists()) coverFile.length() else languageContent.coverImageSize * 1024L
            
            // 🔧 실제 압축 해제된 이미지 폴더 크기 계산
            val actualImageAssetsSize = if (imagesDir.exists()) {
                calculateDirectorySize(imagesDir)
            } else {
                bookMeta.imageAssetsSize * 1024L // 폴백: GitHub 메타데이터
            }
            
            Log.d(TAG, "📏 Actual file sizes measured:")
            Log.d(TAG, "  - Content: ${actualContentSize / 1024}KB (actual file)")
            Log.d(TAG, "  - Cover: ${actualCoverSize / 1024}KB (actual file)")
            Log.d(TAG, "  - Images: ${actualImageAssetsSize / 1024}KB (uncompressed folder)")
            Log.d(TAG, "  - GitHub ZIP size was: ${bookMeta.imageAssetsSize}KB (for reference)")
            
            val hybridBookEntity = HybridBookEntity(
                id = bookId,
                language = normalizedLang,
                title = languageContent.title,
                level = bookMeta.level,
                category = bookMeta.category,
                unlockStep = bookMeta.unlockStep,
                countryOfOrigin = bookMeta.countryOfOrigin,

                // 🔧 수정: 새로운 경로 구조
                contentPath = contentFile.absolutePath,                    // bookDir 직하위
                coverImagePath = coverFile.absolutePath,                   // bookDir 직하위
                imagesDirectoryPath = imagesDir.absolutePath,              // bookDir/images/

                contentVersion = languageContent.contentVersion,
                coverVersion = languageContent.coverVersion,
                imageAssetsVersion = bookMeta.imageAssetsVersion,
                
                // 🆕 실제 파일 크기 저장 (압축 해제된 상태 기준)
                contentSize = actualContentSize,        // 실제 JSON 파일 크기
                coverImageSize = actualCoverSize,       // 실제 커버 이미지 크기
                imageAssetsSize = actualImageAssetsSize, // 실제 압축 해제된 이미지 폴더 크기
                
                source = BookSource.DOWNLOADED,
                isAvailable = true,
                downloadDate = System.currentTimeMillis(),
                aiFeatures = bookMeta.aiFeatures,
                tags = languageContent.tags
            )

            hybridBooksDao.insertBook(hybridBookEntity)
            Log.d(TAG, "Book $bookId download completed and saved to database")

            // 9. Book 객체로 변환하여 반환
            loadDownloadedBook(bookId, normalizedLang)

        } catch (e: Exception) {
            Log.e(TAG, "Error downloading book $bookId", e)
            Result.failure(e)
        }
    }

    /**
     * 다운로드된 책을 로컬에서 로드
     */
    private suspend fun loadDownloadedBook(bookId: Int, languageCode: String): Result<Book> {
        return try {
            val bookEntity = hybridBooksDao.getBook(bookId, languageCode)
                ?: return Result.failure(IllegalStateException("Downloaded book not found in database"))

            // 🔧 수정: 새로운 경로에서 콘텐츠 로드
            val contentResult = unifiedDataSource.loadBookContent(
                bookId = bookId,
                language = languageCode,
                contentBasePath = File(bookEntity.contentPath).parent
            )

            if (contentResult.isFailure) {
                return Result.failure(contentResult.exceptionOrNull()!!)
            }

            // 메타데이터도 필요
            val metadataResult = unifiedDataSource.loadRemoteBooksMetadata()
            if (metadataResult.isFailure) {
                return Result.failure(metadataResult.exceptionOrNull()!!)
            }

            val metadata = metadataResult.getOrNull()!!
            val bookMeta = metadata.books.find { it.id == bookId }
                ?: return Result.failure(IllegalStateException("Book metadata not found"))

            val content = contentResult.getOrNull()!!
            val book = BookMapper.fromUnified(bookMeta, languageCode, content)

            Result.success(book.copy(
                isDownloaded = true,
                downloadProgress = DownloadProgress(DownloadStatus.DOWNLOADED)
            ))

        } catch (e: Exception) {
            Log.e(TAG, "Error loading downloaded book", e)
            Result.failure(e)
        }
    }

    /**
     * 다운로드된 책 삭제
     */
    suspend fun deleteDownloadedBook(storyId: String): Result<Unit> {
        return try {
            val parts = storyId.split("_")
            if (parts.size != 2) {
                return Result.failure(IllegalArgumentException("Invalid storyId format: $storyId"))
            }

            val bookId = parts[0].toIntOrNull()
                ?: return Result.failure(IllegalArgumentException("Invalid bookId in storyId: $storyId"))

            val languageCode = parts[1]
            val normalizedLang = normalizeLanguageCode(languageCode)

            // 데이터베이스에서 삭제
            val deletedCount = hybridBooksDao.deleteBook(bookId, normalizedLang)

            // 🔧 수정: 해당 언어의 파일들만 삭제
            val bookDir = File(downloadedBooksDir, bookId.toString())

            // 콘텐츠 JSON 삭제
            val contentFile = File(bookDir, "${bookId}_${normalizedLang}.json")
            if (contentFile.exists()) {
                contentFile.delete()
                Log.d(TAG, "📝 Content file deleted: ${contentFile.absolutePath}")
            }

            // 커버 이미지 삭제
            val coverFile = File(bookDir, "cover_${bookId}_${normalizedLang}.jpg")
            if (coverFile.exists()) {
                coverFile.delete()
                Log.d(TAG, "🖼️ Cover file deleted: ${coverFile.absolutePath}")
            }

            // 다른 언어 버전이 없으면 images 폴더와 전체 폴더 삭제
            val remainingVersions = hybridBooksDao.getBooksByStoryId(bookId)
            if (remainingVersions.isEmpty() && bookDir.exists()) {
                bookDir.deleteRecursively()
                Log.d(TAG, "📂 Book directory deleted: ${bookDir.absolutePath}")
            } else {
                Log.d(TAG, "📚 Other language versions exist, keeping images folder")
            }

            Log.d(TAG, "Downloaded book deleted: $storyId (rows affected: $deletedCount)")
            Result.success(Unit)

        } catch (e: Exception) {
            Log.e(TAG, "Error deleting downloaded book: $storyId", e)
            Result.failure(e)
        }
    }

    /**
     * 커버 이미지 미리 다운로드 (캐시용)
     */
    suspend fun preloadCoverImage(coverUrl: String): String? {
        return try {
            val cacheDir = File(context.cacheDir, "covers").apply { mkdirs() }
            val fileName = coverUrl.substringAfterLast("/")
            val file = File(cacheDir, fileName)

            if (!file.exists()) {
                val success = networkService.downloadFile(coverUrl, file)
                if (!success) return null
            }

            file.absolutePath
        } catch (e: Exception) {
            Log.e(TAG, "Failed to preload cover: $coverUrl", e)
            null
        }
    }

    /**
     * ZIP 파일 압축 해제 (images 폴더에 평면적으로 추출)
     */
    private fun extractZipFile(zipFile: File, destinationDir: File) {
        try {
            Log.d(TAG, "📦 Extracting ZIP file: ${zipFile.path} to ${destinationDir.path}")
            ZipInputStream(zipFile.inputStream()).use { zipIn ->
                var entry = zipIn.nextEntry
                var entriesExtracted = 0

                while (entry != null) {
                    if (!entry.isDirectory) {
                        // ZIP 내부 경로 구조를 무시하고 파일명만 사용
                        val fileName = entry.name.substringAfterLast("/")
                        val outputFile = File(destinationDir, fileName)

                        Log.d(TAG, "📄 Extracting: ${entry.name} -> ${outputFile.name}")

                        outputFile.parentFile?.mkdirs()
                        FileOutputStream(outputFile).use { output ->
                            zipIn.copyTo(output)
                            entriesExtracted++
                        }

                        Log.d(TAG, "✅ Extracted: ${outputFile.absolutePath}")
                    }

                    zipIn.closeEntry()
                    entry = zipIn.nextEntry
                }

                Log.d(TAG, "✅ ZIP extraction completed. Total files extracted: $entriesExtracted")
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error extracting ZIP file", e)
            throw e
        }
    }

    /**
     * 실제 삭제될 용량 계산
     */
    suspend fun calculateDeleteSize(
        bookId: Int,
        languageCode: String,
        versionCount: Int? = null
    ): Long {
        return try {
            val normalizedLang = normalizeLanguageCode(languageCode)

            Log.d(TAG, "🗑️ Calculating DELETE size for book $bookId in $normalizedLang")

            val existingVersions = hybridBooksDao.getBooksByStoryId(bookId)
            val currentLanguageBook = existingVersions.find { it.language == normalizedLang }

            if (currentLanguageBook == null) {
                Log.d(TAG, "🗑️ Book $bookId ($normalizedLang) not found in database")
                return 0L
            }

            var totalSize = 0L

            // 🔧 수정: 새로운 경로 구조에 따른 크기 계산

            // 1. 커버 이미지 크기
            val coverFile = File(currentLanguageBook.coverImagePath ?: "")
            if (coverFile.exists()) {
                totalSize += coverFile.length()
                Log.d(TAG, "🖼️ Cover file: ${coverFile.name} - ${coverFile.length() / 1024}KB")
            }

            // 2. 콘텐츠 JSON 크기
            val contentFile = File(currentLanguageBook.contentPath)
            if (contentFile.exists()) {
                totalSize += contentFile.length()
                Log.d(TAG, "📝 Content file: ${contentFile.name} - ${contentFile.length() / 1024}KB")
            }

            // 3. 다른 언어 버전 확인
            val otherLanguageVersions = existingVersions.filter { it.language != normalizedLang }
            val isLastVersion = versionCount?.let { it <= 1 } ?: otherLanguageVersions.isEmpty()

            Log.d(TAG, "📚 Other language versions: ${otherLanguageVersions.size}, Is last version: $isLastVersion")

            // 4. 마지막 언어 버전이면 images 폴더 전체 크기 포함
            if (isLastVersion) {
                val imagesDir = File(currentLanguageBook.imagesDirectoryPath ?: "")
                if (imagesDir.exists()) {
                    val imagesFolderSize = calculateDirectorySize(imagesDir)
                    totalSize += imagesFolderSize
                    Log.d(TAG, "📁 Images folder size: ${imagesFolderSize / 1024}KB")
                }
            }

            Log.d(TAG, "🗑️ Total DELETE size for book $bookId ($normalizedLang): ${totalSize / 1024}KB")
            totalSize

        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to calculate delete size for book $bookId in $languageCode", e)
            // 기본값 반환
            try {
                val existingVersions = hybridBooksDao.getBooksByStoryId(bookId)
                val otherLanguageVersions = existingVersions.filter { it.language != normalizeLanguageCode(languageCode) }
                val isLastVersion = otherLanguageVersions.isEmpty()

                if (isLastVersion) {
                    3 * 1024 * 1024L + 250 * 1024L // 3.25MB (전체 삭제)
                } else {
                    250 * 1024L // 250KB (언어별 파일만)
                }
            } catch (dbError: Exception) {
                250 * 1024L // 기본값: 250KB
            }
        }
    }

    /**
     * 디렉토리의 전체 크기 계산
     */
    private fun calculateDirectorySize(directory: File): Long {
        return try {
            var size = 0L

            directory.listFiles()?.forEach { file ->
                size += if (file.isDirectory) {
                    calculateDirectorySize(file)
                } else {
                    file.length()
                }
            }

            size
        } catch (e: Exception) {
            Log.e(TAG, "Error calculating directory size: ${directory.absolutePath}", e)
            0L
        }
    }

    /**
     * 언어 코드 정규화
     */
    private fun normalizeLanguageCode(language: String): String {
        return when {
            language.startsWith("ko") -> "ko"
            language.startsWith("tet") -> "tet"
            language.startsWith("en") -> "en"
            language.startsWith("mn") -> "mn"
            else -> "en"
        }
    }

    /**
     * 실제 다운로드할 용량 계산 (🆕 Room DB 저장 크기 정보 사용)
     */
    suspend fun calculateDownloadSize(bookId: Int, languageCode: String): Long {
        return try {
            val normalizedLang = normalizeLanguageCode(languageCode)

            Log.d(TAG, "🔍 Calculating download size for book $bookId in $normalizedLang")

            // 1. 다른 언어 버전이 이미 있는지 확인
            val existingVersions = hybridBooksDao.getBooksByStoryId(bookId)
            val hasOtherLanguage = existingVersions.isNotEmpty()

            Log.d(TAG, "📚 Existing versions for book $bookId: ${existingVersions.size}, Has other language: $hasOtherLanguage")

            // 2. 🆕 기존 다운로드된 버전에서 크기 정보 가져오기
            val existingSameLanguageBook = existingVersions.find { it.language == normalizedLang }
            
            var totalSize = 0L
            var coverSize = 0L
            var contentSize = 0L
            var imagesSize = 0L
            
            if (existingSameLanguageBook != null) {
                // 🔄 재다운로드: Room DB에 저장된 크기 정보 사용
                Log.d(TAG, "🔄 Re-download detected, using stored size info from Room DB")
                
                coverSize = existingSameLanguageBook.coverImageSize
                contentSize = existingSameLanguageBook.contentSize
                
                // 페이지 이미지가 없으면 이미지 크기도 포함
                val bookDir = File(downloadedBooksDir, bookId.toString())
                val imagesDir = File(bookDir, "images")
                val pageImagePattern = "book_${bookId}_page_"
                val hasPageImages = imagesDir.exists() && imagesDir.listFiles()?.any {
                    it.name.startsWith(pageImagePattern)
                } == true
                
                if (!hasPageImages) {
                    imagesSize = existingSameLanguageBook.imageAssetsSize
                } else {
                    Log.d(TAG, "✅ Page images already exist, skipping images download")
                }
                
                totalSize = coverSize + contentSize + imagesSize
                
                Log.d(TAG, "📊 Using stored size info:")
                Log.d(TAG, "  - Cover: ${coverSize / 1024}KB (from DB)")
                Log.d(TAG, "  - Content: ${contentSize / 1024}KB (from DB)")
                if (!hasPageImages) {
                    Log.d(TAG, "  - Images: ${imagesSize / 1024}KB (from DB)")
                }
                
            } else {
                // 🆕 최초 다운로드: GitHub 메타데이터에서 크기 정보 가져오기
                Log.d(TAG, "🆕 First-time download, fetching size info from GitHub metadata")
                
                val metadataResult = unifiedDataSource.loadRemoteBooksMetadata()
                if (metadataResult.isFailure) {
                    Log.w(TAG, "Failed to load metadata for size calculation")
                    return getEstimatedDownloadSize(bookId, hasOtherLanguage)
                }

                val metadata = metadataResult.getOrNull()!!
                val bookMeta = metadata.books.find { it.id == bookId }
                    ?: return getEstimatedDownloadSize(bookId, hasOtherLanguage)

                val languageContent = bookMeta.languages[normalizedLang]
                    ?: return getEstimatedDownloadSize(bookId, hasOtherLanguage)

                // GitHub 메타데이터에서 크기 정보 사용 (KB -> Bytes)
                coverSize = languageContent.coverImageSize * 1024L
                contentSize = languageContent.contentSize * 1024L
                
                // 다른 언어 버전이 없으면 이미지도 다운로드
                if (!hasOtherLanguage) {
                    // 🔧 주의: GitHub에서는 ZIP 크기를 다운로드하지만, 실제로는 압축 해제된 크기가 저장됨
                    // 압축 해제 비율을 고려하여 다운로드 크기는 ZIP 크기로 표시
                    imagesSize = bookMeta.imageAssetsSize * 1024L  // ZIP 크기 (다운로드용)
                } else {
                    Log.d(TAG, "📚 Other language versions exist, skipping images download")
                }
                
                totalSize = coverSize + contentSize + imagesSize
                
                Log.d(TAG, "📊 Using GitHub metadata:")
                Log.d(TAG, "  - Cover: ${coverSize / 1024}KB (from GitHub)")
                Log.d(TAG, "  - Content: ${contentSize / 1024}KB (from GitHub)")
                if (!hasOtherLanguage) {
                    Log.d(TAG, "  - Images: ${imagesSize / 1024}KB (ZIP size from GitHub)")
                    Log.d(TAG, "  ⚠️ Note: Actual stored size will be larger after ZIP extraction")
                }
            }
            
            if (totalSize == 0L) {
                Log.w(TAG, "Calculated download size is 0 for book $bookId. Using fallback estimation.")
                return getEstimatedDownloadSize(bookId, hasOtherLanguage)
            }

            Log.d(TAG, "📊 Total download size for book $bookId ($normalizedLang): ${totalSize / 1024}KB")
            totalSize

        } catch (e: Exception) {
            Log.e(TAG, "Failed to calculate download size for book $bookId", e)
            return getEstimatedDownloadSize(bookId, false)
        }
    }
    
    /**
     * 추정 다운로드 크기 반환 (폴백용)
     */
    private fun getEstimatedDownloadSize(bookId: Int, hasOtherLanguage: Boolean): Long {
        val estimatedCoverSize = 50 * 1024L  // 50KB
        val estimatedContentSize = 17 * 1024L // 17KB
        val estimatedImagesSize = if (!hasOtherLanguage) {
            getEstimatedImageSize(bookId) * 1024L // KB -> Bytes
        } else {
            0L
        }
        
        return estimatedCoverSize + estimatedContentSize + estimatedImagesSize
    }
    
    /**
     * 책별 추정 이미지 크기 (KB 단위)
     */
    private fun getEstimatedImageSize(bookId: Int): Long {
        val estimatedPageCount = when (bookId) {
            104, 105 -> 7  // 레벨 1 책들
            403 -> 10      // 레벨 2 책
            802 -> 15      // 레벨 3 책
            807 -> 16      // 레벨 4 책 (긴 전설)
            810 -> 13      // 레벨 5 책
            816 -> 13      // 필리핀 사실들
            821 -> 9       // 병아리 별
            904 -> 13      // 햇님의 하루
            else -> 10     // 기본값
        }
        
        return estimatedPageCount * 225L // 페이지당 225KB
    }
}