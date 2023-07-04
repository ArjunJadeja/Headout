package com.arjun.headout.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class Post(
    val id: String = "",
    val experienceId: String? = null,
    val bookingId: String? = null,
    val userId: String? = null,
    val text: Map<String, String?>? = null,
    val imageUrl: List<String?>? = null,
    @ServerTimestamp val createdAt: Timestamp? = null
)

//@Serializable
//@IgnoreExtraProperties
//data class MultiMediaUrls(
//    val imageUrl: List<String?>? = null,
//    val videoUrl: List<String?>? = null
//)