package com.timor.kidsstory.data.remote.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.timor.kidsstory.data.local.database.dao.DownloadedBooksDao
import com.timor.kidsstory.data.remote.BookDownloader
import com.timor.kidsstory.data.remote.model.RemoteBook
import com.timor.kidsstory.data.remote.network.BookNetworkService
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.serialization.json.Json

@HiltWorker
class DownloadWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val params: WorkerParameters,
    private val bookDownloader: BookDownloader,
    private val downloadedBooksDao: DownloadedBooksDao,
    private val networkService: BookNetworkService
) : CoroutineWorker(context, params) {

    companion object {
        private const val TAG = "DownloadWorker"
        const val KEY_BOOK_ID = "book_id"
        const val KEY_LANGUAGE = "language"
        const val KEY_METADATA = "metadata"
    }

    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun doWork(): Result {
        try {
            val bookId = inputData.getInt(KEY_BOOK_ID, -1)
            val language = inputData.getString(KEY_LANGUAGE) ?: return Result.failure()
            val metadataJson = inputData.getString(KEY_METADATA) ?: return Result.failure()

            if (bookId == -1) {
                Log.e(TAG, "Invalid book ID")
                return Result.failure()
            }

            Log.d(TAG, "Starting download for book ID: $bookId, language: $language")

            // 카테고리 정보가 포함된 RemoteBook 객체 디코딩
            val remoteBook = json.decodeFromString<RemoteBook>(metadataJson)

            // bookDownloader.downloadBook 메서드에 remoteBook을 그대로 전달
            // (BookDownloader 클래스는 RemoteBook에서 category 필드를 처리하도록 수정 필요)
            val result = bookDownloader.downloadBook(remoteBook, language)

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