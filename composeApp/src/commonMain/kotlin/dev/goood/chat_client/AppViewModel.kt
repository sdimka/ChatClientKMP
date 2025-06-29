package dev.goood.chat_client

import androidx.lifecycle.ViewModel
import dev.goood.chat_client.model.Chat
import dev.goood.chat_client.services.AuthService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AppViewModel: ViewModel(), KoinComponent {

    private val authService: AuthService by inject()
    val authState : MutableStateFlow<AuthState> = MutableStateFlow(authState())

    private fun authState(): AuthState {
        return if (authService.isAuthorized()) {
                AuthState.Authorized(authService.getBearerToken()!!)
            } else {
                AuthState.Unauthorized("You are not authorized")
            }
    }

    private val _selectedChat: MutableStateFlow<Chat?> = MutableStateFlow(null)
    val selectedChat: StateFlow<Chat?> = _selectedChat

    fun setCurrentChat(chat: Chat?) {
        _selectedChat.value = chat
    }

    fun logout() {
        authService.logout()
        authState.value = authState()

    }

    sealed interface AuthState {
        data class Authorized(val token: String): AuthState
        data class Unauthorized(val message: String): AuthState
    }
}