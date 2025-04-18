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
    val attachedFiles: List<String>? = null
)