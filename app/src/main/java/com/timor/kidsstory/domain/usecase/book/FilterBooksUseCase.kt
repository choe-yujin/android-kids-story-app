package com.timor.kidsstory.domain.usecase.book

import android.util.Log
import com.timor.kidsstory.domain.model.Book
import com.timor.kidsstory.presentation.bookshelf.model.FilterBarCategory
import com.timor.kidsstory.presentation.bookshelf.model.FilterBookCategory
import com.timor.kidsstory.presentation.bookshelf.model.FilterLevel
import javax.inject.Inject

/**
 * 책 목록을 필터링하는 UseCase
 * 
 * 기존 BookshelfViewModel의 applyFilters() 로직을 Domain Layer로 이동
 * - 단계(레벨) 필터링
 * - 카테고리 필터링
 * - 전체 보기
 */
class FilterBooksUseCase @Inject constructor() {
    
    /**
     * 주어진 필터 조건에 따라 책 목록을 필터링
     * 
     * @param books 필터링할 원본 책 목록
     * @param selectedFilter 선택된 필터 타입
     * @param selectedStage 선택된 단계 (STAGE 필터일 때)
     * @param selectedCategory 선택된 카테고리 (CATEGORY 필터일 때)
     * @return 필터링된 책 목록
     */
    suspend operator fun invoke(
        books: List<Book>,
        selectedFilter: FilterBarCategory,
        selectedStage: FilterLevel? = null,
        selectedCategory: FilterBookCategory? = null
    ): List<Book> {
        return try {
            Log.d("FilterBooksUseCase", "Filtering ${books.size} books with filter: $selectedFilter")
            
            val filteredBooks = when (selectedFilter) {
                FilterBarCategory.STAGE -> {
                    filterByStage(books, selectedStage)
                }
                FilterBarCategory.CATEGORY -> {
                    filterByCategory(books, selectedCategory)
                }
                FilterBarCategory.All -> {
                    books // 모든 책 반환
                }
            }
            
            Log.d("FilterBooksUseCase", 
                "Filtered result: ${filteredBooks.size}/${books.size} books")
            
            filteredBooks
            
        } catch (e: Exception) {
            Log.e("FilterBooksUseCase", "Error filtering books", e)
            books // 오류 시 원본 목록 반환
        }
    }
    
    /**
     * 단계(레벨)로 책 필터링
     */
    private fun filterByStage(books: List<Book>, selectedStage: FilterLevel?): List<Book> {
        return if (selectedStage != null) {
            val targetLevel = selectedStage.level
            books.filter { book -> book.level == targetLevel }
        } else {
            books
        }
    }
    
    /**
     * 카테고리로 책 필터링
     */
    private fun filterByCategory(books: List<Book>, selectedCategory: FilterBookCategory?): List<Book> {
        return if (selectedCategory != null) {
            books.filter { book ->
                selectedCategory.matches(book.category)
            }
        } else {
            books
        }
    }
    
    /**
     * 편의 메서드: 특정 레벨의 책만 가져오기
     */
    suspend fun getBooksByLevel(books: List<Book>, level: Int): List<Book> {
        return books.filter { it.level == level }
    }
    
    /**
     * 편의 메서드: 특정 카테고리 키워드를 포함하는 책 가져오기
     */
    suspend fun getBooksByCategory(books: List<Book>, categoryKeyword: String): List<Book> {
        return books.filter { book ->
            book.category.contains(categoryKeyword, ignoreCase = true)
        }
    }
}