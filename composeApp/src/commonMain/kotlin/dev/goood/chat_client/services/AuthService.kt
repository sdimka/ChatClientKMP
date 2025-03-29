package dev.goood.chat_client.services

import dev.goood.chat_client.model.TokenReply
import dev.goood.chat_client.model.User
import kotlinx.coroutines.flow.Flow

interface AuthService {

    fun login(user: User): Flow<TokenReply>
}