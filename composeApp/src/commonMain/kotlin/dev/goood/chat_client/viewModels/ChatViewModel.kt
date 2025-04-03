package dev.goood.chat_client.viewModels

import androidx.lifecycle.ViewModel
import dev.goood.chat_client.model.MessageList
import kotlinx.coroutines.flow.StateFlow

abstract class ChatViewModel: ViewModel() {

    abstract val state: StateFlow<State>
    abstract val messages: StateFlow<MessageList>
    abstract fun getMessages(chatId: Int)
    abstract fun sendMessage(inputValue: String)

    sealed interface State {
        data object Success: State
        data class Error(val message: String): State
        data object Loading: State

    }
}