package dev.goood.chat_client.viewModels

import dev.goood.chat_client.model.SystemMessage
import dev.goood.chat_client.services.SystemMessagesService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SMDetailViewModelPreview: SMDetailViewModel() {

    override val state: StateFlow<SystemMessagesService.State> =
        MutableStateFlow(SystemMessagesService.State.Success)

    override val selectedMessage: StateFlow<SystemMessage?> = MutableStateFlow(
        SystemMessage(
            id = 0,
            title = "Improve phrase",
            content = "Some long content"
        )
    )

    override fun getCurrentMessage(messID: Int) {
    }

    override fun getNewsMessage() {

    }

    override fun setTitle(title: String) {

    }

    override fun setContent(title: String) {

    }

    override fun updateMessage() {

    }
}
