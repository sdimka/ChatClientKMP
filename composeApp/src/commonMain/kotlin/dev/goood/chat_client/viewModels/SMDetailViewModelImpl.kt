package dev.goood.chat_client.viewModels

import dev.goood.chat_client.model.SystemMessage
import dev.goood.chat_client.services.SystemMessagesService
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SMDetailViewModelImpl: SMDetailViewModel(), KoinComponent {

    private val service: SystemMessagesService by inject()

    override val selectedMessage: StateFlow<SystemMessage?> = service.selectedMessage

    override fun setCurrentMessage(messID: Int) {
        service.setCurrentMessage(messID)
    }
}