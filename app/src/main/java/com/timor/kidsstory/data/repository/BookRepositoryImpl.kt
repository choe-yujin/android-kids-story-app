package com.timor.kidsstory.data.repository

import android.util.Log
import com.timor.kidsstory.data.local.assets.AssetDataSource
import com.timor.kidsstory.data.local.database.dao.DownloadedBooksDao
import com.timor.kidsstory.data.mapper.BookMapper
import com.timor.kidsstory.data.mapper.PageMapper
import com.timor.kidsstory.data.mapper.toBook // Added import
import com.timor.kidsstory.domain.model.Book
import com.timor.kidsstory.domain.model.Page
import com.timor.kidsstory.domain.repository.BookRepository
import java.io.File
import javax.inject.Inject

/**
 * BookRepository 인터페이스의 구현체
 * - 실제 데이터 소스에서 책 정보를 가져와 도메인 모델로 변환
 *
 * @property assetDataSource 앱 Assets에서 데이터 로드를 담당하는 데이터 소스
 * @property downloadedBooksDao 다운로드된 책 정보에 접근하는 DAO
 */
class BookRepositoryImpl @Inject constructor(
    private val assetDataSource: AssetDataSource,
    private val downloadedBooksDao: DownloadedBooksDao
) : BookRepository {

    private val TAG = "BookRepositoryImpl"

    /**
     * 특정 언어로 된 모든 책 목록을 가져옴
     *
     * @param language 언어 코드
     * @return 책 목록 또는 오류
     */
    override suspend fun getBooks(language: String): Result<List<Book>> {
        Log.d(TAG, "Getting books for language: $language")

        return try {
            assetDataSource.loadBooks().map { storyDtos ->
                // 언어 접두사 결정 (ko, tet, en)
                val languagePrefix = when {
                    language.startsWith("ko") -> "ko"
                    language.startsWith("tet") -> "tet"
                    language.startsWith("mn") -> "mn" // Added for Mongolian
                    else -> "en"
                }

                // 해당 언어로 된 책만 필터링
                val filteredBooks = storyDtos.filter {
                    it.storyId.contains(languagePrefix, ignoreCase = true)
                }

                Log.d(TAG, "Found ${filteredBooks.size} books for language $language")

                // BookMapper를 통해 DTO를 도메인 모델로 변환
                filteredBooks.map { BookMapper.mapToDomain(it, language) }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting books", e)
            Result.failure(e)
        }
    }

    /**
     * ID로 특정 책의 정보를 가져옴
     *
     * @param storyId 책 ID (예: "801_en-ph")
     * @param language 언어 코드
     * @return 책 정보 또는 오류
     */
    override suspend fun getBookById(storyId: String, language: String): Result<Book?> {
        val baseId = storyId.split("_").firstOrNull() ?: storyId
        Log.d(TAG, "Getting book by ID: $baseId, full storyId: $storyId")

        return try {
            // 1. 먼저 다운로드된 책인지 확인하고, 있다면 외부 저장소에서 로드
            try {
                val downloadedBookEntity = downloadedBooksDao.getDownloadedBook(baseId.toInt(), language)
                if (downloadedBookEntity != null) {
                    Log.d(TAG, "Found downloaded book entity: ${downloadedBookEntity.storyId}")
                    val bookContentResult = assetDataSource.loadExternalBookContent(downloadedBookEntity.contentJsonPath)
                    return bookContentResult.map { response ->
                        val contentJsonFile = File(downloadedBookEntity.contentJsonPath)
                        val bookRootDir = contentJsonFile.parentFile?.parentFile
                        val imageFolderPath = File(bookRootDir, "images").absolutePath
                        response.toBook(
                            language,
                            downloadedBookEntity.level,
                            downloadedBookEntity.category,
                            downloadedBookEntity.coverImagePath, // Pass the full path
                            imageFolderPath, // This is for page images, not cover
                            downloadedBookEntity.bookVersion // Pass bookVersion
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error checking downloaded book or loading external content", e)
                // 오류 발생해도 앱 내장 책 확인 계속 진행
            }

            // 2. 다운로드된 책이 아니면 앱 내장 책 (assets)에서 로드
            // 먼저 assetDataSource에서 메타데이터를 로드하여 selectedBookDto를 찾습니다.
            val selectedBookDtoResult = assetDataSource.loadBooks().mapCatching { allBookDtos ->
                val langPrefix = when {
                    language.startsWith("ko") -> "ko"
                    language.startsWith("tet") -> "tet"
                    language.startsWith("mn") -> "mn"
                    else -> "en"
                }
                allBookDtos.find { bookDto ->
                    bookDto.storyId.split("_").first() == baseId &&
                    (bookDto.storyId.contains(langPrefix, ignoreCase = true) ||
                     (langPrefix == "en" && !bookDto.storyId.contains("ko", ignoreCase = true) && !bookDto.storyId.contains("tet", ignoreCase = true) && !bookDto.storyId.contains("mn", ignoreCase = true)))
                } ?: allBookDtos.find { bookDto ->
                    bookDto.storyId.split("_").first() == baseId && bookDto.storyId.contains("en", ignoreCase = true)
                }
            }

            val selectedBookDto = selectedBookDtoResult.getOrThrow() // Throw if metadata not found

            if (selectedBookDto == null) {
                Log.e(TAG, "BookDto metadata not found for storyId: $storyId")
                return Result.success(null) // Book metadata not found
            }

            // 언어에 맞는 coverUrl 가져오기 (from selectedBookDto)
            val coverUrl = "file:///android_asset/images/${baseId}/${selectedBookDto.coverImage}"

            Log.d(TAG, "Loading built-in book from assets for storyId: $storyId")
            val bookContentResult = assetDataSource.loadBookPages(storyId, language)
            return bookContentResult.map { response ->
                response.toBook(
                    language,
                    selectedBookDto.level, // Pass level from selectedBookDto
                    selectedBookDto.category, // Pass category from selectedBookDto
                    coverUrl, // Pass coverUrl
                    null, // imageFolderPath is null for built-in books
                    selectedBookDto.bookVersion // Pass bookVersion
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting book by ID", e)
            Result.failure(e)
        }
    }

    /**
     * 특정 책의 모든 페이지 정보를 가져옴
     *
     * @param storyId 책 ID
     * @param language 언어 코드
     * @return 페이지 목록 또는 오류
     */
    override suspend fun getBookPages(storyId: String, language: String): Result<List<Page>> {
        // storyId에서 기본 ID 추출
        val baseId = storyId.split("_").firstOrNull() ?: storyId

        Log.d(TAG, "Getting book pages for base ID: $baseId, language: $language")

        try {
            // 먼저 다운로드된 책인지 확인
            try {
                val downloadedBook = downloadedBooksDao.getDownloadedBook(baseId.toInt(), language)

                // 책이 다운로드되어 있는 경우 외부 저장소에서 로드
                if (downloadedBook != null) {
                    Log.d(TAG, "Loading downloaded book from: ${downloadedBook.contentJsonPath}")

                    val pagesResult = assetDataSource.loadExternalBookContent(downloadedBook.contentJsonPath)

                    return pagesResult.map { response ->
                        // 다운로드된 책의 이미지 폴더 경로 구성
                        val contentJsonFile = File(downloadedBook.contentJsonPath)
                        val bookRootDir = contentJsonFile.parentFile?.parentFile
                        val imageFolderPath = File(bookRootDir, "images").absolutePath

                        Log.d(TAG, "Using image folder path: $imageFolderPath")

                        // 개별 페이지 매핑 - 다운로드된 책 처리
                        val mappedPages = response.pages.map { pageDto ->
                            PageMapper.mapToDomain(
                                pageDto = pageDto,
                                storyBaseId = baseId,
                                isDownloaded = true,
                                imageFolderPath = imageFolderPath
                            )
                        }.sortedBy { it.pageNumber }

                        PageMapper.addTotalPagesInfo(mappedPages)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error checking downloaded book", e)
                // 다운로드 확인 중 오류 발생해도 앱 내장 책 로드 계속 진행
            }

            // 앱 내장 책은 기존 방식대로 assets에서 로드
            Log.d(TAG, "Loading built-in book from assets")

            // 언어에 맞는 파일 이름 결정
            val languageCode = when {
                language.startsWith("ko") -> "ko-kr"
                language.startsWith("tet") -> "tetum"
                language.startsWith("mn") -> "mn-MN" // Added for Mongolian
                else -> "en-ph"
            }

            val fullStoryId = "${baseId}_${languageCode}"
            Log.d(TAG, "Loading pages for full story ID: $fullStoryId")

            // 페이지 로드 및 매핑
            val pagesResult = assetDataSource.loadBookPages(fullStoryId, language)

            return pagesResult.map { response ->
                // 개별 페이지 매핑
                val mappedPages = response.pages.map { pageDto ->
                    PageMapper.mapToDomain(pageDto, baseId)
                }.sortedBy { it.pageNumber }

                // totalPages 정보 추가
                PageMapper.addTotalPagesInfo(mappedPages)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting book pages", e)
            return Result.failure(e)
        }
    }
}