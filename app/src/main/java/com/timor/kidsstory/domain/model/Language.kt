package com.timor.kidsstory.domain.model

data class Language(
    val code: String,        // "en-ph", "ko-kr", "tet"
    val displayName: String,
    val flagResId: Int
)