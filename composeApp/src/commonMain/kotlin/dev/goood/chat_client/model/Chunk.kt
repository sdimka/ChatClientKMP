package dev.goood.chat_client.model

import kotlinx.serialization.Serializable

@Serializable
data class Chunk (
    val message: String,
    val index: Int,
)