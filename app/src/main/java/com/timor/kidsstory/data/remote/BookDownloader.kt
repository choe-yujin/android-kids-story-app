package com.timor.kidsstory.data.remote

import android.content.Context
import android.util.Log
import com.timor.kidsstory.data.local.database.dao.DownloadedBooksDao
import com.timor.kidsstory.data.local.database.entity.DownloadedBookEntity
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
 * 통합 구조 전용 책 다운로드 관리 클래스
 * - 새로운 통합 메타데이터 구조만 지원
 * - GitHub에서 책 콘텐츠 다운로드
 */
@Singleton
class BookDownloader @Inject constructor(
    private val context: Context,
    private val networkService: BookNetworkService,
    private val downloadedBooksDao: DownloadedBooksDao,
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
     * @return 다운로드된 Book 객체
     */
    suspend fun downloadBook(bookId: Int, languageCode: String): Result<Book> {
        return try {
            Log.d(TAG, "Starting download for book $bookId in $languageCode")
            
            val normalizedLang = normalizeLanguageCode(languageCode)
            
            // 1. 이미 다운로드된 책인지 확인
            val isAlreadyDownloaded = downloadedBooksDao.isBookDownloaded(bookId, normalizedLang)
            if (isAlreadyDownloaded) {
                Log.d(TAG, "Book $bookId already downloaded, loading from local storage")
                val existingBook = loadDownloadedBook(bookId, normalizedLang)
                if (existingBook.isSuccess) {
                    return existingBook
                }
            }

            // 2. GitHub에서 메타데이터 확인
            val metadataResult = unifiedDataSource.loadBooksMetadata()
            if (metadataResult.isFailure) {
                return Result.failure(metadataResult.exceptionOrNull()!!)
            }

            val metadata = metadataResult.getOrNull()!!
            val bookMeta = metadata.books.find { it.id == bookId }
                ?: return Result.failure(IllegalArgumentException("Book $bookId not found in metadata"))

            val languageContent = bookMeta.languages[normalizedLang]
                ?: return Result.failure(IllegalArgumentException("Language $normalizedLang not supported for book $bookId"))

            // 3. 다운로드 URL 구성 (GitHub Raw URL)
            val baseUrl = "https://raw.githubusercontent.com/your-repo/android-kids-story-app/main"
            val contentUrl = "$baseUrl/app/src/main/assets/content/${bookId}_${normalizedLang}.json"
            val imagesZipUrl = "$baseUrl/app/src/main/assets/images/${bookId}_images.zip"
            
            // 4. 다운로드 디렉토리 생성
            val bookDir = File(downloadedBooksDir, bookId.toString()).apply { mkdirs() }
            val contentDir = File(bookDir, "content").apply { mkdirs() }
            val imagesDir = File(bookDir, "images").apply { mkdirs() }

            // 5. 콘텐츠 JSON 다운로드
            val contentFile = File(contentDir, "${bookId}_${normalizedLang}.json")
            val contentDownloaded = networkService.downloadFile(contentUrl, contentFile)
            if (!contentDownloaded) {
                return Result.failure(Exception("Failed to download content JSON"))
            }

            // 6. 이미지 ZIP 다운로드 (다른 언어 버전이 없는 경우에만)
            val existingVersions = downloadedBooksDao.getDownloadedBooksByStoryId(bookId.toString())
            val hasOtherLanguage = existingVersions.isNotEmpty()
            val imagesExist = imagesDir.exists() && imagesDir.listFiles()?.isNotEmpty() == true

            if (!hasOtherLanguage && !imagesExist) {
                val zipFile = File(bookDir, "images.zip")
                val imagesDownloaded = networkService.downloadFile(imagesZipUrl, zipFile)
                
                if (imagesDownloaded) {
                    withContext(Dispatchers.IO) {
                        extractZipFile(zipFile, imagesDir)
                    }
                    zipFile.delete()
                    Log.d(TAG, "Images extracted for book $bookId")
                }
            }

            // 7. 다운로드 정보를 데이터베이스에 저장
            val downloadEntity = DownloadedBookEntity(
                id = bookId,
                storyId = "${bookId}_${normalizedLang}",
                language = normalizedLang,
                title = languageContent.title,
                coverImagePath = "file:///android_asset/images/${bookId}/cover_${bookId}_${normalizedLang}.jpg",
                contentJsonPath = contentFile.absolutePath,
                hasImages = true,
                downloadDate = System.currentTimeMillis(),
                category = bookMeta.category,
                level = bookMeta.level
            )

            downloadedBooksDao.insertDownloadedBook(downloadEntity)
            Log.d(TAG, "Book $bookId download completed and saved to database")

            // 8. Book 객체로 변환하여 반환
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
            val bookEntity = downloadedBooksDao.getDownloadedBook(bookId, languageCode)
                ?: return Result.failure(IllegalStateException("Downloaded book not found in database"))

            // 외부 콘텐츠 파일에서 로드
            val contentResult = unifiedDataSource.loadBookContent(
                bookId = bookId,
                language = languageCode,
                contentBasePath = File(bookEntity.contentJsonPath).parent
            )

            if (contentResult.isFailure) {
                return Result.failure(contentResult.exceptionOrNull()!!)
            }

            // 메타데이터도 필요
            val metadataResult = unifiedDataSource.loadBooksMetadata()
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

            // 데이터베이스에서 삭제
            val deletedCount = downloadedBooksDao.deleteDownloadedBook(bookId, languageCode)
            
            // 해당 언어 파일만 삭제 (다른 언어 버전이 있을 수 있으므로)
            val bookDir = File(downloadedBooksDir, bookId.toString())
            val contentDir = File(bookDir, "content")
            val contentFile = File(contentDir, "${bookId}_${languageCode}.json")
            
            if (contentFile.exists()) {
                contentFile.delete()
                Log.d(TAG, "Content file deleted: ${contentFile.absolutePath}")
            }

            // 다른 언어 버전이 없으면 전체 폴더 삭제
            val remainingVersions = downloadedBooksDao.getDownloadedBooksByStoryId(bookId.toString())
            if (remainingVersions.isEmpty() && bookDir.exists()) {
                bookDir.deleteRecursively()
                Log.d(TAG, "Book directory deleted: ${bookDir.absolutePath}")
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
     * ZIP 파일 압축 해제
     */
    private fun extractZipFile(zipFile: File, destinationDir: File) {
        try {
            Log.d(TAG, "Extracting ZIP file: ${zipFile.path} to ${destinationDir.path}")
            ZipInputStream(zipFile.inputStream()).use { zipIn ->
                var entry = zipIn.nextEntry
                var entriesExtracted = 0

                while (entry != null) {
                    val entryFile = File(destinationDir, entry.name)
                    Log.d(TAG, "Extracting entry: ${entry.name}")

                    if (entry.isDirectory) {
                        entryFile.mkdirs()
                    } else {
                        entryFile.parentFile?.mkdirs()

                        FileOutputStream(entryFile).use { output ->
                            zipIn.copyTo(output)
                            entriesExtracted++
                        }
                    }

                    zipIn.closeEntry()
                    entry = zipIn.nextEntry
                }

                Log.d(TAG, "ZIP extraction completed. Total entries extracted: $entriesExtracted")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error extracting ZIP file", e)
            throw e
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
}
