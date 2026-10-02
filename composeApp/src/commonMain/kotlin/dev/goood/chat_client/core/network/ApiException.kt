package dev.goood.chat_client.core.network

import dev.goood.chat_client.Const
import io.ktor.client.plugins.sse.SSEClientException
import kotlinx.io.IOException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

/** Non-2xx API response. [message] is already suitable for showing to the user. */
class ApiException(
    val status: Int,
    val serverMessage: String?,
) : Exception(userMessageFor(status, serverMessage))

private val errorMessageKeys = listOf("message", "error", "details", "detail", "description")

/** Extracts a human-readable message from an error body, ignoring HTML error pages and huge payloads. */
internal fun parseServerMessage(body: String): String? {
    val trimmed = body.trim()
    if (trimmed.isEmpty() || trimmed.startsWith("<")) return null

    val json = runCatching { Json.parseToJsonElement(trimmed) }.getOrNull()
    if (json is JsonObject) {
        return errorMessageKeys.firstNotNullOfOrNull { key ->
            (json[key] as? JsonPrimitive)?.takeIf { it.isString }?.content?.takeIf { it.isNotBlank() }
        }
    }
    if (json != null) return null

    return trimmed.takeIf { it.length <= 200 }
}

internal fun userMessageFor(status: Int, serverMessage: String?): String = when (status) {
    400 -> serverMessage ?: "The server rejected the request."
    401 -> "Your session has expired. Please log in again."
    403 -> "You don't have access to this."
    404 -> serverMessage ?: "The requested item no longer exists."
    409 -> serverMessage ?: "This item already exists."
    413 -> "The file is too large. The maximum size is ${Const.Network.MAX_UPLOAD_MB} MB."
    415 -> serverMessage ?: "This file type isn't supported by the chat's provider."
    502, 503, 504 -> "The AI provider is unavailable right now. Please try again later."
    in 500..599 -> "Something went wrong on the server. Please try again."
    else -> serverMessage ?: "Request failed (HTTP $status)."
}

/** Readable message for any error coming out of the network layer. */
fun Throwable.toUserMessage(): String = when (this) {
    is ApiException -> message!!
    is SSEClientException -> response?.status?.value?.let { userMessageFor(it, null) }
        ?: "The connection to the server was interrupted."
    is IOException -> "Can't reach the server. Check your connection and try again."
    else -> message ?: "Unknown error"
}
