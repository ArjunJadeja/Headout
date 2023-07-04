package com.arjun.headout.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class HeadOuter(
    val uid: String = "",
    val name: String? = null,
    val email: String? = null,
    val username: String? = null,
    val profileImageUrl: String? = null,
    val cityId: String? = null,
    val cityName: String? = null,
    val languageCode: String? = null,
    val currencyCode: String? = null,
    val credits: Long? = 0L,
    val socialNetwork: String? = null,
    val socialNetworkLink: String? = null,
    val bookings: ArrayList<String> = ArrayList(),
    val posts: ArrayList<String> = ArrayList(),
    val likedExperiences: ArrayList<String> = ArrayList(),
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)