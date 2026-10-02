package dev.goood.chat_client.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.goood.chat_client.core.network.Api
import dev.goood.chat_client.core.network.toUserMessage
import dev.goood.chat_client.model.ChatModel
import dev.goood.chat_client.model.ChatModelList
import dev.goood.chat_client.model.ChatSource
import dev.goood.chat_client.model.ChatSourceList
import dev.goood.chat_client.model.NewChat
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AddChatViewModel: ViewModel(), KoinComponent  {

    private val api: Api by inject()

    private var _state = MutableStateFlow<State>(State.Loading)
    val state: StateFlow<State> = _state

    private val _sourceList = MutableStateFlow<ChatSourceList>(emptyList())
    val sourceList: StateFlow<ChatSourceList> = _sourceList

    private var allModels = emptyList<ChatModel>()
    private val _modelList = MutableStateFlow<ChatModelList>(emptyList())
    val modelList: StateFlow<ChatModelList> = _modelList

    private val _chatName = MutableStateFlow("")
    val chatName: StateFlow<String> = _chatName

    private val selectedSource = MutableStateFlow<ChatSource?>(null)
    private val _selectedModel = MutableStateFlow<ChatModel?>(null)
    val selectedModel = _selectedModel.asStateFlow()

    fun upDate(){
        _state.value = State.Loading
        viewModelScope.launch {
            try {
                _sourceList.value = api.chatApi.getSources().first()
                allModels = api.chatApi.getModels().first()
                selectedSource.value?.let { source -> _modelList.value = modelsForSource(allModels, source.id) }
                validateForm()
            } catch (error: CancellationException) {
                throw error
            } catch (error: Throwable) {
                _state.value = State.LoadError(error.toUserMessage())
            }
        }
    }

    fun setSelectedSource(source: ChatSource) {
        selectedSource.value = source
        _selectedModel.value = null
        _modelList.value = modelsForSource(allModels, source.id)
        validateForm()
    }

    fun setSelectedModel(model: ChatModel) {
        _selectedModel.value = model
        validateForm()
    }

    fun setChatName(name: String) {
        _chatName.value = name
        validateForm()
    }

    private fun validateForm() {
        if (_state.value is State.LoadError || _state.value is State.Saving) return
        _state.value = if (
            chatName.value.isNotBlank() &&
            isModelValidForSource(_selectedModel.value, selectedSource.value)
        ) {
            State.FormValid
        } else {
            State.Incomplete
        }
    }

    fun createNewChat() {
        val source = selectedSource.value
        val model = _selectedModel.value
        // The server doesn't check that the model belongs to the source, so the client must.
        if (source == null || model?.id == null || !isModelValidForSource(model, source)) {
            _state.value = State.Error("Select a registered model for the chosen source.")
            return
        }

        _state.value = State.Saving
        viewModelScope.launch {
            val chat = NewChat(
                id = 1,
                name = chatName.value.trim(),
                sourceID = source.id,
                modelID = model.id,
            )
            try {
                api.chatApi.addChat(chat).first()
                _state.value = State.Success
            } catch (error: CancellationException) {
                throw error
            } catch (error: Throwable) {
                _state.value = State.Error(error.toUserMessage())
            }
        }
    }

    sealed interface State {
        data object Loading: State
        data class LoadError(val message: String): State
        data object Incomplete: State
        data object FormValid: State
        data object Saving: State
        data object Success: State
        data class Error(val message: String): State
    }
}

/** Registered models (those with a database id) that belong to the given source. */
internal fun modelsForSource(models: ChatModelList, sourceID: Int): ChatModelList =
    models.filter { it.id != null && it.sourceID == sourceID }

internal fun isModelValidForSource(model: ChatModel?, source: ChatSource?): Boolean =
    model?.id != null && source != null && model.sourceID == source.id
