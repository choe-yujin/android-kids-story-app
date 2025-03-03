package com.timor.kidsstory.presentation.bookshelf

import com.timor.kidsstory.domain.model.Language

sealed interface BookShelfAction {
    data class BookSelect(val index: Int) : BookShelfAction
    data object SettingClick : BookShelfAction
    data object ChatbotClick : BookShelfAction
    data object StartMusic : BookShelfAction
    data object StopMusic : BookShelfAction
    data class ChangeLanguage(val language: Language) : BookShelfAction
    data class ShowLanguageDialog(val isShow: Boolean) : BookShelfAction
}