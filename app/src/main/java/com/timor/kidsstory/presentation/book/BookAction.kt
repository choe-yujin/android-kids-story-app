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

    /**
     * 텍스트 섹션 레이아웃 업데이트
     * - 텍스트 영역의 크기 정보 업데이트
     *
     * @property pageIndex 페이지 인덱스
     * @property contentHeight 콘텐츠 높이
     * @property containerHeight 컸테이너 높이
     */
    data class UpdateTextSectionLayout(
        val pageIndex: Int,
        val contentHeight: Int,
        val containerHeight: Int
    ) : BookAction

    /**
     * 텍스트 섹션 스크롤 업데이트
     * - 스크롤 위치 변경 시 호출
     *
     * @property pageIndex 페이지 인덱스
     * @property scrollOffset 스크롤 오프셋
     * @property maxScrollOffset 최대 스크롤 오프셋
     */
    data class UpdateTextSectionScroll(
        val pageIndex: Int,
        val scrollOffset: Int,
        val maxScrollOffset: Int
    ) : BookAction

    /**
     * 완독 축하 화면 확인
     * - 축하 화면에서 확인 버튼 클릭 시 호출
     */
    data object CompletionConfirmed : BookAction
    
    /**
     * 읽기 진도 업데이트
     * - 페이지 변경 시 읽기 진도 저장
     *
     * @property bookId 책 ID
     * @property currentPage 현재 페이지 (1부터 시작)
     * @property totalPages 총 페이지 수
     * @property languageCode 언어 코드
     */
    data class UpdateReadingProgress(
        val bookId: String,
        val currentPage: Int,
        val totalPages: Int,
        val languageCode: String
    ) : BookAction
}