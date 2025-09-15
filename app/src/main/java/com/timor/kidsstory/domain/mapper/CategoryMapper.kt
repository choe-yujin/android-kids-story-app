package com.timor.kidsstory.domain.mapper

import com.timor.kidsstory.domain.model.Category
import com.timor.kidsstory.domain.model.ReadingLevel

/**
 * 카테고리 및 읽기 레벨 매핑을 담당하는 유틸리티 클래스
 * - 기존 문자열/숫자 데이터를 enum으로 변환
 * - 향후 확장성을 위한 매핑 로직 제공
 */
object CategoryMapper {

    /**
     * 문자열 카테고리를 Category enum으로 매핑
     * 
     * @param category 원본 카테고리 문자열
     * @return 매핑된 Category enum
     */
    fun mapToCategory(category: String): Category {
        return Category.fromString(category)
    }

    /**
     * 숫자 레벨을 ReadingLevel enum으로 매핑
     * 
     * @param level 원본 레벨 숫자 (1-5)
     * @return 매핑된 ReadingLevel enum
     */
    fun mapToReadingLevel(level: Int): ReadingLevel {
        return ReadingLevel.fromInt(level)
    }

    /**
     * Category enum을 표시용 문자열로 변환
     * 
     * @param category Category enum
     * @return 표시용 문자열
     */
    fun categoryToDisplayString(category: Category): String {
        return category.displayName
    }

    /**
     * ReadingLevel enum을 표시용 문자열로 변환
     * 
     * @param readingLevel ReadingLevel enum
     * @return 표시용 문자열
     */
    fun readingLevelToDisplayString(readingLevel: ReadingLevel): String {
        return readingLevel.displayName
    }
}
