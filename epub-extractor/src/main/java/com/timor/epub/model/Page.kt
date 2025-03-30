package com.timor.epub.model

/**
 * 페이지 데이터 모델
 *
 * 동화책의 한 페이지를 나타내는 모델 클래스입니다.
 *
 * @property pageNumber 페이지 번호
 * @property texts 페이지에 포함된 텍스트 목록
 */
data class Page(
    val pageNumber: Int,
    val texts: List<String>
)