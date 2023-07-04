package com.arjun.headout.data.local.city

import androidx.room.TypeConverter
import com.arjun.headout.data.model.Category
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class LocalCityTypeConverters {
    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun fromStringMap(value: String?): Map<String, String?>? {
        value ?: return null
        return json.decodeFromString(value)
    }

    @TypeConverter
    fun toStringMap(value: Map<String, String?>?): String? {
        value ?: return null
        return json.encodeToString(value)
    }

    @TypeConverter
    fun fromCategoryList(value: String?): List<Category?>? {
        value ?: return null
        return json.decodeFromString(value)
    }

    @TypeConverter
    fun toCategoryList(value: List<Category?>?): String? {
        value ?: return null
        return json.encodeToString(value)
    }
}