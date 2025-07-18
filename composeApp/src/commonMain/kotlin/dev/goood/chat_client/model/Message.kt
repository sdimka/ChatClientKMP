package dev.goood.chat_client.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class Message (
    val id: Int,
    val content: String,
    val initiator: Int,
    val role: String,
    @SerialName("system_message")
    val systemMessage: SystemMessage? = null,
    val files: List<AttachedFiles>? = null,
    @SerialName("updated_at")
    val updatedAt: String,
    @SerialName("deleted_at")
    val deletedAt: String? = null,
    val isSelected: Boolean = false
)

typealias MessageList = List<Message>


@Serializable
data class MessageRequest (
    val content: String,
    @SerialName("chat_id")
    val chatId: Int,
    @SerialName("system_message")
    val systemMessage: SystemMessage? = null,
    @SerialName("attached_files")
    val attachedFiles: List<AttachedFiles>? = null,
    @SerialName("appended_messages")
    val attachedMessages: List<Int>? = emptyList()
)

@Serializable
data class AttachedFiles (
    val name: String
)