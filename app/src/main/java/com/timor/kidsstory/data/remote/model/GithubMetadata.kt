package com.timor.kidsstory.data.remote.model

import kotlinx.serialization.Serializable
/**
 * GitHub에서 앱 메타데이터를 가져오기 위한 모델 클래스
 * - 앱 버전 확인 및 새 책 목록 다운로드에 사용
 */

/**
 * GitHub에서 관리되는 전체 메타데이터 정보
 *
 * @property version 메타데이터 버전
 * @property lastUpdated 마지막 업데이트 일자
 * @property books 책 메타데이터 목록
 */
@Serializable
data class GithubMetadata(
    val version: String,
    val lastUpdated: String,
    val books: List<RemoteBook>
)

@Serializable
data class RemoteBook(
    val id: Int,
    val title: Map<String, String>,
    val cover: Map<String, String>,
    val download: Map<String, String>,
    val images: String,
    val languages: List<String>
)