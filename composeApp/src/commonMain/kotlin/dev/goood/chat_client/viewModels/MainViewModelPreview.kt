package dev.goood.chat_client.viewModels


import dev.goood.chat_client.model.Chat
import dev.goood.chat_client.model.ChatList
import dev.goood.chat_client.model.ChatModel
import dev.goood.chat_client.model.ChatSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MainViewModelPreview: MainViewModel() {

    override val state: StateFlow<State> = MutableStateFlow(State.Success)
    val chatSource = ChatSource(1, "Chat1")
    val chatSource2 = ChatSource(4, "Chat1")
    val chatModel = ChatModel(1, "Model", "Model", "Model")

    override val chats: MutableStateFlow<ChatList>
    = MutableStateFlow(
        listOf(
            Chat(
                1, "Chat1", source = chatSource, model = chatModel),
            Chat(2,"Chat2", source = chatSource2, model = chatModel),
            Chat(3,"Chat3", source = chatSource, model = chatModel),

        )
    )
    override val addChatDialogState: MutableStateFlow<Boolean> = MutableStateFlow(false)
    override val deleteChatDialogState: MutableStateFlow<Chat?> =
        MutableStateFlow(null) //Chat(1,"","Some name",""))

    override fun getChats() {

    }

    override fun onNewChatSaved() {

    }

    override fun deleteChat(chat: Chat) {

    }
}