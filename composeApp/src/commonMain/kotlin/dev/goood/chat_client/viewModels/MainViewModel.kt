package dev.goood.chat_client.viewModels

import androidx.lifecycle.ViewModel
import dev.goood.chat_client.model.Chat
import dev.goood.chat_client.model.ChatList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

abstract class MainViewModel: ViewModel() {

    abstract val state: StateFlow<State>
    abstract val chats: MutableStateFlow<ChatList>

    abstract val addChatDialogState: MutableStateFlow<Boolean>
    abstract val deleteChatDialogState: MutableStateFlow<Chat?>


    abstract fun getChats()
    abstract fun saveNewChat(chat: Chat)
    abstract fun deleteChat(chat: Chat)

    sealed interface State {
        data object Success: State
        data class Error(val message: String): State
        data object Loading: State

    }
}