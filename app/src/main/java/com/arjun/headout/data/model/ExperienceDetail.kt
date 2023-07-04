package com.arjun.headout.data.model

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class ExperienceDetail(
    val pricing: List<Pricing>? = null,
    val yourExperience: Map<String, String>? = null,
    val knowBeforeYouGo: Map<String, List<String>>? = null,
    val inclusions: Map<String, List<String>>? = null,
    val exclusions: Map<String, List<String>>? = null,
    val cancellationPolicy: Map<String, String>? = null,
    val ratings: Ratings? = null,
    val freeCancellation: Boolean? = null,
    val instantConfirmation: Boolean? = null,
    val availability: Availability? = null
)

@IgnoreExtraProperties
data class Pricing(
    val currency: String? = null,
    val price: Double? = null,
    val mrp: Double? = null,
    val totalSaved: Double? = null
)

@IgnoreExtraProperties
data class Ratings(
    val average: Double? = null,
    val count: Int? = null,
    val reviews: List<Review>? = null
)

@IgnoreExtraProperties
data class Review(
    val userId: String? = null,
    val userName: String? = null,
    val rating: Double? = null,
    val comment: String? = null
)

@IgnoreExtraProperties
data class Availability(
    val startDate: Long? = null,
    val endDate: Long? = null,
    val reservedDates: List<Long>? = null
)