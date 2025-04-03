package dev.goood.chat_client.model

import kotlinx.serialization.Serializable


@Serializable
data class Message (
    val id: Int,
    val content: String,
    val initiator: Int,
    val role: String,
)

typealias MessageList = List<Message>