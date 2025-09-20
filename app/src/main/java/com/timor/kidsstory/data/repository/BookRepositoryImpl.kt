package com.timor.kidsstory.data.repository

import android.util.Log
import com.timor.kidsstory.data.dto.PageContentResponse
import com.timor.kidsstory.data.local.assets.UnifiedDataSource
import com.timor.kidsstory.data.local.database.dao.DownloadedBooksDao
import com.timor.kidsstory.data.mapper.BookMapper
import com.timor.kidsstory.domain.model.Book
import com.timor.kidsstory.domain.model.DownloadProgress
import com.timor.kidsstory.domain.model.DownloadStatus
import com.timor.kidsstory.domain.repository.BookRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 통합 구조 전용 BookRepository 구현체
 * - 새로운 통합 메타데이터 구조만 지원
 * - 기존 호환성 코드 모두 제거
 */
@Singleton
class BookRepositoryImpl @Inject constructor(
    private val unifiedDataSource: UnifiedDataSource,
    private val downloadedBooksDao: DownloadedBooksDao
) : BookRepository {

    /**
     * 지정된 언어의 모든 책 가져오기
     */
    override suspend fun getBooks(languageCode: String): Result<List<Book>> {
        return try {
            Log.d("BookRepositoryImpl", "Getting books for language: $languageCode")

            // 1. 통합 메타데이터 로드
            val metadataResult = unifiedDataSource.loadBooksMetadata()
            if (metadataResult.isFailure) {
                return Result.failure(metadataResult.exceptionOrNull()!!)
            }

            val metadata = metadataResult.getOrNull()!!
            val normalizedLanguageCode = normalizeLanguageCode(languageCode)

            // 2. 해당 언어를 지원하는 책들만 필터링하여 변환
            val books = metadata.books.mapNotNull { bookMeta ->
                if (bookMeta.languages.containsKey(normalizedLanguageCode)) {
                    try {
                        // 각 책의 콘텐츠도 함께 로드하여 완전한 Book 객체 생성
                        val contentResult = unifiedDataSource.loadBookContent(
                            bookId = bookMeta.id,
                            language = normalizedLanguageCode
                        )
                        
                        val content = contentResult.getOrNull()
                        val book = BookMapper.fromUnified(bookMeta, normalizedLanguageCode, content)
                        
                        // 다운로드 상태 설정 (내장 책은 항상 다운로드됨)
                        book.copy(
                            isDownloaded = true,
                            downloadProgress = DownloadProgress(DownloadStatus.DOWNLOADED)
                        )
                    } catch (e: Exception) {
                        Log.w("BookRepositoryImpl", "Failed to load book ${bookMeta.id}", e)
                        null
                    }
                } else {
                    null
                }
            }

            Log.d("BookRepositoryImpl", "Successfully loaded ${books.size} books")
            Result.success(books)

        } catch (e: Exception) {
            Log.e("BookRepositoryImpl", "Error getting books", e)
            Result.failure(e)
        }
    }

    /**
     * 특정 책의 상세 정보 가져오기
     */
    override suspend fun getBookById(storyId: String, languageCode: String): Result<Book?> {
        return try {
            Log.d("BookRepositoryImpl", "Getting book detail for: $storyId")

            // storyId에서 bookId와 언어 추출 (예: "801_ko" -> 801, "ko")
            val parts = storyId.split("_")
            if (parts.size != 2) {
                return Result.failure(IllegalArgumentException("Invalid storyId format: $storyId"))
            }

            val bookId = parts[0].toIntOrNull()
                ?: return Result.failure(IllegalArgumentException("Invalid bookId in storyId: $storyId"))
            
            val normalizedLanguageCode = normalizeLanguageCode(languageCode)

            // 1. 메타데이터에서 책 정보 찾기
            val metadataResult = unifiedDataSource.loadBooksMetadata()
            if (metadataResult.isFailure) {
                return Result.failure(metadataResult.exceptionOrNull()!!)
            }

            val metadata = metadataResult.getOrNull()!!
            val bookMeta = metadata.books.find { it.id == bookId }
                ?: return Result.success(null)

            if (!bookMeta.languages.containsKey(normalizedLanguageCode)) {
                return Result.success(null)
            }

            // 2. 콘텐츠 로드
            val contentResult = unifiedDataSource.loadBookContent(bookId, normalizedLanguageCode)
            val content = contentResult.getOrNull()

            // 3. Book 객체로 변환
            val book = BookMapper.fromUnified(bookMeta, normalizedLanguageCode, content)

            Result.success(book)

        } catch (e: Exception) {
            Log.e("BookRepositoryImpl", "Error getting book detail", e)
            Result.failure(e)
        }
    }

    /**
     * 로컬 Asset 책 목록 로드
     */
    override suspend fun getLocalBooks(languageCode: String): Result<List<Book>> {
        // getBooks와 동일 (모든 내장 책은 로컬 책)
        return getBooks(languageCode)
    }

    /**
     * 다운로드된 책 목록 로드 (향후 구현)
     */
    override suspend fun getDownloadedBooks(languageCode: String): Result<List<Book>> {
        // TODO: 다운로드 기능 구현 시 추가
        return Result.success(emptyList())
    }

    override suspend fun isBookDownloaded(storyId: String): Boolean {
        // 현재는 모든 내장 책이 "다운로드됨" 상태
        return true
    }

    override suspend fun downloadBook(storyId: String): Result<Unit> {
        // TODO: 다운로드 로직 구현
        return Result.success(Unit)
    }

    override fun observeDownloadProgress(storyId: String): Flow<Float> {
        // TODO: 다운로드 진행률 관찰 구현
        return flowOf(0f)
    }

    override suspend fun loadExternalBookContent(contentPath: String): Result<PageContentResponse> {
        // TODO: 향후 다운로드 기능에서 필요시 구현
        return Result.failure(UnsupportedOperationException("Legacy external content not supported"))
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
