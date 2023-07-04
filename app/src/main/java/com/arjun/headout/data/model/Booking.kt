package com.arjun.headout.data.model

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Booking(
    val id: String = "",
    val userId: String = "",
    val bookingTime: Long = 0L,
    val totalPrice: Double = 0.0,
    val currency: String = "",
    var hasPosted: Boolean = false,
    val experience: Experience? = null,
    val bookingDetails: BookingDetails = BookingDetails()
)

@IgnoreExtraProperties
data class BookingDetails(
    val experienceDate: Long = 0L,
    val totalTicketsNumber: Int = 1,
    val contactName: String = "",
    val contactEmail: String = "",
    val contactPhone: String = "",
    val additionalRequests: String = ""
)