package dev.goood.chat_client.core.other

enum class MimeType {
    PDF,
    TEXT,
    IMAGE,
}

class ShareFileModel(
    val fileName: String,
    val mime: MimeType = MimeType.PDF,
    val bytes: ByteArray
)
