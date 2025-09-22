package com.timor.kidsstory.data.local.database

import androidx.room.TypeConverter
import com.timor.kidsstory.data.local.database.entity.BookSource
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

/**
 * Room DB에서 복합 타입 변환을 위한 TypeConverter
 * - Map<String, String>, List<String>, BookSource 등 지원
 */
class Converters {
    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun fromMapToString(map: Map<String, String>): String {
        return json.encodeToString(map)
    }

    @TypeConverter
    fun fromStringToMap(value: String): Map<String, String> {
        return json.decodeFromString(value)
    }

    @TypeConverter
    fun fromListToString(list: List<String>): String {
        return json.encodeToString(list)
    }

    @TypeConverter
    fun fromStringToList(value: String): List<String> {
        return json.decodeFromString(value)
    }

    @TypeConverter
    fun fromBookSourceToString(source: BookSource): String {
        return source.name
    }

    @TypeConverter
    fun fromStringToBookSource(value: String): BookSource {
        return BookSource.valueOf(value)
    }
}
