package com.timor.kidsstory.domain.usecase.book

import com.timor.kidsstory.data.remote.BookDownloader
import com.timor.kidsstory.data.remote.model.RemoteBook
import com.timor.kidsstory.domain.model.Book
import javax.inject.Inject

/**
 * 책 다운로드를 처리하는 유스케이스
 */
class DownloadBookUseCase @Inject constructor(
    private val bookDownloader: BookDownloader
) {
    suspend operator fun invoke(remoteBook: RemoteBook, languageCode: String): Result<Book> {
        return bookDownloader.downloadBook(remoteBook, languageCode)
    }
}