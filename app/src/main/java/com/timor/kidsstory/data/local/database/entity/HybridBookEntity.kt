package com.timor.kidsstory.data.local.database.entity

import androidx.room.Entity
import kotlinx.serialization.Serializable

/**
 * 하이브리드 책 관리 엔티티
 * - 내장 assets 책과 다운로드 책을 통합 관리
 * - 개별 버전 추적 및 업데이트 상태 관리
 */
@Entity(
    tableName = "hybrid_books",
    primaryKeys = ["id", "language"]
)
@Serializable
data class HybridBookEntity(
    val id: Int,                          // 책 ID (801, 802...)
    val language: String,                 // 언어 코드 ("ko", "en", "tet")
    val title: String,                    // 책 제목
    val level: Int,                       // 읽기 레벨 (1-5)
    val category: String,                 // 카테고리
    val countryOfOrigin: String,          // 원산지
    
    // 파일 경로 정보
    val contentPath: String,              // 콘텐츠 JSON 파일 경로
    val coverImagePath: String,           // 커버 이미지 파일 경로
    val imagesDirectoryPath: String,      // 이미지 디렉토리 경로
    
    // 버전 관리
    val contentVersion: Int,              // 콘텐츠 버전
    val coverVersion: Int,                // 커버 이미지 버전
    val imageAssetsVersion: Int,          // 이미지 에셋 버전
    
    // 상태 관리
    val source: BookSource,               // 책 출처 (BUNDLED/DOWNLOADED)
    val isAvailable: Boolean = true,      // 사용 가능 여부
    val lastUpdated: Long = System.currentTimeMillis(), // 마지막 업데이트
    val downloadDate: Long? = null,       // 다운로드 날짜 (다운로드 책만)
    
    // AI 기능 및 태그
    val aiFeatures: List<String> = emptyList(), // ["FEEDBACK", "VOCABULARY"] 등
    val tags: List<String> = emptyList()         // ["전설", "베트남", "용"] 등
)

/**
 * 책의 출처
 */
enum class BookSource {
    BUNDLED,    // 앱에 내장된 책 (assets)
    DOWNLOADED  // GitHub에서 다운로드한 책
}
