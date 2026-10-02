package dev.goood.chat_client.viewModels

import dev.goood.chat_client.core.network.toUserMessage
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.goood.chat_client.model.User
import dev.goood.chat_client.services.AuthService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class LoginViewModel: ViewModel(), KoinComponent {

    private val authService: AuthService by inject()

    val state = MutableStateFlow<LoginState?>(null)

    fun getString(): String {
        return "String from ViewModel"
    }

    fun auth(user: User) {
        viewModelScope.launch {
            state.value = LoginState.Loading
            authService.login(user)
                .catch { e ->
                    state.value = LoginState.Error(e.toUserMessage())
                }
                .collect {
                    val token = it.token
                    if (token == null) {
                        state.value = LoginState.Error("Login failed: the server returned no token.")
                        return@collect
                    }
                    authService.setBearerToken(token)
                    authService.setUser(user)
                    state.value = LoginState.Success(token)
                }
        }
    }

    sealed interface LoginState {
        data class Success(val token: String): LoginState
        data class Error(val message: String): LoginState
        data object Loading: LoginState
    }
}