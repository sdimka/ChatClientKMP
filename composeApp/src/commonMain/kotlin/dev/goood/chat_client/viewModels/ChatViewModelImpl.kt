package dev.goood.chat_client.viewModels

import androidx.lifecycle.viewModelScope
import dev.goood.chat_client.core.network.Api
import dev.goood.chat_client.core.network.ReplyVariants
import dev.goood.chat_client.model.AttachedFiles
import dev.goood.chat_client.model.MFile
import dev.goood.chat_client.model.Message
import dev.goood.chat_client.model.MessageList
import dev.goood.chat_client.model.MessageRequest
import dev.goood.chat_client.model.SystemMessage
import dev.goood.chat_client.model.SystemMessageList
import dev.goood.chat_client.services.SystemMessagesService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ChatViewModelImpl: ChatViewModel(), KoinComponent {

    private val api: Api by inject()
    private val systemMessagesService: SystemMessagesService by inject()

    private val _messages = MutableStateFlow<MessageList>(emptyList())
    override val messages: StateFlow<MessageList> = _messages

    private val _state = MutableStateFlow<State>(State.Loading)
    override val state: StateFlow<State> = _state

    private val _newReply = MutableStateFlow<String>("")
    override val newReply: StateFlow<String> = _newReply

    private var currentChatId = 1

    override val systemMessages: StateFlow<SystemMessageList> = systemMessagesService.messages

    private val _selectedSysMessage = MutableStateFlow<SystemMessage?>(null)
    override val selectedSysMessage: StateFlow<SystemMessage?> = _selectedSysMessage.asStateFlow()

    private val _filesList = MutableStateFlow<List<MFile>>(emptyList())
    override val filesList: StateFlow<List<MFile>> = _filesList

    private val attachedMessages = mutableListOf<Int>()

    private val _inputValue = MutableStateFlow("")
    override val inputValue: StateFlow<String> = _inputValue.asStateFlow()

    private val _isPreviousMessagesEnabled = MutableStateFlow(false)
    override val isPreviousMessagesEnabled: StateFlow<Boolean> = _isPreviousMessagesEnabled.asStateFlow()

    override fun updateInputValue(newValue: String) {
        _inputValue.value = newValue
    }

    override fun clearInputValue() {
        _inputValue.value = ""
    }

    override fun omPreviousMessagesEnabledChanged(checked: Boolean) {
        _isPreviousMessagesEnabled.value = !_isPreviousMessagesEnabled.value
    }

    override fun selectSysMessage(sysMessage: SystemMessage?) {
        _selectedSysMessage.value = sysMessage
    }

    override fun onSelectedMessagesListUpdate(messageID: Int) {
        if (attachedMessages.contains(messageID)) {
            attachedMessages.remove(messageID)
        } else {
            attachedMessages.add(messageID)
        }
    }

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

    override fun deleteMessage(message: Message) {
        _state.value = State.Loading
        viewModelScope.launch {
            api.chatApi.deleteMessage(message.id)
                .catch {
                    _state.value = State.Error(it.message ?: "Unknown error")
                }
                .collect {
                    getMessages(currentChatId)
                }
        }
    }

    override fun updateFileList(file: MFile, operation: (List<MFile>, MFile) -> List<MFile>) {
        _filesList.update { currentList -> operation(currentList, file) }
    }

    override fun sendMessage(messageText: String) {

        var message = MessageRequest(
            content = messageText,
            chatId = currentChatId
        )
        if (_selectedSysMessage.value != null) {
            message = message.copy(systemMessage =  _selectedSysMessage.value!!)
        }

        if (_filesList.value.isNotEmpty()) {
            message = message.copy(attachedFiles = _filesList.value.map { AttachedFiles(it.id) })
        }

        if (_isPreviousMessagesEnabled.value) {
            message = message.copy(attachedMessages = attachedMessages)
        }

        _newReply.value = ""
        _state.value = State.NewReply

        clearInputValue()

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
                            val newList = (_messages.value + event.content)
                                .sortedByDescending { it.id }
                            _messages.value = newList
                        }

                        is ReplyVariants.Chunks -> {
                            _newReply.value += event.content.data
                        }
                        is ReplyVariants.FinalReply -> {
                            val newList = (_messages.value + event.content)
                                .sortedByDescending { it.id }
                            _messages.value = newList
                        }
                        is ReplyVariants.Error -> {

                        }
                    }

                }

        }
    }

    override fun onCleared() {
        super.onCleared()
        // clearInputValue()
    }

}