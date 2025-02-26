package com.timor.epub.model

data class StoryTranslation(
    val storyId: String,
    val title: String,
    val pages: List<Page>
)