package dev.goood.chat_client.viewModels

import androidx.lifecycle.ViewModel
import dev.goood.chat_client.model.FileList
import dev.goood.chat_client.model.MFile
import dev.goood.chat_client.model.Message
import dev.goood.chat_client.model.MessageList
import dev.goood.chat_client.model.SystemMessage
import dev.goood.chat_client.model.SystemMessageList
import kotlinx.coroutines.flow.StateFlow

abstract class ChatViewModel: ViewModel() {

    abstract val state: StateFlow<State>
    abstract val messages: StateFlow<MessageList>
    abstract val newReply: StateFlow<String>
    abstract val systemMessages: StateFlow<SystemMessageList>
    abstract val selectedSysMessage: StateFlow<SystemMessage?>
    abstract val filesList: StateFlow<List<MFile>>

    abstract fun selectSysMessage(sysMessage: SystemMessage?)
    abstract fun updateFilesList(fileList: FileList)
    abstract fun getMessages(chatId: Int)
    abstract fun deleteMessage(message: Message)
    abstract fun sendMessage(messageText: String)

    sealed interface State {
        data object Success: State
        data class Error(val message: String): State
        data object Loading: State
        data object NewReply: State

    }
}