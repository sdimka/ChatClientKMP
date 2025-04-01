package dev.goood.chat_client.model

import kotlinx.serialization.Serializable


@Serializable
data class Chat (
    val id: Int,
    val model: String,
    val name: String,
    val source: String
)

typealias ChatList = List<Chat>