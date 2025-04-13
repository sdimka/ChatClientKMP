package dev.goood.chat_client.viewModels

import androidx.lifecycle.ViewModel
import dev.goood.chat_client.model.SystemMessage
import kotlinx.coroutines.flow.StateFlow


abstract class SMDetailViewModel: ViewModel() {

    abstract val selectedMessage: StateFlow<SystemMessage?>

    abstract fun setCurrentMessage(messID: Int)

}