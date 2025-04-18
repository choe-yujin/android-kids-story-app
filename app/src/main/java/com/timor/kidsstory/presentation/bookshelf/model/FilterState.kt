package com.timor.kidsstory.presentation.bookshelf.model


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
    val displayName: String
) {
    ONE("1"),
    TWO("2"),
    THREE("3"),
    FOUR("4")
}

enum class FilterBookCategory(val displayName: String) {
    LEGEND("Legend"),
    FOLKTALE("Folktale"),
    CULTURE("Culture"),
    LIFE("Life")
}