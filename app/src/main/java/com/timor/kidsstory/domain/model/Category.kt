package com.timor.kidsstory.domain.model

enum class Category(
    val displayName: String,
    val description: String,
    val tags: List<String>
) {
    ENVIRONMENT(
        displayName = "Environment",
        description = "환경",
        tags = listOf("환경보호", "재활용", "생태계", "기후")
    ),
    SCIENCE_NATURE(
        displayName = "Science & Nature",
        description = "과학/자연",
        tags = listOf("자연현상", "동식물", "우주", "과학실험")
    ),
    CULTURE_WORLD(
        displayName = "Culture & World",
        description = "문화/세계",
        tags = listOf("각국문화", "전통", "음식", "축제", "여행")
    ),
    SOCIAL_EMOTIONAL(
        displayName = "Social Emotional",
        description = "사회/정서",
        tags = listOf("감정", "우정", "가족애", "갈등해결")
    ),
    FOLKTALES_HISTORY(
        displayName = "Folktales & History",
        description = "이야기/역사",
        tags = listOf("전설", "신화", "옛이야기", "역사적사건", "위인")
    ),
    DAILY_LIFE(
        displayName = "Daily Life",
        description = "생활",
        tags = listOf("학교", "집", "일과", "생활습관")
    ),
    ADVENTURE_FANTASY(
        displayName = "Adventure & Fantasy",
        description = "모험/판타지",
        tags = listOf("모험", "탐험", "마법", "상상의세계")
    )
}
