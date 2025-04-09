package dev.goood.chat_client.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class Chat (
    val id: Int,
    val name: String,
    val source: ChatSource,
    val model: ChatModel,
)

typealias ChatList = List<Chat>

@Serializable
data class NewChat (
    val id: Int,
    val name: String,
    @SerialName("source_id")
    val sourceID: Int,
    @SerialName("model_id")
    val modelID: Int,
)

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
    val description: String,
    @SerialName("source_id")
    val sourceID: Int
)

typealias ChatModelList = List<ChatModel>