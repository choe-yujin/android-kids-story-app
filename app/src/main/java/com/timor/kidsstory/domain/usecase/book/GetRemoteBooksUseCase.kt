package com.timor.kidsstory.domain.usecase.book

import android.util.Log
import com.timor.kidsstory.data.remote.model.RemoteBook
import com.timor.kidsstory.data.remote.network.BookNetworkService
import javax.inject.Inject

/**
 * GitHub에서 사용 가능한 책 목록을 가져오는 유스케이스
 */
class GetRemoteBooksUseCase @Inject constructor(
    private val networkService: BookNetworkService
) {
    suspend operator fun invoke(languageCode: String): Result<List<RemoteBook>> {
        return try {
            val metadata = networkService.getMetadata()

            // 언어 접두사 추출
            val prefix = when {
                languageCode.startsWith("ko") -> "ko"
                languageCode.startsWith("tet") -> "tet"
                languageCode.startsWith("mn") -> "mn" // Added for Mongolian
                else -> "en" // 기본값
            }

            // 해당 언어가 있는 책만 필터링
            val filteredBooks = metadata.books.filter { book ->
                book.languages.any { it.startsWith(prefix) }
            }

            Result.success(filteredBooks)
        } catch (e: Exception) {
            Log.e("GetRemoteBooksUseCase", "GetRemoteBooksUseCase에서 오류 발생: ${e.message}", e)
            Result.failure(e)
        }
    }
}