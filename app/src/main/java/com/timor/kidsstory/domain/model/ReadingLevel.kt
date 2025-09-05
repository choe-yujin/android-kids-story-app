package com.timor.kidsstory.domain.model

enum class ReadingLevel(
    val level: Int,
    val levelName: String,
    val ageRange: String,
    val description: String
) {
    FIRST_STEPS(
        level = 1,
        levelName = "First Steps",
        ageRange = "3-5세",
        description = "단순한 문장, 반복적 표현, 그림 중심"
    ),
    EARLY_READER(
        level = 2,
        levelName = "Early Reader",
        ageRange = "5-7세",
        description = "짧은 문장, 쉬운 어휘, 간단한 스토리"
    ),
    GROWING_READER(
        level = 3,
        levelName = "Growing Reader",
        ageRange = "7-9세",
        description = "복잡한 문장, 다양한 어휘, 연속된 사건"
    ),
    CONFIDENT_READER(
        level = 4,
        levelName = "Confident Reader",
        ageRange = "9-11세",
        description = "긴 문단, 추상적 개념, 복잡한 플롯"
    ),
    ADVANCED_READER(
        level = 5,
        levelName = "Advanced Reader",
        ageRange = "11세 이상",
        description = "고급 어휘, 복잡한 주제, 비유적 표현"
    );

    companion object {
        fun fromLevel(level: Int): ReadingLevel {
            return values().find { it.level == level } ?: EARLY_READER
        }
    }
}
