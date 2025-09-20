package com.timor.kidsstory.domain.usecase.book

import android.util.Log
import com.timor.kidsstory.data.local.assets.UnifiedDataSource
import com.timor.kidsstory.data.dto.UnifiedBookMetadata
import javax.inject.Inject

/**
 * 통합 구조 기반으로 다운로드 가능한 책 목록을 가져오는 유스케이스
 * - GitHub에서 메타데이터를 가져와 다운로드 가능한 책 확인
 * - RemoteBook 의존성 제거, 통합 메타데이터 구조 사용
 */
class GetRemoteBooksUseCase @Inject constructor(
    private val unifiedDataSource: UnifiedDataSource
) {
    /**
     * 다운로드 가능한 책 목록 조회
     * 
     * @param languageCode 언어 코드 (예: "ko", "en", "tet")
     * @return 해당 언어를 지원하는 책 메타데이터 목록
     */
    suspend operator fun invoke(languageCode: String): Result<List<UnifiedBookMetadata>> {
        return try {
            Log.d("GetRemoteBooksUseCase", "Getting remote books for language: $languageCode")
            
            // 통합 메타데이터 로드 (GitHub 또는 로컬)
            val metadataResult = unifiedDataSource.loadBooksMetadata()
            if (metadataResult.isFailure) {
                return Result.failure(metadataResult.exceptionOrNull()!!)
            }
            
            val metadata = metadataResult.getOrNull()!!
            val normalizedLang = normalizeLanguageCode(languageCode)
            
            // 해당 언어를 지원하는 책들만 필터링
            val filteredBooks = metadata.books.filter { book ->
                book.languages.containsKey(normalizedLang)
            }
            
            Log.d("GetRemoteBooksUseCase", "Found ${filteredBooks.size} books supporting language: $normalizedLang")
            Result.success(filteredBooks)
            
        } catch (e: Exception) {
            Log.e("GetRemoteBooksUseCase", "Error getting remote books", e)
            Result.failure(e)
        }
    }
    
    /**
     * 언어 코드 정규화
     */
    private fun normalizeLanguageCode(language: String): String {
        return when {
            language.startsWith("ko") -> "ko"
            language.startsWith("tet") -> "tet"
            language.startsWith("en") -> "en"
            language.startsWith("mn") -> "mn"
            else -> "en"
        }
    }
}
