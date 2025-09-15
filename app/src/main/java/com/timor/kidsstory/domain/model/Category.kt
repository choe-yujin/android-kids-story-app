package com.timor.kidsstory.domain.model

/**
 * 책의 카테고리를 나타내는 enum
 * - 기존 문자열 카테고리를 enum으로 매핑
 */
enum class Category(val displayName: String) {
    FOLKTALE("민담"),
    FABLE("우화"),
    FAIRY_TALE("동화"),
    LEGEND("전설"),
    MYTH("신화"),
    ADVENTURE("모험"),
    FANTASY("판타지"),
    EDUCATIONAL("교육"),
    SCIENCE("과학"),
    HISTORY("역사"),
    BIOGRAPHY("전기"),
    NATURE("자연"),
    FAMILY("가족"),
    FRIENDSHIP("우정"),
    CULTURE("문화"),
    TRADITION("전통"),
    MODERN("현대"),
    CLASSIC("고전"),
    UNKNOWN("기타");

    companion object {
        /**
         * 문자열을 Category enum으로 변환
         */
        fun fromString(value: String): Category {
            return values().find { 
                it.name.equals(value, ignoreCase = true) || 
                it.displayName.equals(value, ignoreCase = true) 
            } ?: UNKNOWN
        }
    }
}
