package com.timor.kidsstory.data.mapper

import com.timor.kidsstory.data.dto.PageDto
import com.timor.kidsstory.domain.model.Page

object PageMapper {

    /**
     * PageDto 리스트를 도메인 Page 리스트로 변환
     *
     * @param pageDtos 변환할 DTO 리스트
     * @param imagePathProvider 각 페이지에 대한 이미지 경로를 제공하는 함수
     * @param totalPages 전체 페이지 수
     * @return 도메인 Page 객체 리스트
     */
    fun mapToDomain(pageDto: PageDto, storyBaseId: String): Page {
        // 이미지 파일명 생성
        val imageFileName = "book_${storyBaseId}_page_${pageDto.pageNumber}.jpg"
        val imageUrl = "file:///android_asset/images/$storyBaseId/$imageFileName"

        return Page(
            pageNumber = pageDto.pageNumber,
            imageUrl = imageUrl,
            texts = pageDto.texts
        )
    }

    // 페이지 리스트에 totalPages 정보 추가
    fun addTotalPagesInfo(pages: List<Page>): List<Page> {
        val totalPages = pages.size
        return pages.map { page ->
            page.copy(totalPages = totalPages)
        }
    }
}