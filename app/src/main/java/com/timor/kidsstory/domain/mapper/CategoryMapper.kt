package com.timor.kidsstory.domain.mapper

import com.timor.kidsstory.domain.model.Category
import com.timor.kidsstory.domain.model.ReadingLevel

/**
 * 레거시 카테고리 문자열을 새로운 Category enum으로 매핑
 */
object CategoryMapper {
    
    fun mapToCategory(categoryString: String): Category {
        return when (categoryString.lowercase()) {
            "legend", "folktale", "myth", "myths" -> Category.FOLKTALES_HISTORY
            "culture", "cultural", "tradition" -> Category.CULTURE_WORLD
            "life", "daily", "daily life" -> Category.DAILY_LIFE
            "environment", "env", "nature conservation" -> Category.ENVIRONMENT
            "science", "nature", "science & nature" -> Category.SCIENCE_NATURE
            "emotion", "social", "feelings" -> Category.SOCIAL_EMOTIONAL
            "adventure", "fantasy", "magic" -> Category.ADVENTURE_FANTASY
            else -> Category.DAILY_LIFE // 기본값
        }
    }
    
    fun mapToReadingLevel(level: Int): ReadingLevel {
        return when (level) {
            1 -> ReadingLevel.FIRST_STEPS
            2 -> ReadingLevel.EARLY_READER
            3 -> ReadingLevel.GROWING_READER
            4 -> ReadingLevel.CONFIDENT_READER
            5 -> ReadingLevel.ADVANCED_READER
            else -> ReadingLevel.EARLY_READER // 기본값
        }
    }
}