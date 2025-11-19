package com.timor.kidsstory.presentation.leveltest.model

sealed interface LevelTestNavigationTarget {
    data class Bookshelf(
        val language: String,
        val level: Int,
        val wasSkipped: Boolean,
        val showLevelResultPopup: Boolean
    ) : LevelTestNavigationTarget
}
