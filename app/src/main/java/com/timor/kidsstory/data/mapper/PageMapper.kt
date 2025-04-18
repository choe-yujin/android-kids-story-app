package com.timor.kidsstory.data.mapper

import com.timor.kidsstory.data.dto.PageDto
import com.timor.kidsstory.domain.model.Page

/**
 * PageDto를 도메인 모델로 변환하는 매퍼 클래스
 * - 데이터 계층(DTO)과 도메인 계층(Page) 간의 변환 담당
 */
object PageMapper {

    /**
     * PageDto를 도메인 Page 객체로 변환
     * - 페이지 번호, 텍스트를 그대로 유지
     * - 이미지 경로는 Android Assets 경로로 변환하여 제공
     *
     * @param pageDto 변환할 페이지 DTO
     * @param storyBaseId 책의 기본 ID (이미지 경로 생성에 사용)
     * @return 도메인 Page 객체
     */
    fun mapToDomain(pageDto: PageDto, storyBaseId: String): Page {
        // 이미지 파일명 생성 (예: "book_801_page_1.jpg")
        val imageFileName = "book_${storyBaseId}_page_${pageDto.pageNumber}.jpg"
        // Android Assets의 전체 이미지 경로 구성
        val imageUrl = "file:///android_asset/images/$storyBaseId/$imageFileName"

        return Page(
            pageNumber = pageDto.pageNumber,
            imageUrl = imageUrl,
            texts = pageDto.texts
        )
    }

    /**
     * 페이지 리스트에 totalPages 정보 추가
     * - 각 페이지 객체에 전체 페이지 수 정보를 포함시킴
     *
     * @param pages 기존 페이지 목록
     * @return totalPages 정보가 추가된 페이지 목록
     */
    fun addTotalPagesInfo(pages: List<Page>): List<Page> {
        val totalPages = pages.size
        return pages.map { page ->
            page.copy(totalPages = totalPages)
        }
    }
}