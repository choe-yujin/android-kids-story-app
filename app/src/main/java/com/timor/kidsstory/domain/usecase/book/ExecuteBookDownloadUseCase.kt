package com.timor.kidsstory.domain.usecase.book

import android.util.Log
import com.timor.kidsstory.data.remote.BookDownloader
import com.timor.kidsstory.domain.model.Book
import javax.inject.Inject

/**
 * 책 다운로드/업데이트 실행 UseCase
 */
class ExecuteBookDownloadUseCase @Inject constructor(
    private val bookDownloader: BookDownloader
) {
    /**
     * 책 다운로드 또는 업데이트
     */
    suspend operator fun invoke(
        books: List<Book>,
        languageCode: String
    ): Result<Unit> {
        return try {
            val normalizedLang = normalizeLanguageCode(languageCode)
            
            books.forEach { book ->
                val bookId = book.storyId.split("_").first().toInt()
                
                Log.d("ExecuteBookDownloadUseCase", "📥 Downloading book: $bookId ($normalizedLang)")
                
                val result = bookDownloader.downloadBook(bookId, normalizedLang)
                
                if (result.isFailure) {
                    Log.e("ExecuteBookDownloadUseCase", "❌ Failed to download book $bookId", result.exceptionOrNull())
                    return result.map { }
                }
            }
            
            Log.d("ExecuteBookDownloadUseCase", "✅ All books downloaded successfully: ${books.size}")
            Result.success(Unit)
            
        } catch (e: Exception) {
            Log.e("ExecuteBookDownloadUseCase", "❌ Error executing book downloads", e)
            Result.failure(e)
        }
    }
    
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
