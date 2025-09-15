package com.timor.kidsstory.data.repository

import android.util.Log
import com.timor.kidsstory.data.local.assets.AssetDataSource
import com.timor.kidsstory.data.local.database.dao.DownloadedBooksDao
import com.timor.kidsstory.data.mapper.BookMapper
import com.timor.kidsstory.data.mapper.toBook
import com.timor.kidsstory.domain.model.Book
import com.timor.kidsstory.domain.model.DownloadProgress
import com.timor.kidsstory.domain.model.DownloadStatus
import com.timor.kidsstory.domain.repository.BookRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * BookRepository 구현체
 * - 로컬 Asset과 다운로드된 책을 통합 관리
 */
@Singleton
class BookRepositoryImpl @Inject constructor(
    private val assetDataSource: AssetDataSource,
    private val downloadedBooksDao: DownloadedBooksDao
) : BookRepository {

    /**
     * 기존 GetBooksUseCase에서 사용하는 메서드
     */
    override suspend fun getBooks(languageCode: String): Result<List<Book>> {
        return try {
            Log.d("BookRepositoryImpl", "Getting books for language: $languageCode")
            
            // 1. 로컬 Asset 책 로드
            val localBooksResult = getLocalBooks(languageCode)
            val localBooks = localBooksResult.getOrNull() ?: emptyList()
            
            // 2. 다운로드된 책 로드
            val downloadedBooks = getDownloadedBooks(languageCode)
            
            // 3. 로컬 책은 다운로드 상태로 표시
            val localBooksWithDownloadStatus = localBooks.map { localBook ->
                localBook.copy(
                    isDownloaded = true,
                    downloadProgress = DownloadProgress(DownloadStatus.DOWNLOADED)
                )
            }
            
            // 4. 중복 제거 (로컬 책 우선)
            val localStoryIds = localBooksWithDownloadStatus.map { it.storyId.split("_").first() }.toSet()
            val newDownloadedBooks = downloadedBooks.filter {
                !localStoryIds.contains(it.storyId.split("_").first())
            }
            
            val combinedBooks = localBooksWithDownloadStatus + newDownloadedBooks
            
            Log.d("BookRepositoryImpl", "Successfully loaded ${combinedBooks.size} books")
            Result.success(combinedBooks)
            
        } catch (e: Exception) {
            Log.e("BookRepositoryImpl", "Error getting books", e)
            Result.failure(e)
        }
    }

    /**
     * 기존 GetBookDetailUseCase에서 사용하는 메서드
     */
    override suspend fun getBookById(storyId: String, languageCode: String): Result<Book?> {
        return try {
            Log.d("BookRepositoryImpl", "Getting book detail for: $storyId")
            
            val allBooksResult = getBooks(languageCode)
            if (allBooksResult.isSuccess) {
                val book = allBooksResult.getOrNull()?.find { it.storyId == storyId }
                Result.success(book)
            } else {
                allBooksResult.map { null }
            }
        } catch (e: Exception) {
            Log.e("BookRepositoryImpl", "Error getting book detail", e)
            Result.failure(e)
        }
    }

    /**
     * 로컬 Asset 책 목록 로드
     */
    private suspend fun getLocalBooks(languageCode: String): Result<List<Book>> {
        return try {
            // AssetDataSource의 실제 메서드 사용
            val booksResult = assetDataSource.loadBooks()
            
            if (booksResult.isSuccess) {
                val bookDtos = booksResult.getOrNull() ?: emptyList()
                
                // BookDto를 Book으로 변환하면서 언어 필터링
                val books = bookDtos
                    .filter { bookDto ->
                        // 해당 언어 책만 필터링
                        bookDto.storyId.contains(languageCode) || 
                        (languageCode.startsWith("ko") && bookDto.storyId.contains("ko-kr")) ||
                        (languageCode.startsWith("tet") && bookDto.storyId.contains("tetum")) ||
                        (languageCode == "en-ph" && bookDto.storyId.contains("en-ph"))
                    }
                    .map { bookDto ->
                        BookMapper.mapToDomain(bookDto, languageCode)
                    }
                
                Result.success(books)
            } else {
                booksResult.map { emptyList() }
            }
        } catch (e: Exception) {
            Log.e("BookRepositoryImpl", "Error loading local books", e)
            Result.failure(e)
        }
    }

    /**
     * 다운로드된 책 목록 로드
     */
    private suspend fun getDownloadedBooks(languageCode: String): List<Book> {
        return try {
            val downloadedEntities = downloadedBooksDao.getDownloadedBooksByLanguage(languageCode)

            downloadedEntities.mapNotNull { entity ->
                val bookContentResult = assetDataSource.loadExternalBookContent(entity.contentJsonPath)
                bookContentResult.getOrNull()?.let { response ->
                    val contentJsonFile = File(entity.contentJsonPath)
                    val bookRootDir = contentJsonFile.parentFile?.parentFile
                    val imageFolderPath = File(bookRootDir, "images").absolutePath
                    
                    // toBook 메서드의 실제 시그니처에 맞게 호출
                    response.toBook(
                        language = languageCode,
                        level = entity.level,
                        category = entity.category,
                        coverImage = entity.coverImagePath,
                        imageFolderPath = imageFolderPath
                    )
                }
            }
        } catch (e: Exception) {
            Log.e("BookRepositoryImpl", "Error loading downloaded books", e)
            emptyList()
        }
    }

    override suspend fun isBookDownloaded(storyId: String): Boolean {
        return try {
            val languageCode = storyId.split("_").getOrNull(1) ?: return false
            
            // 로컬 Asset 책인지 확인
            val localBooksResult = getLocalBooks(languageCode)
            if (localBooksResult.isSuccess) {
                val hasLocalBook = localBooksResult.getOrNull()?.any { it.storyId == storyId } ?: false
                if (hasLocalBook) return true
            }

            // 다운로드된 책인지 확인
            val downloadedBooks = downloadedBooksDao.getDownloadedBooksByLanguage(languageCode)
            downloadedBooks.any { entity ->
                entity.storyId == storyId
            }
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun downloadBook(storyId: String): Result<Unit> {
        // TODO: 다운로드 로직 구현
        return Result.success(Unit)
    }

    override fun observeDownloadProgress(storyId: String): Flow<Float> {
        // TODO: 다운로드 진행률 관찰 구현
        return flowOf(0f)
    }
}
