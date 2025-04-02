package dev.goood.chat_client.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class Chat (
    val id: Int,
    val model: String,
    val name: String,
    val source: String
)

typealias ChatList = List<Chat>

@Serializable
data class ChatSource (
    val id: Int,
    val name: String
)

typealias ChatSourceList = List<ChatSource>

@Serializable
data class ChatModel (
    val id: Int,
    val name: String,
    @SerialName("display_name")
    val displayName: String,
    val description: String
)

typealias ChatModelList = List<ChatModel>