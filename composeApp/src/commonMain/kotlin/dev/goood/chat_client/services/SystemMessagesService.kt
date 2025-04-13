package dev.goood.chat_client.services

import dev.goood.chat_client.core.network.Api
import dev.goood.chat_client.model.SystemMessage
import dev.goood.chat_client.model.SystemMessageList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SystemMessagesService: KoinComponent {

    val api: Api by inject()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private val _state: MutableStateFlow<State> = MutableStateFlow(State.Loading)
    val state: StateFlow<State> = _state.asStateFlow()

    private val _messages = MutableStateFlow<SystemMessageList>(emptyList())
    val messages: StateFlow<SystemMessageList> = _messages
        .onStart {
            updateMessages()
        }
        .stateIn(
            scope,
            SharingStarted.WhileSubscribed(5000L),
            emptyList()
        )

    private val _selectedMessage: MutableStateFlow<SystemMessage?> = MutableStateFlow(null)
    val selectedMessage = _selectedMessage.asStateFlow()

    private fun updateMessages(){
        scope.launch {
            _state.value = State.Loading
            api.chatApi.getSystemMessages()
                .catch {
                    print(it.message)
                    _state.value = State.Error(message = it.message ?: "Unknown error")
                }
                .collect {
                   _messages.value = it
                   _state.value = State.Success
                }
        }
    }

    fun setCurrentMessage(messID: Int) {
        _selectedMessage.value = messages.value.firstOrNull { it.id == messID }
    }

    sealed interface State {
        data object Success: State
        data class Error(val message: String): State
        data object Loading: State
    }
}