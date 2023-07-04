package com.arjun.headout.data.local.experience

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.arjun.headout.data.model.MediaUrls

@Entity(tableName = "local_experience")
@TypeConverters(LocalExperienceTypeConverters::class)
data class LocalExperience(
    @PrimaryKey val id: String = "",
    val title: Map<String, String>? = null,
    val description: Map<String, String>? = null,
    val mediaUrls: MediaUrls? = null,
    val highlights: Map<String, String>? = null
)