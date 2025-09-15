package com.timor.kidsstory.domain.usecase.book

import android.util.Log
import com.timor.kidsstory.domain.model.Book
import com.timor.kidsstory.domain.model.DownloadProgress
import com.timor.kidsstory.domain.model.DownloadStatus
import com.timor.kidsstory.domain.repository.BookRepository
import javax.inject.Inject

/**
 * 모든 책(로컬 + 다운로드)을 가져오는 UseCase
 * 
 * 기존 BookshelfViewModel의 loadStories() 로직을 Domain Layer로 이동
 * - 로컬 Asset 책과 다운로드된 책을 병합
 * - 중복 제거 로직 적용 
 * - 다운로드 상태 설정
 */
class GetAllBooksUseCase @Inject constructor(
    private val bookRepository: BookRepository
) {
    /**
     * 지정된 언어의 모든 책을 가져옴
     * 
     * @param languageCode 언어 코드 (예: "ko-kr", "en-ph", "tetum")
     * @return 병합된 책 목록
     */
    suspend operator fun invoke(languageCode: String): Result<List<Book>> {
        return try {
            Log.d("GetAllBooksUseCase", "Loading all books for language: $languageCode")
            
            // 1. 로컬 Asset 책 가져오기
            val localBooksResult = bookRepository.getLocalBooks(languageCode)
            val localBooks = localBooksResult.getOrElse {
                Log.e("GetAllBooksUseCase", "Failed to load local books", it)
                return Result.failure(it)
            }
            
            // 2. 로컬 책에 다운로드 상태 설정 (Asset 책은 항상 다운로드됨)
            val localBooksWithDownloadStatus = localBooks.map { localBook ->
                localBook.copy(
                    isDownloaded = true,
                    downloadProgress = DownloadProgress(DownloadStatus.DOWNLOADED)
                )
            }
            
            // 3. 다운로드된 책 가져오기
            val downloadedBooksResult = bookRepository.getDownloadedBooks(languageCode)
            val downloadedBooks = downloadedBooksResult.getOrElse { 
                Log.w("GetAllBooksUseCase", "No downloaded books or error loading them", it)
                emptyList() 
            }
            
            // 4. 중복 제거 (storyId 기준)
            val mergedBooks = mergeLocalAndDownloadedBooks(localBooksWithDownloadStatus, downloadedBooks)
            
            Log.d("GetAllBooksUseCase", "Successfully loaded ${mergedBooks.size} books")
            Result.success(mergedBooks)
            
        } catch (e: Exception) {
            Log.e("GetAllBooksUseCase", "Error loading all books", e)
            Result.failure(e)
        }
    }
    
    /**
     * 로컬 책과 다운로드된 책을 병합하고 중복 제거
     * 
     * 기존 BookshelfViewModel의 병합 로직을 UseCase로 이동
     * - 로컬 Asset 책이 우선순위를 가짐
     * - storyId의 앞부분(숫자)으로 중복 판단
     */
    private fun mergeLocalAndDownloadedBooks(
        localBooks: List<Book>, 
        downloadedBooks: List<Book>
    ): List<Book> {
        // 로컬 책의 storyId에서 숫자 부분만 추출 (예: "801_ko-kr" -> "801")
        val localStoryIds = localBooks.map { 
            it.storyId.split("_").first() 
        }.toSet()
        
        // 다운로드된 책 중에서 로컬에 없는 것들만 필터링
        val uniqueDownloadedBooks = downloadedBooks.filter { downloadedBook ->
            val downloadedStoryId = downloadedBook.storyId.split("_").first()
            !localStoryIds.contains(downloadedStoryId)
        }
        
        val mergedList = localBooks + uniqueDownloadedBooks
        
        Log.d("GetAllBooksUseCase", 
            "Merged books: ${localBooks.size} local + ${uniqueDownloadedBooks.size} downloaded = ${mergedList.size}")
        
        return mergedList
    }
}