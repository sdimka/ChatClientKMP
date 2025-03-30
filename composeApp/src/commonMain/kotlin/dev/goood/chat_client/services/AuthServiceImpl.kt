package dev.goood.chat_client.services

import dev.goood.chat_client.core.network.Api
import dev.goood.chat_client.model.TokenReply
import dev.goood.chat_client.model.User
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AuthServiceImpl: AuthService, KoinComponent {

    private val api: Api by inject()
    private val localStorage: LocalStorage by inject()

    override fun login(user: User): Flow<TokenReply> {
        return api.authApi.login(user)
    }

    override fun logout() {

    }

    override fun isAuthorized(): Boolean {
        return localStorage.user != null
    }

    override fun getUser(): User? {
        return localStorage.user
    }

    override fun setUser(user: User) {
        localStorage.user = user
    }

    override fun getBearerToken(): String? {
        return localStorage.bearerToken
    }

    override fun setBearerToken(token: String) {
        localStorage.bearerToken = token
    }
}