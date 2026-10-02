package dev.goood.chat_client.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Normalized toward OpenAI-style file metadata. Providers may leave any field except `id` empty,
// so everything else is optional.
@Serializable
data class MFile (
    val id: String,
    @SerialName("object")
    val obj: String? = null,
    val bytes: Long? = null,
    @SerialName("created_at")
    val createdAt: Long? = null,
    val filename: String? = null,
    val purpose: String? = null,
    val status: String? = null,
) {
    val displayName: String get() = filename?.takeIf { it.isNotBlank() } ?: id

    /** True while the provider is still processing the file (unknown statuses count as ready). */
    val isProcessing: Boolean
        get() = status?.lowercase() in setOf("processing", "pending", "uploaded", "in_progress")
}

typealias FileList = List<MFile>
