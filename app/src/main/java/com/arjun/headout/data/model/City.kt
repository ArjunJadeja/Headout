package com.arjun.headout.data.model

import com.google.firebase.firestore.IgnoreExtraProperties
import kotlinx.serialization.Serializable

@IgnoreExtraProperties
data class City(
    val id: String = "",
    val name: Map<String, String?>? = null,
    val country: Map<String, String?>? = null,
    val imageUrl: String? = null,
    val bannerUrl: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val discoverable: Boolean? = null,
    val categories: List<Category?>? = null
)

@Serializable
@IgnoreExtraProperties
data class Category(
    val id: String = "",
    val name: Map<String, String?>? = null,
    val imageUrl: String? = null,
    val subcategories: List<Subcategory?>? = null
)

@Serializable
@IgnoreExtraProperties
data class Subcategory(
    val id: String = "",
    val name: Map<String, String?>? = null,
    val imageUrl: String? = null,
    val experiences: List<String?>? = null
)