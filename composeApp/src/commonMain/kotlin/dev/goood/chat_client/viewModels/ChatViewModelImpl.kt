package dev.goood.chat_client.viewModels

import androidx.lifecycle.viewModelScope
import dev.goood.chat_client.core.network.Api
import dev.goood.chat_client.core.network.ReplyVariants
import dev.goood.chat_client.model.MessageList
import dev.goood.chat_client.model.MessageRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ChatViewModelImpl: ChatViewModel(), KoinComponent {

    private val api: Api by inject()

    private val _messages = MutableStateFlow<MessageList>(emptyList())
    override val messages: StateFlow<MessageList> = _messages

    private val _state = MutableStateFlow<State>(State.Loading)
    override val state: StateFlow<State> = _state

    private val _newReply = MutableStateFlow<String>("")
    override val newReply: StateFlow<String> = _newReply

    private var currentChatId = 1

    override fun getMessages(chatId: Int){
        _state.value = State.Loading
        currentChatId = chatId
        viewModelScope.launch {
            api.chatApi.getMessages(chatId)
                .catch {

                    _state.value = State.Error(it.message ?: "Unknown error")
                }
                .collect { list ->
                    _messages.value = list.sortedByDescending { it.id }
                    _state.value = State.Success
                }
        }
    }

    override fun sendMessage(messageText: String) {

        val message = MessageRequest(
            content = messageText,
            chatId = currentChatId
        )

        _newReply.value = ""
        _state.value = State.NewReply


        viewModelScope.launch {
            api.streamApi.streamRequestWithType(message)
                .onCompletion {
                    _state.value = State.Success
                }
                .catch {
                    println(it.message)
                    _state.value = State.Error(it.message ?: "Unknown error")
                }
                .collect { event ->
                    when (event) {
                        is ReplyVariants.SavedRequest -> {
                            val newList = (_messages.value + event.content).sortedByDescending { it.id }
                            _messages.value = newList
                        }

                        is ReplyVariants.Chunks -> {
                            _newReply.value += event.content.data
                        }
                        is ReplyVariants.FinalReply -> {
                            val newList = (_messages.value + event.content).sortedByDescending { it.id }
                            _messages.value = newList
                        }
                        is ReplyVariants.Error -> {

                        }
                    }

                }

        }
    }


}