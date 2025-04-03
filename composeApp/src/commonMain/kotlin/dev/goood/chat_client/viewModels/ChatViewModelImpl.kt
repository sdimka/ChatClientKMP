package dev.goood.chat_client.viewModels

import androidx.lifecycle.viewModelScope
import dev.goood.chat_client.core.network.Api
import dev.goood.chat_client.model.MessageList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ChatViewModelImpl: ChatViewModel(), KoinComponent {

    private val api: Api by inject()

    private val _messages = MutableStateFlow<MessageList>(emptyList())
    override val messages: StateFlow<MessageList> = _messages

    private val _state = MutableStateFlow<State>(State.Loading)
    override val state: StateFlow<State> = _state

    override fun getMessages(chatId: Int){
        _state.value = State.Loading
        viewModelScope.launch {
            api.chatApi.getMessages(chatId)
                .catch {

                    _state.value = State.Error(it.message ?: "Unknown error")
                }
                .collect {
                    _messages.value = it
                    _state.value = State.Success
                }
        }
    }

    override fun sendMessage(inputValue: String) {
        println(inputValue)
    }


}