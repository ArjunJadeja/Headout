package com.arjun.headout.util

import com.arjun.headout.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.UUID

object TranslationUtil {

    suspend fun translateText(text: String): Map<String, String> = withContext(Dispatchers.IO) {

        val key = BuildConfig.TRANSLATOR_KEY
        val endpoint = "https://api.cognitive.microsofttranslator.com"

        val location = "eastus2"

        val path = "/translate"
        val constructedUrl = endpoint + path

        val params = mapOf(
            "api-version" to "3.0", "from" to "en", "to" to "en,zh,es,ar,hi,fr,ru,pt,ja,de"
        )

        val headers = mapOf(
            "Ocp-Apim-Subscription-Key" to key,
            "Ocp-Apim-Subscription-Region" to location,
            "Content-type" to "application/json",
            "X-ClientTraceId" to UUID.randomUUID().toString()
        )

        val client = OkHttpClient()
        val requestBody = Json.encodeToString(listOf(mapOf("text" to text)))
            .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        val requestBuilder = Request.Builder().url(constructedUrl.toHttpUrl().newBuilder().apply {
            params.forEach { (key, value) -> addQueryParameter(key, value) }
        }.build()).post(requestBody)

        headers.forEach { (key, value) -> requestBuilder.addHeader(key, value) }
        val request = requestBuilder.build()
        val response = client.newCall(request).execute()
        val responseBody = response.body?.string() ?: throw Exception("Failed to translate text")
        val jsonResponse = Json.parseToJsonElement(responseBody) as? JsonArray
            ?: throw Exception("Unexpected response format")

        val translations = mutableMapOf("og" to text)
        for (translation in jsonResponse[0].jsonObject["translations"]!!.jsonArray) {
            var langCode = translation.jsonObject["to"]!!.jsonPrimitive.content
            val translatedText = translation.jsonObject["text"]!!.jsonPrimitive.content

            if (langCode == "zh-Hans") {
                langCode = "zh"
            }
            translations[langCode] = translatedText
        }
        translations
    }
}