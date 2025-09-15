package com.timor.kidsstory.presentation.util

import android.content.Context
import com.timor.kidsstory.domain.model.ReadingLevel
import com.timor.kidsstory.presentation.bookshelf.model.FilterBookCategory
import com.timor.kidsstory.presentation.bookshelf.model.FilterLevel

/**
 * 카테고리 현지화 유틸리티
 */
object CategoryLocalizer {

    /**
     * ReadingLevel을 현재 언어로 변환
     */
    fun getLocalizedLevelName(context: Context, level: ReadingLevel): String {
        return when (level) {
            ReadingLevel.FIRST_STEPS -> context.getString(com.timor.kidsstory.R.string.level_1)
            ReadingLevel.EARLY_READER -> context.getString(com.timor.kidsstory.R.string.level_2)
            ReadingLevel.GROWING_READER -> context.getString(com.timor.kidsstory.R.string.level_3)
            ReadingLevel.CONFIDENT_READER -> context.getString(com.timor.kidsstory.R.string.level_4)
            ReadingLevel.ADVANCED_READER -> context.getString(com.timor.kidsstory.R.string.level_5)
        }
    }

    /**
     * ReadingLevel을 지정된 언어로 변환
     */
    fun getLocalizedLevelName(context: Context, level: ReadingLevel, languageCode: String): String {
        return when (level) {
            ReadingLevel.FIRST_STEPS -> ContextLanguageHelper.getStringInLanguage(context, languageCode, com.timor.kidsstory.R.string.level_1)
            ReadingLevel.EARLY_READER -> ContextLanguageHelper.getStringInLanguage(context, languageCode, com.timor.kidsstory.R.string.level_2)
            ReadingLevel.GROWING_READER -> ContextLanguageHelper.getStringInLanguage(context, languageCode, com.timor.kidsstory.R.string.level_3)
            ReadingLevel.CONFIDENT_READER -> ContextLanguageHelper.getStringInLanguage(context, languageCode, com.timor.kidsstory.R.string.level_4)
            ReadingLevel.ADVANCED_READER -> ContextLanguageHelper.getStringInLanguage(context, languageCode, com.timor.kidsstory.R.string.level_5)
        }
    }

    /**
     * FilterLevel의 레벨명을 현재 언어로 변환
     */
    fun getLocalizedFilterLevelName(context: Context, filterLevel: FilterLevel): String {
        return context.getString(filterLevel.levelNameRes)
    }

    /**
     * FilterLevel의 레벨명을 지정된 언어로 변환
     */
    fun getLocalizedFilterLevelName(context: Context, filterLevel: FilterLevel, languageCode: String): String {
        return ContextLanguageHelper.getStringInLanguage(context, languageCode, filterLevel.levelNameRes)
    }

    /**
     * FilterLevel의 연령대를 현재 언어로 변환
     */
    fun getLocalizedAgeRange(context: Context, filterLevel: FilterLevel): String {
        return context.getString(filterLevel.ageRangeRes)
    }

    /**
     * FilterLevel의 연령대를 지정된 언어로 변환
     */
    fun getLocalizedAgeRange(context: Context, filterLevel: FilterLevel, languageCode: String): String {
        return ContextLanguageHelper.getStringInLanguage(context, languageCode, filterLevel.ageRangeRes)
    }

    /**
     * ReadingLevel의 연령대 반환
     */
    fun getAgeRange(level: ReadingLevel): String {
        return level.ageRange
    }

    /**
     * ReadingLevel의 설명 반환
     */
    fun getLevelDescription(level: ReadingLevel): String {
        return level.description
    }

    /**
     * FilterBookCategory를 현재 언어로 변환
     */
    fun getLocalizedCategoryName(context: Context, category: FilterBookCategory): String {
        return context.getString(category.displayNameRes)
    }

    /**
     * FilterBookCategory를 지정된 언어로 변환
     */
    fun getLocalizedCategoryName(context: Context, category: FilterBookCategory, languageCode: String): String {
        return ContextLanguageHelper.getStringInLanguage(context, languageCode, category.displayNameRes)
    }

    /**
     * 문자열 카테고리를 FilterBookCategory로 매핑
     */
    fun mapStringToFilterCategory(categoryString: String): FilterBookCategory? {
        return FilterBookCategory.fromCategoryString(categoryString)
    }

    /**
     * 레벨 번호를 ReadingLevel로 변환
     */
    fun mapLevelToReadingLevel(level: Int): ReadingLevel {
        return ReadingLevel.fromInt(level)
    }
}

/**
 * 카테고리 이름을 가져오는 전역 함수 (Compose에서 사용)
 */
fun getCategoryName(context: Context, category: FilterBookCategory): String {
    return CategoryLocalizer.getLocalizedCategoryName(context, category)
}

/**
 * 지정된 언어로 카테고리 이름을 가져오는 전역 함수
 */
fun getCategoryNameInLanguage(context: Context, category: FilterBookCategory, languageCode: String): String {
    return CategoryLocalizer.getLocalizedCategoryName(context, category, languageCode)
}

/**
 * 지정된 언어로 빈 상태 메시지를 가져오는 함수들
 */
fun getEmptyStateTitle(context: Context, languageCode: String): String {
    return ContextLanguageHelper.getStringInLanguage(context, languageCode, com.timor.kidsstory.R.string.empty_filter_title)
}

fun getEmptyStateMessage(context: Context, languageCode: String): String {
    return ContextLanguageHelper.getStringInLanguage(context, languageCode, com.timor.kidsstory.R.string.empty_filter_message)
}

fun getEmptyBookshelfTitle(context: Context, languageCode: String): String {
    return ContextLanguageHelper.getStringInLanguage(context, languageCode, com.timor.kidsstory.R.string.empty_bookshelf_title)
}

fun getEmptyBookshelfMessage(context: Context, languageCode: String): String {
    return ContextLanguageHelper.getStringInLanguage(context, languageCode, com.timor.kidsstory.R.string.empty_bookshelf_message)
}
