package com.timor.kidsstory.data.remote

import android.content.Context
import android.util.Log
import com.timor.kidsstory.data.local.database.dao.DownloadedBooksDao
import com.timor.kidsstory.data.local.database.entity.DownloadedBookEntity
import com.timor.kidsstory.data.local.assets.AssetDataSource // Added import
import com.timor.kidsstory.data.mapper.toBook // Added import
import com.timor.kidsstory.data.remote.model.RemoteBook
import com.timor.kidsstory.data.remote.network.BookNetworkService
import com.timor.kidsstory.domain.model.Book
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipInputStream
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "BookDownloader"

/**
 * 책 다운로드 관리 클래스
 */
@Singleton
class BookDownloader @Inject constructor(
    private val context: Context,
    private val networkService: BookNetworkService,
    private val downloadedBooksDao: DownloadedBooksDao,
    private val assetDataSource: AssetDataSource // Added injection
) {
    /**
     * 외부 저장소 기본 경로
     */
    private val baseStorageDir: File by lazy {
        context.getExternalFilesDir(null) ?: context.filesDir
    }

    /**
     * 책 저장 디렉토리
     */
    private val booksDir: File by lazy {
        File(baseStorageDir, "books").apply { mkdirs() }
    }

    /**
     * 책 다운로드
     */
    suspend fun downloadBook(remoteBook: RemoteBook, languageCode: String): Result<Book> {
        return try {
            // 이미 다운로드된 책인지 확인
            val isAlreadyDownloaded = downloadedBooksDao.isBookDownloaded(remoteBook.id, languageCode)
            if (isAlreadyDownloaded) {
                // 기존 다운로드된 책 정보 반환
                val existingBook = downloadedBooksDao.getDownloadedBook(remoteBook.id, languageCode)
                if (existingBook != null) {
                    val bookContentResult = assetDataSource.loadExternalBookContent(existingBook.contentJsonPath)
                    return bookContentResult.map { response ->
                        // 다운로드된 책의 이미지 폴더 경로 구성
                        val contentJsonFile = File(existingBook.contentJsonPath)
                        val bookRootDir = contentJsonFile.parentFile?.parentFile
                        val imageFolderPath = File(bookRootDir, "images").absolutePath
                        response.toBook(languageCode, existingBook.level, existingBook.category, existingBook.coverImagePath, imageFolderPath)
                    }
                }
            }

            // 언어코드로 적절한 키 결정
            val langKey = when {
                languageCode.startsWith("ko") -> "ko"
                languageCode.startsWith("tet") -> "tet"
                languageCode.startsWith("mn") -> "mn" // Added for Mongolian
                else -> "en"  // 기본값은 영어
            }

            // 다운로드 URL 가져오기
            val downloadUrl = remoteBook.download[langKey] ?: throw IllegalArgumentException("No download URL for language: $langKey")
            val coverUrl = remoteBook.cover[langKey] ?: throw IllegalArgumentException("No cover URL for language: $langKey")
            val title = remoteBook.title[langKey] ?: remoteBook.title["en"] ?: "Book ${remoteBook.id}"
            // 카테고리 정보 추출
            val category = remoteBook.category

            // 책 저장 디렉토리 생성
            val bookDir = File(booksDir, "${remoteBook.id}").apply { mkdirs() }
            val imagesDir = File(bookDir, "images").apply { mkdirs() }
            val translationsDir = File(bookDir, "translations").apply { mkdirs() }

            // 이미 같은 책의 다른 언어 버전이 있는지 확인
            val otherLanguageVersions = downloadedBooksDao.getDownloadedBooksByStoryId(remoteBook.id.toString())
            val hasOtherLanguage = otherLanguageVersions.isNotEmpty()
            val imagesExist = imagesDir.exists() && imagesDir.listFiles()?.isNotEmpty() == true

            // 1. 북 커버 이미지 다운로드
            val coverFileName = coverUrl.substringAfterLast("/")
            val coverFile = File(bookDir, coverFileName)
            val coverDownloaded = networkService.downloadFile(coverUrl, coverFile)

            if (!coverDownloaded) {
                return Result.failure(Exception("Failed to download cover image"))
            }

            // 2. 콘텐츠 JSON 파일 다운로드
            val jsonFileName = downloadUrl.substringAfterLast("/")
            val jsonFile = File(translationsDir, jsonFileName)
            val jsonDownloaded = networkService.downloadFile(downloadUrl, jsonFile)

            if (!jsonDownloaded) {
                return Result.failure(Exception("Failed to download content JSON"))
            }

            // 3. 이미지 ZIP 다운로드 (다른 언어 버전이 없는 경우에만)
            var imagesDownloaded = imagesExist
            if (!hasOtherLanguage && !imagesExist) {
                val zipFile = File(bookDir, "images.zip")
                imagesDownloaded = networkService.downloadFile(remoteBook.images, zipFile)

                if (imagesDownloaded) {
                    // ZIP 파일 압축 해제
                    withContext(Dispatchers.IO) {
                        extractZipFile(zipFile, imagesDir)
                    }
                    // 압축 해제 후 ZIP 파일 삭제
                    zipFile.delete()
                    Log.d(TAG, "Images ZIP file extracted and deleted")
                } else {
                    return Result.failure(Exception("Failed to download images"))
                }
            }

            // 4. 데이터베이스에 다운로드 정보 저장
            val bookEntity = DownloadedBookEntity(
                id = remoteBook.id,
                storyId = "${remoteBook.id}_$languageCode",
                language = languageCode,
                title = title,
                coverImagePath = coverFile.absolutePath,
                contentJsonPath = jsonFile.absolutePath,
                hasImages = imagesDownloaded || imagesExist,
                downloadDate = System.currentTimeMillis(),
                category = category, // 카테고리 정보 추가
                level = remoteBook.level // level 정보 추가
            )

            downloadedBooksDao.insertDownloadedBook(bookEntity)
            Log.d(TAG, "Book download completed and saved to database: ${bookEntity.storyId}")

            // 5. Book 객체로 변환하여 반환
            val bookContentResult = assetDataSource.loadExternalBookContent(jsonFile.absolutePath)
            return bookContentResult.map { response ->
                // 다운로드된 책의 이미지 폴더 경로 구성
                val bookRootDir = jsonFile.parentFile?.parentFile
                val imageFolderPath = File(bookRootDir, "images").absolutePath
                response.toBook(languageCode, remoteBook.level, remoteBook.category, coverUrl, imageFolderPath)
            }

        } catch (e: Exception) {
            Log.e(TAG, "Error downloading book", e)
            Result.failure(e)
        }
    }

    /**
     * 커버 이미지만 미리 다운로드
     */
    suspend fun preloadCoverImage(coverUrl: String): String? {
        return try {
            val cacheDir = File(context.cacheDir, "covers").apply { mkdirs() }
            val fileName = coverUrl.substringAfterLast("/")
            val file = File(cacheDir, fileName)

            // 이미 다운로드되지 않은 경우에만 다운로드
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

                    // 디렉토리면 생성
                    if (entry.isDirectory) {
                        entryFile.mkdirs()
                        Log.d(TAG, "Created directory: ${entryFile.path}")
                    } else {
                        // 파일이면 내용 복사
                        entryFile.parentFile?.mkdirs()

                        FileOutputStream(entryFile).use { output ->
                            zipIn.copyTo(output)
                            entriesExtracted++
                            Log.d(TAG, "Extracted file: ${entryFile.path}")
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
}