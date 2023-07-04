package com.arjun.headout.data.local.city

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.arjun.headout.data.model.Category

@Entity(tableName = "local_city")
@TypeConverters(LocalCityTypeConverters::class)
data class LocalCity(
    @PrimaryKey val id: String = "",
    val name: Map<String, String?>? = null,
    val country: Map<String, String?>? = null,
    val imageUrl: String? = null,
    val bannerUrl: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val discoverable: Boolean? = null,
    val categories: List<Category?>? = null
)