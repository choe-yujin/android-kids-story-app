package com.timor.kidsstory.presentation.bookshelf

import com.timor.kidsstory.domain.model.Language
import com.timor.kidsstory.presentation.bookshelf.model.FilterBarCategory
import com.timor.kidsstory.presentation.bookshelf.model.FilterBookCategory
import com.timor.kidsstory.presentation.bookshelf.model.FilterLevel

/**
 * 책장 화면에서 발생하는 사용자 액션 정의
 * - 사용자 인터랙션에 따른 액션을 뷰모델에 전달하기 위한 봉인된 인터페이스
 */
sealed interface BookShelfAction {
    /**
     * 책 선택 액션
     * - 사용자가 특정 책을 선택했을 때 발생
     *
     * @property index 선택한 책의 인덱스
     */
    data class BookSelect(val index: Int) : BookShelfAction

    /**
     * 설정 버튼 클릭 액션
     * - 설정 화면으로 이동 요청
     */
    data object SettingClick : BookShelfAction

    /**
     * 챗봇 버튼 클릭 액션
     * - 챗봇 화면으로 이동 요청
     */
    data object ChatbotClick : BookShelfAction

    /**
     * 배경 음악 시작 액션
     */
    data object StartMusic : BookShelfAction

    /**
     * 배경 음악 정지 액션
     */
    data object StopMusic : BookShelfAction

    /**
     * 언어 변경 액션
     * - 사용자가 언어를 변경했을 때 발생
     *
     * @property language 변경할 언어
     */
    data class ChangeLanguage(val language: Language) : BookShelfAction

    /**
     * 언어 선택 다이얼로그 표시/숨김 액션
     *
     * @property isShow 다이얼로그 표시 여부
     */
    data class ShowLanguageDialog(val isShow: Boolean) : BookShelfAction


    /**
     * 책 다운로드 액션
     * - 사용자가 다운로드 버튼을 클릭했을 때 발생
     *
     * @property index 다운로드할 책의 인덱스
     */
    data class DownloadBook(val index: Int) : BookShelfAction
    /*
    * 책 리스트 필터링
    * */
    data class SelectFilter(
        val filter: FilterBarCategory? = null,
        val stage: FilterLevel? = null,
        val category: FilterBookCategory? = null
    ) : BookShelfAction
}