package dev.goood.chat_client.viewModels

import dev.goood.chat_client.model.SystemMessage
import dev.goood.chat_client.model.SystemMessageList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SystemMessagesViewModelPreview: SystemMessagesViewModel() {
    override val state: StateFlow<State> = MutableStateFlow(State.Loading)

    override val messages: StateFlow<SystemMessageList> = MutableStateFlow(
        listOf(
            SystemMessage(
                id = 0,
                title = "Improve phrase",
                content = "Some long content"
            )
        )
    )

}