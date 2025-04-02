package dev.goood.chat_client.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.goood.chat_client.core.network.Api
import dev.goood.chat_client.model.ChatModelList
import dev.goood.chat_client.model.ChatSourceList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


class NewChatViewModel: ViewModel(), KoinComponent  {

    private val api: Api by inject()

    private var _state = MutableStateFlow<State>(State.Loading)
    val state: StateFlow<State> = _state

    val sourceList = MutableStateFlow<ChatSourceList>(emptyList())
    val modelList = MutableStateFlow<ChatModelList>(emptyList())

    private fun getSources() {
        viewModelScope.launch {
            api.chatApi.getSources()
                .catch {

                }
                .collect{
                    sourceList.value = it
                }
        }
    }

    private fun getModels(sourceId: Int) {
        viewModelScope.launch {
            api.chatApi.getModels(sourceId)
                .catch {

                }
                .collect{
                    modelList.value = it
                }

        }
    }

    sealed interface State {
        data object Success: State
        data class Error(val message: String): State
        data object Loading: State
    }
}