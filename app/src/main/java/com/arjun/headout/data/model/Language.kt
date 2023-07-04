package com.arjun.headout.data.model

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Language(
    val name: String? = null,
    val code: String? = null,
    val nativeName: String? = null
)