package dev.goood.chat_client.viewModels

import dev.goood.chat_client.core.network.toUserMessage

import androidx.lifecycle.viewModelScope
import dev.goood.chat_client.core.network.Api
import dev.goood.chat_client.model.Chat
import dev.goood.chat_client.model.ChatIdRequest
import dev.goood.chat_client.model.ChatList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MainViewModelImpl: MainViewModel(), KoinComponent {

    private val api: Api by inject()

    private var _state = MutableStateFlow<State>(State.Loading)
    override val state: StateFlow<State> = _state
        .onStart { getChats() }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            State.Loading
        )

    override val addChatDialogState: MutableStateFlow<Boolean> = MutableStateFlow(false)
    override val deleteChatDialogState: MutableStateFlow<Chat?> = MutableStateFlow(null)

    override val chats = MutableStateFlow<ChatList>(emptyList())

    override fun getChats() {
        viewModelScope.launch {
            _state.value = State.Loading
            api.chatApi.getChats()
                .catch {
                    println(it)
                    _state.value = State.Error(it.toUserMessage())
                }
                .collect { chatList ->
                    chats.value = chatList.sortedByDescending { it.source.id }
                    _state.value = State.Success
                }
        }
    }

    override fun onNewChatSaved() {
        getChats()
        addChatDialogState.value = false
    }

    override fun deleteChat(chat: Chat) {
        viewModelScope.launch {
            api.chatApi.deleteChat(ChatIdRequest(chat.id))
                .catch {
                    _state.value = State.Error(it.toUserMessage())
                }
                .collect { result ->
                    println(result)
                    getChats()
                }
        }
    }
}