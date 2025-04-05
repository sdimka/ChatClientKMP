package dev.goood.chat_client.model

import kotlinx.serialization.Serializable

@Serializable
data class Chunk (
    val data: String,
    val index: Int,
)