package com.timor.kidsstory.data.repository

import android.util.Log
import com.timor.kidsstory.data.dto.PageContentResponse
import com.timor.kidsstory.data.local.assets.UnifiedDataSource
import com.timor.kidsstory.data.local.database.dao.HybridBooksDao
import com.timor.kidsstory.data.local.database.entity.BookSource
import com.timor.kidsstory.data.mapper.BookMapper
import com.timor.kidsstory.domain.manager.content.HybridContentManager
import com.timor.kidsstory.domain.model.Book
import com.timor.kidsstory.domain.model.StoryInfo
import com.timor.kidsstory.domain.repository.BookRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 통합 구조 전용 BookRepository 구현체
 * - 새로운 통합 메타데이터 구조만 지원
 * - 이제 DB를 Single Source of Truth로 사용
 */
@Singleton
class BookRepositoryImpl @Inject constructor(
    private val unifiedDataSource: UnifiedDataSource,
    private val hybridBooksDao: HybridBooksDao,
    private val hybridContentManager: HybridContentManager // 🆕 추가
) : BookRepository {

    /**
     * 지정된 언어의 모든 책 가져오기 (DB 기반)
     */
    override suspend fun getBooks(languageCode: String): Result<List<Book>> {
        return try {
            val normalizedLanguageCode = normalizeLanguageCode(languageCode)
            val bookEntities = hybridBooksDao.getAvailableBooksByLanguage(normalizedLanguageCode)

            val remoteMetadata = unifiedDataSource.loadRemoteBooksMetadata().getOrNull()

            // 🆕 비동기 변환을 위해 순차 처리
            val books = mutableListOf<Book>()
            for (entity in bookEntities) {
                try {
                    // 🔧 수정: 책의 소스에 따라 다른 로딩 방식 사용
                    val contentResult = when (entity.source) {
                        BookSource.BUNDLED -> {
                            unifiedDataSource.loadBookContent(
                                bookId = entity.id,
                                language = normalizedLanguageCode,
                                contentBasePath = null
                            )
                        }
                        BookSource.DOWNLOADED -> {
                            val contentBasePath = File(entity.contentPath).parent
                            unifiedDataSource.loadBookContent(
                                bookId = entity.id,
                                language = normalizedLanguageCode,
                                contentBasePath = contentBasePath
                            )
                        }
                    }
                    val content = contentResult.getOrNull()
                    
                    val bookMeta = remoteMetadata?.books?.find { it.id == entity.id }
                    
                    // 🆕 HybridContentManager를 BookMapper에 전달하여 DB 기반 이미지 경로 사용
                    val book = BookMapper.fromHybridEntity(entity, content, hybridContentManager, bookMeta)
                    books.add(book)
                } catch (e: Exception) {
                    Log.e("BookRepositoryImpl", "Error mapping book entity ${entity.id}", e)
                }
            }
            Result.success(books)
        } catch (e: Exception) {
            Log.e("BookRepositoryImpl", "Error getting books from DB", e)
            Result.failure(e)
        }
    }

    /**
     * 특정 책의 상세 정보 가져오기
     */
    override suspend fun getBookById(storyId: String, languageCode: String): Result<Book?> {
        return try {
            Log.d("BookRepositoryImpl", "Getting book detail for: $storyId")

            val parts = storyId.split("_")
            if (parts.size != 2) {
                return Result.failure(IllegalArgumentException("Invalid storyId format: $storyId"))
            }

            val bookId = parts[0].toIntOrNull()
                ?: return Result.failure(IllegalArgumentException("Invalid bookId in storyId: $storyId"))
            
            val normalizedLanguageCode = normalizeLanguageCode(languageCode)

            val entity = hybridBooksDao.getBook(bookId, normalizedLanguageCode)
                ?: return Result.success(null)

            // 🔧 수정: 책의 소스에 따라 다른 로딩 방식 사용
            val contentResult = when (entity.source) {
                BookSource.BUNDLED -> {
                    unifiedDataSource.loadBookContent(
                        bookId = entity.id,
                        language = normalizedLanguageCode,
                        contentBasePath = null
                    )
                }
                BookSource.DOWNLOADED -> {
                    val contentBasePath = File(entity.contentPath).parent
                    unifiedDataSource.loadBookContent(
                        bookId = entity.id,
                        language = normalizedLanguageCode,
                        contentBasePath = contentBasePath
                    )
                }
            }
            val content = contentResult.getOrNull()

            val remoteMetadata = unifiedDataSource.loadRemoteBooksMetadata().getOrNull()
            val bookMeta = remoteMetadata?.books?.find { it.id == entity.id }

            // 🆕 HybridContentManager를 BookMapper에 전달하여 DB 기반 이미지 경로 사용
            val book = BookMapper.fromHybridEntity(entity, content, hybridContentManager, bookMeta)

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
     * 특정 책의 상세 정보 (줄거리, 사전/사후 질문) 조회
     */
    override suspend fun getStoryInfo(storyId: String, languageCode: String): Result<StoryInfo> {
        return try {
            Log.d("BookRepositoryImpl", "🔍 Getting story info for: $storyId, language: $languageCode")

            val parts = storyId.split("_")
            if (parts.size != 2) {
                Log.e("BookRepositoryImpl", "❌ Invalid storyId format: $storyId")
                return Result.failure(IllegalArgumentException("Invalid storyId format: $storyId"))
            }

            val bookId = parts[0].toIntOrNull()
            if (bookId == null) {
                Log.e("BookRepositoryImpl", "❌ Invalid bookId in storyId: $storyId")
                return Result.failure(IllegalArgumentException("Invalid bookId in storyId: $storyId"))
            }
            
            val normalizedLanguageCode = normalizeLanguageCode(languageCode)
            Log.d("BookRepositoryImpl", "🔍 Looking for book ID: $bookId, normalized language: $normalizedLanguageCode")

            // 🔍 디버깅: DB에 어떤 책들이 있는지 확인
            val allBooksInLanguage = hybridBooksDao.getAvailableBooksByLanguage(normalizedLanguageCode)
            Log.d("BookRepositoryImpl", "📚 All books in $normalizedLanguageCode: ${allBooksInLanguage.map { "${it.id} (${it.source})" }}")

            val entity = hybridBooksDao.getBook(bookId, normalizedLanguageCode)
            if (entity == null) {
                Log.e("BookRepositoryImpl", "❌ Book not found in DB: bookId=$bookId, language=$normalizedLanguageCode")
                return Result.failure(NoSuchElementException("Book not found in DB"))
            }

            Log.d("BookRepositoryImpl", "✅ Found book entity: ${entity.id}, source: ${entity.source}, contentPath: ${entity.contentPath}")

            // 🔧 수정: 책의 소스에 따라 다른 로딩 방식 사용
            val contentResult = when (entity.source) {
                BookSource.BUNDLED -> {
                    // 내장 책: 기존 방식 (contentBasePath = null)
                    Log.d("BookRepositoryImpl", "📖 Loading BUNDLED book content")
                    unifiedDataSource.loadBookContent(
                        bookId = entity.id,
                        language = normalizedLanguageCode,
                        contentBasePath = null
                    )
                }
                BookSource.DOWNLOADED -> {
                    // 다운로드된 책: contentPath의 디렉토리를 contentBasePath로 사용
                    val contentBasePath = File(entity.contentPath).parent
                    Log.d("BookRepositoryImpl", "📱 Loading DOWNLOADED book content from: $contentBasePath")
                    unifiedDataSource.loadBookContent(
                        bookId = entity.id,
                        language = normalizedLanguageCode,
                        contentBasePath = contentBasePath
                    )
                }
            }

            if (contentResult.isFailure) {
                Log.e("BookRepositoryImpl", "❌ Failed to load book content", contentResult.exceptionOrNull())
                return Result.failure(contentResult.exceptionOrNull()!!)
            }

            val content = contentResult.getOrNull()!!
            Log.d("BookRepositoryImpl", "✅ Loaded book content successfully")
            
            val summary = content.summary
            Log.d("BookRepositoryImpl", "📖 Summary length: ${summary?.length ?: 0}")
            
            val preQuestions = content.comprehensionChecks?.preQuestions?.map { it.question } ?: emptyList()
            val postQuestions = content.comprehensionChecks?.postQuestions?.map { it.question } ?: emptyList()
            Log.d("BookRepositoryImpl", "❓ Questions - Pre: ${preQuestions.size}, Post: ${postQuestions.size}")
            
            val storyInfo = StoryInfo(
                storyId = storyId,
                summary = summary,
                preQuestions = preQuestions,
                postQuestions = postQuestions
            )
            
            Log.d("BookRepositoryImpl", "✅ Story info created successfully for $storyId")
            Result.success(storyInfo)
            
        } catch (e: Exception) {
            Log.e("BookRepositoryImpl", "❌ Error getting story info for $storyId", e)
            Result.failure(e)
        }
    }

    /**
     * 🔍 디버깅용: 특정 책의 이미지 경로 상태 조회
     */
    suspend fun debugBookImagePaths(storyId: String): String {
        val parts = storyId.split("_")
        if (parts.size != 2) {
            return "Invalid storyId format: $storyId"
        }
        
        val bookId = parts[0].toIntOrNull() ?: return "Invalid bookId in storyId: $storyId"
        
        return hybridContentManager.debugImagePathsForBook(bookId)
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
