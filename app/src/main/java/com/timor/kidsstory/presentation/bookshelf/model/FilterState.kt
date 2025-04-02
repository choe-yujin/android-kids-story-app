package com.timor.kidsstory.presentation.bookshelf.model


data class FilterButtonState(
    val buttonState: FilterBarCategory = FilterBarCategory.All
)


enum class FilterBarCategory(
    val displayName: String
) {
    All("All"),
    STAGE("Stage"),
    CATEGORY("Category")
}


// 필터 옵션 (ALL, LEVEL, CATEGORY)
enum class FilterOption {
    ALL,
    LEVEL,
    CATEGORY
}

// 책 레벨 (1, 2, 3, 4)
enum class BookLevel(val level: Int) {
    LEVEL_1(1),
    LEVEL_2(2),
    LEVEL_3(3),
    LEVEL_4(4)
}

// 책 카테고리 (LEGEND, FOLKTALE, CULTURE, LIFE)
enum class BookCategory {
    LEGEND,
    FOLKTALE,
    CULTURE,
    LIFE
}