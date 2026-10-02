package dev.goood.chat_client.viewModels

import dev.goood.chat_client.core.network.toUserMessage

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dev.goood.chat_client.cache.Database
import dev.goood.chat_client.cache.DatabaseDriverFactory
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Instant
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class ChatViewModelImpl(
    private val handle: SavedStateHandle,
    private val api: Api,
    databaseDriverFactory: DatabaseDriverFactory,
) : ChatViewModel(), KoinComponent {

    private val systemMessagesService: SystemMessagesService by inject()
    private val database = Database(databaseDriverFactory)

    private val _messages = MutableStateFlow<MessageList>(emptyList())
    override val messages: StateFlow<MessageList> = _messages

    private val _state = MutableStateFlow<State>(State.Loading)
    override val state: StateFlow<State> = _state

    private val _newReply = MutableStateFlow<String>("")
    override val newReply: StateFlow<String> = _newReply

    private var currentChatId: Int
        get() = handle["chatId"] ?: -1
        set(value) { handle["chatId"] = value }

    override val systemMessages: StateFlow<SystemMessageList> = systemMessagesService.messages

    private val _selectedSysMessage = MutableStateFlow<SystemMessage?>(null)
    override val selectedSysMessage: StateFlow<SystemMessage?> = _selectedSysMessage.asStateFlow()

    private val _filesList = MutableStateFlow<List<MFile>>(emptyList())
    override val filesList: StateFlow<List<MFile>> = _filesList

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

    override fun onPreviousMessagesEnabledChanged(checked: Boolean) {
        _isPreviousMessagesEnabled.value = checked
    }

    override fun selectSysMessage(sysMessage: SystemMessage?) {
        _selectedSysMessage.value = sysMessage
    }

    override fun onSelectedMessagesListUpdate(messageID: Int) {
        _messages.update { currentList ->
            currentList.map { message ->
                if (message.id == messageID) {
                    message.copy(isSelected = !message.isSelected) // Toggle selection
                } else {
                    message
                }
            }
        }
    }

    @OptIn(ExperimentalTime::class)
    override fun getMessages(chatId: Int){
        _state.value = State.Loading
        currentChatId = chatId
        if (currentChatId == -1) {
            _state.value = State.Success
            _messages.value = emptyList()
            return
        }

        viewModelScope.launch {

            try {
                // For restore selected messages
                val currentSelectedIds = _messages.value
                    .filter { it.isSelected }
                    .map { it.id }
                    .toSet()

                val dbMessages = database.getMessages(chatId).first()
                    .map { msg -> msg.copy(isSelected = currentSelectedIds.contains(msg.id)) }
                    .sortedByDescending { it.id } // Ensure initial sort

                _messages.value = dbMessages

            } catch (e: Exception) {
                println("Error loading initial messages from DB: ${e.printStackTrace()}")
                _state.value = State.Error(e.toUserMessage())
            }

            // --- Stage 2: Fetch from API & Update Database ---
            try {
                val timeStamp = database.getLastUpdateTime(chatId)?.let {
                    Instant.fromEpochMilliseconds(it).toString()
                }

                val apiMessageList = api.chatApi.getNewMessages(chatId, timeStamp ?: "").first()

                if (apiMessageList.isNotEmpty()) {
                    database.updateMessages(chatId, apiMessageList)
                }

                // --- Stage 3: Reload from Database as Single Source of Truth for UI ---
                val currentSelectedIds =
                    _messages.value // Potentially updated selections if user interacted
                        .filter { it.isSelected }
                        .map { it.id }
                        .toSet()

                val finalMessagesFromDb = database.getMessages(chatId).first()
                    .map { msg -> msg.copy(isSelected = currentSelectedIds.contains(msg.id)) }
                    .sortedByDescending { it.id } // Ensure final sort

                _messages.value = finalMessagesFromDb
                _state.value = State.Success

            } catch (e: Exception) {
                println("Error fetching/processing API messages: ${e.printStackTrace()}")
                _state.value = State.Error(e.toUserMessage())
            }
        }
    }

    override fun deleteMessage(message: Message) {
        _state.value = State.Loading
        viewModelScope.launch {
            api.chatApi.deleteMessage(message.id)
                .catch {
                    _state.value = State.Error(it.toUserMessage())
                }
                .collect {
                    database.deleteMessage(message.id)
                    _messages.update { currentList ->
                        currentList.filter { it.id != message.id }
                    }
                    _state.value = State.Success
                }
        }
    }

    override fun updateFileList(file: MFile, operation: (List<MFile>, MFile) -> List<MFile>) {
        _filesList.update { currentList -> operation(currentList, file) }
    }

    private val _failedReply = MutableStateFlow<FailedReply?>(null)
    override val failedReply: StateFlow<FailedReply?> = _failedReply.asStateFlow()

    private var lastRequest: MessageRequest? = null

    override fun sendMessage(messageText: String) {

        var message = MessageRequest(
            content = messageText,
            chatId = currentChatId
        )
        if (_selectedSysMessage.value != null) {
            message = message.copy(systemMessage =  _selectedSysMessage.value!!)
        }

        if (_filesList.value.isNotEmpty()) {
            message = message.copy(attachedFiles =
                _filesList.value.map { AttachedFiles(it.id) })
        }

        if (_isPreviousMessagesEnabled.value) {
            message = message.copy(attachedMessages =
                _messages.value.filter { it.isSelected }.map { it.id })
        }

        clearInputValue()
        streamReply(message)
    }

    override fun retryFailedReply() {
        lastRequest?.let(::streamReply)
    }

    override fun dismissFailedReply() {
        _failedReply.value = null
    }

    private fun streamReply(message: MessageRequest) {
        lastRequest = message
        _failedReply.value = null
        _newReply.value = ""
        _state.value = State.NewReply

        fun fail(error: String, partialText: String = _newReply.value) {
            _failedReply.value = FailedReply(partialText = partialText, error = error)
            _state.value = State.Success
        }

        viewModelScope.launch {
            api.streamApi.streamRequestWithType(message)
                .onCompletion {
                    if (_state.value is State.NewReply) _state.value = State.Success
                }
                .catch {
                    println(it.message)
                    fail(it.toUserMessage())
                }
                .collect { event ->
                    when (event) {
                        is ReplyVariants.SavedRequest -> {
                            database.updateMessages(currentChatId, listOf(event.content))
                            _messages.update { currentList ->
                                (listOf(event.content) + currentList)//.sortedByDescending { it.id }
                            }
                        }

                        is ReplyVariants.Chunks -> {
                            _newReply.value += event.content.data
                        }

                        is ReplyVariants.FinalReply -> {
                            database.updateMessages(currentChatId, listOf(event.content))
                            _messages.update { currentList ->
                                (listOf(event.content) + currentList)//.sortedByDescending { it.id }
                            }
                        }

                        is ReplyVariants.Error -> {
                            fail(
                                error = event.message,
                                partialText = event.partialResponse?.takeIf { it.isNotBlank() } ?: _newReply.value,
                            )
                        }
                    }
                }
        }
    }

    override fun resetChatSpecificStates() {
        _selectedSysMessage.value = null
        _filesList.value = emptyList()
        _inputValue.value = ""
        _isPreviousMessagesEnabled.value = false
        _newReply.value = ""
        _failedReply.value = null
        lastRequest = null
        // _messages.value = emptyList() // Consider if messages should be cleared immediately or wait for new messages to load
    }

    override fun onCleared() {
        super.onCleared()
//        println("ViwModel cleared")
    }

}