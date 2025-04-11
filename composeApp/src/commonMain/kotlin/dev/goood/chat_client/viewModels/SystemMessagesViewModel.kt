package dev.goood.chat_client.viewModels

import androidx.lifecycle.ViewModel
import dev.goood.chat_client.model.SystemMessageList
import kotlinx.coroutines.flow.StateFlow

abstract class SystemMessagesViewModel: ViewModel() {

    abstract val state: StateFlow<State>
    abstract val messages: StateFlow<SystemMessageList>

    sealed interface State {
        data object Success: State
        data class Error(val message: String): State
        data object Loading: State
    }
}