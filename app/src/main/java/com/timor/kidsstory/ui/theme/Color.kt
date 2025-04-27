package com.timor.kidsstory.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * 앱 전체에서 사용할 색상 정의 객체
 *
 * 앱의 일관된 디자인을 위해 모든 색상 값을 중앙에서 관리합니다.
 * 색상은 기능과 강도에 따라 그룹화되어 있으며 숫자가 커질수록 더 어두운 색조를 나타냅니다.
 */
object AppColors {
    /**
     * 메인 브랜드 색상 - 노란색 계열
     * 앱의 주요 UI 요소, 강조 컴포넌트, 헤더 등에 사용됩니다.
     */
    val primary50 = Color(0xFFFFF9E0)  // 가장 밝은 배경색으로 사용
    val primary100 = Color(0xFFFFF3C1) // 연한 배경, 비활성 버튼
    val primary200 = Color(0xFFFFE892) // 밝은 강조 요소
    val primary300 = Color(0xFFFFDD7C) // 중간 강조, 활성 요소
    val primary400 = Color(0xFFFDDE5A) // 주요 버튼, 강조 요소
    val primary500 = Color(0xFFE3C44A) // 기본 브랜드 색상
    val primary600 = Color(0xFFC9A93B) // 강조 텍스트, 아이콘
    val primary700 = Color(0xFFAF8F2D) // 진한 강조, 포커스 요소
    val primary800 = Color(0xFF624407) // 진한 텍스트
    val primary900 = Color(0xFF7C5C12) // 가장 진한 브랜드 요소

    /**
     * 보조 브랜드 색상 - 녹색 계열
     * 액션 버튼, 진행 상태 등 보조적인 UI 요소에 사용됩니다.
     */
    val secondary200 = Color(0xFFC5FF7A) // 밝은 보조 강조
    val secondary600 = Color(0xFF47A714) // 진한 보조 강조

    /**
     * 중립 색상 - 흰색/회색/검정색 계열
     * 텍스트, 배경, 구분선 등 기본 UI 요소에 사용됩니다.
     */
    val neutralWhite = Color(0xFFFFFFFF) // 순수 흰색
    val neutral50 = Color(0xFFFAFAFA)    // 거의 흰색에 가까운 배경
    val neutral100 = Color(0xFFF5F5F5)   // 매우 밝은 회색 배경
    val neutral200 = Color(0xFFE5E5E5)   // 밝은 회색 구분선
    val neutral300 = Color(0xFFD4D4D4)   // 중간 밝은 회색
    val neutral400 = Color(0xFFA3A3A3)   // 중간 회색, 비활성 요소
    val neutral500 = Color(0xFF737373)   // 중간 회색 텍스트
    val neutral600 = Color(0xFF525252)   // 진한 회색 텍스트
    val neutral700 = Color(0xFF404040)   // 더 진한 회색 텍스트
    val neutral800 = Color(0xFF262626)   // 거의 검정에 가까운 텍스트
    val neutral900 = Color(0xFF171717)   // 진한 검정 텍스트
    val neutral950 = Color(0xFF0A0A0A)   // 매우 진한 검정
    val neutralBlack = Color(0xFF000000) // 순수 검정

    /**
     * 빨간색 계열 - 경고, 에러 표시
     * 경고 메시지, 오류 표시, 삭제 버튼 등에 사용됩니다.
     */
    val red600 = Color(0xFFE32F27) // 기본 오류 색상
    val red700 = Color(0xFFC52820) // 진한 오류 색상

    /**
     * 노란빨간색 계열 - 주의, 경고 표시
     * 경고 알림, 주의 메시지 등에 사용됩니다.
     */
    val yellowRed500 = Color(0xFFFF9500) // 기본 주의 색상
    val yellowRed600 = Color(0xFFE38400) // 중간 주의 색상
    val yellowRed700 = Color(0xFFC57600) // 진한 주의 색상

    /**
     * 녹색 계열 - 성공, 진행 표시
     * 성공 메시지, 완료 상태, 진행 표시 등에 사용됩니다.
     */
    val green50 = Color(0xFFE9FCE9)  // 매우 밝은 녹색 배경
    val green100 = Color(0xFFC3F6C4) // 밝은 녹색 배경
    val green600 = Color(0xFF2DA148) // 기본 성공 색상
    val green700 = Color(0xFF237D38) // 진한 성공 색상

    /**
     * 파란색 계열 - 정보, 링크 표시
     * 정보 메시지, 링크, 상호작용 요소 등에 사용됩니다.
     */
    val blue50 = Color(0xFFE5F2FF)  // 매우 밝은 파란색 배경
    val blue100 = Color(0xFFB8DBFF) // 밝은 파란색 배경
    val blue400 = Color(0xFF2E93FF) // 중간 파란색
    val blue500 = Color(0xFF5A98FD) // 기본 정보 색상
    val blue600 = Color(0xFF2A78DC) // 진한 정보 색상
    val blue700 = Color(0xFF1E5FAD) // 더 진한 정보 색상

    /**
     * 보라색 계열 - 특별 강조 요소
     * 프리미엄 기능, 특별 항목 등에 사용됩니다.
     */
    val purple700 = Color(0xFF67387A) // 진한 보라색


    /*
    * 피그마에 없어서 즉석으로 추가한것들
    * */
    val unknown200 = Color(0xFFA1A1A1)
    val unknown300 = Color(0xFF272727)
    val unknown400 = Color(0xFFD9D9D9)
    val unknown500 = Color(0xFFFECA00)

    /*
    * Level 관련 색상
    * */
    val level1 = Color(0xFF285E0B)
    val level2 = Color(0xFF715B11)
    val level3 = Color(0xFF7C4A05)
    val level4 = Color(0xFF791813)
    val level5 = Color(0xFF1F002C)


}