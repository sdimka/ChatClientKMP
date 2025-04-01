package dev.goood.chat_client.model

import kotlinx.serialization.Serializable

@Serializable
data class ResultMessage (
    val status: Int,
    val message: String
)