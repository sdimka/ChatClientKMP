package dev.goood.chat_client.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Chunk (
    val data: String,
    val index: Int,
)
/** Payload of the `error` SSE event from /api/streamMessage. */
@Serializable
data class StreamError(
    val error: String? = null,
    val details: String? = null,
    @SerialName("partial_response")
    val partialResponse: String? = null,
) {
    fun userMessage(): String = listOfNotNull(
        error?.takeIf { it.isNotBlank() },
        details?.takeIf { it.isNotBlank() },
    ).joinToString(": ").ifEmpty { "The reply could not be completed." }
}
