package com.timor.kidsstory.domain.model

/**
 * 동화책 페이지의 레이아웃 타입을 정의하는 열거형
 * 
 * @property SPLIT 기존의 왼쪽 이미지, 오른쪽 텍스트 분할 레이아웃
 * @property FULL_IMAGE 전체 화면을 차지하는 이미지 레이아웃 (텍스트는 이미지 위에 오버레이)
 */
enum class PageType(val value: String) {
    SPLIT("SPLIT"),
    FULL_IMAGE("FULL_IMAGE");
    
    companion object {
        /**
         * 문자열 값으로부터 PageType을 찾아 반환
         * 
         * @param value 페이지 타입 문자열
         * @return 해당하는 PageType, 없으면 SPLIT (기본값)
         */
        fun fromString(value: String): PageType {
            return entries.find { it.value == value } ?: SPLIT
        }
    }
}
