package dev.goood.chat_client.viewModels

import dev.goood.chat_client.model.SystemMessage
import dev.goood.chat_client.model.SystemMessageList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import dev.goood.chat_client.services.SystemMessagesService.State

class SystemMessagesViewModelPreview: SystemMessagesViewModel() {
    override val state: StateFlow<State> = MutableStateFlow(State.Success)

    override val messages: StateFlow<SystemMessageList> = MutableStateFlow(
        listOf(
            SystemMessage(
                id = 0,
                title = "Improve phrase",
                content = "Some long content"
            ),
            SystemMessage(
                id = 1,
                title = "Translate",
                content = "Some long content"
            ),

        )
    )

    override fun deleteMessage(messageID: Int) {

    }

}