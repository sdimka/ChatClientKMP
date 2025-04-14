package dev.goood.chat_client.viewModels


import dev.goood.chat_client.services.SystemMessagesService
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.component.KoinComponent
import dev.goood.chat_client.services.SystemMessagesService.State
import org.koin.core.component.inject

class SystemMessagesViewModelImpl: SystemMessagesViewModel(), KoinComponent {

    private val service: SystemMessagesService by inject()

    override val state: StateFlow<State> = service.state

    override val messages = service.messages

    override fun deleteMessage(messageID: Int) {
        service.deleteMessage(messageID)
    }

}