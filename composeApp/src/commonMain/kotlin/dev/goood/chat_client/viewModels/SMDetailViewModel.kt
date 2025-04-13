package dev.goood.chat_client.viewModels

import androidx.lifecycle.ViewModel
import dev.goood.chat_client.model.SystemMessage
import dev.goood.chat_client.services.SystemMessagesService
import kotlinx.coroutines.flow.StateFlow


abstract class SMDetailViewModel: ViewModel() {

    abstract val state: StateFlow<SystemMessagesService.State>
    abstract val selectedMessage: StateFlow<SystemMessage?>

    abstract fun getCurrentMessage(messID: Int)

    abstract fun setTitle(title: String)
    abstract fun setContent(title: String)

    abstract fun updateMessage()

}