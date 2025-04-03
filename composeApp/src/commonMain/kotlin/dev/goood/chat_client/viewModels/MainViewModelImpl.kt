package dev.goood.chat_client.viewModels

import androidx.lifecycle.viewModelScope
import dev.goood.chat_client.core.network.Api
import dev.goood.chat_client.model.Chat
import dev.goood.chat_client.model.ChatList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MainViewModelImpl: MainViewModel(), KoinComponent {

    private val api: Api by inject()

    private var _state = MutableStateFlow<State>(State.Loading)
    override val state: StateFlow<State> = _state

    override val addChatDialogState: MutableStateFlow<Boolean> = MutableStateFlow(false)
    override val deleteChatDialogState: MutableStateFlow<Chat?> = MutableStateFlow(null)

    override val chats = MutableStateFlow<ChatList>(emptyList())
//    val reqString = MutableStateFlow("")

    fun getData(): String {
        return "Data from ViewModel"
    }

    fun getApiData() {
        viewModelScope.launch {
            api.testApi.getTestData()
                .collect {
                    print(it)
                }
        }
    }

    fun getApiList() {
        viewModelScope.launch {
            api.testApi.getTestDataList()
                .catch {
                    print(it.message)
                }
                .collect {
                    print(it)
                }
        }
    }

//    fun getStreamString() {
//        reqString.value = ""
//        viewModelScope.launch {
//            api.streamApi.myOtherDataStream()
//                .collect { res ->
//                    reqString.value += res.message
//                }
//        }
//    }

    override fun getChats() {
        viewModelScope.launch {
            _state.value = State.Loading
            api.chatApi.getChats()
                .catch {
                    println(it)
                }
                .collect {
                    chats.value = it
                    _state.value = State.Success
                    println("Chat collected")
                }
        }
    }

    override fun onNewChatSaved() {
        getChats()
        addChatDialogState.value = false
    }

    override fun deleteChat(chat: Chat) {
        viewModelScope.launch {
            api.chatApi.deleteChat(chat)
                .catch {
                    println(it)
                }
                .collect { result ->
                    println(result)
                    getChats()
                }
        }
    }
}