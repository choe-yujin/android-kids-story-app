package com.timor.kidsstory.data.remote.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.timor.kidsstory.data.local.database.dao.DownloadedBooksDao
import com.timor.kidsstory.data.remote.BookDownloader
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class DownloadWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val params: WorkerParameters,
    private val bookDownloader: BookDownloader
) : CoroutineWorker(context, params) {

    companion object {
        private const val TAG = "DownloadWorker"
        const val KEY_BOOK_ID = "book_id"
        const val KEY_LANGUAGE = "language"
    }

    override suspend fun doWork(): Result {
        try {
            val bookId = inputData.getInt(KEY_BOOK_ID, -1)
            val language = inputData.getString(KEY_LANGUAGE) ?: return Result.failure()

            if (bookId == -1) {
                Log.e(TAG, "Invalid book ID")
                return Result.failure()
            }

            Log.d(TAG, "Starting download for book ID: $bookId, language: $language")

            // 통합 구조 기반 다운로드
            val result = bookDownloader.downloadBook(bookId, language)

            return if (result.isSuccess) {
                Log.d(TAG, "Book downloaded successfully: $bookId")
                Result.success()
            } else {
                Log.e(TAG, "Failed to download book: $bookId", result.exceptionOrNull())
                Result.failure()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in download worker", e)
            return Result.failure()
        }
    }
}
