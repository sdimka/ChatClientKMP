package dev.goood.chat_client.viewModels

import androidx.lifecycle.ViewModel
import dev.goood.chat_client.model.SystemMessageList
import dev.goood.chat_client.services.SystemMessagesService.State
import kotlinx.coroutines.flow.StateFlow

abstract class SystemMessagesViewModel: ViewModel() {

    abstract val state: StateFlow<State>
    abstract val messages: StateFlow<SystemMessageList>

}