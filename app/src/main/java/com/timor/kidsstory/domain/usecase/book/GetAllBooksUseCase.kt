package com.timor.kidsstory.domain.usecase.book

import android.util.Log
import com.timor.kidsstory.domain.model.Book
import com.timor.kidsstory.domain.model.DownloadProgress
import com.timor.kidsstory.domain.model.DownloadStatus
import com.timor.kidsstory.domain.repository.BookRepository
import javax.inject.Inject

/**
 * 모든 책(하이브리드 DB)을 가져오는 UseCase
 * 
 * - HybridBooksDao에 있는 책만 표시
 * - 삭제된 책은 DB에서 제거되므로 자동으로 숨겨짐
 * - 다운로드 상태 설정
 */
class GetAllBooksUseCase @Inject constructor(
    private val bookRepository: BookRepository
) {
    /**
     * 지정된 언어의 모든 책을 가져옴 (HybridBooksDao 기반)
     * 
     * @param languageCode 언어 코드 (예: "ko-kr", "en-ph", "tetum")
     * @return HybridBooksDao에 있는 책 목록 (내장 + 다운로드)
     */
    suspend operator fun invoke(languageCode: String): Result<List<Book>> {
        return try {
            Log.d("GetAllBooksUseCase", "📚 Loading all books from HybridDB for language: $languageCode")
            
            // 🆕 HybridBooksDao에 있는 모든 책 가져오기 (내장 + 다운로드)
            // - 삭제된 책은 DB에서 제거되므로 자동으로 포함 안 됨
            val booksResult = bookRepository.getBooks(languageCode)
            val books = booksResult.getOrElse {
                Log.e("GetAllBooksUseCase", "Failed to load books from HybridDB", it)
                return Result.failure(it)
            }
            
            // 다운로드 상태 설정 (HybridDB에 있는 책은 모두 다운로드됨)
            val booksWithDownloadStatus = books.map { book ->
                book.copy(
                    isDownloaded = true,
                    downloadProgress = DownloadProgress(DownloadStatus.DOWNLOADED)
                )
            }
            
            Log.d("GetAllBooksUseCase", "✅ Successfully loaded ${booksWithDownloadStatus.size} books from HybridDB")
            Result.success(booksWithDownloadStatus)
            
        } catch (e: Exception) {
            Log.e("GetAllBooksUseCase", "❌ Error loading books from HybridDB", e)
            Result.failure(e)
        }
    }
}