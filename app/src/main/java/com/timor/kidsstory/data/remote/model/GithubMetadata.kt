package com.timor.kidsstory.data.remote.model

data class GithubMetadata(
    val version: String,
    val lastUpdated: String,
    val books: List<BookMetadata>
)