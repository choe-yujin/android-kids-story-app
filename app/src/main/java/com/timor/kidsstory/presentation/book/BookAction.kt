package com.timor.kidsstory.presentation.book

sealed interface BookAction {
    data class TextToSpeak(val textList: List<String>) : BookAction
    data object BackBookShelf : BookAction
    data class PageChange(val page: Int) : BookAction
}