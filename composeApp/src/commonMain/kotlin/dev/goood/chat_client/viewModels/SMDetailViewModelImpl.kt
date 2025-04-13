package dev.goood.chat_client.viewModels

import dev.goood.chat_client.model.SystemMessage
import dev.goood.chat_client.services.SystemMessagesService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SMDetailViewModelImpl: SMDetailViewModel(), KoinComponent {

    private val service: SystemMessagesService by inject()

    override val state: StateFlow<SystemMessagesService.State> = service.state

    private val _selectedMessage: MutableStateFlow<SystemMessage?> = MutableStateFlow(null)
    override val selectedMessage: StateFlow<SystemMessage?> = _selectedMessage.asStateFlow()

    override fun getCurrentMessage(messID: Int) {
        _selectedMessage.value = service.getCurrentMessage(messID)
    }

    override fun setTitle(title: String) {
        _selectedMessage.value = _selectedMessage.value?.copy(title = title)
    }

    override fun setContent(title: String) {
        _selectedMessage.value = _selectedMessage.value?.copy(content = title)
    }

    override fun updateMessage() {
        service.updateMessage(_selectedMessage.value!!)
    }
}