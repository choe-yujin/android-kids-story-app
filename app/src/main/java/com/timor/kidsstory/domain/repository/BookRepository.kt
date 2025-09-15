package com.timor.kidsstory.domain.repository

import com.timor.kidsstory.domain.model.Book
import com.timor.kidsstory.data.dto.PageContentResponse
import kotlinx.coroutines.flow.Flow

/**
 * 책 관련 데이터 접근을 위한 Repository 인터페이스
 * - 로컬 Asset 책과 다운로드된 책을 통합 관리
 * - Domain Layer의 인터페이스
 */
interface BookRepository {
    
    /**
     * 특정 언어의 모든 책 조회 (기존 GetBooksUseCase에서 사용)
     */
    suspend fun getBooks(languageCode: String): Result<List<Book>>
    
    /**
     * 특정 책 상세 정보 조회 (기존 GetBookDetailUseCase에서 사용)
     */
    suspend fun getBookById(storyId: String, languageCode: String): Result<Book?>
    
    /**
     * 책 다운로드 상태 확인
     */
    suspend fun isBookDownloaded(storyId: String): Boolean
    
    /**
     * 책 다운로드 시작
     */
    suspend fun downloadBook(storyId: String): Result<Unit>
    
    /**
     * 다운로드 진행률 관찰
     */
    fun observeDownloadProgress(storyId: String): Flow<Float>
    
    /**
     * 로컬 Asset 책만 가져오기 (GetAllBooksUseCase에서 사용)
     */
    suspend fun getLocalBooks(languageCode: String): Result<List<Book>>
    
    /**
     * 다운로드된 책만 가져오기 (GetAllBooksUseCase에서 사용)
     */
    suspend fun getDownloadedBooks(languageCode: String): Result<List<Book>>
    
    /**
     * 외부 파일에서 책 콘텐츠 로드 (BookshelfViewModel의 loadExternalBookContent 로직 이동)
     */
    suspend fun loadExternalBookContent(contentPath: String): Result<PageContentResponse>
}
