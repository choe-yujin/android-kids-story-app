package com.timor.kidsstory.presentation.bookshelf

import com.timor.kidsstory.domain.model.Language
import com.timor.kidsstory.presentation.bookshelf.components.ReadingStatusFilter
import com.timor.kidsstory.presentation.bookshelf.model.FilterBarCategory
import com.timor.kidsstory.presentation.bookshelf.model.FilterBookCategory
import com.timor.kidsstory.presentation.bookshelf.model.FilterLevel
import com.timor.kidsstory.presentation.bookshelf.model.ManagementTab

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

    // DownloadBook 액션 제거 - 관리 모드에서만 다운로드 처리

    /**
     * 책 리스트 필터링 액션
     */
    data class SelectFilter(
        val filter: FilterBarCategory? = null,
        val stage: FilterLevel? = null,
        val category: FilterBookCategory? = null
    ) : BookShelfAction

    /**
     * 출석 팝업 닫기 액션
     * - 출석 축하 팝업을 닫을 때 발생
     */
    data object DismissAttendancePopup : BookShelfAction

    /**
     * MyPage 클릭 액션
     * - 출석/진도 영역을 클릭하여 MyPage로 이동할 때 발생
     * - 현재는 로그만 출력하고 향후 네비게이션 구현 예정
     */
    data object MyPageClick : BookShelfAction

    /**
     * 읽음 상태 필터 선택 액션
     * - Reading Status Bar에서 상태를 선택했을 때 발생
     *
     * @property status 선택한 읽음 상태
     */
    data class SelectReadingStatus(val status: ReadingStatusFilter) : BookShelfAction

    // 🆕 관리 모드 관련 액션들
    /**
     * 관리 모드 전환 액션
     * - 헤더의 [관리] 버튼을 클릭했을 때 발생
     */
    data object ToggleManagementMode : BookShelfAction

    /**
     * 관리 탭 선택 액션
     * - 관리 모드에서 전체/다운로드/업데이트 탭 선택
     *
     * @property tab 선택한 관리 탭
     */
    data class SelectManagementTab(val tab: ManagementTab) : BookShelfAction

    /**
     * 책 선택 상태 변경 액션 (관리 모드)
     * - 체크박스로 책을 선택/해제할 때 발생
     *
     * @property bookId 책 ID
     * @property isSelected 선택 여부
     */
    data class ToggleBookSelection(val bookId: String, val isSelected: Boolean) : BookShelfAction

    /**
     * 선택된 항목들 다운로드/업데이트 실행
     * - FloatingActionButton 클릭 시 발생
     */
    data object ExecuteSelectedActions : BookShelfAction

    /**
     * 확인 팝업 표시/숨김
     *
     * @property show 팝업 표시 여부
     */
    data class ShowConfirmationPopup(val show: Boolean) : BookShelfAction

    data object CheckForUpdate : BookShelfAction

    data class ShowUpdateDialog(val show: Boolean) : BookShelfAction

    data object PostponeUpdate : BookShelfAction

    /**
     * 레벨 테스트 결과 팝업 닫기 액션
     */
    data object DismissLevelResultPopup : BookShelfAction
    
    /**
     * 잠긴 책 클릭 시 팝업 표시/숨김
     */
    data class ShowLockedBookPopup(val show: Boolean) : BookShelfAction
    
    /**
     * 선택 취소 액션 (관리 모드)
     * - 팝업에서 취소 버튼 클릭 시 발생
     * - 선택 내역 초기화 + 팝업 닫기
     */
    data object CancelSelection : BookShelfAction
    
    /**
     * 동화 정보 팝업 표시 액션
     * - BookCover 더블클릭 시 발생
     * 
     * @property storyId 동화 ID
     */
    data class ShowStoryInfoDialog(val storyId: String) : BookShelfAction
    
    /**
     * 동화 정보 팝업 닫기 액션
     */
    data object DismissStoryInfoDialog : BookShelfAction
}
