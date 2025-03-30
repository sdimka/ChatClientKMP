package dev.goood.chat_client.services

import dev.goood.chat_client.model.TokenReply
import dev.goood.chat_client.model.User
import kotlinx.coroutines.flow.Flow

interface AuthService {

    fun getUser(): User?

    fun setUser(user: User)

    fun getBearerToken() : String?

    fun setBearerToken(token: String)

    fun login(user: User): Flow<TokenReply>

    fun logout()

    fun isAuthorized(): Boolean
}