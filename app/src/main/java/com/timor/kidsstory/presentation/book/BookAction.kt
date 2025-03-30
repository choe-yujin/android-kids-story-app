package com.timor.kidsstory.presentation.book

/**
 * 책 읽기 화면에서 발생하는 사용자 액션 정의
 * - 사용자 인터랙션에 따른 액션을 뷰모델에 전달하기 위한 sealed 인터페이스
 */
sealed interface BookAction {
    /**
     * 텍스트 음성 변환 요청
     * - 선택한 텍스트를 음성으로 읽기 위한 액션
     *
     * @property textList 읽을 텍스트 목록
     */
    data class TextToSpeak(val textList: List<String>) : BookAction

    /**
     * 책장 화면으로 돌아가기 요청
     */
    data object BackBookShelf : BookAction

    /**
     * 페이지 변경 요청
     * - 특정 페이지로 이동하기 위한 액션
     *
     * @property page 이동할 페이지 인덱스
     */
    data class PageChange(val page: Int) : BookAction
}