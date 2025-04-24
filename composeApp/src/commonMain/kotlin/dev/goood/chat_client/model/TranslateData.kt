package dev.goood.chat_client.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class Lang {
    @SerialName("en-US")
    EN,
    @SerialName("ru")
    RU,
}

@Serializable
data class TranslateData (
    val text: String,
    @SerialName("source_language_code")
    val sourceLanguageCode: Lang,
    @SerialName("target_language_code")
    val targetLanguageCode : Lang
)

@Serializable
data class TranslateResponse (
    @SerialName("translated_text")
    val translatedText: List<String>
)