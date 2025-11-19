package com.timor.kidsstory.domain.model

/**
 * 책 완독 시 표시할 미션/축하 메시지 정보를 담는 도메인 모델 클래스
 *
 * @property title 미션 제목 (크게 표시)
 * @property description 미션 설명 (일반 크기로 표시)
 */
data class Mission(
    val title: String,
    val description: String
)
