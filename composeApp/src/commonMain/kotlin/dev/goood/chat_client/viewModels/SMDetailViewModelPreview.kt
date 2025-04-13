package dev.goood.chat_client.viewModels

import dev.goood.chat_client.model.SystemMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SMDetailViewModelPreview: SMDetailViewModel() {

    override val selectedMessage: StateFlow<SystemMessage?> = MutableStateFlow(
        SystemMessage(
            id = 0,
            title = "Improve phrase",
            content = "Some long content"
        )
    )

    override fun setCurrentMessage(messID: Int) {
    }
}
