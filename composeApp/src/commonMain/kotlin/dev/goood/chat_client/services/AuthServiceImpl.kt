package dev.goood.chat_client.services

import dev.goood.chat_client.core.network.Api
import dev.goood.chat_client.model.TokenReply
import dev.goood.chat_client.model.User
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AuthServiceImpl: AuthService, KoinComponent {

    val api: Api by inject()

    override fun login(user: User): Flow<TokenReply> {
        return api.authApi.login(user)
    }


}