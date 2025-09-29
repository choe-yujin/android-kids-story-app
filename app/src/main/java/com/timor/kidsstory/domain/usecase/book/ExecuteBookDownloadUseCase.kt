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
     * 
     * @param books 다운로드할 책 목록
     * @param languageCode 언어 코드
     * @param isUpdate 업데이트 모드 여부 (기존 버전 무시하고 새로 다운로드)
     */
    suspend operator fun invoke(
        books: List<Book>,
        languageCode: String,
        isUpdate: Boolean = false
    ): Result<Unit> {
        return try {
            val normalizedLang = normalizeLanguageCode(languageCode)
            
            books.forEach { book ->
                val bookId = book.storyId.split("_").first().toInt()
                
                if (isUpdate) {
                    Log.d("ExecuteBookDownloadUseCase", "🔄 Updating book: $bookId ($normalizedLang)")
                } else {
                    Log.d("ExecuteBookDownloadUseCase", "📥 Downloading book: $bookId ($normalizedLang)")
                }
                
                val result = bookDownloader.downloadBook(bookId, normalizedLang, forceUpdate = isUpdate)
                
                if (result.isFailure) {
                    val action = if (isUpdate) "update" else "download"
                    Log.e("ExecuteBookDownloadUseCase", "❌ Failed to $action book $bookId", result.exceptionOrNull())
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
