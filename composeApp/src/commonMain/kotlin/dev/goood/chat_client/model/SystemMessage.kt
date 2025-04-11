package dev.goood.chat_client.model

import kotlinx.serialization.Serializable

@Serializable
data class SystemMessage (
    val id: Int,
    val title: String,
    val content: String,
)

typealias SystemMessageList = List<SystemMessage>