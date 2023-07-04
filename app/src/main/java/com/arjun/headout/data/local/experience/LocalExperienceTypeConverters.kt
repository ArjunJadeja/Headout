package com.arjun.headout.data.local.experience

import androidx.room.TypeConverter
import com.arjun.headout.data.model.MediaUrls
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class LocalExperienceTypeConverters {
    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun fromStringMap(value: String?): Map<String, String>? {
        value ?: return null
        return json.decodeFromString<Map<String, String>>(value)
    }

    @TypeConverter
    fun toStringMap(value: Map<String, String>?): String? {
        value ?: return null
        return json.encodeToString(value)
    }

    @TypeConverter
    fun fromMediaUrls(value: String?): MediaUrls? {
        value ?: return null
        return json.decodeFromString(value)
    }

    @TypeConverter
    fun toMediaUrls(value: MediaUrls?): String? {
        value ?: return null
        return json.encodeToString(value)
    }
}