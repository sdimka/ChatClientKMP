package dev.goood.chat_client.model

import kotlinx.serialization.Serializable

@Serializable
data class TestResponse(
    val data: Data? = null,
    val status: Int
)

@Serializable
data class Data(
    val message: String? = null
)