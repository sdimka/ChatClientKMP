package dev.goood.chat_client.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.goood.chat_client.model.User
import dev.goood.chat_client.services.AuthService
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class LoginViewModel: ViewModel(), KoinComponent {

    private val service: AuthService by inject()

    fun getString(): String {
        return "String from ViewModel"
    }

    fun auth(user: User) {
        viewModelScope.launch {
            service.login(user)
                .catch { e ->
                    print(e)
                }
                .collect {
                    print(it)
                }
        }
    }
}