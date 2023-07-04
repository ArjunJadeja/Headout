package com.arjun.headout.data.model

import com.google.firebase.firestore.IgnoreExtraProperties
import kotlinx.serialization.Serializable

@IgnoreExtraProperties
data class Experience(
    val id: String? = null,
    val title: Map<String, String?>? = null,
    val description: Map<String, Any?>? = null,
    val mediaUrls: MediaUrls? = null,
    val highlights: Map<String, Any?>? = null
)

@Serializable
@IgnoreExtraProperties
data class MediaUrls(
    val images: List<String?>? = null,
    val videos: String? = null
)