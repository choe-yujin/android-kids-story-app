package com.timor.kidsstory.presentation.util

fun formatFileSize(bytes: Long): String {
    if (bytes < 1024) return "$bytes B"
    val kb = bytes / 1024.0
    val mb = kb / 1024.0
    return if (mb >= 1) {
        String.format("%.1f MB", mb)
    } else {
        String.format("%.0f KB", kb)
    }
}