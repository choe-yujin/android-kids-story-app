package com.timor.kidsstory.presentation.bookshelf.model

import com.timor.kidsstory.domain.model.Category
import com.timor.kidsstory.domain.model.ReadingLevel

data class FilterBarState(
    val selectedFilter: FilterBarCategory = FilterBarCategory.All,
    val selectedStage: FilterLevel? = null,
    val selectedCategory: FilterBookCategory? = null,
    val isStageFilterExpanded: Boolean = false,
    val isCategoryFilterExpanded: Boolean = false,
)

enum class FilterBarCategory(
    val displayName: String
) {
    All("All"),
    STAGE("Stage"),
    CATEGORY("Category")
}

enum class FilterLevel(
    val displayName: String,
    val level: Int
) {
    ONE("1", 1),
    TWO("2", 2),
    THREE("3", 3),
    FOUR("4", 4),
    FIVE("5", 5);
    
    fun toReadingLevel(): ReadingLevel {
        return when(this) {
            ONE -> ReadingLevel.FIRST_STEPS
            TWO -> ReadingLevel.EARLY_READER
            THREE -> ReadingLevel.GROWING_READER
            FOUR -> ReadingLevel.CONFIDENT_READER
            FIVE -> ReadingLevel.ADVANCED_READER
        }
    }
}

enum class FilterBookCategory(
    val displayName: String,
    val categoryId: String
) {
    ENVIRONMENT("Environment", "ENVIRONMENT"),
    SCIENCE_NATURE("Science & Nature", "SCIENCE_NATURE"),
    CULTURE_WORLD("Culture & World", "CULTURE_WORLD"),
    SOCIAL_EMOTIONAL("Social Emotional", "SOCIAL_EMOTIONAL"),
    FOLKTALES_HISTORY("Folktales & History", "FOLKTALES_HISTORY"),
    DAILY_LIFE("Daily Life", "DAILY_LIFE"),
    ADVENTURE_FANTASY("Adventure & Fantasy", "ADVENTURE_FANTASY");

    fun toCategory(): Category {
        return when(categoryId) {
            "ENVIRONMENT" -> Category.ENVIRONMENT
            "SCIENCE_NATURE" -> Category.SCIENCE_NATURE
            "CULTURE_WORLD" -> Category.CULTURE_WORLD
            "SOCIAL_EMOTIONAL" -> Category.SOCIAL_EMOTIONAL
            "FOLKTALES_HISTORY" -> Category.FOLKTALES_HISTORY
            "DAILY_LIFE" -> Category.DAILY_LIFE
            "ADVENTURE_FANTASY" -> Category.ADVENTURE_FANTASY
            else -> Category.DAILY_LIFE
        }
    }

}