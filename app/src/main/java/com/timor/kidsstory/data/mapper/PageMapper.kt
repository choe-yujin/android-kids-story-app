package com.timor.kidsstory.data.mapper

import android.util.Log
import com.timor.kidsstory.data.dto.PageDto
import com.timor.kidsstory.domain.model.Page

/**
 * PageDto를 도메인 모델로 변환하는 매퍼 클래스
 * - 데이터 계층(DTO)과 도메인 계층(Page) 간의 변환 담당
 */
object PageMapper {

    private const val TAG = "PageMapper"

    /**
     * PageDto를 도메인 Page 객체로 변환
     * - 페이지 번호, 텍스트를 그대로 유지
     * - 이미지 경로는 책의 출처(내장 또는 다운로드)에 따라 결정
     *
     * @param pageDto 변환할 페이지 DTO
     * @param storyBaseId 책의 기본 ID (이미지 경로 생성에 사용)
     * @param isDownloaded 다운로드된 책인지 여부 (기본값: false)
     * @param imageFolderPath 다운로드된 책의 이미지 폴더 경로 (다운로드된 책일 경우에만 사용)
     * @return 도메인 Page 객체
     */
    fun mapToDomain(
        pageDto: PageDto,
        storyBaseId: String,
        isDownloaded: Boolean = false,
        imageFolderPath: String? = null
    ): Page {
        // 이미지 파일명 생성 (예: "book_801_page_1.jpg")
        val imageFileName = "book_${storyBaseId}_page_${pageDto.pageNumber}.jpg"

        // 이미지 경로 - 다운로드된 책은 외부 저장소, 그 외는 assets 경로 사용
        val imageUrl = if (isDownloaded && imageFolderPath != null) {
            Log.d(TAG, "Using external storage path for image: file://${imageFolderPath}/${imageFileName}")
            "file://${imageFolderPath}/${imageFileName}"
        } else {
            Log.d(TAG, "Using assets path for image: file:///android_asset/images/${storyBaseId}/${imageFileName}")
            "file:///android_asset/images/${storyBaseId}/${imageFileName}"
        }

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