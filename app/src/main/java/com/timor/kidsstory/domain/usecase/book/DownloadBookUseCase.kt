package com.timor.kidsstory.domain.usecase.book

import com.timor.kidsstory.data.remote.BookDownloader
import com.timor.kidsstory.domain.model.Book
import javax.inject.Inject

/**
 * 통합 구조 기반 책 다운로드 유스케이스
 * - bookId와 languageCode만으로 다운로드
 * - RemoteBook 의존성 제거
 */
class DownloadBookUseCase @Inject constructor(
    private val bookDownloader: BookDownloader
) {
    /**
     * 책 다운로드
     * 
     * @param bookId 책 ID (예: 801)
     * @param languageCode 언어 코드 (예: "ko", "en", "tet")
     * @return 다운로드된 Book 객체
     */
    suspend operator fun invoke(bookId: Int, languageCode: String): Result<Book> {
        return bookDownloader.downloadBook(bookId, languageCode)
    }
    
    /**
     * 다운로드된 책 삭제
     * 
     * @param storyId 스토리 ID (예: "801_ko")
     */
    suspend fun deleteBook(storyId: String): Result<Unit> {
        return bookDownloader.deleteDownloadedBook(storyId)
    }
}
