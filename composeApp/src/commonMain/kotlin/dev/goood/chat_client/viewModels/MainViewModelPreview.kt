package dev.goood.chat_client.viewModels


import dev.goood.chat_client.model.Chat
import dev.goood.chat_client.model.ChatList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MainViewModelPreview: MainViewModel() {

    override val state: StateFlow<State> = MutableStateFlow(State.Success)

    override val chats: MutableStateFlow<ChatList>
    = MutableStateFlow(
        listOf(
            Chat(
                1, "Model", "Chat1", source = "Source"),
            Chat(2,"Model", "Chat1", source = "Source"),
            Chat(3,"Model", "Chat1", source = "Source"),

        )
    )
    override val addChatDialogState: MutableStateFlow<Boolean> = MutableStateFlow(false)
    override val deleteChatDialogState: MutableStateFlow<Chat?> = MutableStateFlow(Chat(1,"","Some name",""))

    override fun getChats() {

    }

    override fun saveNewChat(chat: Chat) {

    }

    override fun deleteChat(chat: Chat) {

    }
}