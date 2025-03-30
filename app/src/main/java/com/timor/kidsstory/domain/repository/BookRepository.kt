package com.timor.kidsstory.domain.repository

import com.timor.kidsstory.domain.model.Book
import com.timor.kidsstory.domain.model.Page

/**
 * 책 데이터 접근을 위한 레포지토리 인터페이스
 * - Data 레이어를 추상화하여 비즈니스 로직에 데이터 제공
 */
interface BookRepository {

    /**
     * 특정 언어로 된 모든 책 목록을 가져옴
     *
     * @param language 언어 코드 (예: "ko-kr", "en-ph", "tetum")
     * @return 책 목록 또는 오류
     */
    suspend fun getBooks(language: String): Result<List<Book>>

    /**
     * ID로 특정 책의 정보를 가져옴
     *
     * @param storyId 책 ID
     * @param language 언어 코드
     * @return 책 정보 또는 오류
     */
    suspend fun getBookById(storyId: String, language: String): Result<Book?>

    /**
     * 특정 책의 모든 페이지 정보를 가져옴
     *
     * @param storyId 책 ID
     * @param language 언어 코드
     * @return 페이지 목록 또는 오류
     */
    suspend fun getBookPages(storyId: String, language: String): Result<List<Page>>
}