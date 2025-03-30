package com.timor.kidsstory.data.remote.model

/**
 * 원격 서버에서 책 메타데이터를 가져오기 위한 모델 클래스
 * - 앱 업데이트 및 새 콘텐츠 다운로드에 사용
 */

/**
 * 원격 서버에 저장된 책 메타데이터 정보
 * - 콘텐츠 업데이트 시 사용
 *
 * @property id 책 ID
 * @property title 각 언어별 책 제목 맵
 * @property cover 각 언어별 커버 이미지 경로 맵
 * @property download 각 언어별 다운로드 URL 맵
 * @property images 이미지 디렉토리 경로
 * @property languages 지원하는 언어 목록
 */
data class BookMetadata(
    val id: Int,
    val title: Map<String, String>,
    val cover: Map<String, String>,
    val download: Map<String, String>,
    val images: String,
    val languages: List<String>
)