package com.timor.kidsstory.data.repository

import android.util.Log
import com.timor.kidsstory.data.dto.PageContentResponse
import com.timor.kidsstory.data.dto.UnifiedBookContent
import com.timor.kidsstory.data.local.database.dao.HybridBooksDao
import com.timor.kidsstory.data.local.database.entity.BookSource
import com.timor.kidsstory.data.local.database.entity.HybridBookEntity
import com.timor.kidsstory.data.mapper.BookMapper
import com.timor.kidsstory.domain.manager.content.HybridContentManager
import com.timor.kidsstory.domain.model.Book
import com.timor.kidsstory.domain.model.DownloadProgress
import com.timor.kidsstory.domain.model.DownloadStatus
import com.timor.kidsstory.domain.repository.BookRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 하이브리드 BookRepository 구현체
 * - Room DB 기반 통합 책 관리
 * - 내장 assets와 다운로드 책 통합 제공
 * - 실시간 상태 관찰 지원
 */
@Singleton
class HybridBookRepositoryImpl @Inject constructor(
    private val hybridContentManager: HybridContentManager,
    private val hybridBooksDao: HybridBooksDao
) : BookRepository {

    /**
     * 지정된 언어의 모든 책 가져오기 (DB 기반)
     */
    override suspend fun getBooks(languageCode: String): Result<List<Book>> {
        return try {
            Log.d(TAG, "Getting books for language: $languageCode")

            // 1. Room DB에서 책 목록 조회
            val booksResult = hybridContentManager.getAllBooksFromDb(languageCode)
            if (booksResult.isFailure) {
                return Result.failure(booksResult.exceptionOrNull()!!)
            }

            val hybridBookEntities = booksResult.getOrThrow()
            
            // 🆕 2. 메타데이터 로드 (unlockStep 정보 위해)
            val metadataResult = hybridContentManager.loadMetadata()
            val metadata = metadataResult.getOrNull()

            // 3. HybridBookEntity → Book 변환
            val books = hybridBookEntities.mapNotNull { entity ->
                try {
                    // 해당 책의 메타데이터 찾기
                    val bookMetadata = metadata?.books?.find { it.id == entity.id }
                    
                    // 콘텐츠 로드
                    val contentResult = hybridContentManager.loadBookContent(entity.id, entity.language)
                    val content = contentResult.getOrNull()

                    // Book 객체로 변환 (🆕 메타데이터 마지막에 전달)
                    val book = BookMapper.fromHybridEntity(
                        entity = entity, 
                        content = content, 
                        hybridContentManager = hybridContentManager,
                        metadata = bookMetadata  // 🆕 메타데이터 마지막
                    )
                    
                    book.copy(
                        isDownloaded = true,
                        downloadProgress = DownloadProgress(DownloadStatus.DOWNLOADED)
                    )
                } catch (e: Exception) {
                    Log.w(TAG, "Failed to load book ${entity.id}", e)
                    null
                }
            }

            Log.d(TAG, "Successfully loaded ${books.size} books from hybrid DB")
            Result.success(books)

        } catch (e: Exception) {
            Log.e(TAG, "Error getting books", e)
            Result.failure(e)
        }
    }

    /**
     * 특정 책의 상세 정보 가져오기
     */
    override suspend fun getBookById(storyId: String, languageCode: String): Result<Book?> {
        return try {
            Log.d(TAG, "Getting book detail for: $storyId")

            // 🔧 통일된 storyId 파싱 사용
            val (bookId, extractedLanguageCode) = parseStoryId(storyId)
            val normalizedLanguageCode = normalizeLanguageCode(extractedLanguageCode)
            
            // 🆕 메타데이터 로드
            val metadataResult = hybridContentManager.loadMetadata()
            val metadata = metadataResult.getOrNull()
            val bookMetadata = metadata?.books?.find { it.id == bookId }

            // 1. Room DB에서 책 정보 조회
            val bookEntity = hybridBooksDao.getBook(bookId, normalizedLanguageCode)
                ?: return Result.success(null)

            // 2. 통일된 콘텐츠 로딩 로직 사용
            val contentResult = loadBookContentForEntity(bookEntity)
            if (contentResult.isFailure) {
                Log.e(TAG, "Failed to load book content for ${storyId}: ${contentResult.exceptionOrNull()?.message}")
            }
            val content = contentResult.getOrNull()

            // 3. Book 객체로 변환 (🆕 메타데이터 마지막에 전달)
            val book = BookMapper.fromHybridEntity(
                entity = bookEntity, 
                content = content, 
                hybridContentManager = hybridContentManager,
                metadata = bookMetadata // 🆕 메타데이터 마지막
            )

            Result.success(book)

        } catch (e: Exception) {
            Log.e(TAG, "Error getting book detail", e)
            Result.failure(e)
        }
    }

    /**
     * 내장 Asset 책 목록 로드 (DB에서 BUNDLED 출처만)
     */
    override suspend fun getLocalBooks(languageCode: String): Result<List<Book>> {
        return try {
            val normalizedLanguageCode = normalizeLanguageCode(languageCode)
            
            // 🆕 메타데이터 로드
            val metadataResult = hybridContentManager.loadMetadata()
            val metadata = metadataResult.getOrNull()
            
            // DB에서 내장 책만 조회
            val bundledEntities = hybridBooksDao.getBooksBySource(normalizedLanguageCode, BookSource.BUNDLED)
            
            val books = bundledEntities.mapNotNull { entity ->
                try {
                    // 해당 책의 메타데이터 찾기
                    val bookMetadata = metadata?.books?.find { it.id == entity.id }
                    
                    val contentResult = hybridContentManager.loadBookContent(entity.id, entity.language)
                    val content = contentResult.getOrNull()
                    BookMapper.fromHybridEntity(
                        entity = entity,
                        content = content,
                        hybridContentManager = hybridContentManager,
                        metadata = bookMetadata // 🆕 메타데이터 전달
                    )
                } catch (e: Exception) {
                    Log.w(TAG, "Failed to load bundled book ${entity.id}", e)
                    null
                }
            }

            Log.d(TAG, "Loaded ${books.size} bundled books")
            Result.success(books)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting local books", e)
            Result.failure(e)
        }
    }

    /**
     * 다운로드된 책 목록 로드 (DB에서 DOWNLOADED 출처만)
     */
    override suspend fun getDownloadedBooks(languageCode: String): Result<List<Book>> {
        return try {
            val normalizedLanguageCode = normalizeLanguageCode(languageCode)
            
            // 🆕 메타데이터 로드
            val metadataResult = hybridContentManager.loadMetadata()
            val metadata = metadataResult.getOrNull()
            
            // DB에서 다운로드 책만 조회
            val downloadedEntities = hybridBooksDao.getBooksBySource(normalizedLanguageCode, BookSource.DOWNLOADED)
            
            val books = downloadedEntities.mapNotNull { entity ->
                try {
                    // 해당 책의 메타데이터 찾기
                    val bookMetadata = metadata?.books?.find { it.id == entity.id }
                    
                    val contentResult = hybridContentManager.loadBookContent(entity.id, entity.language)
                    val content = contentResult.getOrNull()
                    BookMapper.fromHybridEntity(
                        entity = entity,
                        content = content,
                        hybridContentManager = hybridContentManager,
                        metadata = bookMetadata // 🆕 메타데이터 전달
                    )
                } catch (e: Exception) {
                    Log.w(TAG, "Failed to load downloaded book ${entity.id}", e)
                    null
                }
            }

            Log.d(TAG, "Loaded ${books.size} downloaded books")
            Result.success(books)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting downloaded books", e)
            Result.failure(e)
        }
    }

    /**
     * 책 다운로드 여부 확인 (DB 기반)
     */
    override suspend fun isBookDownloaded(storyId: String): Boolean {
        return try {
            val parts = storyId.split("_")
            if (parts.size != 2) return false
            
            val bookId = parts[0].toIntOrNull() ?: return false
            val languageCode = normalizeLanguageCode(parts[1])
            
            // DB에서 다운로드 여부 확인 (DOWNLOADED 출처 또는 존재 여부)
            hybridBooksDao.isBookExists(bookId, languageCode)
        } catch (e: Exception) {
            Log.e(TAG, "Error checking download status", e)
            false
        }
    }

    /**
     * 책 다운로드 (향후 구현)
     */
    override suspend fun downloadBook(storyId: String): Result<Unit> {
        // TODO: BookDownloader와 연동하여 구현
        return Result.success(Unit)
    }

    /**
     * 다운로드 진행률 관찰 (향후 구현)
     */
    override fun observeDownloadProgress(storyId: String): Flow<Float> {
        // TODO: 실제 다운로드 진행률 구현
        return flowOf(0f)
    }

    /**
     * 언어별 책 목록 실시간 관찰
     */
    fun observeBooksByLanguage(languageCode: String): Flow<List<Book>> {
        val normalizedLanguageCode = normalizeLanguageCode(languageCode)
        
        return hybridBooksDao.observeBooksByLanguage(normalizedLanguageCode).map { entities ->
            // 🆕 메타데이터 로드 (비동기 환경에서 runBlocking 사용)
            val metadata = try {
                kotlinx.coroutines.runBlocking { hybridContentManager.loadMetadata().getOrNull() }
            } catch (e: Exception) {
                Log.w(TAG, "Failed to load metadata in observer", e)
                null
            }
            
            entities.mapNotNull { entity ->
                try {
                    // 해당 책의 메타데이터 찾기
                    val bookMetadata = metadata?.books?.find { it.id == entity.id }
                    
                    val contentResult = hybridContentManager.loadBookContent(entity.id, entity.language)
                    val content = contentResult.getOrNull()
                    BookMapper.fromHybridEntity(
                        entity = entity,
                        content = content,
                        hybridContentManager = hybridContentManager,
                        metadata = bookMetadata // 🆕 메타데이터 전달
                    )
                } catch (e: Exception) {
                    Log.w(TAG, "Failed to load book ${entity.id} in observer", e)
                    null
                }
            }
        }
    }

    /**
     * 다운로드된 책 목록 실시간 관찰
     */
    fun observeDownloadedBooks(languageCode: String): Flow<List<Book>> {
        val normalizedLanguageCode = normalizeLanguageCode(languageCode)
        
        return hybridBooksDao.observeDownloadedBooks(normalizedLanguageCode).map { entities ->
            // 🆕 메타데이터 로드 (비동기 환경에서 runBlocking 사용)
            val metadata = try {
                kotlinx.coroutines.runBlocking { hybridContentManager.loadMetadata().getOrNull() }
            } catch (e: Exception) {
                Log.w(TAG, "Failed to load metadata in downloaded books observer", e)
                null
            }
            
            entities.mapNotNull { entity ->
                try {
                    // 해당 책의 메타데이터 찾기
                    val bookMetadata = metadata?.books?.find { it.id == entity.id }
                    
                    val contentResult = hybridContentManager.loadBookContent(entity.id, entity.language)
                    val content = contentResult.getOrNull()
                    BookMapper.fromHybridEntity(
                        entity = entity,
                        content = content,
                        hybridContentManager = hybridContentManager,
                        metadata = bookMetadata // 🆕 메타데이터 전달
                    )
                } catch (e: Exception) {
                    Log.w(TAG, "Failed to load downloaded book ${entity.id} in observer", e)
                    null
                }
            }
        }
    }

    /**
     * 책 통계 조회
     */
    suspend fun getBookStatistics(languageCode: String): Result<com.timor.kidsstory.data.local.database.dao.BookStatistics> {
        return try {
            val normalizedLanguageCode = normalizeLanguageCode(languageCode)
            val statistics = hybridBooksDao.getBookStatistics(normalizedLanguageCode)
            Result.success(statistics)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting book statistics", e)
            Result.failure(e)
        }
    }

    override suspend fun loadExternalBookContent(contentPath: String): Result<PageContentResponse> {
        // 레거시 외부 콘텐츠 지원 안함
        return Result.failure(UnsupportedOperationException("Legacy external content not supported in hybrid system"))
    }

    override suspend fun getStoryInfo(storyId: String, languageCode: String): Result<com.timor.kidsstory.domain.model.StoryInfo> {
        return try {
            Log.d(TAG, "🔍 Getting story info for: $storyId, language: $languageCode")
            
            // 🔧 통일된 storyId 파싱 사용
            val (bookId, extractedLanguageCode) = parseStoryId(storyId)
            val normalizedLanguageCode = normalizeLanguageCode(extractedLanguageCode)
            
            Log.d(TAG, "🔍 Parsed - bookId: $bookId, language: $normalizedLanguageCode")

            // 🔍 디버깅: DB에 어떤 책들이 있는지 확인
            val allBooksInLanguage = hybridBooksDao.getAvailableBooksByLanguage(normalizedLanguageCode)
            Log.d(TAG, "📚 Available books in $normalizedLanguageCode: ${allBooksInLanguage.map { "${it.id} (${it.source})" }}")

            val entity = hybridBooksDao.getBook(bookId, normalizedLanguageCode)
            if (entity == null) {
                Log.e(TAG, "❌ Book not found in DB: bookId=$bookId, language=$normalizedLanguageCode")
                return Result.failure(NoSuchElementException("Book not found in DB"))
            }

            Log.d(TAG, "✅ Found book entity: ${entity.id}, source: ${entity.source}, available: ${entity.isAvailable}")

            // 🔧 통일된 콘텐츠 로딩 로직 사용
            val contentResult = loadBookContentForEntity(entity)
            
            if (contentResult.isFailure) {
                Log.e(TAG, "❌ Failed to load book content for story info", contentResult.exceptionOrNull())
                return Result.failure(contentResult.exceptionOrNull()!!)
            }
            
            val unifiedBookContent = contentResult.getOrNull()
            if (unifiedBookContent == null) {
                Log.e(TAG, "❌ Content for storyId $storyId is null")
                return Result.failure(NoSuchElementException("Content for storyId $storyId not found."))
            }

            Log.d(TAG, "✅ Loaded book content successfully for story info")

            val preQuestions = unifiedBookContent.comprehensionChecks?.preQuestions?.map { it.question } ?: emptyList()
            val postQuestions = unifiedBookContent.comprehensionChecks?.postQuestions?.map { it.question } ?: emptyList()

            val storyInfo = com.timor.kidsstory.domain.model.StoryInfo(
                storyId = storyId,
                summary = unifiedBookContent.summary,
                preQuestions = preQuestions,
                postQuestions = postQuestions
            )
            
            Log.d(TAG, "✅ Story info created successfully for $storyId")
            Result.success(storyInfo)
        } catch (e: Exception) {
            Log.d(TAG, "❌ Error getting story info for $storyId", e)
            Result.failure(e)
        }
    }

    /**
     * storyId 파싱: "103_ko" -> Pair(103, "ko")
     */
    private fun parseStoryId(storyId: String): Pair<Int, String> {
        val parts = storyId.split("_")
        if (parts.size != 2) {
            throw IllegalArgumentException("Invalid storyId format: $storyId")
        }
        val bookId = parts[0].toIntOrNull()
            ?: throw IllegalArgumentException("Invalid bookId in storyId: $storyId")
        return Pair(bookId, parts[1])
    }
    
    /**
     * 통일된 콘텐츠 로딩 로직
     */
    private suspend fun loadBookContentForEntity(entity: HybridBookEntity): Result<UnifiedBookContent> {
        return try {
            when (entity.source) {
                BookSource.BUNDLED -> {
                    Log.d(TAG, "📚 Loading BUNDLED book content: ${entity.id}")
                    hybridContentManager.loadBookContent(entity.id, entity.language)
                }
                BookSource.DOWNLOADED -> {
                    Log.d(TAG, "📱 Loading DOWNLOADED book content: ${entity.id}")
                    Log.d(TAG, "📁 Content path: ${entity.contentPath}")
                    
                    // 파일 존재 여부 확인
                    if (!File(entity.contentPath).exists()) {
                        Log.e(TAG, "❌ Content file does not exist: ${entity.contentPath}")
                        // 누락된 파일이 있는 책을 비활성화
                        try {
                            hybridBooksDao.updateBookAvailability(entity.id, entity.language, false)
                            Log.w(TAG, "⚠️ Marked book as unavailable: ${entity.id}/${entity.language}")
                        } catch (e: Exception) {
                            Log.e(TAG, "❌ Failed to update book availability", e)
                        }
                        return Result.failure(IllegalStateException("Downloaded content file not found: ${entity.contentPath}"))
                    }
                    
                    val contentBasePath = File(entity.contentPath).parent
                    Log.d(TAG, "📁 Content base path: $contentBasePath")
                    hybridContentManager.loadBookContent(entity.id, entity.language, contentBasePath)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error loading book content for entity ${entity.id}/${entity.language}", e)
            Result.failure(e)
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
    
    companion object {
        private const val TAG = "HybridBookRepository"
    }
}
