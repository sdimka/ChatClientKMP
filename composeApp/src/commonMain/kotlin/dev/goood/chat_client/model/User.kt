package dev.goood.chat_client.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class User (
    @SerialName("email")
    val email: String,
    @SerialName("password")
    val password: String,
    )

@Serializable
data class TokenReply (
    @SerialName("data")
    val token: String
)