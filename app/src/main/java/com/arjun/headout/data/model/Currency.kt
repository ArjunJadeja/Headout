package com.arjun.headout.data.model

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Currency(
    val name: String? = null,
    val code: String? = null,
    val symbol: String? = null
)