package com.timor.kidsstory.domain.usecase.book

import android.util.Log
import com.timor.kidsstory.data.local.database.dao.HybridBooksDao
import com.timor.kidsstory.domain.manager.content.HybridContentManager
import com.timor.kidsstory.domain.model.Book
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

/**
 * 책 삭제 UseCase (하이브리드 시스템)
 * - HybridBooksDao에서 책 제거
 * - 내부 저장소의 콘텐츠 및 이미지 파일 삭제
 */
class DeleteBookUseCase @Inject constructor(
    private val hybridBooksDao: HybridBooksDao,
    private val hybridContentManager: HybridContentManager
) {
    /**
     * 다운로드된 책 삭제
     */
    suspend operator fun invoke(
        books: List<Book>,
        languageCode: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            val normalizedLang = normalizeLanguageCode(languageCode)
            
            books.forEach { book ->
                Log.d("DeleteBookUseCase", "🗑️ Deleting book: ${book.storyId}")
                
                // storyId에서 bookId 추출 ("801_ko" -> 801)
                val parts = book.storyId.split("_")
                if (parts.size != 2) {
                    Log.e("DeleteBookUseCase", "❌ Invalid storyId format: ${book.storyId}")
                    return@withContext Result.failure(
                        IllegalArgumentException("Invalid storyId format: ${book.storyId}")
                    )
                }
                
                val bookId = parts[0].toIntOrNull()
                if (bookId == null) {
                    Log.e("DeleteBookUseCase", "❌ Invalid bookId in storyId: ${book.storyId}")
                    return@withContext Result.failure(
                        IllegalArgumentException("Invalid bookId in storyId: ${book.storyId}")
                    )
                }
                
                // 1. HybridBooksDao에서 삭제
                val deletedCount = hybridBooksDao.deleteBook(bookId, normalizedLang)
                Log.d("DeleteBookUseCase", "✅ Deleted from DB: $deletedCount rows")
                
                // 2. 내부 저장소 파일 삭제
                deleteBookFiles(bookId, normalizedLang)
            }
            
            Log.d("DeleteBookUseCase", "✅ All books deleted successfully: ${books.size}")
            Result.success(Unit)
            
        } catch (e: Exception) {
            Log.e("DeleteBookUseCase", "❌ Error deleting books", e)
            Result.failure(e)
        }
    }
    
    /**
     * 내부 저장소의 책 파일 삭제
     */
    private suspend fun deleteBookFiles(bookId: Int, languageCode: String) {
        withContext(Dispatchers.IO) {
            try {
                // 콘텐츠 파일 삭제
                val contentFileName = "${bookId}_$languageCode.json"
                val contentFile = File(
                    hybridContentManager.getImagePath(bookId, "").substringBeforeLast("/images/") + "/content",
                    contentFileName
                )

                if (contentFile.exists()) {
                    contentFile.delete()
                    Log.d("DeleteBookUseCase", "🗄️ Content file deleted: ${contentFile.absolutePath}")
                }

                // 커버 이미지 파일 삭제
                val coverFileName = "cover_${bookId}_${languageCode}.jpg"
                val coverImageFile = File(hybridContentManager.getImagePath(bookId, coverFileName))
                if (coverImageFile.exists()) {
                    coverImageFile.delete()
                    Log.d("DeleteBookUseCase", "🖼️ Cover image file deleted: ${coverImageFile.absolutePath}")
                }

                // 이미지 파일들 삭제 (다른 언어 버전이 없는 경우에만)
                val remainingVersions = hybridBooksDao.getBooksByStoryId(bookId)
                Log.d("DeleteBookUseCase", "🔍 Remaining versions for book $bookId: ${remainingVersions.size}")

                if (remainingVersions.isEmpty()) {
                    // 🔧 FIXED: 특정 책의 이미지 디렉토리만 삭제
                    val bookImagesDir = File(
                        hybridContentManager.getImagePath(bookId, "book_page_0.jpg").substringBeforeLast("/")
                    )
                    
                    if (bookImagesDir.exists() && bookImagesDir.name == bookId.toString()) {
                        bookImagesDir.deleteRecursively()
                        Log.d("DeleteBookUseCase", "🖼️ Book images directory deleted: ${bookImagesDir.absolutePath}")
                    } else {
                        Log.w("DeleteBookUseCase", "⚠️ Suspicious path, skipping deletion: ${bookImagesDir.absolutePath}")
                    }
                } else {
                    Log.d("DeleteBookUseCase", "⚠️ Keeping images for book $bookId (${remainingVersions.size} versions remain)")
                }

            } catch (e: Exception) {
                Log.e("DeleteBookUseCase", "❌ Error deleting book files for $bookId", e)
                throw e
            }
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
