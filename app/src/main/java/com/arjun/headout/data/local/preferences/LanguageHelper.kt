package com.arjun.headout.data.local.preferences

import com.arjun.headout.data.model.Language

object LanguageHelper {
    val languages = listOf(
        Language("English", "en", "English"),
        Language("Mandarin Chinese", "zh", "中文"),
        Language("Spanish", "es", "Español"),
        Language("Arabic", "ar", "العربية"),
        Language("Hindi", "hi", "हिन्दी"),
        Language("French", "fr", "Français"),
        Language("Russian", "ru", "Русский"),
        Language("Portuguese", "pt", "Português"),
        Language("Japanese", "ja", "日本語"),
        Language("German", "de", "Deutsch")
    )

    fun getLanguageByCode(code: String): Language? {
        return languages.find { it.code == code }
    }
}