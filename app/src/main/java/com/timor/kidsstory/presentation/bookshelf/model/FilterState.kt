package com.timor.kidsstory.presentation.bookshelf.model

import androidx.annotation.StringRes
import com.timor.kidsstory.R
import com.timor.kidsstory.domain.model.ReadingLevel

/**
 * 필터 바 상태 관리
 */
data class FilterBarState(
    val selectedFilter: FilterBarCategory = FilterBarCategory.All,
    val selectedStage: FilterLevel = FilterLevel.ONE,
    val selectedCategory: FilterBookCategory = FilterBookCategory.ENVIRONMENT,
    val isStageFilterExpanded: Boolean = false,
    val isCategoryFilterExpanded: Boolean = false
)

/**
 * 필터 바 카테고리 타입
 */
enum class FilterBarCategory(@StringRes val displayNameRes: Int) {
    All(R.string.filter_all),
    STAGE(R.string.filter_stage),
    CATEGORY(R.string.filter_category)
}

/**
 * 읽기 단계 필터
 */
enum class FilterLevel(
    val level: Int, 
    val readingLevel: ReadingLevel,
    val displayName: String,
    @StringRes val levelNameRes: Int,
    @StringRes val ageRangeRes: Int, // This will now be a format string
    val minAge: Int, // New
    val maxAge: Int // New
) {
    ONE(1, ReadingLevel.FIRST_STEPS, "1", R.string.level_1, R.string.age_range_format, 3, 5),
    TWO(2, ReadingLevel.EARLY_READER, "2", R.string.level_2, R.string.age_range_format, 5, 7),
    THREE(3, ReadingLevel.GROWING_READER, "3", R.string.level_3, R.string.age_range_format, 7, 9),
    FOUR(4, ReadingLevel.CONFIDENT_READER, "4", R.string.level_4, R.string.age_range_format, 9, 11),
    FIVE(5, ReadingLevel.ADVANCED_READER, "5", R.string.level_5, R.string.age_11_plus_format, 11, 0) // maxAge can be 0 or ignored for this case
}

/**
 * 책 주제 카테고리 필터 - 기존 7개 카테고리만 사용
 */
enum class FilterBookCategory(
    @StringRes val displayNameRes: Int, 
    val keywords: List<String>,
    val iconRes: Int
) {
    ENVIRONMENT(
        displayNameRes = R.string.category_environment_nature,
        keywords = listOf("environment", "nature", "환경", "자연", "생태", "동물", "식물", "기후", "재활용"),
        iconRes = R.drawable.ic_category_environment
    ),
    SCIENCE_NATURE(
        displayNameRes = R.string.category_science_math, 
        keywords = listOf("math", "science", "수학", "과학", "실험", "숫자", "계산", "우주", "물리"),
        iconRes = R.drawable.ic_category_math
    ),
    CULTURE_WORLD(
        displayNameRes = R.string.category_culture_world,
        keywords = listOf("culture", "world", "문화", "세계", "전통", "음식", "축제", "여행", "나라"),
        iconRes = R.drawable.ic_category_culture
    ),
    SOCIAL_EMOTIONAL(
        displayNameRes = R.string.category_social_emotional,
        keywords = listOf("social", "emotional", "사회", "정서", "감정", "우정", "가족", "갈등", "친구"),
        iconRes = R.drawable.ic_category_society
    ),
    FOLKTALES_HISTORY(
        displayNameRes = R.string.category_folktales_history,
        keywords = listOf("folktale", "history", "이야기", "역사", "전설", "신화", "옛이야기", "위인", "과거"),
        iconRes = R.drawable.ic_category_history
    ),
    DAILY_LIFE(
        displayNameRes = R.string.category_daily_life,
        keywords = listOf("daily", "life", "일상", "생활", "학교", "집", "일과", "습관", "하루"),
        iconRes = R.drawable.ic_category_life
    ),
    ADVENTURE_FANTASY(
        displayNameRes = R.string.category_adventure_fantasy,
        keywords = listOf("adventure", "fantasy", "모험", "판타지", "탐험", "마법", "상상", "꿈", "여행"),
        iconRes = R.drawable.ic_category_adventure
    );

    /**
     * 주어진 카테고리 문자열이 이 필터와 일치하는지 확인
     */
    fun matches(category: String): Boolean {
        return keywords.any { keyword ->
            category.contains(keyword, ignoreCase = true) ||
            keyword.contains(category, ignoreCase = true)
        }
    }

    companion object {
        /**
         * 카테고리 문자열에서 적절한 FilterBookCategory 찾기
         */
        fun fromCategoryString(category: String): FilterBookCategory? {
            return values().find { it.matches(category) }
        }
    }
}
