package com.timor.kidsstory.presentation.util

import android.content.Context
import com.timor.kidsstory.R
import com.timor.kidsstory.domain.model.Category
import com.timor.kidsstory.domain.model.ReadingLevel
import com.timor.kidsstory.presentation.bookshelf.model.FilterBookCategory
import com.timor.kidsstory.presentation.bookshelf.model.FilterLevel

/**
 * 카테고리와 레벨의 다국어 이름을 가져오는 유틸리티
 */
object CategoryLocalizer {

    fun getLevelName(level: ReadingLevel, language: String): String {
        return when(language) {
            "ko", "ko-kr" -> when(level) {
                ReadingLevel.FIRST_STEPS -> "첫걸음"
                ReadingLevel.EARLY_READER -> "초급 읽기"
                ReadingLevel.GROWING_READER -> "중급 읽기"
                ReadingLevel.CONFIDENT_READER -> "숙련 읽기"
                ReadingLevel.ADVANCED_READER -> "고급 읽기"
            }
            "tet", "tetum" -> when(level) {
                ReadingLevel.FIRST_STEPS -> "Pasu Primeiru"
                ReadingLevel.EARLY_READER -> "Lee Inísiu"
                ReadingLevel.GROWING_READER -> "Lee Dezenvolvimentu"
                ReadingLevel.CONFIDENT_READER -> "Lee Konfiante"
                ReadingLevel.ADVANCED_READER -> "Lee Avansadu"
            }
            else -> level.levelName
        }
    }

    fun getAgeRange(level: ReadingLevel, language: String): String {
        return when(language) {
            "ko", "ko-kr" -> level.ageRange
            "tet", "tetum" -> when(level) {
                ReadingLevel.FIRST_STEPS -> "Tinan 3-5"
                ReadingLevel.EARLY_READER -> "Tinan 5-7"
                ReadingLevel.GROWING_READER -> "Tinan 7-9"
                ReadingLevel.CONFIDENT_READER -> "Tinan 9-11"
                ReadingLevel.ADVANCED_READER -> "Tinan 11+"
            }
            else -> level.ageRange.replace("세", " years")
        }
    }
}

fun getCategoryName(context: Context, category: FilterBookCategory): String {
    return when (category) {
        FilterBookCategory.ENVIRONMENT -> context.getString(R.string.category_environment)
        FilterBookCategory.SCIENCE_NATURE -> context.getString(R.string.category_science_nature)
        FilterBookCategory.CULTURE_WORLD -> context.getString(R.string.category_culture_world)
        FilterBookCategory.SOCIAL_EMOTIONAL -> context.getString(R.string.category_social_emotional)
        FilterBookCategory.FOLKTALES_HISTORY -> context.getString(R.string.category_folktales_history)
        FilterBookCategory.DAILY_LIFE -> context.getString(R.string.category_daily_life)
        FilterBookCategory.ADVENTURE_FANTASY -> context.getString(R.string.category_adventure_fantasy)
    }
}