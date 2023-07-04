package com.arjun.headout.data.model

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Notification(
    val id: String = "",
    val title: Map<String, String> = mapOf(),
    val message: Map<String, String> = mapOf(),
    val type: NotificationType = NotificationType.INFO,
    val read: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

@IgnoreExtraProperties
enum class NotificationType {
    INFO,
    WARNING,
    ERROR
}