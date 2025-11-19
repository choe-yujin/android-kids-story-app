package com.timor.kidsstory.domain.model

/**
 * 연령대별 읽기 레벨 체계
 * 테툼드림즈 앱의 5단계 레벨 시스템
 */
enum class ReadingLevel(
    val level: Int,
    val displayName: String,
    val ageRange: String,
    val description: String
) {
    FIRST_STEPS(
        level = 1,
        displayName = "First Steps",
        ageRange = "3-5세",
        description = "단순한 문장, 반복적 표현, 그림 중심"
    ),
    EARLY_READER(
        level = 2,
        displayName = "Early Reader", 
        ageRange = "5-7세",
        description = "짧은 문장, 쉬운 어휘, 그림과 텍스트 균형"
    ),
    GROWING_READER(
        level = 3,
        displayName = "Growing Reader",
        ageRange = "7-9세", 
        description = "복잡한 문장 구조, 다양한 어휘, 기본 문단"
    ),
    CONFIDENT_READER(
        level = 4,
        displayName = "Confident Reader",
        ageRange = "9-11세",
        description = "긴 문단, 챕터 구성, 추상적 개념 포함"
    ),
    ADVANCED_READER(
        level = 5,
        displayName = "Advanced Reader",
        ageRange = "11세 이상",
        description = "고급 어휘, 복잡한 주제, 비유적 표현"
    );

    companion object {
        /**
         * 숫자를 ReadingLevel enum으로 변환
         */
        fun fromInt(level: Int): ReadingLevel {
            return values().find { it.level == level } ?: FIRST_STEPS
        }

        /**
         * 모든 레벨 목록 반환
         */
        fun getAllLevels(): List<ReadingLevel> {
            return values().toList()
        }
    }
}
