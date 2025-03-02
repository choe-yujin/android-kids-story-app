package com.timor.kidsstory.data.local.database

import androidx.room.TypeConverter
import kotlinx.serialization.json.Json

// Map과 List를 Room에서 사용하기 위한 TypeConverter
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
}