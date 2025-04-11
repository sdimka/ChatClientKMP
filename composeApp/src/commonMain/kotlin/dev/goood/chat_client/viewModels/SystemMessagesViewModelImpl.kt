package dev.goood.chat_client.viewModels

import androidx.lifecycle.viewModelScope
import dev.goood.chat_client.core.network.Api
import dev.goood.chat_client.model.SystemMessageList
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

class SystemMessagesViewModelImpl: SystemMessagesViewModel(), KoinComponent {

    private val api: Api by inject()

    private val _state = MutableStateFlow<State>(State.Loading)
    override val state: StateFlow<State> = _state
        .onStart { getSysMessages() }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            State.Loading
        )

    private val _messages = MutableStateFlow<SystemMessageList>(emptyList())
    override val messages = _messages.asStateFlow()

    private fun getSysMessages(){
        viewModelScope.launch {
            _state.value = State.Loading
            api.chatApi.getSystemMessages()
                .catch {
                    _state.value = State.Error(it.message ?: "Unknown error")
                }
                .collect{
                    _messages.value = it
                    _state.value = State.Success
                }
        }
    }
}