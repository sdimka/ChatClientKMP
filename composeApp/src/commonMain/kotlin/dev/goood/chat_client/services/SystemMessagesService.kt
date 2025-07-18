package dev.goood.chat_client.services

import dev.goood.chat_client.cache.DatabaseDriverFactory
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

class SystemMessagesService(
    private val api: Api,
    databaseDriverFactory: DatabaseDriverFactory,
): KoinComponent {

//    val api: Api by inject()
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


    private fun updateMessages(){
        scope.launch {
            _state.value = State.Loading
            api.chatApi.getSystemMessages()
                .catch {
                    _state.value = State.Error(message = it.message ?: "Unknown error")
                }
                .collect {
                   _messages.value = it
                   _state.value = State.Success
                }
        }
    }

    fun getCurrentMessage(messID: Int): SystemMessage? {
        return messages.value.firstOrNull { it.id == messID }
    }

    fun updateMessage(message: SystemMessage) {
        scope.launch {
            _state.value = State.Loading
            api.chatApi.updateSystemMessage(message)
                .catch {
                    print(it.message)
                    _state.value = State.Error(message = it.message ?: "Unknown error")
                }
                .collect {
                    _state.value = State.Success
                    updateMessages()
                }
        }
    }

    fun createMessage(message: SystemMessage, param: (SystemMessage) -> Unit) {
        scope.launch {
            _state.value = State.Loading
            api.chatApi.createSystemMessage(message)
                .catch {
                    print(it.message)
                    _state.value = State.Error(message = it.message ?: "Unknown error")
                }
                .collect { savedMessage ->
                    param(savedMessage)
                    updateMessages()
                }
        }
    }

    fun deleteMessage(messageID: Int) {
        scope.launch {
            _state.value = State.Loading
            api.chatApi.deleteSystemMessage(messageID)
                .catch {
                    print(it.message)
                    _state.value = State.Error(message = it.message ?: "Unknown error")
                }
                .collect{
                    updateMessages()
                }
        }
    }

    sealed interface State {
        data object Success: State
        data class Error(val message: String): State
        data object Loading: State
    }
}