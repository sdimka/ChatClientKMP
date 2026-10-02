package dev.goood.chat_client.core.other

import dev.goood.chat_client.Const

// Accepted upload extensions per provider, as documented for POST /api/file.
private val geminiExtensions = setOf(
    "pdf", "txt", "md", "csv", "html", "xml", "rtf", "png", "jpg", "jpeg", "webp", "heic", "heif",
    "mp3", "wav", "ogg", "flac", "mp4", "mov", "webm",
)
private val openAiExtensions = setOf("pdf", "png", "jpg", "jpeg", "webp", "gif")
private val mockExtensions = setOf("pdf", "txt", "md", "csv", "png", "jpg", "jpeg", "webp", "gif")

internal const val MAX_UPLOAD_BYTES = Const.Network.MAX_UPLOAD_MB * 1024L * 1024L

/**
 * Extensions the chat's provider accepts, guessed from the source name.
 * Returns an empty set when the provider has no file support, and null when the provider is
 * unknown — then nothing is filtered and the server's validation decides.
 */
internal fun acceptedUploadExtensions(sourceName: String): Set<String>? {
    val name = sourceName.lowercase()
    return when {
        "assistant" in name -> emptySet()
        "gemini" in name -> geminiExtensions
        "openai" in name || "gpt" in name -> openAiExtensions
        "mock" in name || "test" in name -> mockExtensions
        else -> null
    }
}

/** Why this file can't be uploaded, or null when it looks fine. */
internal fun uploadValidationError(
    fileName: String,
    sizeBytes: Long,
    providerName: String?,
    acceptedExtensions: Set<String>?,
): String? {
    val provider = providerName ?: "this provider"
    if (acceptedExtensions != null && acceptedExtensions.isEmpty()) {
        return "File attachments aren't supported by $provider."
    }

    val extension = fileName.substringAfterLast('.', "").lowercase()
    if (acceptedExtensions != null && extension !in acceptedExtensions) {
        val shown = if (extension.isEmpty()) "Files without an extension aren't" else "“.$extension” files aren't"
        return "$shown supported by $provider. Supported: ${acceptedExtensions.sorted().joinToString(", ")}."
    }

    if (sizeBytes > MAX_UPLOAD_BYTES) {
        val sizeMb = ((sizeBytes * 10) / (1024 * 1024)) / 10.0
        return "“$fileName” is $sizeMb MB. Files up to ${Const.Network.MAX_UPLOAD_MB} MB can be uploaded."
    }
    if (sizeBytes == 0L) return "“$fileName” is empty."
    return null
}
